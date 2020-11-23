package com.procurement.contracting.application.service

import com.procurement.contracting.application.exception.repository.ReadEntityException
import com.procurement.contracting.application.exception.repository.SaveEntityException
import com.procurement.contracting.application.repository.ac.AwardContractRepository
import com.procurement.contracting.application.repository.ac.model.AwardContractEntity
import com.procurement.contracting.application.repository.can.CANRepository
import com.procurement.contracting.application.repository.model.ContractProcess
import com.procurement.contracting.domain.entity.CANEntity
import com.procurement.contracting.domain.model.ac.status.AwardContractStatus
import com.procurement.contracting.domain.model.ac.status.AwardContractStatusDetails
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.exception.ErrorType.CANS_NOT_FOUND
import com.procurement.contracting.infrastructure.handler.v1.CommandMessage
import com.procurement.contracting.infrastructure.handler.v1.cpid
import com.procurement.contracting.infrastructure.handler.v1.language
import com.procurement.contracting.infrastructure.handler.v1.mainProcurementCategory
import com.procurement.contracting.infrastructure.handler.v1.model.request.CreateAcRq
import com.procurement.contracting.infrastructure.handler.v1.model.request.CreateAcRs
import com.procurement.contracting.infrastructure.handler.v1.owner
import com.procurement.contracting.infrastructure.handler.v1.startDate
import com.procurement.contracting.model.dto.ocds.AwardContract
import com.procurement.contracting.model.dto.ocds.Can
import com.procurement.contracting.model.dto.ocds.ContractedAward
import com.procurement.contracting.model.dto.ocds.DocumentAward
import com.procurement.contracting.model.dto.ocds.ValueTax
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service
import java.math.RoundingMode

@Service
class CreateAwardContractService(
    private val acRepository: AwardContractRepository,
    private val canRepository: CANRepository,
    private val generationService: GenerationService
) {

    @Deprecated(message = "Use method create in ACService.", level = DeprecationLevel.ERROR)
    fun createAC(cm: CommandMessage): CreateAcRs {
        val cpid = cm.cpid
        val owner = cm.owner
        val language = cm.language
        val mainProcurementCategory = cm.mainProcurementCategory
        val dateTime = cm.startDate
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
            if (acc.add(item.id))
                acc
            else
                throw ErrorException(ErrorType.DUPLICATE_CAN_ID)
        }
        val canEntities = canRepository.findBy(cpid)
            .orThrow {
                ReadEntityException(message = "Error read CAN(s) from the database.", cause = it.exception)
            }
        val canEntityIds: Set<CANId> = canEntities.fold(initial = HashSet()) { acc, item ->
            acc.add(item.id)
            acc
        }
        val isValidCANIds = idsOfCANs.all { canEntityIds.contains(it) }
        if (!isValidCANIds) throw ErrorException(CANS_NOT_FOUND)

        val updatedCanEntities = ArrayList<CANEntity>()
        val awardContractId = generationService.awardContractId(cpid)
        val cans = ArrayList<Can>()
        //BR-9.1.3

        for (canEntity in canEntities) {
            if (idsOfCANs.contains(canEntity.id)) {
                if (!(canEntity.status == CANStatus.PENDING && canEntity.statusDetails == CANStatusDetails.CONTRACT_PROJECT)) {
                    throw ErrorException(ErrorType.CAN_ALREADY_USED)
                }
                val can = toObject(Can::class.java, canEntity.jsonData)
                can.statusDetails = CANStatusDetails.ACTIVE

                val updatedCANEntity = canEntity.copy(
                    awardContractId = awardContractId,
                    status = can.status,
                    statusDetails = can.statusDetails,
                    jsonData = toJson(can)
                )

                updatedCanEntities.add(updatedCANEntity)
                cans.add(can)
            }
        }
        val wasAppliedCAN = canRepository.update(cpid = cpid, entities = updatedCanEntities)
            .orThrow { it.exception }
        if (!wasAppliedCAN)
            throw SaveEntityException(message = "An error occurred when writing a record(s) of CAN by cpid '$cpid' to the database. Record is already.")

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
        val contract = AwardContract(
            id = awardContractId,
            token = generationService.token(),
            awardId = awardId,
            status = AwardContractStatus.PENDING,
            statusDetails = AwardContractStatusDetails.CONTRACT_PROJECT
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

        val acEntity = AwardContractEntity(
            cpid = cpid,
            id = contract.id,
            token = contract.token!!,
            owner = owner,
            createdDate = dateTime,
            status = contract.status,
            statusDetails = contract.statusDetails,
            mainProcurementCategory = mainProcurementCategory,
            language = language,
            jsonData = toJson(contractProcess)
        )

        val wasAppliedAC = acRepository.saveNew(acEntity)
            .orThrow { it.exception }
        if (!wasAppliedAC)
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the new contract by cpid '${acEntity.cpid}' and id '${acEntity.id}' to the database. Record is already.")

        return CreateAcRs(cans = cans, contract = contract, contractedAward = contractedAward)
    }
}
