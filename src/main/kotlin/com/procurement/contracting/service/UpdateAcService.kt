package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType.*
import com.procurement.contracting.model.dto.*
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.ocds.*
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toLocalDateTime
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.*

@Service
class UpdateAcService(private val acDao: AcDao,
                      private val generationService: GenerationService,
                      private val templateService: TemplateService) {

    fun updateAC(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val ocId = cm.context.ocid ?: throw ErrorException(CONTEXT)
        val token = cm.context.token ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val country = cm.context.country ?: throw ErrorException(CONTEXT)
        val language = cm.context.language ?: throw ErrorException(CONTEXT)
        val pmd = cm.context.pmd ?: throw ErrorException(CONTEXT)
        val dateTime = cm.context.startDate?.toLocalDateTime() ?: throw ErrorException(CONTEXT)
        val mpc = MainProcurementCategory.fromValue(cm.context.mainProcurementCategory ?: throw ErrorException(CONTEXT))
        val dto = toObject(UpdateAcRq::class.java, cm.data)

        val entity = acDao.getByCpIdAndAcId(cpId, ocId)
        if (entity.owner != owner) throw ErrorException(OWNER)
        if (entity.token.toString() != token) throw ErrorException(INVALID_TOKEN)
        val contractProcess = toObject(ContractProcess::class.java, entity.jsonData)
        validateAwards(dto, contractProcess)
        contractProcess.award.apply {
            value = updateAwardValue(dto, contractProcess)
            items = updateAwardItems(dto, contractProcess)//BR-9.2.3
            documents = updateAwardDocuments(dto, contractProcess)//BR-9.2.2
            suppliers = updateAwardSuppliers(dto, contractProcess)// BR-9.2.21
        }
        contractProcess.contract.apply {
            title = dto.contract.title
            description = dto.contract.description
            statusDetails = setStatusDetails(statusDetails) //BR-9.2.25
            value = updateContractValue(dto)//BR-9.2.19
            period = updateContractPeriod(dto, dateTime) //VR-9.2.18
            documents = updateContractDocuments(dto, contractProcess)//BR-9.2.10
            milestones = updateContractMilestones(dto, contractProcess, mpc, dateTime)//BR-9.2.11
            confirmationRequests = updateConfirmationRequests(dto = dto, documents = documents, country = country, pmd = pmd, language = language)//BR-9.2.16
            agreedMetrics = dto.contract.agreedMetrics
        }
        contractProcess.apply {
            planning = validateUpdatePlanning(dto)
            buyer = dto.buyer//BR-9.2.20
            funders = dto.funders//BR-9.2.20
            payers = dto.payers//BR-9.2.20
            treasuryBudgetSources = dto.treasuryBudgetSources//BR-9.2.24
        }

        entity.jsonData = toJson(contractProcess)
        acDao.save(entity)
        return ResponseDto(data = UpdateAcRs(
                planning = contractProcess.planning!!,
                contract = contractProcess.contract,
                award = contractProcess.award))
    }

    private fun updateContractValue(dto: UpdateAcRq): ValueTax {
        return ValueTax(
                amount = dto.award.value.amount,
                currency = dto.award.value.currency,
                amountNet = dto.award.value.amountNet,
                valueAddedTaxIncluded = dto.award.value.valueAddedTaxIncluded)
    }

    private fun updateContractPeriod(dto: UpdateAcRq, dateTime: LocalDateTime): Period {
        val periodDto = dto.contract.period
        if (periodDto.startDate <= dateTime) throw ErrorException(CONTRACT_PERIOD)
        if (periodDto.startDate > periodDto.endDate) throw ErrorException(CONTRACT_PERIOD)
        return periodDto
    }

    private fun updateContractDocuments(dto: UpdateAcRq, contractProcess: ContractProcess): List<DocumentContract>? {
        //validation
        val documentsDto = dto.contract.documents ?: return contractProcess.contract.documents
        val documentDtoIds = documentsDto.asSequence().map { it.id }.toSet()
        if (documentDtoIds.size != documentsDto.size) throw ErrorException(DOCUMENTS)
        //update
        val documentsDb = contractProcess.contract.documents ?: return documentsDto
        val documentsDbIds = documentsDb.asSequence().map { it.id }.toSet()
        documentsDb.forEach { docDb -> docDb.update(documentsDto.firstOrNull { it.id == docDb.id }) }
        val newDocumentsId = documentDtoIds - documentsDbIds
        val newDocuments = documentsDto.asSequence().filter { it.id in newDocumentsId }.toList()
        return (documentsDb + newDocuments)
    }

    private fun DocumentContract.update(documentDto: DocumentContract?) {
        if (documentDto != null) {
            this.title = documentDto.title
            this.description = documentDto.description
            this.documentType = documentDto.documentType
            this.relatedLots = documentDto.relatedLots
        }
    }

    private fun updateContractMilestones(dto: UpdateAcRq,
                                         contractProcess: ContractProcess,
                                         mpc: MainProcurementCategory,
                                         dateTime: LocalDateTime): List<Milestone>? {
        val milestonesDto = dto.contract.milestones
        //validation
        val relatedItemIds = milestonesDto.asSequence()
                .filter { it.type != MilestoneType.X_REPORTING && it.relatedItems != null }
                .flatMap { it.relatedItems!!.asSequence() }.toSet()
        val awardItemIds = dto.award.items.asSequence().map { it.id }.toSet()
        if (!awardItemIds.containsAll(relatedItemIds)) throw ErrorException(MILESTONE_RELATED_ITEMS)
        milestonesDto.asSequence().forEach { milestone ->
            //validation
            if (mpc == MainProcurementCategory.GOODS || mpc == MainProcurementCategory.WORKS) {
                if (milestone.type != MilestoneType.DELIVERY && milestone.type != MilestoneType.X_WARRANTY) throw ErrorException(MILESTONE_TYPE)
            }
            if (mpc == MainProcurementCategory.SERVICES) {
                if (milestone.type != MilestoneType.X_REPORTING) throw ErrorException(MILESTONE_TYPE)
            }
            if (milestone.dueDate <= dateTime) throw ErrorException(MILESTONE_DUE_DATE)
        }
        milestonesDto.asSequence().forEach { milestone ->
            milestone.status = MilestoneStatus.SCHEDULED
            when (milestone.type) {
                MilestoneType.X_REPORTING -> {
                    val party = RelatedParty(id = dto.buyer.id, name = dto.buyer.name ?: "")
                    milestone.relatedParties = listOf(party)
                    milestone.id = "approval-" + party.id + "-" + generationService.getTimeBasedUUID()
                }
                MilestoneType.DELIVERY -> {
                    val party = contractProcess.award.suppliers.asSequence()
                            .map { RelatedParty(id = it.id, name = it.name) }.first()
                    milestone.relatedParties = listOf(party)
                    milestone.id = "delivery-" + party.id + "-" + generationService.getTimeBasedUUID()
                }
                MilestoneType.X_WARRANTY -> {
                    val party = contractProcess.award.suppliers.asSequence()
                            .map { RelatedParty(id = it.id, name = it.name) }.first()
                    milestone.relatedParties = listOf(party)
                    milestone.id = "x_warranty-" + party.id + "-" + generationService.getTimeBasedUUID()
                }
            }
        }
        return milestonesDto
    }

    private fun updateConfirmationRequests(dto: UpdateAcRq,
                                           documents: List<DocumentContract>?,
                                           country: String,
                                           pmd: String,
                                           language: String): List<ConfirmationRequest>? {
        val confRequestDto = dto.contract.confirmationRequests
        //validation
        if (documents != null) {
            val relatedItemIds = confRequestDto.asSequence().map { it.relatedItem }.toSet()
            val documentIds = documents.asSequence().map { it.id }.toSet()
            if (!documentIds.containsAll(relatedItemIds)) throw ErrorException(CONFIRMATION_ITEM)
        }

        val buyerAuthority = getPersonByBFType(dto.buyer.persones, "authority")
                ?: throw ErrorException(PERSON_NOT_FOUND)
        val buyerTemplate = templateService.getConfirmationRequestTemplate(
                country = country,
                pmd = pmd,
                language = language,
                templateId = "cs-buyer-confirmation-on")

        val awardSupplier = dto.award.suppliers[0]
        val tendererAuthority = getPersonByBFType(awardSupplier.persones, "authority")
                ?: throw ErrorException(PERSON_NOT_FOUND)
        val tendererTemplate = templateService.getConfirmationRequestTemplate(country = country, pmd = pmd, language = language,
                templateId = "cs-tenderer-confirmation-on")
        //set
        for (confRequest in confRequestDto) {
            when (confRequest.source) {
                "buyer" -> {
                    confRequest.id = buyerTemplate.id + confRequest.relatedItem
                    confRequest.description = buyerTemplate.description
                    confRequest.title = buyerTemplate.title
                    confRequest.type = buyerTemplate.type
                    confRequest.relatesTo = buyerTemplate.relatesTo
                    confRequest.requestGroups = setOf(
                            RequestGroup(
                                    id = buyerTemplate.id + confRequest.relatedItem + "-" + dto.buyer.id,
                                    requests = setOf(Request(
                                            id = buyerTemplate.id + confRequest.relatedItem + "-" + buyerAuthority.id,
                                            title = buyerTemplate.requestTitle + buyerAuthority.name,
                                            description = buyerTemplate.requestDescription,
                                            relatedPerson = buyerAuthority
                                    ))
                            )
                    )
                }
                "tenderer" -> {
                    confRequest.id = tendererTemplate.id + confRequest.relatedItem
                    confRequest.description = tendererTemplate.description
                    confRequest.title = tendererTemplate.title
                    confRequest.type = tendererTemplate.type
                    confRequest.relatesTo = tendererTemplate.relatesTo
                    confRequest.requestGroups = setOf(
                            RequestGroup(
                                    id = tendererTemplate.id + confRequest.relatedItem + "-" + awardSupplier.id,
                                    requests = setOf(Request(
                                            relatedPerson = tendererAuthority,
                                            id = tendererTemplate.id + confRequest.relatedItem + "-" + tendererAuthority.id,
                                            title = tendererTemplate.requestTitle + tendererAuthority.name,
                                            description = tendererTemplate.requestDescription
                                    ))
                            )
                    )
                }
                else -> throw ErrorException(CONFIRMATION_SOURCE)
            }
        }
        return confRequestDto
    }

    private fun getPersonByBFType(persones: HashSet<Person>, type: String): RelatedPerson? {
        for (person in persones) {
            if (person.businessFunctions.asSequence().any { it.type == type }) {
                return RelatedPerson(id = person.identifier.id, name = person.name)
            }
        }
        return null
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
        if (dto.planning.budget.budgetSource.any { it.currency != dto.award.value.currency }) throw ErrorException(BS_CURRENCY)
        val transactions = dto.planning.implementation.transactions
        if (transactions.isEmpty()) throw ErrorException(TRANSACTIONS)
        val transactionsId = transactions.asSequence().map { it.id }.toHashSet()
        if (transactionsId.size != transactions.size) throw ErrorException(TRANSACTIONS)
        transactions.forEach { it.id = generationService.getTimeBasedUUID() }
        //BR-9.2.7
        val relatedItemIds = dto.planning.budget.budgetAllocation.asSequence().map { it.relatedItem }.toSet()
        val awardItemIds = dto.award.items.asSequence().map { it.id }.toSet()
        if (!awardItemIds.containsAll(relatedItemIds)) throw ErrorException(BA_ITEM_ID)

        return dto.planning
    }

    private fun updateAwardValue(dto: UpdateAcRq, contractProcess: ContractProcess): ValueTax {
        return contractProcess.award.value.copy(
                amountNet = dto.award.value.amountNet,
                valueAddedTaxIncluded = dto.award.value.valueAddedTaxIncluded)
    }

    private fun updateAwardSuppliers(dto: UpdateAcRq, contractProcess: ContractProcess): List<OrganizationReferenceSupplier> {
        val suppliersDb = contractProcess.award.suppliers
        val suppliersDto = dto.award.suppliers
        //validation
        val suppliersDbIds = suppliersDb.asSequence().map { it.id }.toSet()
        val suppliersDtoIds = suppliersDto.asSequence().map { it.id }.toSet()
        if (suppliersDtoIds.size != suppliersDto.size) throw ErrorException(SUPPLIERS)
        if (suppliersDbIds.size != suppliersDtoIds.size) throw ErrorException(SUPPLIERS)
        if (!suppliersDbIds.containsAll(suppliersDtoIds)) throw ErrorException(SUPPLIERS)
        //update
        suppliersDb.forEach { supplierDb -> supplierDb.update(suppliersDto.firstOrNull { it.id == supplierDb.id }) }
        return suppliersDb
    }

    private fun OrganizationReferenceSupplier.update(supplierDto: OrganizationReferenceSupplierUpdate?) {
        if (supplierDto != null) {
            this.persones = updatePersones(this.persones, supplierDto.persones)//BR-9.2.3
            if (supplierDto.additionalIdentifiers.isNotEmpty()) {
                this.additionalIdentifiers = supplierDto.additionalIdentifiers
            }
            this.details = updateDetails(supplierDto.details)
        }
    }

    private fun updateDetails(details: DetailsSupplierUpdate): DetailsSupplier {
        return DetailsSupplier(
                typeOfSupplier = details.typeOfSupplier,
                mainEconomicActivities = details.mainEconomicActivities,
                bankAccounts = details.bankAccounts,
                legalForm = details.legalForm,
                permits = details.permits,
                scale = details.scale
        )
    }

    private fun updatePersones(personesDb: HashSet<Person>?, personesDto: HashSet<Person>): HashSet<Person> {
        if (personesDb == null || personesDb.isEmpty()) return personesDto
        val personesDbIds = personesDb.asSequence().map { it.identifier.id }.toSet()
        val personesDtoIds = personesDto.asSequence().map { it.identifier.id }.toSet()
        if (personesDtoIds.size != personesDto.size) throw ErrorException(PERSONES)
        //update
        personesDb.forEach { personDb -> personDb.update(personesDto.firstOrNull { it.identifier.id == personDb.identifier.id }) }
        val newPersonesId = personesDtoIds - personesDbIds
        val newPersones = personesDto.asSequence().filter { it.identifier.id in newPersonesId }.toHashSet()
        return (personesDb + newPersones).toHashSet()
    }

    private fun Person.update(personDto: Person?) {
        if (personDto != null) {
            this.title = personDto.title
            this.name = personDto.name
            this.businessFunctions = updateBusinessFunctions(this.businessFunctions, personDto.businessFunctions)
        }
    }

    private fun updateBusinessFunctions(bfDb: List<BusinessFunction>, bfDto: List<BusinessFunction>): List<BusinessFunction> {
        val bfDbIds = bfDb.asSequence().map { it.id }.toSet()
        val bfDtoIds = bfDto.asSequence().map { it.id }.toSet()
        if (bfDtoIds.size != bfDto.size) throw ErrorException(BF)
        //update
        bfDb.forEach { businessFunction -> businessFunction.update(bfDto.firstOrNull { it.id == businessFunction.id }) }
        val newBfId = bfDtoIds - bfDbIds
        val newBf = bfDto.asSequence().filter { it.id in newBfId }.toHashSet()
        return bfDb + newBf
    }

    private fun BusinessFunction.update(bfDto: BusinessFunction?) {
        if (bfDto != null) {
            this.type = bfDto.type
            this.jobTitle = bfDto.jobTitle
            this.period = bfDto.period
            this.documents = updateDocuments(this.documents, bfDto.documents)
        }
    }

    private fun updateDocuments(documentsDb: List<DocumentBF>, documentsDto: List<DocumentBF>): List<DocumentBF> {
        //validation
        val documentsDbIds = documentsDb.asSequence().map { it.id }.toSet()
        val documentDtoIds = documentsDto.asSequence().map { it.id }.toSet()
        if (documentDtoIds.size != documentsDto.size) throw ErrorException(DOCUMENTS)
        //update
        documentsDb.forEach { docDb -> docDb.update(documentsDto.firstOrNull { it.id == docDb.id }) }
        val newDocumentsId = documentDtoIds - documentsDbIds
        val newDocuments = documentsDto.asSequence().filter { it.id in newDocumentsId }.toList()
        return (documentsDb + newDocuments)
    }

    private fun DocumentBF.update(documentDto: DocumentBF?) {
        if (documentDto != null) {
            this.title = documentDto.title
            this.description = documentDto.description
        }
    }

    private fun updateAwardDocuments(dto: UpdateAcRq, contractProcess: ContractProcess): List<DocumentAward>? {
        val documentsDb = contractProcess.award.documents
        val documentsDto = dto.award.documents
        if (documentsDto != null) {
            //validation
            val documentDtoIds = documentsDto.asSequence().map { it.id }.toSet()
            if (documentDtoIds.size != documentsDto.size) throw ErrorException(DOCUMENTS)
            //update
            return if (documentsDb != null) {
                val documentsDbIds = documentsDb.asSequence().map { it.id }.toSet()
                documentsDb.forEach { docDb -> docDb.update(documentsDto.firstOrNull { it.id == docDb.id }) }
                val newDocumentsId = documentDtoIds - documentsDbIds
                val newDocuments = documentsDto.asSequence().filter { it.id in newDocumentsId }.toList()
                (documentsDb + newDocuments)
            } else {
                documentsDto
            }
        } else {
            return documentsDb
        }
    }

    private fun DocumentAward.update(documentDto: DocumentAward?) {
        if (documentDto != null) {
            this.title = documentDto.title
            this.description = documentDto.description
            this.documentType = documentDto.documentType
            this.relatedLots = documentDto.relatedLots
        }
    }

    private fun updateAwardItems(dto: UpdateAcRq, contractProcess: ContractProcess): List<Item> {
        val itemsDb = contractProcess.award.items
        val itemsDto = dto.award.items
        //validation
        val itemDbIds = itemsDb.asSequence().map { it.id }.toSet()
        val itemDtoIds = itemsDto.asSequence().map { it.id }.toSet()
        if (itemDtoIds.size != dto.award.items.size) throw ErrorException(ITEM_ID)
        if (itemDbIds.size != itemDtoIds.size) throw ErrorException(ITEM_ID)
        if (!itemDbIds.containsAll(itemDtoIds)) throw ErrorException(ITEM_ID)
        itemsDto.asSequence().forEach { item ->
            val value = item.unit.value
            if (value.valueAddedTaxIncluded && value.amountNet >= value.amount) throw ErrorException(ITEM_AMOUNT)
            if (value.currency != contractProcess.award.value.currency) throw ErrorException(ITEM_CURRENCY)
        }
        //update
        itemsDb.forEach { itemDb -> itemDb.update(itemsDto.firstOrNull { it.id == itemDb.id }) }
        return itemsDb
    }

    private fun Item.update(itemDto: ItemUpdate?) {
        if (itemDto != null) {
            this.quantity = itemDto.quantity
            this.unit.value = ValueTax(
                    amount = itemDto.unit.value.amount,
                    currency = itemDto.unit.value.currency,
                    amountNet = itemDto.unit.value.amountNet,
                    valueAddedTaxIncluded = itemDto.unit.value.valueAddedTaxIncluded)
            this.deliveryAddress = itemDto.deliveryAddress
        }
    }

    private fun validateAwards(dto: UpdateAcRq, contractProcess: ContractProcess) {
        val award = dto.award
        if (award.id != contractProcess.contract.awardId) throw ErrorException(AWARD_ID) //VR-9.2.3
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
}
