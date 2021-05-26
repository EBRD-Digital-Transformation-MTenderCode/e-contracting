package com.procurement.contracting.application.repository.rule

import com.procurement.contracting.domain.model.OperationType
import com.procurement.contracting.domain.model.ProcurementMethodDetails
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result

interface RuleRepository {
    fun find(
        key: String,
        parameter: String,
    ): Result<String?, Fail.Incident.Database.DatabaseInteractionIncident>

    fun get(
        key: String,
        parameter: String,
    ): Result<String, Fail>
}
