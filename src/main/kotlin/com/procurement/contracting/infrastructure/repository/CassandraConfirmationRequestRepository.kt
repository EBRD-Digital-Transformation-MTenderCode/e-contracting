package com.procurement.contracting.infrastructure.repository

import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.procurement.contracting.application.exception.repository.ReadEntityException
import com.procurement.contracting.application.exception.repository.SaveEntityException
import com.procurement.contracting.application.repository.confirmation.ConfirmationRequestEntity
import com.procurement.contracting.application.repository.confirmation.ConfirmationRequestRepository
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.extension.cassandra.tryExecute
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess
import org.springframework.stereotype.Repository

@Repository
class CassandraConfirmationRequestRepository(private val session: Session) : ConfirmationRequestRepository {

    companion object {

        private const val SAVE_CQL = """
               INSERT INTO ${Database.KEYSPACE}.${Database.ConfirmationRequest.TABLE}(
                      ${Database.ConfirmationRequest.COLUMN_CPID},
                      ${Database.ConfirmationRequest.COLUMN_OCID},
                      ${Database.ConfirmationRequest.COLUMN_CONTRACT_ID},
                      ${Database.ConfirmationRequest.COLUMN_ID},
                      ${Database.ConfirmationRequest.COLUMN_GROUPS},
                      ${Database.ConfirmationRequest.COLUMN_JSON_DATA}
               )
               VALUES(?, ?, ?, ?, ?, ?)
               IF NOT EXISTS
            """

        private const val FIND_BY_CPID_AND_OCID_CQL = """
               SELECT ${Database.ConfirmationRequest.COLUMN_CPID},
                      ${Database.ConfirmationRequest.COLUMN_OCID},
                      ${Database.ConfirmationRequest.COLUMN_CONTRACT_ID},
                      ${Database.ConfirmationRequest.COLUMN_ID},
                      ${Database.ConfirmationRequest.COLUMN_GROUPS},
                      ${Database.ConfirmationRequest.COLUMN_JSON_DATA}
                 FROM ${Database.KEYSPACE}.${Database.ConfirmationRequest.TABLE}
                WHERE ${Database.ConfirmationRequest.COLUMN_CPID}=?
                  AND ${Database.ConfirmationRequest.COLUMN_OCID}=?
            """
    }

    private val preparedSaveNewCQL = session.prepare(SAVE_CQL)
    private val preparedFindByCpidAndOcidCQL = session.prepare(FIND_BY_CPID_AND_OCID_CQL)

    override fun save(entity: ConfirmationRequestEntity): Result<Boolean, Fail.Incident.Database> =
        preparedSaveNewCQL.bind()
            .apply {
                setString(Database.ConfirmationRequest.COLUMN_CPID, entity.cpid.underlying)
                setString(Database.ConfirmationRequest.COLUMN_OCID, entity.ocid.underlying)
                setString(Database.ConfirmationRequest.COLUMN_ID, entity.id.underlying.toString())
                setString(Database.ConfirmationRequest.COLUMN_CONTRACT_ID, entity.contractId)
                setSet(Database.ConfirmationRequest.COLUMN_GROUPS, entity.groups)
                setString(Database.ConfirmationRequest.COLUMN_JSON_DATA, entity.jsonData)
            }
            .tryExecute(session)
            .mapFailure {
                Fail.Incident.Database.DatabaseInteractionIncident(
                    SaveEntityException(message = "Error writing new Confirmation Request to database.", cause = it.exception)
                )
            }
            .onFailure { return it }
            .wasApplied()
            .asSuccess()

    override fun findBy(cpid: Cpid, ocid: Ocid): Result<List<ConfirmationRequestEntity>, Fail.Incident.Database> =
        preparedFindByCpidAndOcidCQL.bind()
            .apply {
                setString(Database.ConfirmationRequest.COLUMN_CPID, cpid.underlying)
                setString(Database.ConfirmationRequest.COLUMN_OCID, ocid.underlying)
            }
            .tryExecute(session)
            .mapFailure {
                Fail.Incident.Database.DatabaseInteractionIncident(
                    ReadEntityException(message = "Error read Confirmation Request(s) from the database.", cause = it.exception)
                )
            }
            .onFailure { return it }
            .map { it.convert() }
            .asSuccess()

    private fun Row.convert(): ConfirmationRequestEntity = ConfirmationRequestEntity(
        cpid = Cpid.orNull(getString(Database.ConfirmationRequest.COLUMN_CPID))!!,
        ocid = Ocid.orNull(getString(Database.ConfirmationRequest.COLUMN_OCID))!!,
        contractId = getString(Database.ConfirmationRequest.COLUMN_CONTRACT_ID),
        id = ConfirmationRequestId.orNull(getString(Database.ConfirmationRequest.COLUMN_ID))!!,
        groups = getSet(Database.ConfirmationRequest.COLUMN_GROUPS, String::class.java),
        jsonData = getString(Database.ConfirmationRequest.COLUMN_JSON_DATA)
    )
}
