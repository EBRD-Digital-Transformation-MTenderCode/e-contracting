package com.procurement.contracting.infrastructure.repository

import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.procurement.contracting.application.exception.repository.ReadEntityException
import com.procurement.contracting.application.exception.repository.SaveEntityException
import com.procurement.contracting.application.repository.ac.AwardContractRepository
import com.procurement.contracting.application.repository.ac.model.AwardContractEntity
import com.procurement.contracting.domain.model.MainProcurementCategory
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.ac.id.AwardContractId
import com.procurement.contracting.domain.model.ac.status.AwardContractStatus
import com.procurement.contracting.domain.model.ac.status.AwardContractStatusDetails
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
class CassandraAwardContractRepository(@Qualifier("ocds") private val session: Session) : AwardContractRepository {

    companion object {
        private const val FIND_BY_CPID_AND_ID_CQL = """
               SELECT ${Database.AC.COLUMN_CPID},
                      ${Database.AC.COLUMN_ID},
                      ${Database.AC.COLUMN_TOKEN},
                      ${Database.AC.COLUMN_OWNER},
                      ${Database.AC.COLUMN_CREATED_DATE},
                      ${Database.AC.COLUMN_STATUS},
                      ${Database.AC.COLUMN_STATUS_DETAILS},
                      ${Database.AC.COLUMN_MPC},
                      ${Database.AC.COLUMN_LANGUAGE},
                      ${Database.AC.COLUMN_JSON_DATA}
                 FROM ${Database.KEYSPACE}.${Database.AC.TABLE}
                WHERE ${Database.AC.COLUMN_CPID}=?
                  AND ${Database.AC.COLUMN_ID}=?
            """

        private const val CANCEL_CQL = """
               UPDATE ${Database.KEYSPACE}.${Database.AC.TABLE}
                  SET ${Database.AC.COLUMN_STATUS}=?,
                      ${Database.AC.COLUMN_STATUS_DETAILS}=?,
                      ${Database.AC.COLUMN_JSON_DATA}=?
                WHERE ${Database.AC.COLUMN_CPID}=?
                  AND ${Database.AC.COLUMN_ID}=?
               IF EXISTS
            """

        private const val UPDATE_STATUSES_CQL = """
               UPDATE ${Database.KEYSPACE}.${Database.AC.TABLE}
                  SET ${Database.AC.COLUMN_STATUS}=?, 
                      ${Database.AC.COLUMN_STATUS_DETAILS}=?,
                      ${Database.AC.COLUMN_JSON_DATA}=?
                WHERE ${Database.AC.COLUMN_CPID}=?
                  AND ${Database.AC.COLUMN_ID}=?
               IF EXISTS
            """

        private const val SAVE_NEW_CQL = """
               INSERT INTO ${Database.KEYSPACE}.${Database.AC.TABLE}(
                      ${Database.AC.COLUMN_CPID},
                      ${Database.AC.COLUMN_ID},
                      ${Database.AC.COLUMN_TOKEN},
                      ${Database.AC.COLUMN_OWNER},
                      ${Database.AC.COLUMN_CREATED_DATE},
                      ${Database.AC.COLUMN_STATUS},
                      ${Database.AC.COLUMN_STATUS_DETAILS},
                      ${Database.AC.COLUMN_MPC},
                      ${Database.AC.COLUMN_LANGUAGE},
                      ${Database.AC.COLUMN_JSON_DATA}
               )
               VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
               IF NOT EXISTS
            """
    }

    private val preparedFindByCpidAndCanIdCQL = session.prepare(FIND_BY_CPID_AND_ID_CQL)
    private val preparedCancelCQL = session.prepare(CANCEL_CQL)
    private val preparedUpdateStatusesCQL = session.prepare(UPDATE_STATUSES_CQL)
    private val preparedSaveNewCQL = session.prepare(SAVE_NEW_CQL)

    override fun findBy(cpid: Cpid, id: AwardContractId): Result<AwardContractEntity?, Fail.Incident.Database> =
        preparedFindByCpidAndCanIdCQL.bind()
            .apply {
                setString(Database.AC.COLUMN_CPID, cpid.underlying)
                setString(Database.AC.COLUMN_ID, id.underlying)
            }
            .tryExecute(session)
            .mapFailure {
                Fail.Incident.Database.DatabaseInteractionIncident(
                    ReadEntityException(message = "Error read Contract(s) from the database.", cause = it.exception)
                )
            }
            .onFailure { return it }
            .one()
            ?.convert()
            .asSuccess()

