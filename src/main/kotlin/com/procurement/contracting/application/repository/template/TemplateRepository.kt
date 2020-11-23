package com.procurement.contracting.application.repository.template

import com.procurement.contracting.domain.model.ProcurementMethodDetails
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result

interface TemplateRepository {
    fun findBy(
        country: String,
        pmd: ProcurementMethodDetails,
        language: String,
        templateId: String
    ): Result<String?, Fail.Incident.Database>
}
