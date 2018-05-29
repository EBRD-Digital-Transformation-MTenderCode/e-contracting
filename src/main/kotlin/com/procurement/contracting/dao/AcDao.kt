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

interface AcDao {

    fun save(entity: AcEntity)

    fun saveAll(entities: List<AcEntity>)

    fun getByCpIdAndToken(cpId: String, token: UUID): AcEntity

}

@Service
class AcDaoImpl(private val session: Session) : AcDao {

    override fun save(entity: AcEntity) {
        val insert =
                insertInto(CONTRACT_TABLE)
                        .value(CP_ID, entity.cpId)
                        .value(STAGE, entity.stage)
                        .value(TOKEN, entity.token)
                        .value(OWNER, entity.owner)
                        .value(CREATED_DATE, entity.createdDate)
                        .value(CAN_ID, entity.canId)
                        .value(STATUS, entity.status)
                        .value(STATUS_DETAILS, entity.statusDetails)
                        .value(JSON_DATA, entity.jsonData)
        session.execute(insert)
    }

    override fun saveAll(entities: List<AcEntity>) {
        val operations = ArrayList<Insert>()
        entities.forEach { entity ->
            operations.add(
                    insertInto(CONTRACT_TABLE)
                            .value(CP_ID, entity.cpId)
                            .value(STAGE, entity.stage)
                            .value(TOKEN, entity.token)
                            .value(OWNER, entity.owner)
                            .value(CREATED_DATE, entity.createdDate)
                            .value(CAN_ID, entity.canId)
                            .value(STATUS, entity.status)
                            .value(STATUS_DETAILS, entity.statusDetails)
                            .value(JSON_DATA, entity.jsonData))
        }
        val batch = QueryBuilder.batch(*operations.toTypedArray())
        session.execute(batch)
    }

    override fun getByCpIdAndToken(cpId: String, token: UUID): AcEntity {
        val query = select()
                .all()
                .from(CONTRACT_TABLE)
                .where(eq(CP_ID, cpId))
                .and(eq(TOKEN, token))
                .limit(1)
        val row = session.execute(query).one()
        return if (row != null)
            AcEntity(
                    cpId = row.getString(CP_ID),
                    stage = row.getString(STAGE),
                    token = row.getUUID(TOKEN),
                    owner = row.getString(OWNER),
                    createdDate = row.getTimestamp(CREATED_DATE),
                    canId = row.getString(CAN_ID),
                    status = row.getString(STATUS),
                    statusDetails = row.getString(STATUS_DETAILS),
                    jsonData = row.getString(JSON_DATA))
        else throw ErrorException(ErrorType.CONTRACT_NOT_FOUND)
    }

    companion object {
        private val CONTRACT_TABLE = "contracting_contract"
        private val CP_ID = "cp_id"
        private val STAGE = "stage"
        private val TOKEN = "token_entity"
        private val OWNER = "owner"
        private val CREATED_DATE = "created_date"
        private val CAN_ID = "can_id"
        private val STATUS = "status"
        private val STATUS_DETAILS = "status_details"
        private val JSON_DATA = "json_data"
    }
}