    private fun Row.convert(): AwardContractEntity = AwardContractEntity(
        cpid = Cpid.orNull(getString(Database.AC.COLUMN_CPID))!!,
        id = AwardContractId.orNull(getString(Database.AC.COLUMN_ID))!!,
        token = Token.orNull(getUUID(Database.AC.COLUMN_TOKEN).toString())!!,
        owner = Owner.orNull(getString(Database.AC.COLUMN_OWNER))!!,
        createdDate = getTimestamp(Database.AC.COLUMN_CREATED_DATE).toLocalDateTime(),
        status = AwardContractStatus.creator(getString(Database.AC.COLUMN_STATUS)),
        statusDetails = AwardContractStatusDetails.creator(getString(Database.AC.COLUMN_STATUS_DETAILS)),
        mainProcurementCategory = MainProcurementCategory.creator(getString(Database.AC.COLUMN_MPC)),
        language = getString(Database.AC.COLUMN_LANGUAGE),
        jsonData = getString(Database.AC.COLUMN_JSON_DATA)
    )

    override fun saveNew(entity: AwardContractEntity): Result<Boolean, Fail.Incident.Database> =
        preparedSaveNewCQL.bind()
            .apply {
                setString(Database.AC.COLUMN_CPID, entity.cpid.underlying)
                setString(Database.AC.COLUMN_ID, entity.id.underlying)
                setUUID(Database.AC.COLUMN_TOKEN, entity.token.underlying)
                setString(Database.AC.COLUMN_OWNER, entity.owner.underlying)
                setTimestamp(Database.AC.COLUMN_CREATED_DATE, entity.createdDate.toCassandraTimestamp())
                setString(Database.AC.COLUMN_STATUS, entity.status.key)
                setString(Database.AC.COLUMN_STATUS_DETAILS, entity.statusDetails.key)
                setString(Database.AC.COLUMN_MPC, entity.mainProcurementCategory.key)
                setString(Database.AC.COLUMN_LANGUAGE, entity.language)
                setString(Database.AC.COLUMN_JSON_DATA, entity.jsonData)
            }
            .tryExecute(session)
            .mapFailure {
                Fail.Incident.Database.DatabaseInteractionIncident(
                    SaveEntityException(message = "Error writing new contract to database.", cause = it.exception)
                )
            }
            .onFailure { return it }
            .wasApplied()
            .asSuccess()

    override fun saveCancelledAC(
        cpid: Cpid,
        id: AwardContractId,
        status: AwardContractStatus,
        statusDetails: AwardContractStatusDetails,
        jsonData: String
    ): Result<Boolean, Fail.Incident.Database> =
        preparedCancelCQL.bind()
            .apply {
                setString(Database.AC.COLUMN_CPID, cpid.underlying)
                setString(Database.AC.COLUMN_ID, id.underlying)
                setString(Database.AC.COLUMN_STATUS, status.key)
                setString(Database.AC.COLUMN_STATUS_DETAILS, statusDetails.key)
                setString(Database.AC.COLUMN_JSON_DATA, jsonData)
            }
            .tryExecute(session)
            .mapFailure {
                Fail.Incident.Database.DatabaseInteractionIncident(
                    SaveEntityException(message = "Error writing cancelled contract.", cause = it.exception)
                )
            }
            .onFailure { return it }
            .wasApplied()
            .asSuccess()

    override fun updateStatusesAC(
        cpid: Cpid,
        id: AwardContractId,
        status: AwardContractStatus,
        statusDetails: AwardContractStatusDetails,
        jsonData: String
    ): Result<Boolean, Fail.Incident.Database> = preparedUpdateStatusesCQL.bind()
        .apply {
            setString(Database.AC.COLUMN_CPID, cpid.underlying)
            setString(Database.AC.COLUMN_ID, id.underlying)
            setString(Database.AC.COLUMN_STATUS, status.key)
            setString(Database.AC.COLUMN_STATUS_DETAILS, statusDetails.key)
            setString(Database.AC.COLUMN_JSON_DATA, jsonData)
        }
        .tryExecute(session)
        .mapFailure {
            Fail.Incident.Database.DatabaseInteractionIncident(
                SaveEntityException(message = "Error writing updated contract.", cause = it.exception)
            )
        }
        .onFailure { return it }
        .wasApplied()
        .asSuccess()
}
