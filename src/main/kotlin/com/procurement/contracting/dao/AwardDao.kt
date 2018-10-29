package com.procurement.contracting.dao

import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.Insert
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.querybuilder.QueryBuilder.eq
import com.datastax.driver.core.querybuilder.QueryBuilder.select
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.model.entity.AwardEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class AwardDao(private val session: Session) {

    fun save(entity: AwardEntity) {
        val insert =
                QueryBuilder.insertInto(AWARD_TABLE)
                        .value(CP_ID, entity.cpId)
                        .value(AC_ID, entity.acId)
                        .value(TOKEN, entity.token)
                        .value(OWNER, entity.owner)
                        .value(JSON_DATA, entity.jsonData)
        session.execute(insert)
    }


    fun saveAll(entities: List<AwardEntity>) {
        val operations = ArrayList<Insert>()
        entities.forEach { entity ->
            operations.add(QueryBuilder.insertInto(AWARD_TABLE)
                    .value(CP_ID, entity.cpId)
                    .value(AC_ID, entity.acId)
                    .value(TOKEN, entity.token)
                    .value(OWNER, entity.owner)
                    .value(JSON_DATA, entity.jsonData)
            )
        }
        val batch = QueryBuilder.batch(*operations.toTypedArray())
        session.execute(batch)
    }

    fun findAllByCpId(cpId: String): List<AwardEntity> {
        val query = select()
                .all()
                .from(AWARD_TABLE)
                .where(eq(CP_ID, cpId))
        val resultSet = session.execute(query)
        val entities = ArrayList<AwardEntity>()
        resultSet.forEach { row ->
            entities.add(
                    AwardEntity(
                            cpId = row.getString(CP_ID),
                            acId = row.getString(AC_ID),
                            token = row.getUUID(TOKEN),
                            owner = row.getString(OWNER),
                            jsonData = row.getString(JSON_DATA)))
        }
        return entities
    }

    fun getByCpIdAndAcIdAndToken(cpId: String, acId: String, token: UUID): AwardEntity {
        val query = select()
                .all()
                .from(AWARD_TABLE)
                .where(eq(CP_ID, cpId))
                .and(eq(AC_ID, acId))
                .and(eq(TOKEN, token))
                .limit(1)
        val row = session.execute(query).one()
        return if (row != null)
            AwardEntity(
                    cpId = row.getString(CP_ID),
                    acId = row.getString(AC_ID),
                    token = row.getUUID(TOKEN),
                    owner = row.getString(OWNER),
                    jsonData = row.getString(JSON_DATA))
        else throw ErrorException(ErrorType.AWARDS_NOT_FOUND)
    }

    companion object {
        private const val AWARD_TABLE = "contracting_award"
        private const val CP_ID = "cp_id"
        private const val AC_ID = "ac_id"
        private const val TOKEN = "token_entity"
        private const val OWNER = "owner"
        private const val JSON_DATA = "json_data"
    }
}
