package com.procurement.contracting.dao

import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.Insert
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.querybuilder.QueryBuilder.*
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.model.entity.CanEntity
import org.springframework.stereotype.Service
import java.util.*

interface CanDao {

    fun save(entity: CanEntity)

    fun saveAll(entities: List<CanEntity>)

    fun findAllByCpIdAndStage(cpId: String, stage: String): List<CanEntity>

    fun getByCpIdAndToken(cpId: String, token: UUID): CanEntity

}

@Service
class CanDaoImpl(private val session: Session) : CanDao {

    override fun save(entity: CanEntity) {
        val insert =
                insertInto(NOTICE_TABLE)
                        .value(CP_ID, entity.cpId)
                        .value(STAGE, entity.stage)
                        .value(TOKEN, entity.token)
                        .value(OWNER, entity.owner)
                        .value(CREATED_DATE, entity.createdDate)
                        .value(AWARD_ID, entity.awardId)
                        .value(AC_ID, entity.acId)
                        .value(STATUS, entity.status)
                        .value(STATUS_DETAILS, entity.statusDetails)
        session.execute(insert)
    }

    override fun saveAll(entities: List<CanEntity>) {
        val operations = ArrayList<Insert>()
        entities.forEach { entity ->
            operations.add(
                    insertInto(NOTICE_TABLE)
                            .value(CP_ID, entity.cpId)
                            .value(STAGE, entity.stage)
                            .value(TOKEN, entity.token)
                            .value(OWNER, entity.owner)
                            .value(CREATED_DATE, entity.createdDate)
                            .value(AWARD_ID, entity.awardId)
                            .value(AC_ID, entity.acId)
                            .value(STATUS, entity.status)
                            .value(STATUS_DETAILS, entity.statusDetails))
        }
        val batch = QueryBuilder.batch(*operations.toTypedArray())
        session.execute(batch)
    }

    override fun getByCpIdAndToken(cpId: String, token: UUID): CanEntity {
        val query = select()
                .all()
                .from(NOTICE_TABLE)
                .where(eq(CP_ID, cpId))
                .and(eq(TOKEN, token))
                .limit(1)
        val row = session.execute(query).one()
        return if (row != null)
            CanEntity(
                    cpId = row.getString(CP_ID),
                    stage = row.getString(STAGE),
                    token = row.getUUID(TOKEN),
                    owner = row.getString(OWNER),
                    createdDate = row.getTimestamp(CREATED_DATE),
                    awardId = row.getString(AWARD_ID),
                    acId = row.getString(AC_ID),
                    status = row.getString(STATUS),
                    statusDetails = row.getString(STATUS_DETAILS))
        else throw ErrorException(ErrorType.CANS_NOT_FOUND)
    }

    override fun findAllByCpIdAndStage(cpId: String, stage: String): List<CanEntity> {
        val query = select()
                .all()
                .from(NOTICE_TABLE)
                .where(eq(CP_ID, cpId))
                .and(eq(STAGE, stage))
        val resultSet = session.execute(query)
        val entities = ArrayList<CanEntity>()
        resultSet.forEach { row ->
            entities.add(
                    CanEntity(
                            cpId = row.getString(CP_ID),
                            stage = row.getString(STAGE),
                            token = row.getUUID(TOKEN),
                            owner = row.getString(OWNER),
                            createdDate = row.getTimestamp(CREATED_DATE),
                            awardId = row.getString(AWARD_ID),
                            acId = row.getString(AC_ID),
                            status = row.getString(STATUS),
                            statusDetails = row.getString(STATUS_DETAILS))
            )
        }
        return entities
    }

    companion object {
        private val NOTICE_TABLE = "contracting_notice"
        private val CP_ID = "cp_id"
        private val STAGE = "stage"
        private val TOKEN = "token_entity"
        private val OWNER = "owner"
        private val CREATED_DATE = "created_date"
        private val AWARD_ID = "award_id"
        private val AC_ID = "ac_id"
        private val STATUS = "status"
        private val STATUS_DETAILS = "status_details"
    }
}
