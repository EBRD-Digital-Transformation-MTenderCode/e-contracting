package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType.*
import com.procurement.contracting.model.dto.ContractIssuingAcRs
import com.procurement.contracting.model.dto.ContractProcess
import com.procurement.contracting.model.dto.IssuingAcRs
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.ocds.ContractStatus
import com.procurement.contracting.model.dto.ocds.ContractStatusDetails
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toLocalDateTime
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service

@Service
class IssuingAcService(private val acDao: AcDao) {

    fun setIssuesStatusDetails(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val ocId = cm.context.ocid ?: throw ErrorException(CONTEXT)
        val token = cm.context.token ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val dateTime = cm.context.startDate?.toLocalDateTime() ?: throw ErrorException(CONTEXT)

        val entity = acDao.getByCpIdAndAcId(cpId, ocId)
        val contractProcess = toObject(ContractProcess::class.java, entity.jsonData)

        if (entity.owner != owner) throw ErrorException(OWNER)
        if (entity.token.toString() != token) throw ErrorException(INVALID_TOKEN)
        if (contractProcess.contract.status == ContractStatus.PENDING && contractProcess.contract.statusDetails == ContractStatusDetails.CONTRACT_PREPARATION) {
            contractProcess.contract.statusDetails = ContractStatusDetails.ISSUED
            contractProcess.contract.date = dateTime
        } else {
            throw ErrorException(CONTRACT_STATUS_DETAILS)
        }
        entity.jsonData = toJson(contractProcess)
        acDao.save(entity)
        return ResponseDto(data = IssuingAcRs(ContractIssuingAcRs(date = contractProcess.contract.date, statusDetails = contractProcess.contract.statusDetails)))
    }


}
