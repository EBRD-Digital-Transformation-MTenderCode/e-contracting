package com.procurement.contracting.infrastructure.repository

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.procurement.contracting.application.exception.repository.ReadEntityException
import com.procurement.contracting.application.exception.repository.SaveEntityException
import com.procurement.contracting.application.repository.ACRepository
import com.procurement.contracting.domain.entity.ACEntity
import com.procurement.contracting.domain.model.MainProcurementCategory
import com.procurement.contracting.domain.model.contract.status.ContractStatus
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails
import org.springframework.stereotype.Repository

@Repository
class CassandraACRepository(private val session: Session) : ACRepository {

    companion object {
        private const val keySpace = "ocds"
        private const val tableName = "contracting_ac"
        private const val columnCpid = "cp_id"
        private const val columnContractId = "ac_id"
        private const val columnToken = "token_entity"
        private const val columnOwner = "owner"
        private const val columnCreatedDate = "created_date"
        private const val columnStatus = "status"
        private const val columnStatusDetails = "status_details"
        private const val columnMPC = "mpc"
        private const val columnLanguage = "language"
        private const val columnJsonData = "json_data"

        private const val FIND_BY_CPID_AND_CAN_ID_CQL = """
               SELECT $columnCpid,
                      $columnContractId,
                      $columnToken,
                      $columnOwner,
                      $columnCreatedDate,
                      $columnStatus,
                      $columnStatusDetails,
                      $columnMPC,
                      $columnLanguage,
                      $columnJsonData
                 FROM $keySpace.$tableName
                WHERE $columnCpid=?
                  AND $columnContractId=?
            """

        private const val CANCEL_CQL = """
               UPDATE $keySpace.$tableName
                  SET $columnStatus=?,
                      $columnStatusDetails=?,
                      $columnJsonData=?
                WHERE $columnCpid=?
                  AND $columnContractId=?
               IF EXISTS
            """

        private const val UPDATE_STATUSES_CQL = """
               UPDATE $keySpace.$tableName
                  SET $columnStatus=?, 
                      $columnStatusDetails=?,
                      $columnJsonData=?
                WHERE $columnCpid=?
                  AND $columnContractId=?
               IF EXISTS
            """

        private const val SAVE_NEW_CQL = """
               INSERT INTO $keySpace.$tableName(
                      $columnCpid,
                      $columnContractId,
                      $columnToken,
                      $columnOwner,
                      $columnCreatedDate,
                      $columnStatus,
                      $columnStatusDetails,
                      $columnMPC,
                      $columnLanguage,
                      $columnJsonData
               )
               VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
               IF NOT EXISTS
            """
    }

    private val preparedFindByCpidAndCanIdCQL = session.prepare(FIND_BY_CPID_AND_CAN_ID_CQL)
    private val preparedCancelCQL = session.prepare(CANCEL_CQL)
    private val preparedUpdateStatusesCQL = session.prepare(UPDATE_STATUSES_CQL)
    private val preparedSaveNewCQL = session.prepare(SAVE_NEW_CQL)

    override fun findBy(cpid: String, contractId: String): ACEntity? {
        val query = preparedFindByCpidAndCanIdCQL.bind()
            .apply {
                setString(columnCpid, cpid)
                setString(columnContractId, contractId)
            }

        val resultSet = load(query)
        return resultSet.one()
            ?.let { convertToContractEntity(it) }
    }

    protected fun load(statement: BoundStatement): ResultSet = try {
        session.execute(statement)
    } catch (exception: Exception) {
        throw ReadEntityException(message = "Error read Contract(s) from the database.", cause = exception)
    }

    private fun convertToContractEntity(row: Row): ACEntity = ACEntity(
        cpid = row.getString(columnCpid),
        id = row.getString(columnContractId),
        token = row.getUUID(columnToken),
        owner = row.getString(columnOwner),
        createdDate = row.getTimestamp(columnCreatedDate).toLocalDateTime(),
        status = ContractStatus.creator(row.getString(columnStatus)),
        statusDetails = ContractStatusDetails.creator(row.getString(columnStatusDetails)),
        mainProcurementCategory = MainProcurementCategory.creator(row.getString(columnMPC)),
        language = row.getString(columnLanguage),
        jsonData = row.getString(columnJsonData)
    )

    override fun saveNew(entity: ACEntity) {
        val statements = preparedSaveNewCQL.bind()
            .apply {
                setString(columnCpid, entity.cpid)
                setString(columnContractId, entity.id)
                setUUID(columnToken, entity.token)
                setString(columnOwner, entity.owner)
                setTimestamp(columnCreatedDate, entity.createdDate.toCassandraTimestamp())
                setString(columnStatus, entity.status.key)
                setString(columnStatusDetails, entity.statusDetails.key)
                setString(columnMPC, entity.mainProcurementCategory.key)
                setString(columnLanguage, entity.language)
                setString(columnJsonData, entity.jsonData)
            }

        val result = saveNew(statements)
        if (!result.wasApplied())
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the new contract by cpid '${entity.cpid}' and id '${entity.id}' to the database. Record is already.")
    }

    private fun saveNew(statement: BoundStatement): ResultSet = try {
        session.execute(statement)
    } catch (exception: Exception) {
        throw SaveEntityException(message = "Error writing new contract to database.", cause = exception)
    }

    override fun saveCancelledAC(
        cpid: String,
        id: String,
        status: ContractStatus,
        statusDetails: ContractStatusDetails,
        jsonData: String
    ) {
        val statements = preparedCancelCQL.bind()
            .apply {
                setString(columnCpid, cpid)
                setString(columnContractId, id)
                setString(columnStatus, status.toString())
                setString(columnStatusDetails, statusDetails.toString())
                setString(columnJsonData, jsonData)
            }

        val result = saveCancelledAC(statements)
        if (!result.wasApplied())
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the save cancelled AC by cpid '$cpid' and id '$id' with status '$status' and status details '$statusDetails' to the database. Record is not exists.")
    }

    private fun saveCancelledAC(statement: BoundStatement): ResultSet = try {
        session.execute(statement)
    } catch (exception: Exception) {
        throw SaveEntityException(message = "Error writing cancelled contract.", cause = exception)
    }

    override fun updateStatusesAC(
        cpid: String,
        id: String,
        status: ContractStatus,
        statusDetails: ContractStatusDetails,
        jsonData: String
    ) {
        val statements = preparedUpdateStatusesCQL.bind()
            .apply {
                setString(columnCpid, cpid)
                setString(columnContractId, id)
                setString(columnStatus, status.toString())
                setString(columnStatusDetails, statusDetails.toString())
                setString(columnJsonData, jsonData)
            }

        updateStatusesAC(statements)
    }

    private fun updateStatusesAC(statement: BoundStatement): ResultSet = try {
        session.execute(statement)
    } catch (exception: Exception) {
        throw SaveEntityException(message = "Error writing updated contract.", cause = exception)
    }
}
