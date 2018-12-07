package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType.*
import com.procurement.contracting.model.dto.ActivationAcRs
import com.procurement.contracting.model.dto.ContractActivationAcRs
import com.procurement.contracting.model.dto.ContractProcess
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.ocds.ContractStatus
import com.procurement.contracting.model.dto.ocds.ContractStatusDetails
import com.procurement.contracting.model.dto.ocds.MilestoneStatus
import com.procurement.contracting.model.dto.ocds.MilestoneSubType
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toLocalDateTime
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service

@Service
class ActivationAcService(private val acDao: AcDao) {

    fun activateAc(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val ocId = cm.context.ocid ?: throw ErrorException(CONTEXT)
        val token = cm.context.token ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val startDate = cm.context.startDate?.toLocalDateTime() ?: throw ErrorException(CONTEXT)

        val entity = acDao.getByCpIdAndAcId(cpId, ocId)
        if (entity.owner != owner) throw ErrorException(OWNER)
        if (entity.token.toString() != token) throw ErrorException(INVALID_TOKEN)
        val contractProcess = toObject(ContractProcess::class.java, entity.jsonData)

//        if (contractProcess.contract.status != ContractStatus.PENDING) throw ErrorException(CONTRACT_STATUS)
//        if (contractProcess.contract.statusDetails != ContractStatusDetails.VERIFIED) throw ErrorException(CONTRACT_STATUS_DETAILS)

        contractProcess.contract.milestones?.asSequence()
                ?.filter { it.subtype == MilestoneSubType.CONTRACT_ACTIVATION }
                ?.forEach { milestone ->
                    milestone.apply {
                        dateModified = startDate
                        dateMet = startDate
                        status = MilestoneStatus.MET
                    }
                }
        contractProcess.contract.apply {
            status = ContractStatus.ACTIVE
            statusDetails = ContractStatusDetails.EXECUTION

        }
        val relatedLot = contractProcess.award.relatedLots.firstOrNull()
                ?: throw ErrorException(EMPTY_AWARD_RELATED_LOT)

        val stageEnd = !isAnyContractPending(cpId)

        entity.jsonData = toJson(contractProcess)
        acDao.save(entity)
        return ResponseDto(data = ActivationAcRs(
                stageEnd = stageEnd,
                lotId = relatedLot,
                contract = ContractActivationAcRs(
                        status = contractProcess.contract.status,
                        statusDetails = contractProcess.contract.statusDetails,
                        milestones = contractProcess.contract.milestones
                )

        ))
    }

    private fun isAnyContractPending(cpId: String): Boolean {
        val contractProcesses = acDao.getAllByCpId(cpId)
        if (contractProcesses.isEmpty()) throw ErrorException(AC_NOT_FOUND)
        return contractProcesses.asSequence().any { it.status == ContractStatus.PENDING.value }
    }

}
