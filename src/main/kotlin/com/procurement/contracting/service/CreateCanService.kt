package com.procurement.contracting.service

import com.procurement.contracting.dao.CanDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.exception.ErrorType.CONTEXT
import com.procurement.contracting.model.dto.CanCreate
import com.procurement.contracting.model.dto.CreateCanRs
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
        val dto = toObject(CanCreate::class.java, cm.data)

        val can = createCan(dto.award.id, lotId, dateTime)
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

    private fun createCan(awardId: String, lotId: String, dateTime: LocalDateTime): Can {
        return Can(
                id = generationService.generateRandomUUID().toString(),
                token = generationService.generateRandomUUID().toString(),
                date = dateTime,
                awardId = awardId,
                lotId = lotId,
                status = ContractStatus.PENDING,
                statusDetails = ContractStatusDetails.CONTRACT_PROJECT,
                documents = null,
                amendment = null)
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