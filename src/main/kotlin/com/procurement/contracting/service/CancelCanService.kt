package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.dao.CanDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType.*
import com.procurement.contracting.model.dto.CancelCanContractRs
import com.procurement.contracting.model.dto.CancelCanRq
import com.procurement.contracting.model.dto.CancelCanRs
import com.procurement.contracting.model.dto.ContractProcess
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.ocds.Can
import com.procurement.contracting.model.dto.ocds.Contract
import com.procurement.contracting.model.dto.ocds.ContractStatus
import com.procurement.contracting.model.dto.ocds.ContractStatusDetails
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

        val canEntity = canDao.getByCpIdAndCanId(cpId, UUID.fromString(canId))
        if (canEntity.owner != owner) throw ErrorException(OWNER)
        if (canEntity.token.toString() != token) throw ErrorException(INVALID_TOKEN)
        val can = toObject(Can::class.java, canEntity.jsonData)

        if (!(can.status == ContractStatus.PENDING
                        && (can.statusDetails == ContractStatusDetails.CONTRACT_PROJECT
                        || can.statusDetails == ContractStatusDetails.ACTIVE
                        || can.statusDetails == ContractStatusDetails.UNSUCCESSFUL))
        ) throw ErrorException(CAN_STATUS)
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
                lotId = can.lotId,
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

}

