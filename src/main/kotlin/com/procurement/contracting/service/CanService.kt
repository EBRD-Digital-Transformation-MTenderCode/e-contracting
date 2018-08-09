package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.dao.CanDao
import com.procurement.contracting.model.dto.CreateCanRQ
import com.procurement.contracting.model.dto.CreateCanRS
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.ocds.*
import com.procurement.contracting.model.entity.CanEntity
import com.procurement.contracting.utils.localNowUTC
import com.procurement.contracting.utils.toDate
import org.springframework.stereotype.Service

interface CanService {

    fun createCAN(cpId: String, stage: String, owner: String, dto: CreateCanRQ): ResponseDto

//    fun checkCAN(cpId: String, token: String, idPlatform: String): ResponseDto<*>
//
//    fun changeStatus(cpId: String, awardId: String): ResponseDto<*>
}

@Service
class CanServiceImpl(private val canDao: CanDao,
                     private val acDao: AcDao,
                     private val generationService: GenerationService) : CanService {

    override fun createCAN(cpId: String, stage: String, owner: String, dto: CreateCanRQ): ResponseDto {
        val canEntities = createCANEntities(cpId, stage, owner, dto)
        val cans = convertEntitiesToDtoList(canEntities)
        canDao.saveAll(canEntities)
        return ResponseDto(true, null, CreateCanRS(cans))
    }

    private fun createCANEntities(cpId: String, stage: String, owner: String, dto: CreateCanRQ): List<CanEntity> {
        return dto.awards.asSequence()
                .filter { it.status == AwardStatus.ACTIVE }
                .map { createCanEntity(cpId, stage, it.id, owner) }
                .toList()
    }

    private fun convertEntitiesToDtoList(canEntities: List<CanEntity>): List<Can> {
        return canEntities.asSequence().map { convertEntityToCanDto(it) }.toList()
    }

    private fun convertEntityToCanDto(entity: CanEntity): Can {
        val contract = Contract(
                token = entity.token.toString(),
                id = entity.token.toString(),
                date = localNowUTC(),
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
                                owner: String): CanEntity {
        return CanEntity(
                cpId = cpId,
                stage = stage,
                token = generationService.generateRandomUUID(),
                awardId = awardId,
                acId = null,
                owner = owner,
                status = ContractStatus.PENDING.value(),
                statusDetails = ContractStatusDetails.CONTRACT_PROJECT.value(),
                createdDate = localNowUTC().toDate()
        )
    }
}
