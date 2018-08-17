package com.procurement.contracting.service

import com.procurement.contracting.dao.CanDao
import com.procurement.contracting.model.dto.CreateCanRQ
import com.procurement.contracting.model.dto.CreateCanRS
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.ocds.*
import com.procurement.contracting.model.entity.CanEntity
import com.procurement.contracting.utils.toDate
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

interface CanService {

    fun createCAN(cpId: String, stage: String, owner: String, dateTime: LocalDateTime, dto: CreateCanRQ): ResponseDto

//    fun checkCAN(cpId: String, token: String, idPlatform: String): ResponseDto<*>
//
//    fun changeStatus(cpId: String, awardId: String): ResponseDto<*>
}

@Service
class CanServiceImpl(private val canDao: CanDao,
                     private val generationService: GenerationService) : CanService {

    override fun createCAN(cpId: String, stage: String, owner: String, dateTime: LocalDateTime, dto: CreateCanRQ): ResponseDto {
        val canEntities = createCANEntities(cpId, stage, owner, dateTime, dto)
        val cans = convertEntitiesToDtoList(canEntities, dateTime)
        canDao.saveAll(canEntities)
        return ResponseDto(true, null, CreateCanRS(cans))
    }

    private fun createCANEntities(cpId: String, stage: String, owner: String, dateTime: LocalDateTime, dto: CreateCanRQ): List<CanEntity> {
        return dto.awards.asSequence()
                .filter { it.statusDetails == AwardStatus.ACTIVE }
                .map { createCanEntity(cpId, stage, it.id, owner, dateTime) }
                .toList()
    }

    private fun convertEntitiesToDtoList(canEntities: List<CanEntity>, dateTime: LocalDateTime): List<Can> {
        return canEntities.asSequence().map { convertEntityToCanDto(it, dateTime) }.toList()
    }

    private fun convertEntityToCanDto(entity: CanEntity, dateTime: LocalDateTime): Can {
        val contract = Contract(
                token = entity.token.toString(),
                id = UUID.randomUUID().toString(),
                date = dateTime,
                awardId = entity.awardId,
                status = ContractStatus.fromValue(entity.status),
                statusDetails = ContractStatusDetails.fromValue(entity.statusDetails),
                documents = null,
                classification = null,
                relatedProcesses = null,
                amendments = null,
                budgetSource = null,
                dateSigned = null,
                extendsContractID = null,
                period = null,
                items = null,
                value = null,
                description = null,
                title = null)
        return Can(contract.token!!, contract)
    }

    private fun createCanEntity(cpId: String,
                                stage: String,
                                awardId: String,
                                owner: String,
                                dateTime: LocalDateTime): CanEntity {
        return CanEntity(
                cpId = cpId,
                stage = stage,
                token = generationService.generateRandomUUID(),
                awardId = awardId,
                acId = null,
                owner = owner,
                status = ContractStatus.PENDING.value(),
                statusDetails = ContractStatusDetails.CONTRACT_PROJECT.value(),
                createdDate = dateTime.toDate()
        )
    }
}
