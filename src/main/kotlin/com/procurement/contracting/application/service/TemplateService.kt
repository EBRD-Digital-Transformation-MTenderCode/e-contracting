package com.procurement.contracting.application.service

import com.procurement.contracting.application.repository.template.TemplateRepository
import com.procurement.contracting.application.service.model.ConfirmationRequestTemplate
import com.procurement.contracting.domain.model.ProcurementMethod
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service

@Service
class TemplateService(private val templateRepository: TemplateRepository) {

    fun getConfirmationRequestTemplate(
        country: String,
        pmd: ProcurementMethod,
        language: String,
        templateId: String
    ): ConfirmationRequestTemplate {
        val template =
            templateRepository.findBy(country = country, pmd = pmd, language = language, templateId = templateId)
                .orThrow { it.exception }
                ?: throw ErrorException(ErrorType.TEMPLATE_NOT_FOUND)

        return toObject(ConfirmationRequestTemplate::class.java, template)
    }

    fun getVerificationTemplate(
        country: String,
        pmd: ProcurementMethod,
        language: String,
        templateId: String
    ): String {
        return templateRepository.findBy(country = country, pmd = pmd, language = language, templateId = templateId)
            .orThrow { it.exception }
            ?: throw ErrorException(ErrorType.TEMPLATE_NOT_FOUND)
    }
}
