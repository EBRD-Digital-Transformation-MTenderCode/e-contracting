package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType.*
import com.procurement.contracting.model.dto.ContractProcess
import com.procurement.contracting.model.dto.GetActualBsRs
import com.procurement.contracting.model.dto.GetBidIdRs
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.ocds.ContractStatus
import com.procurement.contracting.model.dto.ocds.ContractStatusDetails
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service

@Service
class StatusService(private val acDao: AcDao) {

    fun getActualBudgetSources(cm: CommandMessage): ResponseDto {
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
        return ResponseDto(data = GetActualBsRs(
                language = entity.language,
                actualBudgetSource = actualBudgetSource,
                itemsCPVs = itemsCPVs))
    }

    fun getRelatedBidId(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val ocId = cm.context.ocid ?: throw ErrorException(CONTEXT)
        val entity = acDao.getByCpIdAndAcId(cpId, ocId)
        val contractProcess = toObject(ContractProcess::class.java, entity.jsonData)
        return ResponseDto(data = GetBidIdRs(contractProcess.award.relatedBids))
    }
}
