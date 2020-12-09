package com.procurement.contracting.application.service

import com.procurement.contracting.application.repository.ac.AwardContractRepository
import com.procurement.contracting.application.repository.ac.model.AwardContractEntity
import com.procurement.contracting.application.repository.model.ContractProcess
import com.procurement.contracting.domain.model.ac.id.asAwardContractId
import com.procurement.contracting.domain.model.ac.status.AwardContractStatus
import com.procurement.contracting.domain.model.ac.status.AwardContractStatusDetails
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.exception.ErrorType.CONTRACT_ID
import com.procurement.contracting.exception.ErrorType.CONTRACT_STATUS
import com.procurement.contracting.exception.ErrorType.CONTRACT_STATUS_DETAILS
import com.procurement.contracting.exception.ErrorType.INVALID_OWNER
import com.procurement.contracting.exception.ErrorType.INVALID_TOKEN
import com.procurement.contracting.infrastructure.handler.v1.CommandMessage
import com.procurement.contracting.infrastructure.handler.v1.cpid
import com.procurement.contracting.infrastructure.handler.v1.model.request.GetActualBsRs
import com.procurement.contracting.infrastructure.handler.v1.model.request.GetBidIdRs
import com.procurement.contracting.infrastructure.handler.v1.ocid
import com.procurement.contracting.infrastructure.handler.v1.owner
import com.procurement.contracting.infrastructure.handler.v1.token
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service

@Service
class StatusService(
    private val acRepository: AwardContractRepository
) {

    fun getActualBudgetSources(cm: CommandMessage): GetActualBsRs {
        val cpid = cm.cpid
        val ocid = cm.ocid
        val token = cm.token
        val owner = cm.owner
        val awardContractId = ocid.asAwardContractId()
        val entity: AwardContractEntity = acRepository.findBy(cpid, awardContractId)
            .orThrow { it.exception }
            ?: throw ErrorException(ErrorType.CONTRACT_NOT_FOUND)
        if (entity.owner != owner) throw ErrorException(error = INVALID_OWNER)
        if (entity.token != token) throw ErrorException(INVALID_TOKEN)
        val contractProcess = toObject(ContractProcess::class.java, entity.jsonData)
        val contract = contractProcess.contract
        if (contract.id.underlying != ocid.underlying) throw ErrorException(CONTRACT_ID)
        if (contract.status != AwardContractStatus.PENDING) throw ErrorException(CONTRACT_STATUS)
        if (contract.statusDetails != AwardContractStatusDetails.CONTRACT_PROJECT
                && contract.statusDetails != AwardContractStatusDetails.CONTRACT_PREPARATION)
            throw ErrorException(CONTRACT_STATUS_DETAILS)
        val actualBudgetSource = contractProcess.planning?.budget?.budgetSource ?: emptyList()
        val itemsCPVs = contractProcess.award.items.map { it.classification.id }
        return GetActualBsRs(
                language = entity.language,
                actualBudgetSource = actualBudgetSource,
                itemsCPVs = itemsCPVs)
    }

    fun getRelatedBidId(cm: CommandMessage): GetBidIdRs {
        val cpid = cm.cpid
        val ocid = cm.ocid
        val awardContractId = ocid.asAwardContractId()
        val entity: AwardContractEntity = acRepository.findBy(cpid, awardContractId)
            .orThrow { it.exception }
            ?: throw ErrorException(ErrorType.CONTRACT_NOT_FOUND)
        val contractProcess = toObject(ContractProcess::class.java, entity.jsonData)
        return GetBidIdRs(contractProcess.award.relatedBids)
    }
}
