package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.dao.CanDao
import com.procurement.contracting.domain.model.contract.status.ContractStatus
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails
import com.procurement.contracting.domain.model.milestone.status.MilestoneStatus
import com.procurement.contracting.domain.model.milestone.type.MilestoneSubType
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType.CANS_NOT_FOUND
import com.procurement.contracting.exception.ErrorType.CONTEXT
import com.procurement.contracting.exception.ErrorType.INVALID_OWNER
import com.procurement.contracting.exception.ErrorType.INVALID_TOKEN
import com.procurement.contracting.model.dto.ActivationAcRs
import com.procurement.contracting.model.dto.ActivationCan
import com.procurement.contracting.model.dto.ActivationContract
import com.procurement.contracting.model.dto.ContractProcess
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.ocds.Can
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
        if (contractEntity.owner != owner) throw ErrorException(error = INVALID_OWNER)
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
                relatedLots = relatedLots,
                contract = ActivationContract(
                        status = contractProcess.contract.status,
                        statusDetails = contractProcess.contract.statusDetails,
                        milestones = contractProcess.contract.milestones
                ),
                cans = cansRs

        ))
    }

}
