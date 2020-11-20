package com.procurement.contracting.application.repository.template

import com.procurement.contracting.domain.model.ProcurementMethod
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result

interface TemplateRepository {
    fun findBy(
        country: String,
        pmd: ProcurementMethod,
        language: String,
        templateId: String
    ): Result<String?, Fail.Incident.Database>
}
