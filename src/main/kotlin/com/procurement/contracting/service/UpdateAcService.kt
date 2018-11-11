package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType.*
import com.procurement.contracting.model.dto.*
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.ocds.*
import com.procurement.contracting.model.entity.AcEntity
import com.procurement.contracting.model.entity.CanEntity
import com.procurement.contracting.utils.toDate
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.*

@Service
class UpdateAcService(private val acDao: AcDao,
                      private val generationService: GenerationService) {

    fun updateAC(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val token = cm.context.token ?: throw ErrorException(CONTEXT)
        val dto = toObject(UpdateAcRq::class.java, cm.data)

        val entity = acDao.getByCpIdAndToken(cpId, UUID.fromString(token))
        val contractProcess = toObject(ContractProcess::class.java, entity.jsonData)

        validateAwards(dto, contractProcess)
        val awardValue = validateUpdateAwardValue(dto, contractProcess)
        val awardItems = validateUpdateAwardItems(dto, contractProcess)//BR-9.2.3
        val awardDocuments = validateUpdateAwardDocuments(dto, contractProcess)//BR-9.2.2
        val awardSuppliers = validateUpdateAwardSuppliers(dto, contractProcess)// BR-9.2.21
        contractProcess.awards.apply {
            value = awardValue
            items = awardItems
            documents = awardDocuments
            suppliers = awardSuppliers
        }

        val contractValue = validateUpdateContractValue(dto, contractProcess)//BR-9.2.19
        val contractPeriod = validateUpdateContractPeriod(dto, contractProcess) //VR-9.2.18
        val contractDocuments = validateUpdateContractDocuments(dto, contractProcess)//BR-9.2.10
        val contractMilestones = validateUpdateContractMilestones(dto, contractProcess)//BR-9.2.11
        val contractConfirmationRequests = validateUpdateConfirmationRequests(dto, contractProcess)//BR-9.2.16
        contractProcess.contracts.apply {
            title = dto.contracts.title
            description = dto.contracts.description
            statusDetails = setStatusDetails(statusDetails) //BR-9.2.25
            value = contractValue
            period = contractPeriod
            documents = contractDocuments
            milestones = contractMilestones
            confirmationRequests = contractConfirmationRequests
        }

        contractProcess.apply {
            planning = validateUpdatePlanning(dto)
            buyer = dto.buyer//BR-9.2.20
            treasuryBudgetSources = dto.treasuryBudgetSources//BR-9.2.24
        }

        entity.jsonData = toJson(contractProcess)
        acDao.save(entity)

        return ResponseDto(data = contractProcess)
    }

    private fun validateUpdateConfirmationRequests(dto: UpdateAcRq, contractProcess: ContractProcess): List<ConfirmationRequest>? {
        TODO()
    }

    private fun validateUpdateContractMilestones(dto: UpdateAcRq, contractProcess: ContractProcess): List<Milestone>? {
        TODO()
    }

    private fun validateUpdateContractDocuments(dto: UpdateAcRq, contractProcess: ContractProcess): List<Document>? {
        TODO()
    }

    private fun validateUpdateContractPeriod(dto: UpdateAcRq, contractProcess: ContractProcess): Period? {
        TODO()
    }

    private fun validateUpdateContractValue(dto: UpdateAcRq, contractProcess: ContractProcess): Value? {
        TODO()
    }


    private fun setStatusDetails(contractStatusDetails: ContractStatusDetails): ContractStatusDetails {
        return when (contractStatusDetails) {
            ContractStatusDetails.CONTRACT_PROJECT -> ContractStatusDetails.CONTRACT_PREPARATION
            ContractStatusDetails.CONTRACT_PREPARATION -> ContractStatusDetails.CONTRACT_PREPARATION
            else -> throw ErrorException(CONTRACT_STATUS_DETAILS)
        }
    }

    private fun validateUpdatePlanning(dto: UpdateAcRq): Planning {
        //BR-9.2.6
        val transactions = dto.planning.implementation.transactions
        val transactionsId = transactions.asSequence().map { it.id }.toHashSet()
        if (transactionsId.size != transactions.size) throw ErrorException(TRANSACTIONS)
        transactions.forEach { it.id = generationService.getTimeBasedUUID() }
        //BR-9.2.7
        val relatedItemIds = dto.planning.budget.budgetAllocation.asSequence().map { it.relatedItem }.toSet()
        val awardItemIds = dto.awards.items.asSequence().map { it.id }.toSet()
        if (!awardItemIds.containsAll(relatedItemIds)) throw ErrorException(BA_ITEM_ID)
        return dto.planning
    }


    private fun validateUpdateAwardValue(dto: UpdateAcRq, contractProcess: ContractProcess): ValueAward {
        return contractProcess.awards.value.copy(
                amountNet = dto.awards.value.amountNet,
                valueAddedTaxIncluded = dto.awards.value.valueAddedTaxIncluded)
    }

    private fun validateUpdateAwardSuppliers(dto: UpdateAcRq, contractProcess: ContractProcess): List<OrganizationReferenceSupplier> {
        val suppliersDb = contractProcess.awards.suppliers
        val suppliersDto = dto.awards.suppliers
        //validation
        val suppliersDbIds = suppliersDb.asSequence().map { it.id }.toSet()
        val suppliersDtoIds = suppliersDto.asSequence().map { it.id }.toSet()
        if (suppliersDtoIds.size != suppliersDto.size) throw ErrorException(SUPPLIERS)
        if (suppliersDbIds.size != suppliersDtoIds.size) throw ErrorException(SUPPLIERS)
        if (!suppliersDbIds.containsAll(suppliersDtoIds)) throw ErrorException(TRANSACTIONS)
        //update
        suppliersDb.forEach { supplierDb -> supplierDb.update(suppliersDto.first { it.id == supplierDb.id }) }
        return suppliersDb
    }

    private fun OrganizationReferenceSupplier.update(supplierDto: OrganizationReferenceSupplier) {
        this.persones = updatePersones(this.persones, supplierDto.persones!!)//BR-9.2.3
        this.additionalIdentifiers = supplierDto.additionalIdentifiers
        this.details = supplierDto.details
    }

    private fun updatePersones(personesDb: HashSet<Person>?, personesDto: HashSet<Person>): HashSet<Person> {
        if (personesDb == null || personesDb.isEmpty()) return personesDto
        val personesDbIds = personesDb.asSequence().map { it.identifier.id }.toSet()
        val personesDtoIds = personesDto.asSequence().map { it.identifier.id }.toSet()
        if (personesDtoIds.size != personesDto.size) throw ErrorException(PERSONES)
        //update
        personesDb.forEach { personDb -> personDb.update(personesDto.first { it.identifier.id == personDb.identifier.id }) }
        val newPersonesId = personesDtoIds - personesDbIds
        val newPersones = personesDto.asSequence().filter { it.identifier.id in newPersonesId }.toHashSet()
        return (personesDb + newPersones).toHashSet()
    }

    private fun Person.update(personDto: Person) {
        this.title = personDto.title
        this.name = personDto.name
        this.businessFunctions = personDto.businessFunctions
    }

    private fun validateUpdateAwardDocuments(dto: UpdateAcRq, contractProcess: ContractProcess): List<Document> {
        val documentsDb = contractProcess.awards.documents
        val documentsDto = dto.awards.documents ?: return documentsDb
        //validation
        val documentsDbIds = documentsDb.asSequence().map { it.id }.toSet()
        val documentDtoIds = documentsDto.asSequence().map { it.id }.toSet()
        if (documentDtoIds.size != documentsDto.size) throw ErrorException(DOCUMENTS)
        if (!documentsDbIds.containsAll(documentDtoIds)) throw ErrorException(DOCUMENTS)
        //update
        documentsDb.forEach { docDb -> docDb.update(documentsDto.first { it.id == docDb.id }) }
        val newDocumentsId = documentDtoIds - documentsDbIds
        val newDocuments = documentsDto.asSequence().filter { it.id in newDocumentsId }.toList()
        return (documentsDb + newDocuments)
    }

    private fun Document.update(documentDto: Document) {
        this.title = documentDto.title
        this.description = documentDto.description
        this.relatedLots = documentDto.relatedLots
    }

    private fun validateUpdateAwardItems(dto: UpdateAcRq, contractProcess: ContractProcess): List<Item> {
        val itemsDb = contractProcess.awards.items
        val itemsDto = dto.awards.items
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
        itemsDb.forEach { itemDb -> itemDb.update(itemsDto.first { it.id == itemDb.id }) }
        return itemsDb
    }

    private fun Item.update(itemDto: ItemUpdate) {
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
