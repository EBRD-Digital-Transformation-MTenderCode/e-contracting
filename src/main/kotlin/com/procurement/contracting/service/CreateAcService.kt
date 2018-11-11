package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.dao.CanDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType.*
import com.procurement.contracting.model.dto.*
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
class CreateAcService(private val acDao: AcDao,
                      private val canDao: CanDao,
                      private val generationService: GenerationService) {

    fun createAC(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val prevStage = cm.context.prevStage ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val language = cm.context.language ?: throw ErrorException(CONTEXT)
        val mainProcurementCategory = cm.context.mainProcurementCategory ?: throw ErrorException(CONTEXT)
        val dateTime = cm.context.startDate?.toLocalDateTime() ?: throw ErrorException(CONTEXT)
        val dto = toObject(CreateAcRq::class.java, cm.data)

        val cans = ArrayList<Can>()
        val contractProcesses = ArrayList<ContractProcess>()
        val contracts = ArrayList<Contract>()
        val acEntities = ArrayList<AcEntity>()
        val canEntities = canDao.findAllByCpIdAndStage(cpId, prevStage)
        if (canEntities.isEmpty()) return ResponseDto(data = CreateAcRs(listOf(), listOf()))
        val activeAwardsDto = getActiveAwards(dto.awards)
        for (awardDto in activeAwardsDto) {
            val contract = Contract(
                    id = generationService.newOcId(cpId, stage),
                    token = generationService.generateRandomUUID().toString(),
                    date = dateTime,
                    awardId = awardDto.id,
                    status = ContractStatus.PENDING,
                    statusDetails = ContractStatusDetails.CONTRACT_PROJECT,
                    title = null,
                    description = null,
                    value = null,
                    items = null,
                    period = null,
                    extendsContractID = null,
                    dateSigned = null,
                    budgetSource = null,
                    amendments = null,
                    relatedProcesses = null,
                    classification = null,
                    documents = null)
            contracts.add(contract)

            val contractProcess = ContractProcess(
                    planning = null,
                    contracts = contract,
                    awards = convertAwardDtoToAward(awardDto),
                    buyer = null)
            contractProcesses.add(contractProcess)

            val canEntity = canEntities.asSequence().filter { it.awardId == awardDto.id }.firstOrNull()
                    ?: throw ErrorException(CANS_NOT_FOUND)
            canEntity.status = ContractStatus.ACTIVE.value
            canEntity.statusDetails = ContractStatusDetails.EMPTY.value
            canEntity.acId = contract.id
            val can = convertEntityToCanDto(canEntity)
            cans.add(can)

            val acEntity = convertContractToEntity(
                    cpId,
                    stage,
                    dateTime,
                    language,
                    mainProcurementCategory,
                    contract,
                    contractProcess,
                    canEntity)
            acEntities.add(acEntity)
        }
        canDao.saveAll(canEntities)
        acDao.saveAll(acEntities)
        return ResponseDto(data = CreateAcRs(cans, contracts))
    }


    private fun getActiveAwards(awards: List<AwardCreate>): List<AwardCreate> {
        if (awards.isEmpty()) throw ErrorException(NO_ACTIVE_AWARDS)
        val activeAwards = awards.asSequence().filter { it.status == AwardStatus.ACTIVE }.toList()
        if (activeAwards.isEmpty()) throw ErrorException(NO_ACTIVE_AWARDS)
        return activeAwards
    }

    private fun convertAwardDtoToAward(awardDto: AwardCreate): Award {
        return Award(
                id = awardDto.id,
                status = awardDto.status,
                statusDetails = awardDto.statusDetails,
                date = awardDto.date,
                description = awardDto.description,
                value = ValueAward(
                        amount = awardDto.value.amount,
                        currency = awardDto.value.currency,
                        amountNet = null,
                        valueAddedTaxIncluded = null),
                documents = awardDto.documents,
                items = getItems(awardDto.items),
                suppliers = awardDto.suppliers,
                relatedBid = awardDto.relatedBid,
                relatedLots = awardDto.relatedLots
        )
    }

    private fun getItems(items: List<ItemCreate>): List<Item> {
        return items.asSequence().map { convertDtoItemToItem(it) }.toList()
    }

    private fun convertDtoItemToItem(itemDto: ItemCreate): Item {
        return Item(
                id = itemDto.id,
                description = itemDto.description,
                classification = itemDto.classification,
                additionalClassifications = itemDto.additionalClassifications,
                quantity = itemDto.quantity,
                unit = itemDto.unit,
                relatedLot = itemDto.relatedLot,
                deliveryAddress = null
        )
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
                                        dateTime: LocalDateTime,
                                        language: String,
                                        mainProcurementCategory: String,
                                        contract: Contract,
                                        contractProcess: ContractProcess,
                                        canEntity: CanEntity): AcEntity {
        return AcEntity(
                cpId = cpId,
                stage = stage,
                token = UUID.fromString(contract.token!!),
                owner = canEntity.owner,
                createdDate = dateTime.toDate(),
                canId = canEntity.canId.toString(),
                status = contract.status.value,
                statusDetails = contract.statusDetails.value,
                mainProcurementCategory = mainProcurementCategory,
                language = language,
                jsonData = toJson(contractProcess))
    }
}
