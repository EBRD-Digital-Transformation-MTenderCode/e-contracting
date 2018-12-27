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

@Service
class CreateCanService(private val canDao: CanDao,
                       private val generationService: GenerationService) {

    fun createCan(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val dateTime = cm.context.startDate?.toLocalDateTime() ?: throw ErrorException(CONTEXT)
        val lotId = cm.context.id ?: throw ErrorException(CONTEXT)
        val dto = toObject(AwardDto::class.java, cm.data)

        val can = Can(
                id = generationService.generateRandomUUID().toString(),
                token = generationService.generateRandomUUID().toString(),
                date = dateTime,
                awardId = dto.awardId,
                lotId = lotId,
                status = ContractStatus.PENDING,
                statusDetails = ContractStatusDetails.CONTRACT_PROJECT,
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
            throw ErrorException(ErrorType.CAN_STATUS)
        }
        return ResponseDto(data = "ok")
    }

    fun getCans(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val dto = toObject(GetAwardsRq::class.java, cm.data)

        val canEntities = canDao.findAllByCpId(cpId)
        val canIdsSet = dto.contracts.asSequence().map { it.id }.toSet()
        val cans = canEntities.asSequence()
                .filter { canIdsSet.contains(it.canId.toString()) }
                .map { CanGetAwards(id = it.canId.toString(), awardId = it.awardId) }
                .toList()
        return ResponseDto(data = GetAwardsRs(cans))
    }


    fun confirmationCan(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val token = cm.context.token ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val canId = cm.context.id ?: throw ErrorException(CONTEXT)
        val canEntity = canDao.getByCpIdAndCanId(cpId, UUID.fromString(canId))
        if (canEntity.owner != owner) throw ErrorException(ErrorType.OWNER)
        if (canEntity.token.toString() != token) throw ErrorException(ErrorType.INVALID_TOKEN)
        if (canEntity.status != ContractStatus.PENDING.value && canEntity.statusDetails != ContractStatusDetails.UNSUCCESSFUL.value)
            throw ErrorException(ErrorType.CAN_STATUS)
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