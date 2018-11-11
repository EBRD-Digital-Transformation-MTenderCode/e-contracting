package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType.*
import com.procurement.contracting.model.dto.ContractProcess
import com.procurement.contracting.model.dto.ItemUpdate
import com.procurement.contracting.model.dto.UpdateAcRq
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
import java.time.LocalDateTime
import java.util.*

@Service
class UpdateAcService(private val acDao: AcDao) {

    fun updateAC(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val token = cm.context.token ?: throw ErrorException(CONTEXT)
        val dateTime = cm.context.startDate?.toLocalDateTime() ?: throw ErrorException(CONTEXT)
        val dto = toObject(UpdateAcRq::class.java, cm.data)

        val entity = acDao.getByCpIdAndToken(cpId, UUID.fromString(token))
        val contractProcess = toObject(ContractProcess::class.java, entity.jsonData)

        validateAwards(dto, contractProcess)
        val updatedValue = validateUpdateValue(dto, contractProcess)
        val updatedItems = validateUpdateItems(dto, contractProcess)//BR-9.2.3
        val updatedDocuments = validateUpdateDocuments(dto, contractProcess)//BR-9.2.2
        val updatedSuppliers = validateUpdateSuppliers(dto, contractProcess)// BR-9.2.21
        contractProcess.awards.apply {
            value = updatedValue
            items = updatedItems
            documents = updatedDocuments
            suppliers = updatedSuppliers
        }
        contractProcess.planning.apply {
            //        Checks and proceeds Implementation object of Request by rule BR-9.2.6;
            //        Checks and proceeds Budget object of Request by rule BR-9.2.7;
            //        Checks and proceeds budgetSource object of Request by rule BR-9.2.8;
            //        Includes updated Planning object for Response;
        }
        contractProcess.contracts.apply {
            //        Finds saved version of Contract object by OCID from parameter of Request (OCID == Contract.CAN_ID);
            //        Validates the Contract.Period object from Request by rule VR-9.2.18 and save it to DB;
            //        Checks and proceeds Contract.Documents object of Request by rule BR-9.2.10;
            //        Checks and proceeds Contract.Milestones object of Request by rule BR-9.2.11;
            //        Checks and proceeds Contract.confirmationRequests object of Request by rule BR-9.2.16;
            //        Updates saved version of Contract in DB using next fields of Contract from Request:
            //        Updates or adds contract.title;
            //        Updates or adds contract.description;
            //        Calculates Contract.Value object by rule BR-9.2.19 and save it to DB;
            //        Sets Contract.statusDetails by rule BR-9.2.25;
        }
        //        eContracting сохраняет Buyer, treasuryBudgetSources objects по правилам BR-9.2.20, BR-9.2.24
        contractProcess.buyer =

                return ResponseDto(data = contractProcess)
        TODO()
    }

    private fun validateUpdateValue(dto: UpdateAcRq, contractProcess: ContractProcess): ValueAward {
        return contractProcess.awards.value.copy(
                amountNet = dto.awards.value.amountNet,
                valueAddedTaxIncluded = dto.awards.value.valueAddedTaxIncluded)
    }

    private fun validateUpdateSuppliers(dto: UpdateAcRq, contractProcess: ContractProcess): List<OrganizationReference> {
        TODO()
    }

    private fun validateUpdateDocuments(dto: UpdateAcRq, contractProcess: ContractProcess): List<Document> {
        val documentsDb = contractProcess.awards.documents
        val documentsDto = dto.awards.documents ?: return documentsDb
        //validation
        val documentDtoIds = documentsDto.asSequence().map { it.id }.toSet()
        if (documentDtoIds.size != documentsDto.size) throw ErrorException(DOCUMENTS)
        val documentsDbId = documentsDb.asSequence().map { it.id }.toSet()
        if (!documentsDbId.containsAll(documentDtoIds)) throw ErrorException(DOCUMENTS)
        //update
        documentsDb.forEach { docDb -> docDb.updateDocument(documentsDto.first { it.id == docDb.id }) }
        val newDocumentsId = documentDtoIds - documentsDbId
        val newDocuments = documentsDto.asSequence().filter { it.id in newDocumentsId }.toList()
        return (documentsDb + newDocuments)
    }

    private fun Document.updateDocument(documentDto: Document) {
        this.title = documentDto.title
        this.description = documentDto.description
        this.relatedLots = documentDto.relatedLots
    }

    private fun validateUpdateItems(dto: UpdateAcRq, contractProcess: ContractProcess): List<Item> {
        val itemsDto = dto.awards.items
        val itemsDb = contractProcess.awards.items
        //validation
        val itemDbIds = itemsDb.asSequence().map { it.id }.toSet()
        val itemDtoIds = itemsDto.asSequence().map { it.id }.toSet()
        if (itemDtoIds.size != dto.awards.items.size) throw ErrorException(ITEM_ID)
        if (itemDbIds.size != itemDtoIds.size) throw ErrorException(ITEM_ID)
        if (!itemDbIds.containsAll(itemDtoIds)) throw ErrorException(ITEM_ID)
        itemsDto.asSequence().forEach { item ->
            val value = item.unit.value
            if (value.valueAddedTaxIncluded && value.amountNet >= value.amount) throw ErrorException(ITEM_AMOUNT)
            if (value.currency != contractProcess.awards.value.currency) throw ErrorException(ITEM_CURRENCY)
        }
        //update
        itemsDb.forEach { itemDb -> itemDb.updateItem(itemsDto.first { it.id == itemDb.id }) }
        return itemsDb
    }

    private fun Item.updateItem(itemDto: ItemUpdate) {
        this.quantity = itemDto.quantity
        this.unit.value = itemDto.unit.value
        this.deliveryAddress = itemDto.deliveryAddress
    }

    private fun validateAwards(dto: UpdateAcRq, contractProcess: ContractProcess) {
        val award = dto.awards
        if (award.id != contractProcess.contracts.awardId) throw ErrorException(AWARD_ID) //VR-9.2.3
        // VR-9.2.10
        if (award.items.asSequence().any { it.unit.value.valueAddedTaxIncluded != award.value.valueAddedTaxIncluded }) {
            throw ErrorException(AWARD_VALUE)
        }
        if (award.value.valueAddedTaxIncluded) {
            if (award.value.amountNet >= award.value.amount) throw ErrorException(AWARD_VALUE)
        }
        val planningAmount = dto.planning.budget.budgetSource.asSequence()
                .sumByDouble { it.amount.toDouble() }
                .toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        if (award.value.amountNet != planningAmount) throw ErrorException(AWARD_VALUE)
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
