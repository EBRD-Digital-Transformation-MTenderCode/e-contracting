package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.dao.CanDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.exception.ErrorType.*
import com.procurement.contracting.model.dto.*
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.ocds.*
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service
import java.util.*

@Service
class CancelCanService(private val canDao: CanDao,
                       private val acDao: AcDao) {

    fun cancelCan(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val token = cm.context.token ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val canId = cm.context.id ?: throw ErrorException(CONTEXT)
        val dto = toObject(CancelCanRq::class.java, cm.data)

        validateDocumentTypeInRequest(dto.contract.amendment.documents)

        val canEntity = canDao.getByCpIdAndCanId(cpId, UUID.fromString(canId))
        if (canEntity.owner != owner) throw ErrorException(OWNER)
        if (canEntity.token.toString() != token) throw ErrorException(INVALID_TOKEN)
        val can = toObject(Can::class.java, canEntity.jsonData)

//        if (can.status != ContractStatus.PENDING && can.statusDetails != ContractStatusDetails.CONTRACT_PROJECT) throw ErrorException(CAN_STATUS)
//        if (can.status != ContractStatusDetails.ACTIVE && can.statusDetails != ContractStatusDetails.EMPTY) throw ErrorException(CAN_STATUS)

        can.status = ContractStatus.CANCELLED
        can.statusDetails = ContractStatusDetails.EMPTY
        can.amendment = dto.contract.amendment

        canEntity.status = can.status.value
        canEntity.statusDetails = can.statusDetails.value
        canEntity.jsonData = toJson(can)
        var acCancel = false
        var contract: Contract? = null
        if (canEntity.acId != null) {
            val acEntity = acDao.getByCpIdAndAcId(cpId, canEntity.acId!!)
            val contractProcess = toObject(ContractProcess::class.java, acEntity.jsonData)
//            if (contractProcess.contract.status != ContractStatus.PENDING) throw ErrorException(ErrorType.CONTRACT_STATUS)
            contractProcess.contract.status = ContractStatus.CANCELLED
            contractProcess.contract.statusDetails = ContractStatusDetails.EMPTY
            acEntity.status = contractProcess.contract.status.value
            acEntity.statusDetails = contractProcess.contract.statusDetails.value
            acEntity.jsonData = toJson(contractProcess)
            acDao.save(acEntity)
            acCancel = true
            contract = contractProcess.contract
        }
        canDao.save(canEntity)
        return ResponseDto(
                data = CancelCanRs(
                        can = can,
                        acCancel = acCancel,
                        contract = convertToContractDto(contract))
        )
    }

    private fun convertToContractDto(contract: Contract?): CancelCanContractRs? {
        return if (contract != null) {
            CancelCanContractRs(
                    id = contract.id,
                    status = contract.status,
                    statusDetails = contract.statusDetails)
        } else null
    }
    private fun validateDocumentTypeInRequest(documents: List<DocumentAmendment>){
        documents.forEach{
            if(!(it.documentType == DocumentTypeAmendment.CONTRACT_NOTICE
                    ||it.documentType == DocumentTypeAmendment.CONTRACT_ARRANGEMENTS
                    ||it.documentType == DocumentTypeAmendment.CONTRACT_SCHEDULE
                    ||it.documentType == DocumentTypeAmendment.CONTRACT_ANNEXE
                    ||it.documentType == DocumentTypeAmendment.CONTRACT_GUARANTEES
                    ||it.documentType == DocumentTypeAmendment.SUB_CONTRACT
                    ||it.documentType == DocumentTypeAmendment.ILLUSTRATION
                    ||it.documentType == DocumentTypeAmendment.CONTRACT_SUMMARY
                    ||it.documentType == DocumentTypeAmendment.CANCELLATION_DETAILS
                    ||it.documentType == DocumentTypeAmendment.CONFLICT_OF_INTEREST
                    )) throw ErrorException(ErrorType.DOCUMENTS_TYPE_CANCEL_CAN)

        }
    }
}

