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
    }

    private val preparedFindByCpidAndCanIdCQL = session.prepare(FIND_BY_CPID_AND_CAN_ID_CQL)
    private val preparedFindByCpidCQL = session.prepare(FIND_BY_CPID_CQL)
    private val preparedCancelCQL = session.prepare(CANCEL_CQL)

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
        awardId = row.getString(columnAwardId),
        lotId = row.getString(columnLotId),
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
}
