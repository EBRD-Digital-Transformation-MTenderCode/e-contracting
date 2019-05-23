package com.procurement.contracting.infrastructure.repository

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.procurement.contracting.application.exception.repository.ReadEntityException
import com.procurement.contracting.application.exception.repository.SaveEntityException
import com.procurement.contracting.application.repository.ACRepository
import com.procurement.contracting.application.repository.DataCancelAC
import com.procurement.contracting.domain.entity.ACEntity
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
    }

    private val preparedFindByCpidAndCanIdCQL = session.prepare(FIND_BY_CPID_AND_CAN_ID_CQL)
    private val preparedCancelCQL = session.prepare(CANCEL_CQL)

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
        status = row.getString(columnStatus),
        statusDetails = row.getString(columnStatusDetails),
        mainProcurementCategory = row.getString(columnMPC),
        language = row.getString(columnLanguage),
        jsonData = row.getString(columnJsonData)
    )

    override fun cancellationAC(dataCancelAC: DataCancelAC) {
        val statements = preparedCancelCQL.bind()
            .apply {
                setString(columnCpid, dataCancelAC.cpid)
                setString(columnContractId, dataCancelAC.id)
                setString(columnStatus, dataCancelAC.status.toString())
                setString(columnStatusDetails, dataCancelAC.statusDetails.toString())
                setString(columnJsonData, dataCancelAC.jsonData)
            }

        saveCancelledAC(statements)
    }

    private fun saveCancelledAC(statement: BoundStatement): ResultSet = try {
        session.execute(statement)
    } catch (exception: Exception) {
        throw SaveEntityException(message = "Error writing cancelled Contract.", cause = exception)
    }
}
