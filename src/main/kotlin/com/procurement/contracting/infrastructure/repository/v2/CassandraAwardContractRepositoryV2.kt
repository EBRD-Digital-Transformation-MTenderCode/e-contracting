package com.procurement.contracting.infrastructure.repository.v2

import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.procurement.contracting.application.exception.repository.ReadEntityException
import com.procurement.contracting.application.exception.repository.SaveEntityException
import com.procurement.contracting.application.repository.v2.AwardContractEntity
import com.procurement.contracting.application.repository.v2.AwardContractRepository
import com.procurement.contracting.application.service.Transform
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.ac.id.AwardContractId
import com.procurement.contracting.domain.model.ac.status.AwardContractStatus
import com.procurement.contracting.domain.model.ac.status.AwardContractStatusDetails
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.extension.cassandra.toCassandraTimestamp
import com.procurement.contracting.infrastructure.extension.cassandra.toLocalDateTime
import com.procurement.contracting.infrastructure.extension.cassandra.tryExecute
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.repository.Database
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess
import com.procurement.contracting.model.dto.ocds.v2.AwardContract
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class CassandraAwardContractRepositoryV2(@Qualifier("contracting") private val session: Session, private val transform: Transform) :
    AwardContractRepository {

    companion object {
        private const val FIND_BY_CPID_OCID_AND_ID_CQL = """
               SELECT ${Database.AC_V2.COLUMN_CPID},
                      ${Database.AC_V2.COLUMN_OCID},
                      ${Database.AC_V2.COLUMN_TOKEN},
                      ${Database.AC_V2.COLUMN_OWNER},
                      ${Database.AC_V2.COLUMN_CREATED_DATE},
                      ${Database.AC_V2.COLUMN_STATUS},
                      ${Database.AC_V2.COLUMN_STATUS_DETAILS},
                      ${Database.AC_V2.COLUMN_JSON_DATA}
                 FROM ${Database.KEYSPACE_CONTRACTING}.${Database.AC_V2.TABLE}
                WHERE ${Database.AC_V2.COLUMN_CPID}=?
                  AND ${Database.AC_V2.COLUMN_OCID}=?
            """

        private const val SAVE_NEW_CQL = """
               INSERT INTO ${Database.KEYSPACE_CONTRACTING}.${Database.AC_V2.TABLE}(
                      ${Database.AC_V2.COLUMN_CPID},
                      ${Database.AC_V2.COLUMN_OCID},                      
                      ${Database.AC_V2.COLUMN_TOKEN},
                      ${Database.AC_V2.COLUMN_OWNER},
                      ${Database.AC_V2.COLUMN_CREATED_DATE},
                      ${Database.AC_V2.COLUMN_STATUS},
                      ${Database.AC_V2.COLUMN_STATUS_DETAILS},
                      ${Database.AC_V2.COLUMN_JSON_DATA}
               )
               VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)
               IF NOT EXISTS
            """
    }

    private val preparedFindByCpidOCidAndIdCQL = session.prepare(FIND_BY_CPID_OCID_AND_ID_CQL)
    private val preparedSaveNewCQL = session.prepare(SAVE_NEW_CQL)

    override fun findBy(
        cpid: Cpid,
        ocid: Ocid,
        id: AwardContractId
    ): Result<AwardContractEntity?, Fail.Incident.Database> =
        preparedFindByCpidOCidAndIdCQL.bind()
            .apply {
                setString(Database.AC_V2.COLUMN_CPID, cpid.underlying)
                setString(Database.AC_V2.COLUMN_OCID, ocid.underlying)
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
            ?.onFailure { return it }
            .asSuccess()

    private fun Row.convert(): Result<AwardContractEntity, Fail.Incident.Database> = AwardContractEntity(
        cpid = Cpid.orNull(getString(Database.AC_V2.COLUMN_CPID))!!,
        ocid = Ocid.orNull(getString(Database.AC_V2.COLUMN_OCID))!!,
        token = Token.orNull(getUUID(Database.AC_V2.COLUMN_TOKEN).toString())!!,
        owner = Owner.orNull(getString(Database.AC_V2.COLUMN_OWNER))!!,
        createdDate = getTimestamp(Database.AC_V2.COLUMN_CREATED_DATE).toLocalDateTime(),
        status = AwardContractStatus.creator(getString(Database.AC_V2.COLUMN_STATUS)),
        statusDetails = AwardContractStatusDetails.creator(getString(Database.AC_V2.COLUMN_STATUS_DETAILS)),
        awardContract = transform.tryDeserialization(
            getString(Database.AC_V2.COLUMN_JSON_DATA),
            AwardContract::class.java
        )
            .onFailure { return Fail.Incident.Database.DatabaseInteractionIncident(it.reason.exception).asFailure() }
    ).asSuccess()

    override fun save(awardContract: AwardContract): Result<Boolean, Fail.Incident.Database> {
        val jsonData = transform.trySerialization(awardContract)
            .onFailure { error ->
                return Fail.Incident.Database.DatabaseInteractionIncident(error.reason.exception)
                    .asFailure()
            }

        val contract = awardContract.contracts.first()

        return preparedSaveNewCQL.bind()
            .apply {
                setString(Database.AC_V2.COLUMN_CPID, awardContract.cpid.underlying)
                setString(Database.AC_V2.COLUMN_OCID, awardContract.ocid.underlying)
                setUUID(Database.AC_V2.COLUMN_TOKEN, awardContract.token.underlying)
                setString(Database.AC_V2.COLUMN_OWNER, awardContract.owner.underlying)
                setTimestamp(Database.AC_V2.COLUMN_CREATED_DATE, contract.date.toCassandraTimestamp())
                setString(Database.AC_V2.COLUMN_STATUS, contract.status.key)
                setString(Database.AC_V2.COLUMN_STATUS_DETAILS, contract.statusDetails.key)
                setString(Database.AC_V2.COLUMN_JSON_DATA, jsonData)
            }
            .tryExecute(session)
            .mapFailure {
                Fail.Incident.Database.DatabaseInteractionIncident(
                    SaveEntityException(message = "Error writing new ac to database.", cause = it.exception)
                )
            }
            .onFailure { return it }
            .wasApplied()
            .asSuccess()
    }
}
