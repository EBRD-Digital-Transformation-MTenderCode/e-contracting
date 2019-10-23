package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.domain.model.contract.status.ContractStatus
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails
import com.procurement.contracting.domain.model.item.ItemId
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType.BA_ITEM_ID
import com.procurement.contracting.exception.ErrorType.CONTEXT
import com.procurement.contracting.exception.ErrorType.CONTRACT_STATUS
import com.procurement.contracting.exception.ErrorType.CONTRACT_STATUS_DETAILS
import com.procurement.contracting.exception.ErrorType.INVALID_OWNER
import com.procurement.contracting.exception.ErrorType.INVALID_TOKEN
import com.procurement.contracting.model.dto.ContractIssuingAcRs
import com.procurement.contracting.model.dto.ContractProcess
import com.procurement.contracting.model.dto.IssuingAcRs
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toLocalDateTime
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service

@Service
class IssuingAcService(private val acDao: AcDao) {

    fun issuingAc(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val ocId = cm.context.ocid ?: throw ErrorException(CONTEXT)
        val token = cm.context.token ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val dateTime = cm.context.startDate?.toLocalDateTime() ?: throw ErrorException(error = CONTEXT)

        val entity = acDao.getByCpIdAndAcId(cpId, ocId)
        if (entity.owner != owner) throw ErrorException(error = INVALID_OWNER)
        if (entity.token.toString() != token) throw ErrorException(INVALID_TOKEN)
        val contractProcess = toObject(ContractProcess::class.java, entity.jsonData)

        if (contractProcess.contract.status != ContractStatus.PENDING) throw ErrorException(CONTRACT_STATUS)
        if (contractProcess.contract.statusDetails != ContractStatusDetails.CONTRACT_PREPARATION)
            throw ErrorException(error = CONTRACT_STATUS_DETAILS)

        val relatedItemIds: Set<ItemId> = contractProcess.planning!!
            .budget
            .budgetAllocation.asSequence()
            .map { it.relatedItem }
            .toSet()
        val awardItemIds: Set<ItemId> = contractProcess.award.items.asSequence().map { it.id }.toSet()
        if (awardItemIds.size != relatedItemIds.size) throw ErrorException(BA_ITEM_ID)
        if (!awardItemIds.containsAll(relatedItemIds)) throw ErrorException(BA_ITEM_ID)

        contractProcess.contract.statusDetails = ContractStatusDetails.ISSUED
        contractProcess.contract.date = dateTime

        entity.statusDetails = ContractStatusDetails.ISSUED
        entity.jsonData = toJson(contractProcess)
        acDao.save(entity)
        return ResponseDto(
            data = IssuingAcRs(
                ContractIssuingAcRs(
                    date = contractProcess.contract.date,
                    statusDetails = contractProcess.contract.statusDetails
                )
            )
        )
    }
}
