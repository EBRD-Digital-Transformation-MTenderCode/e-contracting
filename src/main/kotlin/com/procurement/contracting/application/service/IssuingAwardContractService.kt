package com.procurement.contracting.application.service

import com.procurement.contracting.application.exception.repository.SaveEntityException
import com.procurement.contracting.application.repository.ac.AwardContractRepository
import com.procurement.contracting.application.repository.ac.model.AwardContractEntity
import com.procurement.contracting.application.repository.model.ContractProcess
import com.procurement.contracting.domain.model.ac.id.asAwardContractId
import com.procurement.contracting.domain.model.ac.status.AwardContractStatus
import com.procurement.contracting.domain.model.ac.status.AwardContractStatusDetails
import com.procurement.contracting.domain.model.item.ItemId
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.exception.ErrorType.BA_ITEM_ID
import com.procurement.contracting.exception.ErrorType.CONTRACT_STATUS
import com.procurement.contracting.exception.ErrorType.CONTRACT_STATUS_DETAILS
import com.procurement.contracting.exception.ErrorType.INVALID_OWNER
import com.procurement.contracting.exception.ErrorType.INVALID_TOKEN
import com.procurement.contracting.infrastructure.handler.v1.CommandMessage
import com.procurement.contracting.infrastructure.handler.v1.cpid
import com.procurement.contracting.infrastructure.handler.v1.model.request.ContractIssuingAcRs
import com.procurement.contracting.infrastructure.handler.v1.model.request.IssuingAcRs
import com.procurement.contracting.infrastructure.handler.v1.ocid
import com.procurement.contracting.infrastructure.handler.v1.owner
import com.procurement.contracting.infrastructure.handler.v1.startDate
import com.procurement.contracting.infrastructure.handler.v1.token
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service

@Service
class IssuingAwardContractService(
    private val acRepository: AwardContractRepository
) {

    fun issuingAc(cm: CommandMessage): IssuingAcRs {
        val cpid = cm.cpid
        val ocid = cm.ocid
        val token = cm.token
        val owner = cm.owner
        val dateTime = cm.startDate

        val awardContractId = ocid.asAwardContractId()
        val entity: AwardContractEntity = acRepository.findBy(cpid, awardContractId)
            .orThrow { it.exception }
            ?: throw ErrorException(ErrorType.CONTRACT_NOT_FOUND)
        if (entity.owner != owner) throw ErrorException(error = INVALID_OWNER)
        if (entity.token != token) throw ErrorException(INVALID_TOKEN)
        val contractProcess = toObject(ContractProcess::class.java, entity.jsonData)

        if (contractProcess.contract.status != AwardContractStatus.PENDING) throw ErrorException(CONTRACT_STATUS)
        if (contractProcess.contract.statusDetails != AwardContractStatusDetails.CONTRACT_PREPARATION)
            throw ErrorException(error = CONTRACT_STATUS_DETAILS)

        val relatedItemIds: Set<ItemId> = contractProcess.planning!!
            .budget
            .budgetAllocation.asSequence()
            .map { it.relatedItem }
            .toSet()
        val awardItemIds: Set<ItemId> = contractProcess.award.items.asSequence().map { it.id }.toSet()
        if (awardItemIds.size != relatedItemIds.size) throw ErrorException(BA_ITEM_ID)
        if (!awardItemIds.containsAll(relatedItemIds)) throw ErrorException(BA_ITEM_ID)

        contractProcess.contract.statusDetails = AwardContractStatusDetails.ISSUED
        contractProcess.contract.date = dateTime

        val updatedContractEntity = entity.copy(
            status = contractProcess.contract.status,
            statusDetails = contractProcess.contract.statusDetails,
            jsonData = toJson(contractProcess)
        )

        val wasApplied = acRepository
            .updateStatusesAC(
                cpid = cpid,
                id = updatedContractEntity.id,
                status = updatedContractEntity.status,
                statusDetails = updatedContractEntity.statusDetails,
                jsonData = updatedContractEntity.jsonData
            )
            .orThrow { it.exception }
        if (!wasApplied)
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the save updated AC by cpid '${cpid}' and id '${updatedContractEntity.id}' with status '${updatedContractEntity.status}' and status details '${updatedContractEntity.statusDetails}' to the database. Record is not exists.")

        return IssuingAcRs(
            ContractIssuingAcRs(
                date = contractProcess.contract.date,
                statusDetails = contractProcess.contract.statusDetails
            )
        )
    }
}
