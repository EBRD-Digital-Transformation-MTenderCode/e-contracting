package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType.*
import com.procurement.contracting.model.dto.*
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.ocds.ContractStatusDetails
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service

@Service
class VerificationAcService(private val acDao: AcDao) {

    fun verificationAc(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val ocId = cm.context.ocid ?: throw ErrorException(CONTEXT)

        val entity = acDao.getByCpIdAndAcId(cpId, ocId)
        val contractProcess = toObject(ContractProcess::class.java, entity.jsonData)

//        if (contractProcess.contract.status != ContractStatus.PENDING) throw ErrorException(CONTRACT_STATUS)
//        if (contractProcess.contract.statusDetails != ContractStatusDetails.SIGNED) throw ErrorException(CONTRACT_STATUS_DETAILS)

        contractProcess.contract.statusDetails = ContractStatusDetails.VERIFICATION

        entity.statusDetails=ContractStatusDetails.VERIFICATION.toString()
        entity.jsonData = toJson(contractProcess)
        acDao.save(entity)
        return ResponseDto(data = VerificationAcRs(ContractVerifiedAcRs(contractProcess.contract.statusDetails)))
    }


}
