package com.procurement.contracting.application.service

import com.procurement.contracting.application.repository.model.AcEntity
import com.procurement.contracting.application.repository.model.CanEntity
import com.procurement.contracting.application.repository.model.ContractProcess
import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.dao.CanDao
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.contract.status.ContractStatus
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.exception.ErrorType.CANS_NOT_FOUND
import com.procurement.contracting.exception.ErrorType.CONTEXT
import com.procurement.contracting.infrastructure.handler.v1.CommandMessage
import com.procurement.contracting.infrastructure.handler.v1.model.request.CreateAcRq
import com.procurement.contracting.infrastructure.handler.v1.model.request.CreateAcRs
import com.procurement.contracting.model.dto.ocds.Can
import com.procurement.contracting.model.dto.ocds.Contract
import com.procurement.contracting.model.dto.ocds.ContractedAward
import com.procurement.contracting.model.dto.ocds.DocumentAward
import com.procurement.contracting.model.dto.ocds.ValueTax
import com.procurement.contracting.utils.toDate
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toLocalDateTime
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

@Service
class CreateAcService(
    private val acDao: AcDao,
    private val canDao: CanDao,
    private val generationService: GenerationService
) {

    @Deprecated(message = "Use method create in ACService.", level = DeprecationLevel.ERROR)
    fun createAC(cm: CommandMessage): CreateAcRs {
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

        val idsOfCANs: Set<CANId> = dto.contracts.fold(initial = HashSet()) { acc, item ->
            if(acc.add(item.id))
                acc
            else
                throw ErrorException(ErrorType.DUPLICATE_CAN_ID)
        }
        val canEntities = canDao.findAllByCpId(cpId)
        val canEntityIds: Set<CANId> = canEntities.fold(initial = HashSet()) { acc, item ->
            acc.add(item.canId)
            acc
        }
        val isValidCANIds = idsOfCANs.all { canEntityIds.contains(it) }
        if (!isValidCANIds) throw ErrorException(CANS_NOT_FOUND)

        val updatedCanEntities = ArrayList<CanEntity>()
        val acId = generationService.contractId(cpId)
        val cans = ArrayList<Can>()
        //BR-9.1.3

        for (canEntity in canEntities) {
            if (idsOfCANs.contains(canEntity.canId)) {
                if (!(canEntity.status == CANStatus.PENDING && canEntity.statusDetails == CANStatusDetails.CONTRACT_PROJECT)) {
                    throw ErrorException(ErrorType.CAN_ALREADY_USED)
                }
                val can = toObject(Can::class.java, canEntity.jsonData)
                can.statusDetails = CANStatusDetails.ACTIVE
                canEntity.statusDetails = can.statusDetails
                canEntity.acId = acId
                canEntity.jsonData = toJson(can)
                updatedCanEntities.add(canEntity)
                cans.add(can)
            }
        }
        updatedCanEntities.asSequence().forEach { canDao.save(it) }

        val awardId = generationService.awardId()
        val awardsIdsSet = dto.awards.asSequence().map { it.id }.toSet()
        val awardsLotsIdsSet = dto.awards.asSequence().map { it.relatedLots[0] }.toSet()
        val awardsBidsIdsSet = dto.awards.asSequence().map { it.relatedBid }.toSet()
        val amountSum = dto.awards.asSequence()
            .sumByDouble { it.value.amount.toDouble() }
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
            statusDetails = ContractStatusDetails.CONTRACT_PROJECT
        )

        val contractedAward = ContractedAward(
            id = awardId,
            date = dateTime,
            relatedLots = awardsLotsIdsSet.toList(),
            relatedBids = awardsBidsIdsSet.toList(),
            relatedAwards = awardsIdsSet.toList(),
            value = ValueTax(
                amount = amountSum,
                currency = dto.awards[0].value.currency
            ),
            items = dto.contractedTender.items.toList(),
            documents = if (awardDocuments.isNotEmpty()) awardDocuments else null,
            suppliers = dto.awards[0].suppliers
        )

        val contractProcess = ContractProcess(
            contract = contract,
            award = contractedAward
        )

        val acEntity = AcEntity(
            cpId = cpId,
            acId = contract.id,
            token = UUID.fromString(contract.token!!),
            owner = owner,
            createdDate = dateTime.toDate(),
            status = contract.status,
            statusDetails = contract.statusDetails,
            mainProcurementCategory = mainProcurementCategory,
            language = language,
            jsonData = toJson(contractProcess)
        )

        acDao.save(acEntity)

        return CreateAcRs(cans = cans, contract = contract, contractedAward = contractedAward)
    }
}
