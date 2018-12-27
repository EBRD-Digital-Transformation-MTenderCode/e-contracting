package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.dao.CanDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType.*
import com.procurement.contracting.model.dto.ActivationAcRs
import com.procurement.contracting.model.dto.ActivationCan
import com.procurement.contracting.model.dto.ActivationContract
import com.procurement.contracting.model.dto.ContractProcess
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.ocds.*
import com.procurement.contracting.model.entity.CanEntity
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toLocalDateTime
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service

@Service
class ActivationAcService(private val acDao: AcDao,
                          private val canDao: CanDao) {

    fun activateAc(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val ocId = cm.context.ocid ?: throw ErrorException(CONTEXT)
        val token = cm.context.token ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val startDate = cm.context.startDate?.toLocalDateTime() ?: throw ErrorException(CONTEXT)

        val contractEntity = acDao.getByCpIdAndAcId(cpId, ocId)
        if (contractEntity.owner != owner) throw ErrorException(OWNER)
        if (contractEntity.token.toString() != token) throw ErrorException(INVALID_TOKEN)
        val contractProcess = toObject(ContractProcess::class.java, contractEntity.jsonData)

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
        val relatedLots = contractProcess.award.relatedLots

        contractEntity.jsonData = toJson(contractProcess)
        contractEntity.status = ContractStatus.ACTIVE.value
        contractEntity.statusDetails = ContractStatusDetails.EXECUTION.value
        acDao.save(contractEntity)
        val stageEnd = !isAnyContractPending(cpId)

        val canEntities = canDao.findAllByCpId(cpId)
        if (canEntities.isEmpty()) throw ErrorException(CANS_NOT_FOUND)
        val updatedCanEntities = ArrayList<CanEntity>()
        val cans = ArrayList<Can>()
        for (canEntity in canEntities) {
            if (canEntity.acId == contractEntity.acId) {
                val can = toObject(Can::class.java, canEntity.jsonData)
                can.status = ContractStatus.ACTIVE
                can.statusDetails = ContractStatusDetails.EMPTY
                canEntity.status = can.status.value
                canEntity.statusDetails = can.statusDetails.value
                canEntity.jsonData = toJson(can)
                updatedCanEntities.add(canEntity)
                cans.add(can)
            }
        }
        updatedCanEntities.asSequence().forEach { canDao.save(it) }
        val cansRs = cans.asSequence().map { ActivationCan(id = it.id, status = it.status, statusDetails = it.statusDetails) }.toList()
        return ResponseDto(data = ActivationAcRs(
                stageEnd = stageEnd,
                relatedLots = relatedLots,
                contract = ActivationContract(
                        status = contractProcess.contract.status,
                        statusDetails = contractProcess.contract.statusDetails,
                        milestones = contractProcess.contract.milestones
                ),
                cans = cansRs

        ))
    }

    private fun isAnyContractPending(cpId: String): Boolean {
        val contractProcesses = acDao.getAllByCpId(cpId)
        if (contractProcesses.isEmpty()) throw ErrorException(AC_NOT_FOUND)
        return contractProcesses.asSequence().any { it.status == ContractStatus.PENDING.value }
    }

}
