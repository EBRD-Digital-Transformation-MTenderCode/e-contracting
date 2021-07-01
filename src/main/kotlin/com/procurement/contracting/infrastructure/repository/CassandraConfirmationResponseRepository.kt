package com.procurement.contracting.infrastructure.repository

import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.procurement.contracting.application.repository.confirmation.ConfirmationResponseEntity
import com.procurement.contracting.application.repository.confirmation.ConfirmationResponseRepository
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.extension.cassandra.tryExecute
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class CassandraConfirmationResponseRepository(@Qualifier("contracting") private val session: Session) : ConfirmationResponseRepository {

    companion object {

        private const val SAVE_CQL = """
               INSERT INTO ${Database.KEYSPACE_CONTRACTING}.${Database.ConfirmationResponse.TABLE}(
                      ${Database.ConfirmationResponse.COLUMN_CPID},
                      ${Database.ConfirmationResponse.COLUMN_OCID},
                      ${Database.ConfirmationResponse.COLUMN_CONTRACT_ID},
                      ${Database.ConfirmationResponse.COLUMN_ID},
                      ${Database.ConfirmationResponse.COLUMN_REQUEST_ID},
                      ${Database.ConfirmationResponse.COLUMN_JSON_DATA}
               )
               VALUES(?, ?, ?, ?, ?, ?)
               IF NOT EXISTS
            """

        private const val FIND_BY_CPID_AND_OCID_CQL = """
               SELECT ${Database.ConfirmationResponse.COLUMN_CPID},
                      ${Database.ConfirmationResponse.COLUMN_OCID},
                      ${Database.ConfirmationResponse.COLUMN_CONTRACT_ID},
                      ${Database.ConfirmationResponse.COLUMN_ID},
                      ${Database.ConfirmationResponse.COLUMN_REQUEST_ID},
                      ${Database.ConfirmationResponse.COLUMN_JSON_DATA}
                 FROM ${Database.KEYSPACE_CONTRACTING}.${Database.ConfirmationResponse.TABLE}
                WHERE ${Database.ConfirmationResponse.COLUMN_CPID}=?
                  AND ${Database.ConfirmationResponse.COLUMN_OCID}=?
            """

        private const val FIND_BY_CPID_AND_OCID_AND_CONTRACT_ID_CQL = """
               SELECT ${Database.ConfirmationResponse.COLUMN_CPID},
                      ${Database.ConfirmationResponse.COLUMN_OCID},
                      ${Database.ConfirmationResponse.COLUMN_CONTRACT_ID},
                      ${Database.ConfirmationResponse.COLUMN_ID},
                      ${Database.ConfirmationResponse.COLUMN_REQUEST_ID},
                      ${Database.ConfirmationResponse.COLUMN_JSON_DATA}
                 FROM ${Database.KEYSPACE_CONTRACTING}.${Database.ConfirmationResponse.TABLE}
                WHERE ${Database.ConfirmationResponse.COLUMN_CPID}=?
                  AND ${Database.ConfirmationResponse.COLUMN_OCID}=?
                  AND ${Database.ConfirmationResponse.COLUMN_CONTRACT_ID}=?
            """
    }

    private val preparedSaveNewCQL = session.prepare(SAVE_CQL)
    private val preparedFindByCpidAndOcidCQL = session.prepare(FIND_BY_CPID_AND_OCID_CQL)
    private val preparedFindByCpidAndOcidAndContractIdCQL = session.prepare(FIND_BY_CPID_AND_OCID_AND_CONTRACT_ID_CQL)

    override fun save(entity: ConfirmationResponseEntity): Result<Boolean, Fail.Incident.Database> =
        preparedSaveNewCQL.bind()
            .apply {
                setString(Database.ConfirmationResponse.COLUMN_CPID, entity.cpid.underlying)
                setString(Database.ConfirmationResponse.COLUMN_OCID, entity.ocid.underlying)
                setString(Database.ConfirmationResponse.COLUMN_ID, entity.id)
                setString(Database.ConfirmationResponse.COLUMN_CONTRACT_ID, entity.contractId)
                setString(Database.ConfirmationResponse.COLUMN_REQUEST_ID, entity.requestId.underlying.toString())
                setString(Database.ConfirmationResponse.COLUMN_JSON_DATA, entity.jsonData)
            }
            .tryExecute(session)
            .onFailure { return it }
            .wasApplied()
            .asSuccess()

    override fun findBy(cpid: Cpid, ocid: Ocid): Result<List<ConfirmationResponseEntity>, Fail.Incident.Database> =
        preparedFindByCpidAndOcidCQL.bind()
            .apply {
                setString(Database.ConfirmationResponse.COLUMN_CPID, cpid.underlying)
                setString(Database.ConfirmationResponse.COLUMN_OCID, ocid.underlying)
            }
            .tryExecute(session)
            .onFailure { return it }
            .map { it.convert() }
            .asSuccess()

    override fun findBy(cpid: Cpid, ocid: Ocid, contractId: String): Result<List<ConfirmationResponseEntity>, Fail.Incident.Database> =
        preparedFindByCpidAndOcidAndContractIdCQL.bind()
            .apply {
                setString(Database.ConfirmationResponse.COLUMN_CPID, cpid.underlying)
                setString(Database.ConfirmationResponse.COLUMN_OCID, ocid.underlying)
                setString(Database.ConfirmationResponse.COLUMN_CONTRACT_ID, contractId)
            }
            .tryExecute(session)
            .onFailure { return it }
            .map { it.convert() }
            .asSuccess()

    private fun Row.convert(): ConfirmationResponseEntity = ConfirmationResponseEntity(
        cpid = Cpid.orNull(getString(Database.ConfirmationResponse.COLUMN_CPID))!!,
        ocid = Ocid.orNull(getString(Database.ConfirmationResponse.COLUMN_OCID))!!,
        contractId = getString(Database.ConfirmationResponse.COLUMN_CONTRACT_ID),
        id = getString(Database.ConfirmationResponse.COLUMN_ID),
        requestId = ConfirmationRequestId.orNull(getString(Database.ConfirmationResponse.COLUMN_REQUEST_ID))!!,
        jsonData = getString(Database.ConfirmationResponse.COLUMN_JSON_DATA)
    )
}
