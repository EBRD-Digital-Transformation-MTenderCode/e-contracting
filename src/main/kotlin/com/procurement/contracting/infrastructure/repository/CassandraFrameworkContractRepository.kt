package com.procurement.contracting.infrastructure.repository

import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.procurement.contracting.application.exception.repository.ReadEntityException
import com.procurement.contracting.application.exception.repository.SaveEntityException
import com.procurement.contracting.application.repository.fc.FrameworkContractRepository
import com.procurement.contracting.application.repository.fc.model.FrameworkContractEntity
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatus
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatusDetails
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.extension.cassandra.toCassandraTimestamp
import com.procurement.contracting.infrastructure.extension.cassandra.toLocalDateTime
import com.procurement.contracting.infrastructure.extension.cassandra.tryExecute
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class CassandraFrameworkContractRepository(@Qualifier("contracting") private val session: Session) : FrameworkContractRepository {

    companion object {
        private const val ID_VALUES = "id_values"

        private const val FIND_BY_CPID_AND_OCID_CQL = """
               SELECT ${Database.FC.COLUMN_CPID},
                      ${Database.FC.COLUMN_OCID},
                      ${Database.FC.COLUMN_ID},
                      ${Database.FC.COLUMN_TOKEN},
                      ${Database.FC.COLUMN_OWNER},
                      ${Database.FC.COLUMN_CREATED_DATE},
                      ${Database.FC.COLUMN_STATUS},
                      ${Database.FC.COLUMN_STATUS_DETAILS},
                      ${Database.FC.COLUMN_JSON_DATA}
                 FROM ${Database.KEYSPACE_CONTRACTING}.${Database.FC.TABLE}
                WHERE ${Database.FC.COLUMN_CPID}=?
                  AND ${Database.FC.COLUMN_OCID}=?
            """

        private const val FIND_BY_CPID_AND_OCID_AND_ID_CQL = """
               SELECT ${Database.FC.COLUMN_CPID},
                      ${Database.FC.COLUMN_OCID},
                      ${Database.FC.COLUMN_ID},
                      ${Database.FC.COLUMN_TOKEN},
                      ${Database.FC.COLUMN_OWNER},
                      ${Database.FC.COLUMN_CREATED_DATE},
                      ${Database.FC.COLUMN_STATUS},
                      ${Database.FC.COLUMN_STATUS_DETAILS},
                      ${Database.FC.COLUMN_JSON_DATA}
                 FROM ${Database.KEYSPACE_CONTRACTING}.${Database.FC.TABLE}
                WHERE ${Database.FC.COLUMN_CPID}=?
                  AND ${Database.FC.COLUMN_OCID}=?
                  AND ${Database.FC.COLUMN_ID}=?
            """

        private const val FIND_BY_CPID_AND_OCID_AND_IDS_CQL = """
               SELECT ${Database.FC.COLUMN_CPID},
                      ${Database.FC.COLUMN_OCID},
                      ${Database.FC.COLUMN_ID},
                      ${Database.FC.COLUMN_TOKEN},
                      ${Database.FC.COLUMN_OWNER},
                      ${Database.FC.COLUMN_CREATED_DATE},
                      ${Database.FC.COLUMN_STATUS},
                      ${Database.FC.COLUMN_STATUS_DETAILS},
                      ${Database.FC.COLUMN_JSON_DATA}
                 FROM ${Database.KEYSPACE_CONTRACTING}.${Database.FC.TABLE}
                WHERE ${Database.FC.COLUMN_CPID}=?
                  AND ${Database.FC.COLUMN_OCID}=?
                  AND ${Database.FC.COLUMN_ID} in :$ID_VALUES;
            """

        private const val SAVE_NEW_CQL = """
               INSERT INTO ${Database.KEYSPACE_CONTRACTING}.${Database.FC.TABLE}(
                      ${Database.FC.COLUMN_CPID},
                      ${Database.FC.COLUMN_OCID},
                      ${Database.FC.COLUMN_ID},
                      ${Database.FC.COLUMN_TOKEN},
                      ${Database.FC.COLUMN_OWNER},
                      ${Database.FC.COLUMN_CREATED_DATE},
                      ${Database.FC.COLUMN_STATUS},
                      ${Database.FC.COLUMN_STATUS_DETAILS},
                      ${Database.FC.COLUMN_JSON_DATA}
               )
               VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)
               IF NOT EXISTS
            """

        private const val UPDATE_STATUSES_CQL = """
               UPDATE ${Database.KEYSPACE_CONTRACTING}.${Database.FC.TABLE}
                  SET ${Database.FC.COLUMN_STATUS}=?, 
                      ${Database.FC.COLUMN_STATUS_DETAILS}=?,
                      ${Database.FC.COLUMN_JSON_DATA}=?
                WHERE ${Database.FC.COLUMN_CPID}=?
                  AND ${Database.FC.COLUMN_OCID}=?
                  AND ${Database.FC.COLUMN_ID}=?
               IF EXISTS
            """
    }

    private val preparedFindByCpidAndOcidCQL = session.prepare(FIND_BY_CPID_AND_OCID_CQL)
    private val preparedFindByCpidAndOcidAndIdCQL = session.prepare(FIND_BY_CPID_AND_OCID_AND_ID_CQL)
    private val preparedFindByCpidAndOcidAndIdsCQL = session.prepare(FIND_BY_CPID_AND_OCID_AND_IDS_CQL)
    private val preparedSaveNewCQL = session.prepare(SAVE_NEW_CQL)
    private val preparedUpdateCQL = session.prepare(UPDATE_STATUSES_CQL)

    override fun findBy(cpid: Cpid, ocid: Ocid): Result<List<FrameworkContractEntity>, Fail.Incident.Database> =
        preparedFindByCpidAndOcidCQL.bind()
            .apply {
                setString(Database.FC.COLUMN_CPID, cpid.underlying)
                setString(Database.FC.COLUMN_OCID, ocid.underlying)
            }
            .tryExecute(session)
            .mapFailure {
                Fail.Incident.Database.DatabaseInteractionIncident(
                    ReadEntityException(message = "Error read FC Contract(s) from the database.", cause = it.exception)
                )
            }
            .onFailure { return it }
            .map { it.convert() }
            .asSuccess()

    override fun findBy(
        cpid: Cpid,
        ocid: Ocid,
        contractId: FrameworkContractId
    ): Result<FrameworkContractEntity?, Fail.Incident.Database> =
        preparedFindByCpidAndOcidAndIdCQL.bind()
            .apply {
                setString(Database.FC.COLUMN_CPID, cpid.underlying)
                setString(Database.FC.COLUMN_OCID, ocid.underlying)
                setString(Database.FC.COLUMN_ID, contractId.underlying)
            }
            .tryExecute(session)
            .mapFailure {
                Fail.Incident.Database.DatabaseInteractionIncident(
                    ReadEntityException(message = "Error read FC Contract(s) from the database.", cause = it.exception)
                )
            }
            .onFailure { return it }
            .one()
            ?.convert()
            .asSuccess()

    override fun findBy(cpid: Cpid, ocid: Ocid, contractIds: List<FrameworkContractId>): Result<List<FrameworkContractEntity>, Fail.Incident.Database> =
        preparedFindByCpidAndOcidAndIdsCQL.bind()
            .apply {
                setString(Database.FC.COLUMN_CPID, cpid.underlying)
                setString(Database.FC.COLUMN_OCID, ocid.underlying)
                setList(ID_VALUES, contractIds.map { it.underlying })
            }
            .tryExecute(session)
            .onFailure { return it }
            .map { it.convert() }
            .asSuccess()

    private fun Row.convert(): FrameworkContractEntity = FrameworkContractEntity(
        cpid = Cpid.orNull(getString(Database.FC.COLUMN_CPID))!!,
        ocid = Ocid.orNull(getString(Database.FC.COLUMN_OCID))!!,
        id = FrameworkContractId.orNull(getString(Database.FC.COLUMN_ID))!!,
        token = Token.orNull(getString(Database.FC.COLUMN_TOKEN))!!,
        owner = Owner.orNull(getString(Database.FC.COLUMN_OWNER))!!,
        createdDate = getTimestamp(Database.FC.COLUMN_CREATED_DATE).toLocalDateTime(),
        status = FrameworkContractStatus.creator(getString(Database.FC.COLUMN_STATUS)),
        statusDetails = FrameworkContractStatusDetails.creator(getString(Database.FC.COLUMN_STATUS_DETAILS)),
        jsonData = getString(Database.FC.COLUMN_JSON_DATA)
    )

    override fun saveNew(entity: FrameworkContractEntity): Result<Boolean, Fail.Incident.Database> =
        preparedSaveNewCQL.bind()
            .apply {
                setString(Database.FC.COLUMN_CPID, entity.cpid.underlying)
                setString(Database.FC.COLUMN_OCID, entity.ocid.underlying)
                setString(Database.FC.COLUMN_ID, entity.id.underlying)
                setString(Database.FC.COLUMN_TOKEN, entity.token.underlying.toString())
                setString(Database.FC.COLUMN_OWNER, entity.owner.underlying)
                setTimestamp(Database.FC.COLUMN_CREATED_DATE, entity.createdDate.toCassandraTimestamp())
                setString(Database.FC.COLUMN_STATUS, entity.status.key)
                setString(Database.FC.COLUMN_STATUS_DETAILS, entity.statusDetails.key)
                setString(Database.FC.COLUMN_JSON_DATA, entity.jsonData)
            }
            .tryExecute(session)
            .mapFailure {
                Fail.Incident.Database.DatabaseInteractionIncident(
                    SaveEntityException(message = "Error writing new FC contract to database.", cause = it.exception)
                )
            }
            .onFailure { return it }
            .wasApplied()
            .asSuccess()

    override fun update(entity: FrameworkContractEntity): Result<Boolean, Fail.Incident.Database> =
        preparedUpdateCQL.bind()
            .apply {
                setString(Database.FC.COLUMN_CPID, entity.cpid.underlying)
                setString(Database.FC.COLUMN_OCID, entity.ocid.underlying)
                setString(Database.FC.COLUMN_ID, entity.id.underlying)
                setString(Database.FC.COLUMN_STATUS, entity.status.key)
                setString(Database.FC.COLUMN_STATUS_DETAILS, entity.statusDetails.key)
                setString(Database.FC.COLUMN_JSON_DATA, entity.jsonData)
            }
            .tryExecute(session)
            .mapFailure {
                Fail.Incident.Database.DatabaseInteractionIncident(
                    ReadEntityException(message = "Error write FC Contract(s) to the database.", cause = it.exception)
                )
            }
            .onFailure { return it }
            .wasApplied()
            .asSuccess()
}
