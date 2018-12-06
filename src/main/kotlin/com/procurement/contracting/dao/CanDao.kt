package com.procurement.contracting.dao

import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.Insert
import com.datastax.driver.core.querybuilder.QueryBuilder.*
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.model.entity.CanEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class CanDao(private val session: Session) {

    fun save(entity: CanEntity) {
        val insert =
                insertInto(NOTICE_TABLE)
                        .value(CP_ID, entity.cpId)
                        .value(CAN_ID, entity.canId)
                        .value(OWNER, entity.owner)
                        .value(CREATED_DATE, entity.createdDate)
                        .value(AWARD_ID, entity.awardId)
                        .value(AC_ID, entity.acId)
                        .value(STATUS, entity.status)
                        .value(STATUS_DETAILS, entity.statusDetails)
        session.execute(insert)
    }

    fun saveAll(entities: List<CanEntity>) {
        val operations = ArrayList<Insert>()
        entities.forEach { entity ->
            operations.add(insertInto(NOTICE_TABLE)
                    .value(CP_ID, entity.cpId)
                    .value(CAN_ID, entity.canId)
                    .value(OWNER, entity.owner)
                    .value(CREATED_DATE, entity.createdDate)
                    .value(AWARD_ID, entity.awardId)
                    .value(AC_ID, entity.acId)
                    .value(STATUS, entity.status)
                    .value(STATUS_DETAILS, entity.statusDetails))
        }
        val batch = batch(*operations.toTypedArray())
        session.execute(batch)
    }

    fun findAllByCpId(cpId: String): List<CanEntity> {
        val query = select()
                .all()
                .from(NOTICE_TABLE)
                .where(eq(CP_ID, cpId))
        val resultSet = session.execute(query)
        val entities = ArrayList<CanEntity>()
        resultSet.forEach { row ->
            entities.add(
                    CanEntity(
                            cpId = row.getString(CP_ID),
                            canId = row.getUUID(CAN_ID),
                            owner = row.getString(OWNER),
                            createdDate = row.getTimestamp(CREATED_DATE),
                            awardId = row.getString(AWARD_ID),
                            acId = row.getString(AC_ID),
                            status = row.getString(STATUS),
                            statusDetails = row.getString(STATUS_DETAILS),
                        jsonData = row.getString(JSON_DATA))
            )
        }
        return entities
    }
    fun getByCpIdAndAcId(cpId: String, canId: String): CanEntity {
        val query = select()
            .all()
            .from(NOTICE_TABLE)
            .where(eq(CP_ID, cpId))
            .and(eq(CAN_ID, canId))
            .limit(1)
        val row = session.execute(query).one()
        return if (row != null)
            CanEntity(
                cpId = row.getString(CP_ID),
                canId = row.getUUID(CAN_ID),
                owner = row.getString(OWNER),
                createdDate = row.getTimestamp(CREATED_DATE),
                awardId = row.getString(AWARD_ID),
                acId = row.getString(AC_ID),
                status = row.getString(STATUS),
                statusDetails = row.getString(STATUS_DETAILS),
                jsonData = row.getString(JSON_DATA))
        else throw ErrorException(ErrorType.CAN_NOT_FOUND)
    }

    companion object {
        private const val NOTICE_TABLE = "contracting_can"
        private const val CP_ID = "cp_id"
        private const val CAN_ID = "can_id"
        private const val AC_ID = "ac_id"
        private const val AWARD_ID = "award_id"
        private const val OWNER = "owner"
        private const val CREATED_DATE = "created_date"
        private const val STATUS = "status"
        private const val STATUS_DETAILS = "status_details"
        private const val JSON_DATA = "json_data"
    }
}
