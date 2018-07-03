package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.dao.CanDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.model.dto.CreateContractRQ
import com.procurement.contracting.model.dto.CreateContractRS
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.ocds.*
import com.procurement.contracting.model.entity.AcEntity
import com.procurement.contracting.model.entity.CanEntity
import com.procurement.contracting.utils.localNowUTC
import com.procurement.contracting.utils.toDate
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toLocal
import org.springframework.stereotype.Service
import java.util.*

interface ACService {

    fun createAC(cpId: String, stage: String, dto: CreateContractRQ): ResponseDto

//    fun updateAC(cpId: String, token: String, platformId: String, updateACRQ: UpdateACRQ): ResponseDto<*>
//
//    fun changeStatus(cpId: String, token: String, platformId: String, changeStatusRQ: ChangeStatusRQ): ResponseDto<*>
//
//    fun checkStatus(cpId: String, token: String): ResponseDto<*>
}

@Service
class ACServiceImpl(private val acDao: AcDao,
                    private val canDao: CanDao,
                    private val generationService: GenerationService) : ACService {

    override fun createAC(cpId: String, stage: String, dto: CreateContractRQ): ResponseDto {
        val cans = ArrayList<Can>()
        val contracts = ArrayList<Contract>()
        val acEntities = ArrayList<AcEntity>()
        val canEntities = canDao.findAllByCpIdAndStage(cpId, stage)
        val activeAwards = getActiveAwards(dto.awards)
        for (award in activeAwards) {
            val lotComplete = getCompletedLot(dto.lots, award)
            val items = getItemsForRelatedLot(dto.items, award)
            val contract = createContract(award, lotComplete, items)
            contracts.add(contract)
            val canEntity = canEntities.asSequence()
                    .filter { it.awardId == award.id }.firstOrNull()
                    ?: throw ErrorException(ErrorType.CANS_NOT_FOUND)
            canEntity.status = ContractStatus.ACTIVE.value()
            canEntity.statusDetails = ContractStatusDetails.EMPTY.value()
            canEntity.acId = contract.id
            cans.add(convertEntityToCanDto(canEntity))
            acEntities.add(convertContractToEntity(cpId, stage, contract, canEntity))
        }
        canDao.saveAll(canEntities)
        acDao.saveAll(acEntities)
        return ResponseDto(true, null, CreateContractRS(cans, contracts))
    }

    private fun getActiveAwards(awards: List<Award>): List<Award> {
        if (awards.isEmpty()) throw ErrorException(ErrorType.NO_ACTIVE_AWARDS)
        val activeAwards = awards.asSequence().filter { it.status == AwardStatus.ACTIVE }.toList()
        if (activeAwards.isEmpty()) throw ErrorException(ErrorType.NO_ACTIVE_AWARDS)
        return activeAwards
    }

    private fun getCompletedLot(lots: List<Lot>, award: Award): Lot {
        if (lots.isEmpty()) throw ErrorException(ErrorType.NO_COMPLETED_LOT)
        val awardRelatedLot = award.relatedLots?.get(0)
        return lots.asSequence()
                .filter { it.id == awardRelatedLot }
                .firstOrNull()
                ?: throw ErrorException(ErrorType.NO_COMPLETED_LOT)
    }

    private fun getItemsForRelatedLot(items: List<Item>, award: Award): List<Item> {
        if (items.isEmpty()) throw ErrorException(ErrorType.NO_ITEMS)
        val awardRelatedLot = award.relatedLots?.get(0)
        val filteredItems = items.asSequence().filter { it.relatedLot == awardRelatedLot }.toList()
        if (filteredItems.isEmpty()) throw ErrorException(ErrorType.NO_ITEMS)
        return filteredItems
    }

    private fun convertEntityToCanDto(entity: CanEntity): Can {
        val contract = Contract(
                token = entity.token.toString(),
                id = entity.token.toString(),
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
        return Can(contract.token!!, contract)
    }

    private fun convertContractToEntity(cpId: String,
                                        stage: String,
                                        contract: Contract,
                                        canEntity: CanEntity): AcEntity {
        return AcEntity(
                cpId = cpId,
                stage = stage,
                token = UUID.fromString(contract.token!!),
                owner = canEntity.owner,
                createdDate = localNowUTC().toDate(),
                canId = canEntity.token.toString(),
                status = contract.status.value(),
                statusDetails = contract.statusDetails.value(),
                jsonData = toJson(contract))
    }

    private fun createContract(award: Award, lotComplete: Lot, items: List<Item>): Contract {
        return Contract(
                id = generationService.generateTimeBasedUUID().toString(),
                token = generationService.generateRandomUUID().toString(),
                date = localNowUTC(),
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
