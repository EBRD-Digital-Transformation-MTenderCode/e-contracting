package com.procurement.contracting.infrastructure.extension.cassandra

import com.datastax.driver.core.BatchStatement
import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Session
import com.procurement.contracting.domain.functional.Result
import com.procurement.contracting.domain.functional.Result.Companion.failure
import com.procurement.contracting.domain.functional.Result.Companion.success
import com.procurement.contracting.infrastructure.fail.Fail

fun BoundStatement.tryExecute(session: Session): Result<ResultSet, Fail.Incident.Database.DatabaseInteractionIncident> = try {
    success(session.execute(this))
} catch (expected: Exception) {
    failure(Fail.Incident.Database.DatabaseInteractionIncident(exception = expected))
}

fun BatchStatement.tryExecute(session: Session): Result<ResultSet, Fail.Incident.Database.DatabaseInteractionIncident> =
    try {
        success(session.execute(this))
    } catch (expected: Exception) {
        failure(Fail.Incident.Database.DatabaseInteractionIncident(exception = expected))
    }

