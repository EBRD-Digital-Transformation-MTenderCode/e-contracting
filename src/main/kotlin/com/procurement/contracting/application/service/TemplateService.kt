package com.procurement.contracting.application.service

import com.procurement.contracting.application.service.model.ConfirmationRequestTemplate
import com.procurement.contracting.dao.TemplateDao
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service

@Service
class TemplateService(private val templateDao: TemplateDao) {

    fun getConfirmationRequestTemplate(country: String,
                                       pmd: String,
                                       language: String,
                                       templateId: String): ConfirmationRequestTemplate {
        val template = templateDao.getTemplate(country = country, pmd = pmd, language = language, templateId = templateId)
        return toObject(ConfirmationRequestTemplate::class.java, template)
    }


    fun getVerificationTemplate(country: String,
                                pmd: String,
                                language: String,
                                templateId: String): String {
        return templateDao.getTemplate(country = country, pmd = pmd, language = language, templateId = templateId)
    }

}