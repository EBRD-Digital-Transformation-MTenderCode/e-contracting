package com.procurement.contracting.application.service

import com.procurement.contracting.application.repository.model.ContractProcess
import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.domain.model.contract.status.ContractStatus
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType.CONTEXT
import com.procurement.contracting.exception.ErrorType.CONTRACT_ID
import com.procurement.contracting.exception.ErrorType.CONTRACT_STATUS
import com.procurement.contracting.exception.ErrorType.CONTRACT_STATUS_DETAILS
import com.procurement.contracting.exception.ErrorType.INVALID_OWNER
import com.procurement.contracting.exception.ErrorType.INVALID_TOKEN
import com.procurement.contracting.infrastructure.handler.v1.CommandMessage
import com.procurement.contracting.infrastructure.handler.v1.model.request.GetActualBsRs
import com.procurement.contracting.infrastructure.handler.v1.model.request.GetBidIdRs
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service

@Service
class StatusService(private val acDao: AcDao) {

    fun getActualBudgetSources(cm: CommandMessage): GetActualBsRs {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val ocId = cm.context.ocid ?: throw ErrorException(CONTEXT)
        val token = cm.context.token ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)

        val entity = acDao.getByCpIdAndAcId(cpId, ocId)
        if (entity.owner != owner) throw ErrorException(error = INVALID_OWNER)
        if (entity.token.toString() != token) throw ErrorException(INVALID_TOKEN)
        val contractProcess = toObject(ContractProcess::class.java, entity.jsonData)
        val contract = contractProcess.contract
        if (contract.id != ocId) throw ErrorException(CONTRACT_ID)
        if (contract.status != ContractStatus.PENDING) throw ErrorException(CONTRACT_STATUS)
        if (contract.statusDetails != ContractStatusDetails.CONTRACT_PROJECT
                && contract.statusDetails != ContractStatusDetails.CONTRACT_PREPARATION)
            throw ErrorException(CONTRACT_STATUS_DETAILS)
        val actualBudgetSource = contractProcess.planning?.budget?.budgetSource?.asSequence()?.toSet() ?: setOf()
        val itemsCPVs = contractProcess.award.items.asSequence().map { it.classification.id }.toHashSet()
        return GetActualBsRs(
                language = entity.language,
                actualBudgetSource = actualBudgetSource,
                itemsCPVs = itemsCPVs)
    }

    fun getRelatedBidId(cm: CommandMessage): GetBidIdRs {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val ocId = cm.context.ocid ?: throw ErrorException(CONTEXT)
        val entity = acDao.getByCpIdAndAcId(cpId, ocId)
        val contractProcess = toObject(ContractProcess::class.java, entity.jsonData)
        return GetBidIdRs(contractProcess.award.relatedBids)
    }
}
