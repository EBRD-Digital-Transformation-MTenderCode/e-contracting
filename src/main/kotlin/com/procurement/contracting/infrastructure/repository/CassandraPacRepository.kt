package com.procurement.contracting.infrastructure.repository

import com.datastax.driver.core.BatchStatement
import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.procurement.contracting.application.exception.repository.ReadEntityException
import com.procurement.contracting.application.exception.repository.SaveEntityException
import com.procurement.contracting.application.repository.pac.PacRepository
import com.procurement.contracting.application.repository.pac.model.PacRecord
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.pac.PacStatus
import com.procurement.contracting.domain.model.pac.PacStatusDetails
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.extension.cassandra.toCassandraTimestamp
import com.procurement.contracting.infrastructure.extension.cassandra.toLocalDateTime
import com.procurement.contracting.infrastructure.extension.cassandra.tryExecute
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.MaybeFail
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class CassandraPacRepository(@Qualifier("ocds") private val session: Session) : PacRepository {

    companion object {
        private const val FIND_BY_CPID_AND_OCID_CQL = """
               SELECT ${Database.PAC.COLUMN_CPID},
                      ${Database.PAC.COLUMN_OCID},
                      ${Database.PAC.COLUMN_ID},
                      ${Database.PAC.COLUMN_OWNER},
                      ${Database.PAC.COLUMN_TOKEN},
                      ${Database.PAC.COLUMN_CREATED_DATE},
                      ${Database.PAC.COLUMN_STATUS},
                      ${Database.PAC.COLUMN_STATUS_DETAILS},
                      ${Database.PAC.COLUMN_JSON_DATA}
                 FROM ${Database.KEYSPACE}.${Database.PAC.TABLE}
                WHERE ${Database.PAC.COLUMN_CPID}=?
                  AND ${Database.PAC.COLUMN_OCID}=?
            """

        private const val FIND_BY_CPID_AND_OCID_AND_ID_CQL = """
               SELECT ${Database.PAC.COLUMN_CPID},
                      ${Database.PAC.COLUMN_OCID},
                      ${Database.PAC.COLUMN_ID},
                      ${Database.PAC.COLUMN_OWNER},
                       ${Database.PAC.COLUMN_TOKEN},
                      ${Database.PAC.COLUMN_CREATED_DATE},
                      ${Database.PAC.COLUMN_STATUS},
                      ${Database.PAC.COLUMN_STATUS_DETAILS},
                      ${Database.PAC.COLUMN_JSON_DATA}
                 FROM ${Database.KEYSPACE}.${Database.PAC.TABLE}
                WHERE ${Database.PAC.COLUMN_CPID}=?
                  AND ${Database.PAC.COLUMN_OCID}=?
                  AND ${Database.PAC.COLUMN_ID}=?
            """

        private const val SAVE_NEW_CQL = """
               INSERT INTO ${Database.KEYSPACE}.${Database.PAC.TABLE}(
                      ${Database.PAC.COLUMN_CPID},
                      ${Database.PAC.COLUMN_OCID},
                      ${Database.PAC.COLUMN_ID},
                      ${Database.PAC.COLUMN_OWNER},
                      ${Database.PAC.COLUMN_TOKEN},
                      ${Database.PAC.COLUMN_CREATED_DATE},
                      ${Database.PAC.COLUMN_STATUS},
                      ${Database.PAC.COLUMN_STATUS_DETAILS},
                      ${Database.PAC.COLUMN_JSON_DATA}
               )
               VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)
               IF NOT EXISTS
            """

        private const val UPDATE_STATUSES_CQL = """
               UPDATE ${Database.KEYSPACE}.${Database.PAC.TABLE}
                  SET ${Database.PAC.COLUMN_STATUS}=?, 
                      ${Database.PAC.COLUMN_STATUS_DETAILS}=?,
                      ${Database.PAC.COLUMN_JSON_DATA}=?
                WHERE ${Database.PAC.COLUMN_CPID}=?
                  AND ${Database.PAC.COLUMN_OCID}=?
                  AND ${Database.PAC.COLUMN_ID}=?
               IF EXISTS
            """
    }

    private val preparedFindByCpidAndOcidCQL = session.prepare(FIND_BY_CPID_AND_OCID_CQL)
    private val preparedFindByCpidAndOcidAndIdCQL = session.prepare(FIND_BY_CPID_AND_OCID_AND_ID_CQL)
    private val preparedSaveNewCQL = session.prepare(SAVE_NEW_CQL)
    private val preparedUpdateCQL = session.prepare(UPDATE_STATUSES_CQL)

    override fun findBy(cpid: Cpid, ocid: Ocid): Result<List<PacRecord>, Fail.Incident.Database> =
        preparedFindByCpidAndOcidCQL.bind()
            .apply {
                setString(Database.PAC.COLUMN_CPID, cpid.underlying)
                setString(Database.PAC.COLUMN_OCID, ocid.underlying)
            }
            .tryExecute(session)
            .mapFailure {
                Fail.Incident.Database.DatabaseInteractionIncident(
                    ReadEntityException(message = "Error read PAC Contract(s) from the database.", cause = it.exception)
                )
            }
            .onFailure { return it }
            .map { it.convert() }
            .asSuccess()

    override fun findBy(
        cpid: Cpid,
        ocid: Ocid,
        contractId: PacId
    ): Result<PacRecord?, Fail.Incident.Database> =
        preparedFindByCpidAndOcidAndIdCQL.bind()
            .apply {
                setString(Database.PAC.COLUMN_CPID, cpid.underlying)
                setString(Database.PAC.COLUMN_OCID, ocid.underlying)
                setString(Database.PAC.COLUMN_ID, contractId.underlying)
            }
            .tryExecute(session)
            .mapFailure {
                Fail.Incident.Database.DatabaseInteractionIncident(
                    ReadEntityException(message = "Error read PAC Contract(s) from the database.", cause = it.exception)
                )
            }
            .onFailure { return it }
            .one()
            ?.convert()
            .asSuccess()

    private fun Row.convert(): PacRecord {
        return PacRecord(
            cpid = Cpid.orNull(getString(Database.PAC.COLUMN_CPID))!!,
            ocid = Ocid.orNull(getString(Database.PAC.COLUMN_OCID))!!,
            id = PacId.orNull(getString(Database.PAC.COLUMN_ID))!!,
            owner = Owner.orNull(getString(Database.PAC.COLUMN_OWNER))!!,
            token = Token.orNull(getString(Database.PAC.COLUMN_TOKEN))!!,
            createdDate = getTimestamp(Database.PAC.COLUMN_CREATED_DATE).toLocalDateTime(),
            status = PacStatus.creator(getString(Database.PAC.COLUMN_STATUS)),
            statusDetails = getString(Database.PAC.COLUMN_STATUS_DETAILS)?.let { PacStatusDetails.creator(it) },
            jsonData = getString(Database.PAC.COLUMN_JSON_DATA)
        )
    }

    override fun saveNew(record: PacRecord): Result<Boolean, Fail.Incident.Database> =
        getSaveStatement(record)
            .tryExecute(session)
            .mapFailure {
                Fail.Incident.Database.DatabaseInteractionIncident(
                    SaveEntityException(message = "Error writing new PAC contract to database.", cause = it.exception)
                )
            }
            .onFailure { return it }
            .wasApplied()
            .asSuccess()

    override fun update(record: PacRecord): Result<Boolean, Fail.Incident.Database> =
        preparedUpdateCQL.bind()
            .apply {
                setString(Database.PAC.COLUMN_CPID, record.cpid.underlying)
                setString(Database.PAC.COLUMN_OCID, record.ocid.underlying)
                setString(Database.PAC.COLUMN_ID, record.id.underlying)
                setString(Database.PAC.COLUMN_STATUS, record.status.key)
                setString(Database.PAC.COLUMN_STATUS_DETAILS, record.statusDetails?.key)
                setString(Database.PAC.COLUMN_JSON_DATA, record.jsonData)
            }
            .tryExecute(session)
            .mapFailure {
                Fail.Incident.Database.DatabaseInteractionIncident(
                    ReadEntityException(message = "Error write PAC Contract(s) to the database.", cause = it.exception)
                )
            }
            .onFailure { return it }
            .wasApplied()
            .asSuccess()

    override fun save(records: Collection<PacRecord>): MaybeFail<Fail.Incident.Database> {
        val batchStatement = BatchStatement()

        records.forEach { entity ->
           val statement = getSaveStatement(entity)
            batchStatement.add(statement)
        }

        batchStatement.tryExecute(session)
            .onFailure { return MaybeFail.fail(it.reason) }

        return MaybeFail.none()
    }

    private fun getSaveStatement(record: PacRecord): BoundStatement {
        return preparedSaveNewCQL.bind()
            .apply {
                setString(Database.PAC.COLUMN_CPID, record.cpid.underlying)
                setString(Database.PAC.COLUMN_OCID, record.ocid.underlying)
                setString(Database.PAC.COLUMN_ID, record.id.underlying)
                setString(Database.PAC.COLUMN_OWNER, record.owner.underlying)
                setString(Database.PAC.COLUMN_TOKEN, record.token.underlying.toString())
                setTimestamp(Database.PAC.COLUMN_CREATED_DATE, record.createdDate.toCassandraTimestamp())
                setString(Database.PAC.COLUMN_STATUS, record.status.key)
                setString(Database.PAC.COLUMN_STATUS_DETAILS, record.statusDetails?.key)
                setString(Database.PAC.COLUMN_JSON_DATA, record.jsonData)
            }
    }
}
