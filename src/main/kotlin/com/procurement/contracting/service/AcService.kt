package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.dao.CanDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType.*
import com.procurement.contracting.model.dto.CreateContractRQ
import com.procurement.contracting.model.dto.CreateContractRS
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.ocds.*
import com.procurement.contracting.model.entity.AcEntity
import com.procurement.contracting.model.entity.CanEntity
import com.procurement.contracting.utils.*
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class AcService(private val acDao: AcDao,
                private val canDao: CanDao,
                private val generationService: GenerationService) {

    fun createAC(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val dateTime = cm.context.startDate?.toLocalDateTime() ?: throw ErrorException(CONTEXT)
        val dto = toObject(CreateContractRQ::class.java, cm.data)

        val cans = ArrayList<Can>()
        val contracts = ArrayList<Contract>()
        val acEntities = ArrayList<AcEntity>()
        val canEntities = canDao.findAllByCpIdAndStage(cpId, stage)
        if (canEntities.isEmpty()) return ResponseDto(data = CreateContractRS(listOf(), listOf()))

        for (award in dto.awards) {
            val lotComplete = getCompletedLot(dto.lots, award)
            val items = getItemsForRelatedLot(dto.items, award)
            val contract = createContract(
                    cpId = cpId,
                    stage = "AC",
                    award = award,
                    lotComplete = lotComplete,
                    items = items,
                    dateTime = dateTime)
            contracts.add(contract)
            val canEntity = canEntities.asSequence().filter { it.awardId == award.id }.firstOrNull()
                    ?: throw ErrorException(CANS_NOT_FOUND)
            canEntity.status = ContractStatus.ACTIVE.value()
            canEntity.statusDetails = ContractStatusDetails.EMPTY.value()
            canEntity.acId = contract.id
            cans.add(convertEntityToCanDto(canEntity))
            acEntities.add(convertContractToEntity(cpId, stage, contract, dateTime, canEntity))
        }
        canDao.saveAll(canEntities)
        acDao.saveAll(acEntities)
        return ResponseDto(data = CreateContractRS(cans, contracts))
    }

    private fun getCompletedLot(lots: List<Lot>, award: Award): Lot {
        if (lots.isEmpty()) throw ErrorException(NO_COMPLETED_LOT)
        val awardRelatedLot = award.relatedLots?.get(0)
        return lots.asSequence().filter { it.id == awardRelatedLot }.firstOrNull()
                ?: throw ErrorException(NO_COMPLETED_LOT)
    }

    private fun getItemsForRelatedLot(items: List<Item>, award: Award): List<Item> {
        if (items.isEmpty()) throw ErrorException(NO_ITEMS)
        val awardRelatedLot = award.relatedLots?.get(0)
        val filteredItems = items.asSequence().filter { it.relatedLot == awardRelatedLot }.toList()
        if (filteredItems.isEmpty()) throw ErrorException(NO_ITEMS)
        return filteredItems
    }

    private fun convertEntityToCanDto(entity: CanEntity): Can {
        val contract = Contract(
                token = null,
                id = entity.canId.toString(),
                date = entity.createdDate.toLocal(),
                awardId = entity.awardId,
                status = ContractStatus.fromValue(entity.status),
                statusDetails = ContractStatusDetails.fromValue(entity.statusDetails),
                documents = null,
                description = null,
                value = null,
                title = null,
                items = null,
                classification = null,
                relatedProcesses = null,
                amendments = null,
                budgetSource = null,
                dateSigned = null,
                extendsContractID = null,
                period = null)
        return Can(contract)
    }

    private fun convertContractToEntity(cpId: String,
                                        stage: String,
                                        contract: Contract,
                                        dateTime: LocalDateTime,
                                        canEntity: CanEntity): AcEntity {
        return AcEntity(
                cpId = cpId,
                stage = stage,
                token = UUID.fromString(contract.token!!),
                owner = canEntity.owner,
                createdDate = dateTime.toDate(),
                canId = canEntity.canId.toString(),
                status = contract.status.value(),
                statusDetails = contract.statusDetails.value(),
                jsonData = toJson(contract))
    }

    private fun createContract(cpId: String,
                               stage: String,
                               award: Award,
                               lotComplete: Lot,
                               items: List<Item>,
                               dateTime: LocalDateTime): Contract {
        return Contract(
                id = generationService.newOcId(cpId, stage),
                token = generationService.generateRandomUUID().toString(),
                date = dateTime,
                awardId = award.id,
                status = ContractStatus.PENDING,
                statusDetails = ContractStatusDetails.CONTRACT_PROJECT,
                title = lotComplete.title,
                description = lotComplete.description,
                value = award.value,
                items = items,
                period = null,
                extendsContractID = null,
                dateSigned = null,
                budgetSource = null,
                amendments = null,
                relatedProcesses = null,
                classification = null,
                documents = null)
    }
}
