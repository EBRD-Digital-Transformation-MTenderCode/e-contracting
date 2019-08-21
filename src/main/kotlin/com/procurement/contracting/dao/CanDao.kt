package com.procurement.contracting.dao

import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder.eq
import com.datastax.driver.core.querybuilder.QueryBuilder.insertInto
import com.datastax.driver.core.querybuilder.QueryBuilder.select
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.model.entity.CanEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class CanDao(private val session: Session) {

    fun save(entity: CanEntity) {
        val insert =
            insertInto(CAN_TABLE)
                .value(CP_ID, entity.cpId)
                .value(CAN_ID, entity.canId.toString())
                .value(TOKEN, entity.token)
                .value(OWNER, entity.owner)
                .value(CREATED_DATE, entity.createdDate)
                .value(AWARD_ID, entity.awardId)
                .value(LOT_ID, entity.lotId)
                .value(AC_ID, entity.acId)
                .value(STATUS, entity.status.value)
                .value(STATUS_DETAILS, entity.statusDetails.value)
                .value(JSON_DATA, entity.jsonData)
        session.execute(insert)
    }

    fun findAllByCpId(cpId: String): List<CanEntity> {
        val query = select()
            .all()
            .from(CAN_TABLE)
            .where(eq(CP_ID, cpId))
        val resultSet = session.execute(query)
        val entities = ArrayList<CanEntity>()
        resultSet.forEach { row ->
            entities.add(
                CanEntity(
                    cpId = row.getString(CP_ID),
                    canId = row.getUUID(CAN_ID),
                    token = row.getUUID(TOKEN),
                    owner = row.getString(OWNER),
                    createdDate = row.getTimestamp(CREATED_DATE),
                    awardId = UUID.fromString(row.getString(AWARD_ID)),
                    lotId = UUID.fromString(row.getString(LOT_ID)),
                    acId = row.getString(AC_ID),
                    status = CANStatus.fromString(row.getString(STATUS)),
                    statusDetails = CANStatusDetails.fromString(row.getString(STATUS_DETAILS)),
                    jsonData = row.getString(JSON_DATA) ?: ""
                )
            )
        }
        return entities
    }

    fun getByCpIdAndCanId(cpId: String, canId: CANId): CanEntity {
        val query = select()
            .all()
            .from(CAN_TABLE)
            .where(eq(CP_ID, cpId))
            .and(eq(CAN_ID, canId.toString()))
            .limit(1)
        val row = session.execute(query).one()
        return if (row != null)
            CanEntity(
                cpId = row.getString(CP_ID),
                canId = row.getUUID(CAN_ID),
                token = row.getUUID(TOKEN),
                owner = row.getString(OWNER),
                createdDate = row.getTimestamp(CREATED_DATE),
                awardId = UUID.fromString(row.getString(AWARD_ID)),
                lotId = UUID.fromString(row.getString(LOT_ID)),
                acId = row.getString(AC_ID),
                status = CANStatus.fromString(row.getString(STATUS)),
                statusDetails = CANStatusDetails.fromString(row.getString(STATUS_DETAILS)),
                jsonData = row.getString(JSON_DATA) ?: ""
            )
        else throw ErrorException(ErrorType.CAN_NOT_FOUND)
    }

    companion object {
        private const val CAN_TABLE = "contracting_can"
        private const val CP_ID = "cp_id"
        private const val CAN_ID = "can_id"
        private const val TOKEN = "token_entity"
        private const val AC_ID = "ac_id"
        private const val AWARD_ID = "award_id"
        private const val LOT_ID = "lot_id"
        private const val OWNER = "owner"
        private const val CREATED_DATE = "created_date"
        private const val STATUS = "status"
        private const val STATUS_DETAILS = "status_details"
        private const val JSON_DATA = "json_data"
    }
}
