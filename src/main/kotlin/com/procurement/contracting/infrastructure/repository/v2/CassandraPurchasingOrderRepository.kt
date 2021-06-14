package com.procurement.contracting.infrastructure.repository.v2

import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.procurement.contracting.application.exception.repository.ReadEntityException
import com.procurement.contracting.application.exception.repository.SaveEntityException
import com.procurement.contracting.application.repository.v2.PurchasingOrderEntity
import com.procurement.contracting.application.repository.v2.PurchasingOrderRepository
import com.procurement.contracting.application.service.Transform
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.po.PurchasingOrderId
import com.procurement.contracting.domain.model.po.status.PurchasingOrderStatus
import com.procurement.contracting.domain.model.po.status.PurchasingOrderStatusDetails
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
import com.procurement.contracting.model.dto.ocds.v2.PurchasingOrder
import org.springframework.stereotype.Repository

@Repository
class CassandraPurchasingOrderRepository(private val session: Session, private val transform: Transform) :
    PurchasingOrderRepository {

    companion object {
        private const val FIND_BY_CPID_OCID_AND_ID_CQL = """
               SELECT ${Database.PO.COLUMN_CPID},
                      ${Database.PO.COLUMN_OCID},
                      ${Database.PO.COLUMN_TOKEN},
                      ${Database.PO.COLUMN_OWNER},
                      ${Database.PO.COLUMN_CREATED_DATE},
                      ${Database.PO.COLUMN_STATUS},
                      ${Database.PO.COLUMN_STATUS_DETAILS},
                      ${Database.PO.COLUMN_JSON_DATA}
                 FROM ${Database.KEYSPACE_CONTRACTING}.${Database.PO.TABLE}
                WHERE ${Database.PO.COLUMN_CPID}=?
                  AND ${Database.PO.COLUMN_OCID}=?
            """

        private const val SAVE_NEW_CQL = """
               INSERT INTO ${Database.KEYSPACE_CONTRACTING}.${Database.PO.TABLE}(
                      ${Database.PO.COLUMN_CPID},
                      ${Database.PO.COLUMN_OCID},                      
                      ${Database.PO.COLUMN_TOKEN},
                      ${Database.PO.COLUMN_OWNER},
                      ${Database.PO.COLUMN_CREATED_DATE},
                      ${Database.PO.COLUMN_STATUS},
                      ${Database.PO.COLUMN_STATUS_DETAILS},
                      ${Database.PO.COLUMN_JSON_DATA}
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
        id: PurchasingOrderId
    ): Result<PurchasingOrderEntity?, Fail.Incident.Database> =
        preparedFindByCpidOCidAndIdCQL.bind()
            .apply {
                setString(Database.PO.COLUMN_CPID, cpid.underlying)
                setString(Database.PO.COLUMN_OCID, ocid.underlying)
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

    private fun Row.convert(): Result<PurchasingOrderEntity, Fail.Incident.Database> = PurchasingOrderEntity(
        cpid = Cpid.orNull(getString(Database.PO.COLUMN_CPID))!!,
        ocid = Ocid.orNull(getString(Database.PO.COLUMN_OCID))!!,
        token = Token.orNull(getUUID(Database.PO.COLUMN_TOKEN).toString())!!,
        owner = Owner.orNull(getString(Database.PO.COLUMN_OWNER))!!,
        createdDate = getTimestamp(Database.PO.COLUMN_CREATED_DATE).toLocalDateTime(),
        status = PurchasingOrderStatus.creator(getString(Database.PO.COLUMN_STATUS)),
        statusDetails = PurchasingOrderStatusDetails.creator(getString(Database.PO.COLUMN_STATUS_DETAILS)),
        purchasingOrder = transform.tryDeserialization(
            getString(Database.PO.COLUMN_JSON_DATA),
            PurchasingOrder::class.java
        )
            .onFailure { return Fail.Incident.Database.DatabaseInteractionIncident(it.reason.exception).asFailure() }
    ).asSuccess()

    override fun save(purchasingOrder: PurchasingOrder): Result<Boolean, Fail.Incident.Database> {
        val jsonData = transform.trySerialization(purchasingOrder)
            .onFailure { error ->
                return Fail.Incident.Database.DatabaseInteractionIncident(error.reason.exception)
                    .asFailure()
            }

        val contract = purchasingOrder.contracts.first()

        return preparedSaveNewCQL.bind()
            .apply {
                setString(Database.PO.COLUMN_CPID, purchasingOrder.cpid.underlying)
                setString(Database.PO.COLUMN_OCID, purchasingOrder.ocid.underlying)
                setUUID(Database.PO.COLUMN_TOKEN, purchasingOrder.token.underlying)
                setString(Database.PO.COLUMN_OWNER, purchasingOrder.owner.underlying)
                setTimestamp(Database.PO.COLUMN_CREATED_DATE, contract.date.toCassandraTimestamp())
                setString(Database.PO.COLUMN_STATUS, contract.status.key)
                setString(Database.PO.COLUMN_STATUS_DETAILS, contract.statusDetails.key)
                setString(Database.PO.COLUMN_JSON_DATA, jsonData)
            }
            .tryExecute(session)
            .mapFailure {
                Fail.Incident.Database.DatabaseInteractionIncident(
                    SaveEntityException(message = "Error writing new po to database.", cause = it.exception)
                )
            }
            .onFailure { return it }
            .wasApplied()
            .asSuccess()
    }
}
