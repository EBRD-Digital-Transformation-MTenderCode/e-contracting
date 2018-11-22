package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.dao.CanDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType.CANS_NOT_FOUND
import com.procurement.contracting.exception.ErrorType.CONTEXT
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
        val language = cm.context.language ?: throw ErrorException(CONTEXT)
        val mainProcurementCategory = cm.context.mainProcurementCategory ?: throw ErrorException(CONTEXT)
        val dateTime = cm.context.startDate?.toLocalDateTime() ?: throw ErrorException(CONTEXT)
        val dto = toObject(CreateAcRq::class.java, cm.data)

        val cans = ArrayList<Can>()
        val contractProcesses = ArrayList<ContractProcess>()
        val contracts = ArrayList<Contract>()
        val acEntities = ArrayList<AcEntity>()
        val canEntities = canDao.findAllByCpId(cpId)
        if (canEntities.isEmpty()) return ResponseDto(data = CreateAcRs(listOf(), listOf()))
        for (awardDto in dto.activeAwards) {
            val contract = Contract(
                    id = generationService.newOcId(cpId),
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
                    documents = null,
                    agreedMetrics = null,
                    confirmationRequests = null,
                    milestones = null)
            contracts.add(contract)

            val contractProcess = ContractProcess(
                    planning = null,
                    contract = contract,
                    award = convertAwardDtoToAward(awardDto),
                    buyer = null,
                    funders = null,
                    payers = null,
                    treasuryBudgetSources = null)
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

    private fun convertAwardDtoToAward(awardDto: AwardCreate): Award {
        return Award(
                id = awardDto.id,
                date = awardDto.date,
                description = awardDto.description,
                relatedBid = awardDto.relatedBid,
                relatedLots = awardDto.relatedLots,
                value = ValueTax(
                        amount = awardDto.value.amount,
                        currency = awardDto.value.currency,
                        amountNet = null,
                        valueAddedTaxIncluded = null),
                items = getItems(awardDto.items),
                documents = awardDto.documents,
                suppliers = awardDto.suppliers
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
        return Can(contract = Contract(
                id = entity.canId.toString(),
                token = null,
                date = entity.createdDate.toLocal(),
                awardId = entity.awardId,
                status = ContractStatus.fromValue(entity.status),
                statusDetails = ContractStatusDetails.fromValue(entity.statusDetails),
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
                documents = null,
                agreedMetrics = null,
                confirmationRequests = null,
                milestones = null)
        )
    }

    private fun convertContractToEntity(cpId: String,
                                        dateTime: LocalDateTime,
                                        language: String,
                                        mainProcurementCategory: String,
                                        contract: Contract,
                                        contractProcess: ContractProcess,
                                        canEntity: CanEntity): AcEntity {
        return AcEntity(
                cpId = cpId,
                acId = contract.id,
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
