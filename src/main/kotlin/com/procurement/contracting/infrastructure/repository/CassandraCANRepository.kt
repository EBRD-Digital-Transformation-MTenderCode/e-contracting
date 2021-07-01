package com.procurement.contracting.infrastructure.repository

import com.datastax.driver.core.BatchStatement
import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.datastax.driver.core.Statement
import com.procurement.contracting.application.exception.repository.SaveEntityException
import com.procurement.contracting.application.repository.can.CANRepository
import com.procurement.contracting.application.repository.can.model.CANEntity
import com.procurement.contracting.application.repository.can.model.DataCancelCAN
import com.procurement.contracting.application.repository.can.model.DataRelatedCAN
import com.procurement.contracting.application.repository.can.model.DataResetCAN
import com.procurement.contracting.application.repository.can.model.RelatedContract
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.ac.id.AwardContractId
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.infrastructure.extension.cassandra.toCassandraTimestamp
import com.procurement.contracting.infrastructure.extension.cassandra.toLocalDateTime
import com.procurement.contracting.infrastructure.extension.cassandra.tryExecute
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class CassandraCANRepository(@Qualifier("ocds") private val session: Session) : CANRepository {

    companion object {
        private const val ID_VALUES = "id_values"

        private const val FIND_BY_CPID_CQL = """
               SELECT ${Database.CAN.COLUMN_CPID},
                      ${Database.CAN.COLUMN_CANID},
                      ${Database.CAN.COLUMN_TOKEN},
                      ${Database.CAN.COLUMN_OWNER},
                      ${Database.CAN.COLUMN_CREATED_DATE},
                      ${Database.CAN.COLUMN_AWARD_ID},
                      ${Database.CAN.COLUMN_LOT_ID},
                      ${Database.CAN.COLUMN_AWARD_CONTRACT_ID},
                      ${Database.CAN.COLUMN_STATUS},
                      ${Database.CAN.COLUMN_STATUS_DETAILS},
                      ${Database.CAN.COLUMN_JSON_DATA}
                 FROM ${Database.KEYSPACE}.${Database.CAN.TABLE}
                WHERE ${Database.CAN.COLUMN_CPID}=?
            """

        private const val FIND_BY_CPID_AND_CAN_ID_CQL = """
               SELECT ${Database.CAN.COLUMN_CPID},
                      ${Database.CAN.COLUMN_CANID},
                      ${Database.CAN.COLUMN_TOKEN},
                      ${Database.CAN.COLUMN_OWNER},
                      ${Database.CAN.COLUMN_CREATED_DATE},
                      ${Database.CAN.COLUMN_AWARD_ID},
                      ${Database.CAN.COLUMN_LOT_ID},
                      ${Database.CAN.COLUMN_AWARD_CONTRACT_ID},
                      ${Database.CAN.COLUMN_STATUS},
                      ${Database.CAN.COLUMN_STATUS_DETAILS},
                      ${Database.CAN.COLUMN_JSON_DATA}
                 FROM ${Database.KEYSPACE}.${Database.CAN.TABLE}
                WHERE ${Database.CAN.COLUMN_CPID}=?
                  AND ${Database.CAN.COLUMN_CANID}=?
            """

        private const val FIND_BY_CPID_AND_CAN_IDS_CQL = """
               SELECT ${Database.CAN.COLUMN_CPID},
                      ${Database.CAN.COLUMN_CANID},
                      ${Database.CAN.COLUMN_TOKEN},
                      ${Database.CAN.COLUMN_OWNER},
                      ${Database.CAN.COLUMN_CREATED_DATE},
                      ${Database.CAN.COLUMN_AWARD_ID},
                      ${Database.CAN.COLUMN_LOT_ID},
                      ${Database.CAN.COLUMN_AWARD_CONTRACT_ID},
                      ${Database.CAN.COLUMN_STATUS},
                      ${Database.CAN.COLUMN_STATUS_DETAILS},
                      ${Database.CAN.COLUMN_JSON_DATA}
                 FROM ${Database.KEYSPACE}.${Database.CAN.TABLE}
                WHERE ${Database.CAN.COLUMN_CPID}=?
                  AND ${Database.CAN.COLUMN_CANID} in :$ID_VALUES;
            """

        private const val CANCEL_CQL = """
               UPDATE ${Database.KEYSPACE}.${Database.CAN.TABLE}
                  SET ${Database.CAN.COLUMN_AWARD_CONTRACT_ID}=?,
                      ${Database.CAN.COLUMN_STATUS}=?,
                      ${Database.CAN.COLUMN_STATUS_DETAILS}=?,
                      ${Database.CAN.COLUMN_JSON_DATA}=?
                WHERE ${Database.CAN.COLUMN_CPID}=?
                  AND ${Database.CAN.COLUMN_CANID}=?
               IF EXISTS
            """

        private const val RESET_CQL = """
               UPDATE ${Database.KEYSPACE}.${Database.CAN.TABLE}
                  SET ${Database.CAN.COLUMN_AWARD_CONTRACT_ID}=?,
                      ${Database.CAN.COLUMN_STATUS}=?,
                      ${Database.CAN.COLUMN_STATUS_DETAILS}=?,
                      ${Database.CAN.COLUMN_JSON_DATA}=?
                WHERE ${Database.CAN.COLUMN_CPID}=?
                  AND ${Database.CAN.COLUMN_CANID}=?
               IF EXISTS
            """

        private const val RELATE_CONTRACT_CQL = """
               UPDATE ${Database.KEYSPACE}.${Database.CAN.TABLE}
                  SET ${Database.CAN.COLUMN_AWARD_CONTRACT_ID}=?,
                      ${Database.CAN.COLUMN_STATUS}=?,
                      ${Database.CAN.COLUMN_STATUS_DETAILS}=?,
                      ${Database.CAN.COLUMN_JSON_DATA}=?
                WHERE ${Database.CAN.COLUMN_CPID}=?
                AND   ${Database.CAN.COLUMN_CANID}=?
               IF EXISTS
            """

        private const val SAVE_NEW_CAN_CQL = """
               INSERT INTO ${Database.KEYSPACE}.${Database.CAN.TABLE}(
                           ${Database.CAN.COLUMN_CPID},
                           ${Database.CAN.COLUMN_CANID},
                           ${Database.CAN.COLUMN_TOKEN},
                           ${Database.CAN.COLUMN_OWNER},
                           ${Database.CAN.COLUMN_CREATED_DATE},
                           ${Database.CAN.COLUMN_AWARD_ID},
                           ${Database.CAN.COLUMN_LOT_ID},
                           ${Database.CAN.COLUMN_AWARD_CONTRACT_ID},
                           ${Database.CAN.COLUMN_STATUS},
                           ${Database.CAN.COLUMN_STATUS_DETAILS},
                           ${Database.CAN.COLUMN_JSON_DATA}
               )
               VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) 
               IF NOT EXISTS
            """

        private const val UPDATE_CQL = """
               UPDATE ${Database.KEYSPACE}.${Database.CAN.TABLE}
                  SET ${Database.CAN.COLUMN_STATUS}=?,
                      ${Database.CAN.COLUMN_STATUS_DETAILS}=?,
                      ${Database.CAN.COLUMN_JSON_DATA}=?
                WHERE ${Database.CAN.COLUMN_CPID}=?
                AND   ${Database.CAN.COLUMN_CANID}=?
               IF EXISTS
            """
    }

    private val preparedFindByCpidAndCanIdCQL = session.prepare(FIND_BY_CPID_AND_CAN_ID_CQL)
    private val preparedFindByCpidAndCanIdsCQL = session.prepare(FIND_BY_CPID_AND_CAN_IDS_CQL)
    private val preparedFindByCpidCQL = session.prepare(FIND_BY_CPID_CQL)
    private val preparedCancelCQL = session.prepare(CANCEL_CQL)
    private val preparedResetCQL = session.prepare(RESET_CQL)
    private val preparedRelateContractCQL = session.prepare(RELATE_CONTRACT_CQL)
    private val preparedSaveNewCANCQL = session.prepare(SAVE_NEW_CAN_CQL)
    private val preparedUpdateNewCANCQL = session.prepare(UPDATE_CQL)

    override fun findBy(cpid: Cpid, canId: CANId): Result<CANEntity?, Fail.Incident.Database> =
        preparedFindByCpidAndCanIdCQL.bind()
            .apply {
                setString(Database.CAN.COLUMN_CPID, cpid.underlying)
                setUUID(Database.CAN.COLUMN_CANID, canId.underlying)
            }
            .tryExecute(session)
            .onFailure { return it }
            .one()
            ?.convert()
            .asSuccess()

    override fun findBy(cpid: Cpid): Result<List<CANEntity>, Fail.Incident.Database> =
        preparedFindByCpidCQL.bind()
            .apply {
                setString(Database.CAN.COLUMN_CPID, cpid.underlying)
            }
            .tryExecute(session)
            .onFailure { return it }
            .map { it.convert() }
            .asSuccess()

    override fun findBy(cpid: Cpid, canIds: List<CANId>): Result<List<CANEntity>, Fail.Incident.Database> =
        preparedFindByCpidAndCanIdsCQL.bind()
            .apply {
                setString(Database.CAN.COLUMN_CPID, cpid.underlying)
                setList(ID_VALUES, canIds.map { it.underlying })
            }
            .tryExecute(session)
            .onFailure { return it }
            .map { it.convert() }
            .asSuccess()

    private fun Row.convert(): CANEntity = CANEntity(
        cpid = Cpid.orNull(getString(Database.CAN.COLUMN_CPID))!!,
        id = CANId(getUUID(Database.CAN.COLUMN_CANID)),
        token = Token.orNull(getUUID(Database.CAN.COLUMN_TOKEN).toString())!!,
        owner = Owner.orNull(getString(Database.CAN.COLUMN_OWNER))!!,
        createdDate = getTimestamp(Database.CAN.COLUMN_CREATED_DATE).toLocalDateTime(),
        awardId = getString(Database.CAN.COLUMN_AWARD_ID)?.let { AwardId.orNull(it)!! },
        lotId = LotId.orNull(getString(Database.CAN.COLUMN_LOT_ID))!!,
        awardContractId = getString(Database.CAN.COLUMN_AWARD_CONTRACT_ID)?.let { AwardContractId.orNull(it)!! },
        status = CANStatus.creator(getString(Database.CAN.COLUMN_STATUS)),
        statusDetails = CANStatusDetails.creator(getString(Database.CAN.COLUMN_STATUS_DETAILS)),
        jsonData = getString(Database.CAN.COLUMN_JSON_DATA)
    )

    override fun saveCancelledCANs(
        cpid: Cpid,
        dataCancelledCAN: DataCancelCAN,
        dataRelatedCANs: List<DataRelatedCAN>
    ): Result<Boolean, Fail.Incident.Database> = BatchStatement()
        .apply {
            add(statementForCancelCAN(cpid = cpid, dataCancelledCAN = dataCancelledCAN))
            for (dataRelatedCan in dataRelatedCANs) {
                add(statementForRelatedCAN(cpid = cpid, dataCancelledCAN = dataRelatedCan))
            }
        }
        .tryExecute(session)
        .mapFailure {
            Fail.Incident.Database.DatabaseInteractionIncident(
                SaveEntityException(message = "Error writing cancelled CAN(s).", cause = it.exception)
            )
        }
        .onFailure { return it }
        .wasApplied()
        .asSuccess()

    private fun statementForCancelCAN(cpid: Cpid, dataCancelledCAN: DataCancelCAN): Statement =
        preparedCancelCQL.bind()
            .apply {
                setString(Database.CAN.COLUMN_CPID, cpid.underlying)
                setUUID(Database.CAN.COLUMN_CANID, dataCancelledCAN.id.underlying)
                setString(Database.CAN.COLUMN_AWARD_CONTRACT_ID, null)
                setString(Database.CAN.COLUMN_STATUS, dataCancelledCAN.status.key)
                setString(Database.CAN.COLUMN_STATUS_DETAILS, dataCancelledCAN.statusDetails.key)
                setString(Database.CAN.COLUMN_JSON_DATA, dataCancelledCAN.jsonData)
            }

    private fun statementForRelatedCAN(cpid: Cpid, dataCancelledCAN: DataRelatedCAN): Statement =
        preparedCancelCQL.bind()
            .apply {
                setString(Database.CAN.COLUMN_CPID, cpid.underlying)
                setUUID(Database.CAN.COLUMN_CANID, dataCancelledCAN.id.underlying)
                setString(Database.CAN.COLUMN_AWARD_CONTRACT_ID, null)
                setString(Database.CAN.COLUMN_STATUS, dataCancelledCAN.status.key)
                setString(Database.CAN.COLUMN_STATUS_DETAILS, dataCancelledCAN.statusDetails.key)
                setString(Database.CAN.COLUMN_JSON_DATA, dataCancelledCAN.jsonData)
            }

    override fun resetCANs(cpid: Cpid, cans: List<DataResetCAN>): Result<Boolean, Fail.Incident.Database> =
        BatchStatement()
            .apply {
                for (can in cans) {
                    add(statementForResetCAN(cpid = cpid, dataResetCAN = can))
                }
            }
            .tryExecute(session)
            .mapFailure {
                Fail.Incident.Database.DatabaseInteractionIncident(
                    SaveEntityException(message = "Error writing updated reset CAN(s).", cause = it.exception)
                )
            }
            .onFailure { return it }
            .wasApplied()
            .asSuccess()

    private fun statementForResetCAN(cpid: Cpid, dataResetCAN: DataResetCAN): Statement =
        preparedResetCQL.bind()
            .apply {
                setString(Database.CAN.COLUMN_CPID, cpid.underlying)
                setUUID(Database.CAN.COLUMN_CANID, dataResetCAN.id.underlying)
                setString(Database.CAN.COLUMN_AWARD_CONTRACT_ID, null)
                setString(Database.CAN.COLUMN_STATUS, dataResetCAN.status.key)
                setString(Database.CAN.COLUMN_STATUS_DETAILS, dataResetCAN.statusDetails.key)
                setString(Database.CAN.COLUMN_JSON_DATA, dataResetCAN.jsonData)
            }

    override fun relateContract(cpid: Cpid, cans: List<RelatedContract>): Result<Boolean, Fail.Incident.Database> =
        BatchStatement()
            .apply {
                for (can in cans) {
                    add(statementForRelateContract(cpid = cpid, can = can))
                }
            }
            .tryExecute(session)
            .mapFailure {
                Fail.Incident.Database.DatabaseInteractionIncident(
                    SaveEntityException(
                        message = "Error writing updated CAN(s) with related Contract.",
                        cause = it.exception
                    )
                )
            }
            .onFailure { return it }
            .wasApplied()
            .asSuccess()

    private fun statementForRelateContract(cpid: Cpid, can: RelatedContract): Statement =
        preparedRelateContractCQL.bind()
            .apply {
                setString(Database.CAN.COLUMN_CPID, cpid.underlying)
                setUUID(Database.CAN.COLUMN_CANID, can.id.underlying)
                setString(Database.CAN.COLUMN_AWARD_CONTRACT_ID, can.awardContractId.underlying)
                setString(Database.CAN.COLUMN_STATUS, can.status.key)
                setString(Database.CAN.COLUMN_STATUS_DETAILS, can.statusDetails.key)
                setString(Database.CAN.COLUMN_JSON_DATA, can.jsonData)
            }

    override fun saveNewCAN(cpid: Cpid, entity: CANEntity): Result<Boolean, Fail.Incident.Database> =
        preparedSaveNewCANCQL.bind()
            .apply {
                setString(Database.CAN.COLUMN_CPID, cpid.underlying)
                setUUID(Database.CAN.COLUMN_CANID, entity.id.underlying)
                setUUID(Database.CAN.COLUMN_TOKEN, entity.token.underlying)
                setString(Database.CAN.COLUMN_OWNER, entity.owner.underlying)
                setTimestamp(Database.CAN.COLUMN_CREATED_DATE, entity.createdDate.toCassandraTimestamp())
                setString(Database.CAN.COLUMN_AWARD_ID, entity.awardId?.toString())
                setString(Database.CAN.COLUMN_LOT_ID, entity.lotId.underlying)
                setString(Database.CAN.COLUMN_AWARD_CONTRACT_ID, entity.awardContractId?.underlying)
                setString(Database.CAN.COLUMN_STATUS, entity.status.key)
                setString(Database.CAN.COLUMN_STATUS_DETAILS, entity.statusDetails.key)
                setString(Database.CAN.COLUMN_JSON_DATA, entity.jsonData)
            }
            .tryExecute(session)
            .mapFailure {
                Fail.Incident.Database.DatabaseInteractionIncident(
                    SaveEntityException(message = "Error writing new CAN.", cause = it.exception)
                )
            }
            .onFailure { return it }
            .wasApplied()
            .asSuccess()

    override fun update(cpid: Cpid, entity: CANEntity): Result<Boolean, Fail.Incident.Database> =
        preparedUpdateNewCANCQL.bind()
            .apply {
                setString(Database.CAN.COLUMN_CPID, cpid.underlying)
                setUUID(Database.CAN.COLUMN_CANID, entity.id.underlying)
                setString(Database.CAN.COLUMN_STATUS, entity.status.key)
                setString(Database.CAN.COLUMN_STATUS_DETAILS, entity.statusDetails.key)
                setString(Database.CAN.COLUMN_JSON_DATA, entity.jsonData)
            }
            .tryExecute(session)
            .mapFailure {
                Fail.Incident.Database.DatabaseInteractionIncident(
                    SaveEntityException(message = "Error writing updated CAN.", cause = it.exception)
                )
            }
            .onFailure { return it }
            .wasApplied()
            .asSuccess()

    override fun update(cpid: Cpid, entities: Collection<CANEntity>): Result<Boolean, Fail.Incident.Database> =
        BatchStatement()
            .apply {
                for (entity in entities) {
                    add(statementForUpdate(cpid = cpid, entity = entity))
                }
            }
            .tryExecute(session)
            .mapFailure {
                Fail.Incident.Database.DatabaseInteractionIncident(
                    SaveEntityException(message = "Error writing updated CANs.", cause = it.exception)
                )
            }
            .onFailure { return it }
            .wasApplied()
            .asSuccess()

    private fun statementForUpdate(cpid: Cpid, entity: CANEntity): BoundStatement =
        preparedUpdateNewCANCQL.bind()
            .apply {
                setString(Database.CAN.COLUMN_CPID, cpid.underlying)
                setUUID(Database.CAN.COLUMN_CANID, entity.id.underlying)
                setString(Database.CAN.COLUMN_STATUS, entity.status.key)
                setString(Database.CAN.COLUMN_STATUS_DETAILS, entity.statusDetails.key)
                setString(Database.CAN.COLUMN_JSON_DATA, entity.jsonData)
            }
}
