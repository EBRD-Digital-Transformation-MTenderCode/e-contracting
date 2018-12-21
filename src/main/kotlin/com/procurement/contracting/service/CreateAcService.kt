package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.dao.CanDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.exception.ErrorType.CONTEXT
import com.procurement.contracting.model.dto.ContractProcess
import com.procurement.contracting.model.dto.CreateAcRq
import com.procurement.contracting.model.dto.CreateAcRs
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.ocds.*
import com.procurement.contracting.model.entity.AcEntity
import com.procurement.contracting.model.entity.CanEntity
import com.procurement.contracting.utils.toDate
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toLocalDateTime
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList

@Service
class CreateAcService(private val acDao: AcDao,
                      private val canDao: CanDao,
                      private val generationService: GenerationService) {

    fun createAC(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val language = cm.context.language ?: throw ErrorException(CONTEXT)
        val mainProcurementCategory = cm.context.mainProcurementCategory ?: throw ErrorException(CONTEXT)
        val dateTime = cm.context.startDate?.toLocalDateTime() ?: throw ErrorException(CONTEXT)
        val dto = toObject(CreateAcRq::class.java, cm.data)

        //VR-9.1.1
        if (dto.awards.asSequence().flatMap { it.suppliers.asSequence().map { s -> s.id } }.toSet().size > 1) {
            throw ErrorException(ErrorType.SUPPLIERS_ID)
        }
        //VR-9.1.2
        if (dto.awards.asSequence().map { it.value.currency }.toSet().size > 1) {
            throw ErrorException(ErrorType.AWARD_CURRENCY)
        }

        val canEntities = canDao.findAllByCpId(cpId)
        val updatedCanEntities = ArrayList<CanEntity>()
        val acId = generationService.newOcId(cpId)
        val cans = ArrayList<Can>()
        //BR-9.1.3
        val canIdsSet = dto.contracts.asSequence().map { it.id }.toSet()
        for (canEntity in canEntities) {
            if (canIdsSet.contains(canEntity.canId.toString())) {
                if (canEntity.status != ContractStatus.PENDING.value && canEntity.statusDetails != ContractStatusDetails.CONTRACT_PROJECT.value) {
                    throw ErrorException(ErrorType.CAN_ALREADY_USED)
                }
                val can = toObject(Can::class.java, canEntity.jsonData)
                can.status = ContractStatus.ACTIVE
                can.statusDetails = ContractStatusDetails.EMPTY
                canEntity.status = can.status.value
                canEntity.statusDetails = can.statusDetails.value
                canEntity.acId = acId
                canEntity.jsonData = toJson(can)
                updatedCanEntities.add(canEntity)
                cans.add(can)
            }
        }
        updatedCanEntities.asSequence().forEach{canDao.save(it)}

        val awardId = generationService.generateRandomUUID().toString()
        val awardsIdsSet = dto.awards.asSequence().map { it.id }.toSet()
        val awardsLotsIdsSet = dto.awards.asSequence().map { it.relatedLots[0] }.toSet()
        val amountSum = dto.awards.asSequence()
                .sumByDouble { it.value.amount!!.toDouble() }
                .toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        val awardDocuments = ArrayList<DocumentAward>()
        dto.awards.forEach {
            if (it.documents != null && it.documents!!.isNotEmpty())
                awardDocuments.addAll(it.documents!!)
        }

        val contract = Contract(
                id = acId,
                token = generationService.generateRandomUUID().toString(),
                awardId = awardId,
                status = ContractStatus.PENDING,
                statusDetails = ContractStatusDetails.CONTRACT_PROJECT)

        val contractedAward = Award(
                id = awardId,
                date = dateTime,
                relatedLots = awardsLotsIdsSet.toList(),
                relatedAwards = awardsIdsSet.toList(),
                value = ValueTax(
                        amount = amountSum,
                        currency = dto.awards[0].value.currency),
                items = dto.contractedTender.items,
                documents = awardDocuments,
                suppliers = dto.awards[0].suppliers)

        val contractProcess = ContractProcess(
                contract = contract,
                award = contractedAward)

        val acEntity = AcEntity(
                cpId = cpId,
                acId = contract.id,
                token = UUID.fromString(contract.token!!),
                owner = owner,
                createdDate = dateTime.toDate(),
                status = contract.status.value,
                statusDetails = contract.statusDetails.value,
                mainProcurementCategory = mainProcurementCategory,
                language = language,
                jsonData = toJson(contractProcess))

        acDao.save(acEntity)

        return ResponseDto(data = CreateAcRs(cans = cans, contract = contract, contractedAward = contractedAward))
    }
}
