package com.procurement.contracting.service

import com.procurement.contracting.dao.TemplateDao
import com.procurement.contracting.model.dto.templates.ConfirmationRequestTemplate
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service

@Service
class TemplateService(private val templateDao: TemplateDao) {

    fun getConfirmationRequestTemplate(country: String,
                                       pmd: String,
                                       language: String,
                                       templateId: String): ConfirmationRequestTemplate {
        val template = templateDao.getTemplate(country, pmd, language, templateId)
        return toObject(ConfirmationRequestTemplate::class.java, template)
    }

}