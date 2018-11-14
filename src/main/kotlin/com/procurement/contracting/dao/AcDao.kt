package com.procurement.contracting.dao

import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.Insert
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.querybuilder.QueryBuilder.*
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.model.entity.AcEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class AcDao(private val session: Session) {

    fun save(entity: AcEntity) {
        val insert =
                insertInto(CONTRACT_TABLE)
                        .value(CP_ID, entity.cpId)
                        .value(AC_ID, entity.acId)
                        .value(TOKEN, entity.token)
                        .value(OWNER, entity.owner)
                        .value(CREATED_DATE, entity.createdDate)
                        .value(CAN_ID, entity.canId)
                        .value(STATUS, entity.status)
                        .value(STATUS_DETAILS, entity.statusDetails)
                        .value(MPC, entity.mainProcurementCategory)
                        .value(LANGUAGE, entity.language)
                        .value(JSON_DATA, entity.jsonData)
        session.execute(insert)
    }

    fun saveAll(entities: List<AcEntity>) {
        val operations = ArrayList<Insert>()
        entities.forEach { entity ->
            operations.add(
                    insertInto(CONTRACT_TABLE)
                            .value(CP_ID, entity.cpId)
                            .value(AC_ID, entity.acId)
                            .value(TOKEN, entity.token)
                            .value(OWNER, entity.owner)
                            .value(CREATED_DATE, entity.createdDate)
                            .value(CAN_ID, entity.canId)
                            .value(STATUS, entity.status)
                            .value(STATUS_DETAILS, entity.statusDetails)
                            .value(MPC, entity.mainProcurementCategory)
                            .value(LANGUAGE, entity.language)
                            .value(JSON_DATA, entity.jsonData))
        }
        val batch = QueryBuilder.batch(*operations.toTypedArray())
        session.execute(batch)
    }

    fun getByCpIdAndAcId(cpId: String, acId: String): AcEntity {
        val query = select()
                .all()
                .from(CONTRACT_TABLE)
                .where(eq(CP_ID, cpId))
                .and(eq(AC_ID, acId))
                .limit(1)
        val row = session.execute(query).one()
        return if (row != null)
            AcEntity(
                    cpId = row.getString(CP_ID),
                    acId = row.getString(AC_ID),
                    token = row.getUUID(TOKEN),
                    owner = row.getString(OWNER),
                    createdDate = row.getTimestamp(CREATED_DATE),
                    canId = row.getString(CAN_ID),
                    status = row.getString(STATUS),
                    statusDetails = row.getString(STATUS_DETAILS),
                    mainProcurementCategory = row.getString(MPC),
                    language = row.getString(LANGUAGE),
                    jsonData = row.getString(JSON_DATA))
        else throw ErrorException(ErrorType.CONTRACT_NOT_FOUND)
    }

    companion object {
        private const val CONTRACT_TABLE = "contracting_ac"
        private const val CP_ID = "cp_id"
        private const val AC_ID = "ac_id"
        private const val CAN_ID = "can_id"
        private const val TOKEN = "token_entity"
        private const val OWNER = "owner"
        private const val CREATED_DATE = "created_date"
        private const val STATUS = "status"
        private const val STATUS_DETAILS = "status_details"
        private const val MPC = "mpc"
        private const val LANGUAGE = "language"
        private const val JSON_DATA = "json_data"
    }
}
