package com.procurement.contracting.service
/*
import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestSource
import com.procurement.contracting.domain.model.confirmation.response.ConfirmationResponseType
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails
import com.procurement.contracting.domain.model.milestone.status.MilestoneStatus
import com.procurement.contracting.domain.model.milestone.type.MilestoneSubType
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType.CONFIRMATION_REQUEST
import com.procurement.contracting.exception.ErrorType.CONTEXT
import com.procurement.contracting.exception.ErrorType.MILESTONE
import com.procurement.contracting.exception.ErrorType.MILESTONE_RELATED_PARTY
import com.procurement.contracting.model.dto.ContractProcess
import com.procurement.contracting.model.dto.TreasuryAcRs
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.ocds.ConfirmationResponse
import com.procurement.contracting.model.dto.ocds.ConfirmationResponseValue
import com.procurement.contracting.model.dto.ocds.TreasuryData
import com.procurement.contracting.model.dto.ocds.Verification
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toLocalDateTime
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service


@Service
class TreasuryAcService(private val acDao: AcDao) {

    fun treasuryApprovingAC(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val ocId = cm.context.ocid ?: throw ErrorException(CONTEXT)
        val dateTime = cm.context.startDate?.toLocalDateTime() ?: throw ErrorException(CONTEXT)
        val treasuryData = toObject(TreasuryData::class.java, cm.data)

        val entity = acDao.getByCpIdAndAcId(cpId, ocId)
        val contractProcess = toObject(ContractProcess::class.java, entity.jsonData)

//        if (contractProcess.contract.status != ContractStatus.PENDING) throw ErrorException(CONTRACT_STATUS)
//        if (contractProcess.contract.statusDetails != ContractStatusDetails.VERIFICATION) throw ErrorException(CONTRACT_STATUS_DETAILS)

        contractProcess.contract.statusDetails = ContractStatusDetails.VERIFIED
        contractProcess.treasuryData = treasuryData

        val confirmationResponses = contractProcess.contract.confirmationResponses?.toHashSet() ?: hashSetOf()

        val confirmationRequest = contractProcess.contract.confirmationRequests?.asSequence()
                ?.firstOrNull { it.source == ConfirmationRequestSource.APPROVE_BODY }
                ?: throw ErrorException(CONFIRMATION_REQUEST)
        val request = confirmationRequest.requestGroups?.firstOrNull()?.requests?.firstOrNull()
                ?: throw ErrorException(CONFIRMATION_REQUEST)

        val milestone = contractProcess.contract.milestones?.asSequence()
                ?.firstOrNull { it.subtype == MilestoneSubType.APPROVE_BODY_VALIDATION }
                ?: throw ErrorException(MILESTONE)
        val milestoneRelatedParty = milestone.relatedParties?.first() ?: throw ErrorException(MILESTONE_RELATED_PARTY)

        val verification = Verification(
            type = ConfirmationResponseType.CODE,
            value = treasuryData.status,
            rationale = treasuryData.descr
        )

        val confirmationResponseValue = ConfirmationResponseValue(
                id = milestoneRelatedParty.id,
                name = milestoneRelatedParty.name,
                date = treasuryData.st_date,
                relatedPerson = null,
                verification = listOf(verification))

        val confirmationResponse = ConfirmationResponse(
                id = "cs-approveBody-confirmation-on-" + confirmationRequest.relatedItem + "-" + milestoneRelatedParty.id,
                value = confirmationResponseValue,
                request = request.id
        )

        confirmationResponses.add(confirmationResponse)
        contractProcess.contract.confirmationResponses = confirmationResponses

        milestone.apply {
            dateModified = dateTime
            dateMet = treasuryData.st_date
            status = MilestoneStatus.MET
        }

        contractProcess.contract.documents?.asSequence()
                ?.filter { it.id == confirmationRequest.relatedItem }
                ?.forEach { document ->
                    val relatedConfirmations = document.relatedConfirmations?.toMutableList()
                    relatedConfirmations?.add(confirmationResponse.id)
                    document.relatedConfirmations = relatedConfirmations
                }

        entity.jsonData = toJson(contractProcess)

        acDao.save(entity)

        return ResponseDto(data = TreasuryAcRs(contractProcess.contract))
    }
}
*/
