package com.procurement.contracting.infrastructure.repository

import com.datastax.driver.core.BatchStatement
import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.datastax.driver.core.Statement
import com.procurement.contracting.application.exception.repository.ReadEntityException
import com.procurement.contracting.application.exception.repository.SaveEntityException
import com.procurement.contracting.application.repository.CANRepository
import com.procurement.contracting.application.repository.DataCancelCAN
import com.procurement.contracting.application.repository.DataRelatedCAN
import com.procurement.contracting.application.repository.DataStatusesCAN
import com.procurement.contracting.domain.entity.CANEntity
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class CassandraCANRepository(private val session: Session) : CANRepository {

    companion object {
        private const val keySpace = "ocds"
        private const val tableName = "contracting_can"
        private const val columnCpid = "cp_id"
        private const val columnCanId = "can_id"
        private const val columnToken = "token_entity"
        private const val columnOwner = "owner"
        private const val columnCreatedDate = "created_date"
        private const val columnAwardId = "award_id"
        private const val columnLotId = "lot_id"
        private const val columnContractId = "ac_id"
        private const val columnStatus = "status"
        private const val columnStatusDetails = "status_details"
        private const val columnJsonData = "json_data"

        private const val FIND_BY_CPID_CQL = """
               SELECT $columnCpid,
                      $columnCanId,
                      $columnToken,
                      $columnOwner,
                      $columnCreatedDate,
                      $columnAwardId,
                      $columnLotId,
                      $columnContractId,
                      $columnStatus,
                      $columnStatusDetails,
                      $columnJsonData
                 FROM $keySpace.$tableName
                WHERE $columnCpid=?
            """

        private const val FIND_BY_CPID_AND_CAN_ID_CQL = """
               SELECT $columnCpid,
                      $columnCanId,
                      $columnToken,
                      $columnOwner,
                      $columnCreatedDate,
                      $columnAwardId,
                      $columnLotId,
                      $columnContractId,
                      $columnStatus,
                      $columnStatusDetails,
                      $columnJsonData
                 FROM $keySpace.$tableName
                WHERE $columnCpid=?
                  AND $columnCanId=?
            """

        private const val CANCEL_CQL = """
               UPDATE $keySpace.$tableName
                  SET $columnContractId=?,
                      $columnStatus=?,
                      $columnStatusDetails=?,
                      $columnJsonData=?
                WHERE $columnCpid=?
                  AND $columnCanId=?
               IF EXISTS
            """

        private const val UPDATE_STATUSES_CQL = """
               UPDATE $keySpace.$tableName
                  SET $columnStatus=?,
                      $columnStatusDetails=?,
                      $columnJsonData=?
                WHERE $columnCpid=?
                AND   $columnCanId=?
               IF EXISTS
            """

        private const val SAVE_NEW_CAN_CQL = """
               INSERT INTO $keySpace.$tableName(
                           $columnCpid,
                           $columnCanId,
                           $columnToken,
                           $columnOwner,
                           $columnCreatedDate,
                           $columnAwardId,
                           $columnLotId,
                           $columnContractId,
                           $columnStatus,
                           $columnStatusDetails,
                           $columnJsonData
               )
               VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) 
               IF NOT EXISTS
            """
    }

    private val preparedFindByCpidAndCanIdCQL = session.prepare(FIND_BY_CPID_AND_CAN_ID_CQL)
    private val preparedFindByCpidCQL = session.prepare(FIND_BY_CPID_CQL)
    private val preparedCancelCQL = session.prepare(CANCEL_CQL)
    private val preparedUpdateStatusesCQL = session.prepare(UPDATE_STATUSES_CQL)
    private val preparedSaveNewCANCQL = session.prepare(SAVE_NEW_CAN_CQL)

    override fun findBy(cpid: String, canId: UUID): CANEntity? {
        val query = preparedFindByCpidAndCanIdCQL.bind()
            .apply {
                setString(columnCpid, cpid)
                setUUID(columnCanId, canId)
            }

        val resultSet = load(query)
        return resultSet.one()
            ?.let { convertToCANEntity(it) }
    }

    override fun findBy(cpid: String): List<CANEntity> {
        val query = preparedFindByCpidCQL.bind()
            .apply {
                setString(columnCpid, cpid)
            }

        val resultSet = load(query)
        return resultSet.map { convertToCANEntity(it) }
    }

    protected fun load(statement: BoundStatement): ResultSet = try {
        session.execute(statement)
    } catch (exception: Exception) {
        throw ReadEntityException(message = "Error read CAN(s) from the database.", cause = exception)
    }

    private fun convertToCANEntity(row: Row): CANEntity = CANEntity(
        cpid = row.getString(columnCpid),
        id = row.getUUID(columnCanId),
        token = row.getUUID(columnToken),
        owner = row.getString(columnOwner),
        createdDate = row.getTimestamp(columnCreatedDate).toLocalDateTime(),
        awardId = UUID.fromString(row.getString(columnAwardId)),
        lotId = UUID.fromString(row.getString(columnLotId)),
        contractId = row.getString(columnContractId),
        status = row.getString(columnStatus),
        statusDetails = row.getString(columnStatusDetails),
        jsonData = row.getString(columnJsonData)
    )

    override fun saveCancelledCANs(
        cpid: String,
        dataCancelledCAN: DataCancelCAN,
        dataRelatedCANs: List<DataRelatedCAN>
    ) {
        val statements = BatchStatement().apply {
            add(statementForCancelCAN(cpid = cpid, dataCancelledCAN = dataCancelledCAN))
            for (dataRelatedCan in dataRelatedCANs) {
                add(statementForRelatedCAN(cpid = cpid, dataCancelledCAN = dataRelatedCan))
            }
        }

        val result = cancellationCANs(statements)
        if (!result.wasApplied())
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the CAN(s) by cpid '$cpid' from the database.")
    }

    private fun statementForCancelCAN(cpid: String, dataCancelledCAN: DataCancelCAN): Statement =
        preparedCancelCQL.bind()
            .apply {
                setString(columnCpid, cpid)
                setUUID(columnCanId, dataCancelledCAN.id)
                setString(columnContractId, null)
                setString(columnStatus, dataCancelledCAN.status.toString())
                setString(columnStatusDetails, dataCancelledCAN.statusDetails.toString())
                setString(columnJsonData, dataCancelledCAN.jsonData)
            }

    private fun statementForRelatedCAN(cpid: String, dataCancelledCAN: DataRelatedCAN): Statement =
        preparedCancelCQL.bind()
            .apply {
                setString(columnCpid, cpid)
                setUUID(columnCanId, dataCancelledCAN.id)
                setString(columnContractId, null)
                setString(columnStatus, dataCancelledCAN.status.toString())
                setString(columnStatusDetails, dataCancelledCAN.statusDetails.toString())
                setString(columnJsonData, dataCancelledCAN.jsonData)
            }

    private fun cancellationCANs(statement: BatchStatement): ResultSet = try {
        session.execute(statement)
    } catch (exception: Exception) {
        throw SaveEntityException(message = "Error writing cancelled CAN(s).", cause = exception)
    }

    override fun updateStatusesCANs(cpid: String, cans: List<DataStatusesCAN>) {
        val statements = BatchStatement().apply {
            for (can in cans) {
                add(statementForUpdateStatusesCAN(cpid = cpid, can = can))
            }
        }

        val result = updateStatusesCANs(statements)
        if (!result.wasApplied())
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the CAN(s) by cpid '$cpid' from the database.")
    }

    private fun statementForUpdateStatusesCAN(cpid: String, can: DataStatusesCAN): Statement =
        preparedUpdateStatusesCQL.bind()
            .apply {
                setString(columnCpid, cpid)
                setUUID(columnCanId, can.id)
                setString(columnStatus, can.status.value)
                setString(columnStatusDetails, can.statusDetails.value)
                setString(columnJsonData, can.jsonData)
            }

    private fun updateStatusesCANs(statement: BatchStatement): ResultSet = try {
        session.execute(statement)
    } catch (exception: Exception) {
        throw SaveEntityException(message = "Error writing updated statuses CAN(s).", cause = exception)
    }

    override fun saveNewCAN(cpid: String, entity: CANEntity) {
        val statement = preparedSaveNewCANCQL.bind()
            .apply {
                setString(columnCpid, cpid)
                setUUID(columnCanId, entity.id)
                setUUID(columnToken, entity.token)
                setString(columnOwner, entity.owner)
                setTimestamp(columnCreatedDate, entity.createdDate.toCassandraTimestamp())
                setString(columnAwardId, entity.awardId.toString())
                setString(columnLotId, entity.lotId.toString())
                setString(columnContractId, entity.contractId)
                setString(columnStatus, entity.status)
                setString(columnStatusDetails, entity.statusDetails)
                setString(columnJsonData, entity.jsonData)
            }

        val result = saveNewCAN(statement)
        if (!result.wasApplied())
            throw SaveEntityException(message = "An error occurred when writing a record(s) of new CAN by cpid '$cpid' and lot id '${entity.lotId}' and award id '${entity.awardId}' to the database. Record is already.")
    }

    private fun saveNewCAN(statement: BoundStatement): ResultSet = try {
        session.execute(statement)
    } catch (exception: Exception) {
        throw SaveEntityException(message = "Error writing new CAN.", cause = exception)
    }
}
