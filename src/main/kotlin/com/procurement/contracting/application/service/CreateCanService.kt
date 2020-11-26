package com.procurement.contracting.application.service

import com.procurement.contracting.application.exception.repository.ReadEntityException
import com.procurement.contracting.application.exception.repository.SaveEntityException
import com.procurement.contracting.application.repository.can.CANRepository
import com.procurement.contracting.application.repository.can.model.CANEntity
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.util.extension.toSetBy
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.infrastructure.handler.v1.CommandMessage
import com.procurement.contracting.infrastructure.handler.v1.canId
import com.procurement.contracting.infrastructure.handler.v1.cpid
import com.procurement.contracting.infrastructure.handler.v1.lotId
import com.procurement.contracting.infrastructure.handler.v1.model.request.AwardDto
import com.procurement.contracting.infrastructure.handler.v1.model.request.CanGetAwards
import com.procurement.contracting.infrastructure.handler.v1.model.request.ConfirmationCan
import com.procurement.contracting.infrastructure.handler.v1.model.request.ConfirmationCanRs
import com.procurement.contracting.infrastructure.handler.v1.model.request.CreateCanRq
import com.procurement.contracting.infrastructure.handler.v1.model.request.CreateCanRs
import com.procurement.contracting.infrastructure.handler.v1.model.request.GetAwardsRq
import com.procurement.contracting.infrastructure.handler.v1.model.request.GetAwardsRs
import com.procurement.contracting.infrastructure.handler.v1.owner
import com.procurement.contracting.infrastructure.handler.v1.startDate
import com.procurement.contracting.infrastructure.handler.v1.token
import com.procurement.contracting.model.dto.ocds.Can
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CreateCanService(
    private val canRepository: CANRepository,
    private val generationService: GenerationService
) {

    fun createCan(cm: CommandMessage): CreateCanRs {
        val cpid = cm.cpid
        val owner = cm.owner
        val dateTime = cm.startDate
        val lotId: LotId = cm.lotId
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
            token = generationService.token(),
            date = dateTime,
            awardId = canAwardId,
            lotId = lotId,
            status = CANStatus.PENDING,
            statusDetails = statusDetails,
            documents = null,
            amendment = null)
        val canEntity = createCanEntity(cpid, owner, dateTime, can)
        val wasApplied = canRepository.saveNewCAN(cpid = cpid, entity = canEntity)
            .orThrow { it.exception }
        if (!wasApplied)
            throw SaveEntityException(message = "An error occurred when writing a record(s) of new CAN by cpid '${canEntity.cpid}' and lot id '${canEntity.lotId}' and award id '${canEntity.awardId}' to the database. Record is already.")
        return CreateCanRs(can)
    }

    fun checkCan(cm: CommandMessage) {
        val cpid = cm.cpid
        val lotId: LotId = cm.lotId

        val canEntities = canRepository.findBy(cpid)
            .orThrow {
                ReadEntityException(message = "Error read CAN(s) from the database.", cause = it.exception)
            }
        if (canEntities.any { it.lotId == lotId && it.status != CANStatus.CANCELLED }) {
            throw ErrorException(ErrorType.CAN_FOR_LOT_EXIST)
        }
    }

    fun checkCanByAwardId(cm: CommandMessage) {
        val cpid = cm.cpid
        val dto = toObject(AwardDto::class.java, cm.data)
        val canEntities = canRepository.findBy(cpid)
            .orThrow {
                ReadEntityException(message = "Error read CAN(s) from the database.", cause = it.exception)
            }
        if (canEntities.none {
                    it.awardId == dto.awardId
                            && it.status == CANStatus.PENDING
                }) {
            throw ErrorException(ErrorType.INVALID_CAN_STATUS)
        }
    }

    fun getCans(cm: CommandMessage): GetAwardsRs {
        val cpid = cm.cpid
        val dto = toObject(GetAwardsRq::class.java, cm.data)

        val canEntities = canRepository.findBy(cpid)
            .orThrow {
                ReadEntityException(message = "Error read CAN(s) from the database.", cause = it.exception)
            }
        val canIdsSet: Set<CANId> = dto.contracts.toSetBy { it.id }
        val canEntitiesFiltered = canEntities.filter { canIdsSet.contains(it.id) }
        val cansRs = ArrayList<CanGetAwards>()
        for (canEntity in canEntitiesFiltered) {
            if (canEntity.statusDetails != CANStatusDetails.UNSUCCESSFUL) {
                cansRs.add(CanGetAwards(id = canEntity.id, awardId = canEntity.awardId!!))
            } else {
                throw ErrorException(ErrorType.INVALID_CAN_STATUS)
            }
        }
        return GetAwardsRs(cansRs)
    }

    fun confirmationCan(cm: CommandMessage): ConfirmationCanRs {
        val cpid = cm.cpid
        val token = cm.token
        val owner = cm.owner
        val canId: CANId = cm.canId
        val canEntity = canRepository.findBy(cpid, canId)
            .orThrow {
                ReadEntityException(message = "Error read CAN from the database.", cause = it.exception)
            }
            ?: throw ErrorException(ErrorType.CAN_NOT_FOUND)
        if (canEntity.owner != owner) throw ErrorException(error = ErrorType.INVALID_OWNER)
        if (canEntity.token != token) throw ErrorException(ErrorType.INVALID_TOKEN)

        if (canEntity.status == CANStatus.PENDING && canEntity.statusDetails == CANStatusDetails.UNSUCCESSFUL) {
            val can = toObject(Can::class.java, canEntity.jsonData)
            can.status = CANStatus.UNSUCCESSFUL
            can.statusDetails = CANStatusDetails.EMPTY

            val updatedCANEntity = canEntity.copy(
                status = can.status,
                statusDetails = can.statusDetails,
                jsonData = toJson(can)
            )
            val wasApplied = canRepository.update(cpid = cpid, entity = updatedCANEntity)
                .orThrow { it.exception }
            if (!wasApplied)
                throw SaveEntityException(message = "An error occurred when writing a record(s) of CAN by cpid '$cpid' and id '${updatedCANEntity.id}' to the database. Record is already.")

            return ConfirmationCanRs(
                cans = listOf(ConfirmationCan(can.id, can.status, can.statusDetails)),
                lotId = can.lotId
            )
        } else
            throw ErrorException(ErrorType.INVALID_CAN_STATUS)
    }


    private fun createCanEntity(cpid: Cpid,
                                owner: Owner,
                                dateTime: LocalDateTime,
                                can: Can): CANEntity {
        return CANEntity(
                cpid = cpid,
                id = can.id,
                token = can.token,
                awardId = can.awardId,
                lotId = can.lotId,
                awardContractId = null,
                owner = owner,
                status = can.status,
                statusDetails = can.statusDetails,
                createdDate = dateTime,
                jsonData = toJson(can)
        )
    }
}
