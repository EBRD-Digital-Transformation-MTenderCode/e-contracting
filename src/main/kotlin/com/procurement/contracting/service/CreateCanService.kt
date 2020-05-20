package com.procurement.contracting.service

import com.procurement.contracting.dao.CanDao
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.exception.ErrorType.CONTEXT
import com.procurement.contracting.model.dto.AwardDto
import com.procurement.contracting.model.dto.CanGetAwards
import com.procurement.contracting.model.dto.ConfirmationCan
import com.procurement.contracting.model.dto.ConfirmationCanRs
import com.procurement.contracting.model.dto.CreateCanRq
import com.procurement.contracting.model.dto.CreateCanRs
import com.procurement.contracting.model.dto.GetAwardsRq
import com.procurement.contracting.model.dto.GetAwardsRs
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.ocds.Can
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

    fun createCan(cm: CommandMessage): CreateCanRs {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val dateTime = cm.context.startDate?.toLocalDateTime() ?: throw ErrorException(CONTEXT)
        val lotId: LotId = cm.context.id?.let{ UUID.fromString(it) } ?: throw ErrorException(CONTEXT)
        val dto = toObject(CreateCanRq::class.java, cm.data)

        val statusDetails: CANStatusDetails
        var canAwardId: AwardId? = null
        if (dto.awardingSuccess) {
            statusDetails = CANStatusDetails.CONTRACT_PROJECT
            canAwardId = dto.awardId
        } else {
            statusDetails = CANStatusDetails.UNSUCCESSFUL
        }
        val can = Can(
            id = generationService.canId(),
            token = generationService.generateRandomUUID().toString(),
            date = dateTime,
            awardId = canAwardId,
            lotId = lotId,
            status = CANStatus.PENDING,
            statusDetails = statusDetails,
            documents = null,
            amendment = null)
        val canEntity = createCanEntity(cpId, owner, dateTime, can)
        canDao.save(canEntity)
        return CreateCanRs(can)
    }

    fun checkCan(cm: CommandMessage) {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val lotId: LotId = cm.context.id?.let{ UUID.fromString(it) } ?: throw ErrorException(CONTEXT)

        val canEntities = canDao.findAllByCpId(cpId)
        if (canEntities.asSequence().any { it.lotId == lotId && it.status != CANStatus.CANCELLED }) {
            throw ErrorException(ErrorType.CAN_FOR_LOT_EXIST)
        }
    }

    fun checkCanByAwardId(cm: CommandMessage) {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val dto = toObject(AwardDto::class.java, cm.data)
        val canEntities = canDao.findAllByCpId(cpId)
        if (canEntities.asSequence().none {
                    it.awardId == dto.awardId
                            && it.status == CANStatus.PENDING
                }) {
            throw ErrorException(ErrorType.INVALID_CAN_STATUS)
        }
    }

    fun getCans(cm: CommandMessage): GetAwardsRs {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val dto = toObject(GetAwardsRq::class.java, cm.data)

        val canEntities = canDao.findAllByCpId(cpId)
        val canIdsSet: Set<CANId> = dto.contracts.asSequence().map { it.id }.toSet()
        val canEntitiesFiltered = canEntities.asSequence().filter { canIdsSet.contains(it.canId) }.toList()
        val cansRs = ArrayList<CanGetAwards>()
        for (canEntity in canEntitiesFiltered) {
            if (canEntity.statusDetails != CANStatusDetails.UNSUCCESSFUL) {
                cansRs.add(CanGetAwards(id = canEntity.canId, awardId = canEntity.awardId!!))
            } else {
                throw ErrorException(ErrorType.INVALID_CAN_STATUS)
            }
        }
        return GetAwardsRs(cansRs)
    }

    fun confirmationCan(cm: CommandMessage): ConfirmationCanRs {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val token = cm.context.token ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val canId: CANId = cm.context.id?.let{ UUID.fromString(it) } ?: throw ErrorException(CONTEXT)
        val canEntity = canDao.getByCpIdAndCanId(cpId, canId)
        if (canEntity.owner != owner) throw ErrorException(error = ErrorType.INVALID_OWNER)
        if (canEntity.token.toString() != token) throw ErrorException(ErrorType.INVALID_TOKEN)

        if (canEntity.status == CANStatus.PENDING && canEntity.statusDetails == CANStatusDetails.UNSUCCESSFUL) {
            val can = toObject(Can::class.java, canEntity.jsonData)
            can.status = CANStatus.UNSUCCESSFUL
            can.statusDetails = CANStatusDetails.EMPTY

            canEntity.status = can.status
            canEntity.statusDetails = can.statusDetails
            canEntity.jsonData = toJson(can)
            canDao.save(canEntity)
            return ConfirmationCanRs(
                cans = listOf(ConfirmationCan(can.id, can.status, can.statusDetails)),
                lotId = can.lotId
            )

        } else
            throw ErrorException(ErrorType.INVALID_CAN_STATUS)
    }


    private fun createCanEntity(cpId: String,
                                owner: String,
                                dateTime: LocalDateTime,
                                can: Can): CanEntity {
        return CanEntity(
                cpId = cpId,
                canId = can.id,
                token = UUID.fromString(can.token),
                awardId = can.awardId,
                lotId = can.lotId,
                acId = null,
                owner = owner,
                status = can.status,
                statusDetails = can.statusDetails,
                createdDate = dateTime.toDate(),
                jsonData = toJson(can)
        )
    }


}