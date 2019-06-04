package com.procurement.contracting.service

import com.procurement.contracting.dao.CanDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.exception.ErrorType.CONTEXT
import com.procurement.contracting.model.dto.*
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.ocds.Can
import com.procurement.contracting.model.dto.ocds.ContractStatus
import com.procurement.contracting.model.dto.ocds.ContractStatusDetails
import com.procurement.contracting.model.entity.CanEntity
import com.procurement.contracting.utils.toDate
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toLocalDateTime
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

@Service
class CreateCanService(private val canDao: CanDao,
                       private val generationService: GenerationService) {

    fun createCan(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val dateTime = cm.context.startDate?.toLocalDateTime() ?: throw ErrorException(CONTEXT)
        val lotId = cm.context.id ?: throw ErrorException(CONTEXT)
        val dto = toObject(CreateCanRq::class.java, cm.data)

        val statusDetails: ContractStatusDetails
        var canAwardId: String? = null
        if (dto.awardingSuccess) {
            statusDetails = ContractStatusDetails.CONTRACT_PROJECT
            canAwardId = dto.awardId
        } else {
            statusDetails = ContractStatusDetails.UNSUCCESSFUL
        }
        val can = Can(
                id = generationService.generateRandomUUID().toString(),
                token = generationService.generateRandomUUID().toString(),
                date = dateTime,
                awardId = canAwardId,
                lotId = lotId,
                status = ContractStatus.PENDING,
                statusDetails = statusDetails,
                documents = null,
                amendment = null)
        val canEntity = createCanEntity(cpId, owner, dateTime, can)
        canDao.save(canEntity)
        return ResponseDto(data = CreateCanRs(can))
    }

    fun checkCan(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val lotId = cm.context.id ?: throw ErrorException(CONTEXT)

        val canEntities = canDao.findAllByCpId(cpId)
        if (canEntities.asSequence().any { it.lotId == lotId && it.status != ContractStatus.CANCELLED.value }) {
            throw ErrorException(ErrorType.CAN_FOR_LOT_EXIST)
        }
        return ResponseDto(data = "ok")
    }

    fun checkCanByAwardId(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val dto = toObject(AwardDto::class.java, cm.data)
        val canEntities = canDao.findAllByCpId(cpId)
        if (canEntities.asSequence().none {
                    it.awardId == dto.awardId
                            && it.status == ContractStatus.PENDING.value
                }) {
            throw ErrorException(ErrorType.INVALID_CAN_STATUS)
        }
        return ResponseDto(data = "ok")
    }

    fun getCans(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val dto = toObject(GetAwardsRq::class.java, cm.data)

        val canEntities = canDao.findAllByCpId(cpId)
        val canIdsSet = dto.contracts.asSequence().map { it.id }.toSet()
        val canEntitiesFiltered = canEntities.asSequence().filter { canIdsSet.contains(it.canId.toString()) }.toList()
        val cansRs = ArrayList<CanGetAwards>()
        for (canEntity in canEntitiesFiltered) {
            if (canEntity.statusDetails != ContractStatusDetails.UNSUCCESSFUL.value) {
                cansRs.add(CanGetAwards(id = canEntity.canId.toString(), awardId = canEntity.awardId!!))
            } else {
                throw ErrorException(ErrorType.INVALID_CAN_STATUS)
            }
        }
        return ResponseDto(data = GetAwardsRs(cansRs))
    }


    fun confirmationCan(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val token = cm.context.token ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val canId = cm.context.id ?: throw ErrorException(CONTEXT)
        val canEntity = canDao.getByCpIdAndCanId(cpId, UUID.fromString(canId))
        if (canEntity.owner != owner) throw ErrorException(error = ErrorType.INVALID_OWNER)
        if (canEntity.token.toString() != token) throw ErrorException(ErrorType.INVALID_TOKEN)
        if (canEntity.status != ContractStatus.PENDING.value && canEntity.statusDetails != ContractStatusDetails.UNSUCCESSFUL.value)
            throw ErrorException(ErrorType.INVALID_CAN_STATUS)
        val can = toObject(Can::class.java, canEntity.jsonData)
        can.status = ContractStatus.UNSUCCESSFUL
        can.statusDetails = ContractStatusDetails.EMPTY
        canEntity.status = can.status.value
        canEntity.statusDetails = can.statusDetails.value
        canEntity.jsonData = toJson(can)
        canDao.save(canEntity)
        return ResponseDto(data = ConfirmationCanRs(
                cans = listOf(ConfirmationCan(can.id, can.status, can.statusDetails)),
                lotId = can.lotId))
    }


    private fun createCanEntity(cpId: String,
                                owner: String,
                                dateTime: LocalDateTime,
                                can: Can): CanEntity {
        return CanEntity(
                cpId = cpId,
                canId = UUID.fromString(can.id),
                token = UUID.fromString(can.token),
                awardId = can.awardId,
                lotId = can.lotId,
                acId = null,
                owner = owner,
                status = can.status.value,
                statusDetails = can.statusDetails.value,
                createdDate = dateTime.toDate(),
                jsonData = toJson(can)
        )
    }


}