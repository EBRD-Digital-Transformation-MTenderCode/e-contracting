package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.domain.model.MainProcurementCategory
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestSource
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails
import com.procurement.contracting.domain.model.item.ItemId
import com.procurement.contracting.domain.model.milestone.status.MilestoneStatus
import com.procurement.contracting.domain.model.milestone.type.MilestoneType
import com.procurement.contracting.domain.model.organization.OrganizationId
import com.procurement.contracting.domain.model.transaction.type.TransactionType
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType.ADDITIONAL_IDENTIFIERS_IN_SUPPLIER_IS_EMPTY_OR_MISSING
import com.procurement.contracting.exception.ErrorType.AWARD_ID
import com.procurement.contracting.exception.ErrorType.AWARD_VALUE
import com.procurement.contracting.exception.ErrorType.BANK_ACCOUNTS_IN_DETAILS_IN_SUPPLIER_IS_EMPTY_OR_MISSING
import com.procurement.contracting.exception.ErrorType.BA_ITEM_ID
import com.procurement.contracting.exception.ErrorType.BF
import com.procurement.contracting.exception.ErrorType.BS_CURRENCY
import com.procurement.contracting.exception.ErrorType.BUSINESS_FUNCTIONS_IN_PERSONES_IN_SUPPLIER_IS_EMPTY
import com.procurement.contracting.exception.ErrorType.CONFIRMATION_ITEM
import com.procurement.contracting.exception.ErrorType.CONTEXT
import com.procurement.contracting.exception.ErrorType.CONTRACT_PERIOD
import com.procurement.contracting.exception.ErrorType.CONTRACT_STATUS_DETAILS
import com.procurement.contracting.exception.ErrorType.DOCUMENTS
import com.procurement.contracting.exception.ErrorType.DOCUMENTS_IN_BUSINESS_FUNCTION_IN_PERSON_IN_SUPPLIER_IS_EMPTY
import com.procurement.contracting.exception.ErrorType.EMPTY_MILESTONE_RELATED_ITEM
import com.procurement.contracting.exception.ErrorType.INVALID_AWARD_CURRENCY
import com.procurement.contracting.exception.ErrorType.INVALID_BUSINESS_FUNCTIONS_TYPE
import com.procurement.contracting.exception.ErrorType.INVALID_DOCS_RELATED_LOTS
import com.procurement.contracting.exception.ErrorType.INVALID_OWNER
import com.procurement.contracting.exception.ErrorType.INVALID_TOKEN
import com.procurement.contracting.exception.ErrorType.INVALID_TR_RELATED_MILESTONES
import com.procurement.contracting.exception.ErrorType.ITEM_AMOUNT
import com.procurement.contracting.exception.ErrorType.ITEM_CURRENCY
import com.procurement.contracting.exception.ErrorType.ITEM_ID
import com.procurement.contracting.exception.ErrorType.MAIN_ECONOMIC_ACTIVITIES_IN_DETAILS_IN_SUPPLIER_IS_EMPTY_OR_MISSING
import com.procurement.contracting.exception.ErrorType.MILESTONES_EMPTY
import com.procurement.contracting.exception.ErrorType.MILESTONE_DUE_DATE
import com.procurement.contracting.exception.ErrorType.MILESTONE_ID
import com.procurement.contracting.exception.ErrorType.MILESTONE_RELATED_ITEMS
import com.procurement.contracting.exception.ErrorType.MILESTONE_TYPE
import com.procurement.contracting.exception.ErrorType.PERSONES
import com.procurement.contracting.exception.ErrorType.PERSONES_IN_SUPPLIERS_IS_EMPTY
import com.procurement.contracting.exception.ErrorType.PERSON_NOT_FOUND
import com.procurement.contracting.exception.ErrorType.SUPPLIERS
import com.procurement.contracting.exception.ErrorType.TRANSACTIONS
import com.procurement.contracting.model.dto.AwardUpdate
import com.procurement.contracting.model.dto.ContractProcess
import com.procurement.contracting.model.dto.DetailsSupplierUpdate
import com.procurement.contracting.model.dto.ItemUpdate
import com.procurement.contracting.model.dto.OrganizationReferenceSupplierUpdate
import com.procurement.contracting.model.dto.UpdateAcRq
import com.procurement.contracting.model.dto.UpdateAcRs
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.ocds.BusinessFunction
import com.procurement.contracting.model.dto.ocds.ConfirmationRequest
import com.procurement.contracting.model.dto.ocds.DetailsSupplier
import com.procurement.contracting.model.dto.ocds.DocumentAward
import com.procurement.contracting.model.dto.ocds.DocumentBF
import com.procurement.contracting.model.dto.ocds.DocumentContract
import com.procurement.contracting.model.dto.ocds.Item
import com.procurement.contracting.model.dto.ocds.Milestone
import com.procurement.contracting.model.dto.ocds.OrganizationReferenceSupplier
import com.procurement.contracting.model.dto.ocds.Period
import com.procurement.contracting.model.dto.ocds.Person
import com.procurement.contracting.model.dto.ocds.Planning
import com.procurement.contracting.model.dto.ocds.RelatedParty
import com.procurement.contracting.model.dto.ocds.RelatedPerson
import com.procurement.contracting.model.dto.ocds.Request
import com.procurement.contracting.model.dto.ocds.RequestGroup
import com.procurement.contracting.model.dto.ocds.ValueTax
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toLocalDateTime
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service
import java.math.RoundingMode
import java.time.LocalDateTime

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
        val mpc = MainProcurementCategory.fromString(cm.context.mainProcurementCategory ?: throw ErrorException(CONTEXT))
        val dto = toObject(UpdateAcRq::class.java, cm.data)

        checkAwardSupplierPersones(dto.award)
        checkAwardSupplierPersonesBusinessFunctionsType(dto.award)

        val entity = acDao.getByCpIdAndAcId(cpId, ocId)
        if (entity.owner != owner) throw ErrorException(error = INVALID_OWNER)
        if (entity.token.toString() != token) throw ErrorException(INVALID_TOKEN)
        val contractProcess = toObject(ContractProcess::class.java, entity.jsonData)
        validateAwards(dto, contractProcess)
        validateValueItems(dto)
        validateDocsRelatedLots(dto, contractProcess)
        contractProcess.award.apply {
            dto.award.description?.let { description = it }
            value = updateAwardValue(dto, contractProcess)
            items = updateAwardItems(dto, contractProcess)//BR-9.2.3
            documents = updateAwardDocuments(dto, contractProcess)//BR-9.2.2
            suppliers = updateAwardSuppliers(dto, contractProcess)// BR-9.2.21
        }

        validateContractMilestones(dto, mpc, dateTime)

        contractProcess.contract.apply {
            title = dto.contract.title
            description = dto.contract.description
            statusDetails = setStatusDetails(statusDetails) //BR-9.2.25
            value = updateContractValue(dto)//BR-9.2.19
            period = updateContractPeriod(dto, dateTime) //VR-9.2.18
            documents = updateContractDocuments(dto, contractProcess)//BR-9.2.10
            milestones = updateContractMilestones(dto, contractProcess)//BR-9.2.11
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

    /**
     * VR-9.2.29
     */
    private fun checkAwardSupplierPersones(award: AwardUpdate) {
        if (award.suppliers.any { it.persones.isEmpty() })
            throw ErrorException(error = PERSONES_IN_SUPPLIERS_IS_EMPTY)

        award.suppliers.forEach { supplier ->
            checkAwardSupplierDetailsMainEconomicActivities(supplier.details)
            checkAwardSupplierDetailsBankAccounts(supplier.details)
            checkAwardSupplierAdditionalIdentifiers(supplier)

            supplier.persones.forEach { person ->
                checkAwardSupplierPersonBusinessFunction(person)
            }
        }
    }

    /**
     * VR-9.2.30
     */
    private fun checkAwardSupplierPersonBusinessFunction(person: Person) {
        if (person.businessFunctions.isEmpty())
            throw ErrorException(error = BUSINESS_FUNCTIONS_IN_PERSONES_IN_SUPPLIER_IS_EMPTY)

        person.businessFunctions.forEach {
            checkAwardSupplierPersonBusinessFunctionDocuments(it)
        }
    }

    /**
     * VR-9.2.31
     */
    private fun checkAwardSupplierPersonBusinessFunctionDocuments(businessFunction: BusinessFunction) {
        if (businessFunction.documents.isEmpty())
            throw ErrorException(error = DOCUMENTS_IN_BUSINESS_FUNCTION_IN_PERSON_IN_SUPPLIER_IS_EMPTY)
    }

    /**
     * VR-9.2.32
     */
    private fun checkAwardSupplierDetailsMainEconomicActivities(details: DetailsSupplierUpdate) {
        if (details.mainEconomicActivities.isEmpty())
            throw ErrorException(error = MAIN_ECONOMIC_ACTIVITIES_IN_DETAILS_IN_SUPPLIER_IS_EMPTY_OR_MISSING)
    }

    /**
     * VR-9.2.33
     */
    private fun checkAwardSupplierDetailsBankAccounts(details: DetailsSupplierUpdate) {
        if (details.bankAccounts.isEmpty())
            throw ErrorException(error = BANK_ACCOUNTS_IN_DETAILS_IN_SUPPLIER_IS_EMPTY_OR_MISSING)
    }

    /**
     * VR-9.2.34
     */
    private fun checkAwardSupplierAdditionalIdentifiers(supplier: OrganizationReferenceSupplierUpdate) {
        if (supplier.additionalIdentifiers.isEmpty())
            throw ErrorException(error = ADDITIONAL_IDENTIFIERS_IN_SUPPLIER_IS_EMPTY_OR_MISSING)
    }

    /**
     * VR-9.2.35
     */
    private fun checkAwardSupplierPersonesBusinessFunctionsType(award: AwardUpdate) {
        award.suppliers.forEach { supplier ->
            supplier.persones.forEach { person ->
                person.businessFunctions.forEach { businessFunction ->
                    if (businessFunction.type == "authority")
                        return
                }
            }
        }

        throw ErrorException(error = INVALID_BUSINESS_FUNCTIONS_TYPE)
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

    private fun updateContractMilestones(dto: UpdateAcRq, contractProcess: ContractProcess): MutableList<Milestone>? {
        val milestonesDto = dto.contract.milestones
        val milestonesDb = contractProcess.contract.milestones ?: mutableListOf()
        val milestonesDtoIds = milestonesDto.asSequence().map { it.id }.toHashSet()
        val milestonesDbIds = milestonesDb.asSequence().map { it.id }.toHashSet()
        val newMilestonesIds = milestonesDtoIds - milestonesDbIds
        val updatedMilestonesDb = milestonesDb.asSequence()
                .filter { it.id in milestonesDtoIds }
                .map { milestoneDb -> milestoneDb.update(milestonesDto.first { it.id == milestoneDb.id }) }
                .toSet()
        val newMilestones = processNewMilestonesIdSet(dto, contractProcess, newMilestonesIds)
        return if (updatedMilestonesDb.isNotEmpty()) {
            (updatedMilestonesDb + newMilestones).toMutableList()
        } else {
            newMilestones
        }
    }

    private fun Milestone.update(milestoneDto: Milestone): Milestone {
        milestoneDto.additionalInformation?.let { this.additionalInformation = it }
        milestoneDto.relatedItems?.let { this.relatedItems = it }
        this.dueDate = milestoneDto.dueDate
        this.title = milestoneDto.title
        this.description = milestoneDto.description
        return this
    }

    private fun processNewMilestonesIdSet(dto: UpdateAcRq, contractProcess: ContractProcess, newMilestonesIds: Set<String>): MutableList<Milestone> {
        val milestonesDto = dto.contract.milestones
        val transactions = dto.planning.implementation.transactions
        val newMilestones = mutableListOf<Milestone>()
        milestonesDto.asSequence()
                .filter { it.id in newMilestonesIds }
                .forEach { milestone ->
                    milestone.status = MilestoneStatus.SCHEDULED
                    var id = ""
                    when (milestone.type) {
                        MilestoneType.X_REPORTING -> {
                            val party = RelatedParty(id = dto.buyer.id, name = dto.buyer.name ?: "")
                            milestone.relatedParties = listOf(party)
                            id = "approval-" + party.id + "-" + generationService.getTimeBasedUUID()
                        }
                        MilestoneType.DELIVERY -> {
                            val party = contractProcess.award.suppliers.asSequence()
                                    .map { RelatedParty(id = it.id, name = it.name) }.first()
                            milestone.relatedParties = listOf(party)
                            id = "delivery-" + party.id + "-" + generationService.getTimeBasedUUID()
                        }
                        MilestoneType.X_WARRANTY -> {
                            val party = contractProcess.award.suppliers.asSequence()
                                    .map { RelatedParty(id = it.id, name = it.name) }.first()
                            milestone.relatedParties = listOf(party)
                            id = "x_warranty-" + party.id + "-" + generationService.getTimeBasedUUID()
                        }
                        MilestoneType.APPROVAL -> {
                        }
                    }
                    transactions.asSequence()
                            .filter { it.type != TransactionType.ADVANCE && it.relatedContractMilestone == milestone.id }
                            .forEach { it.relatedContractMilestone = id }
                    milestone.id = id
                    newMilestones.add(milestone)
                }
        return newMilestones
    }

    private fun validateContractMilestones(dto: UpdateAcRq, mpc: MainProcurementCategory, dateTime: LocalDateTime) {
        //validation
        val milestonesDto = dto.contract.milestones
        val transactions = dto.planning.implementation.transactions

        milestonesDto.asSequence().forEach { milestone ->
            //validation
            if (mpc == MainProcurementCategory.GOODS || mpc == MainProcurementCategory.WORKS) {
                if (milestone.type != MilestoneType.DELIVERY && milestone.type != MilestoneType.X_WARRANTY) throw ErrorException(MILESTONE_TYPE)
            }
            if (mpc == MainProcurementCategory.SERVICES) {
                if (milestone.type != MilestoneType.X_REPORTING) throw ErrorException(MILESTONE_TYPE)
            }
            if (milestone.dueDate != null && milestone.dueDate!! <= dateTime) throw ErrorException(MILESTONE_DUE_DATE)
        }

        val milestonesIdSet = milestonesDto.asSequence().map { it.id }.toHashSet()
        if (milestonesIdSet.size != milestonesDto.size) throw ErrorException(MILESTONE_ID)

        val milestonesFromTrSet = transactions.asSequence()
                .filter { it.type != TransactionType.ADVANCE }
                .map { it.relatedContractMilestone ?: throw ErrorException(INVALID_TR_RELATED_MILESTONES) }.toHashSet()

        if (!milestonesIdSet.containsAll(milestonesFromTrSet)) throw ErrorException(INVALID_TR_RELATED_MILESTONES)

        if (milestonesDto.isEmpty()) throw ErrorException(MILESTONES_EMPTY)

        val relatedItemIds: Set<ItemId> = milestonesDto.asSequence()
                .flatMap {
                    it.relatedItems
                        ?.takeIf { relatedItems -> relatedItems.isNotEmpty() }
                        ?.asSequence()
                        ?: throw ErrorException(EMPTY_MILESTONE_RELATED_ITEM)
                }
                .toSet()
        val awardItemIds: Set<ItemId> = dto.award.items.asSequence().map { it.id }.toSet()
        if (!awardItemIds.containsAll(relatedItemIds)) throw ErrorException(MILESTONE_RELATED_ITEMS)
    }

    private fun updateConfirmationRequests(dto: UpdateAcRq,
                                           documents: List<DocumentContract>?,
                                           country: String,
                                           pmd: String,
                                           language: String): MutableList<ConfirmationRequest>? {
        val confRequestDto = dto.contract.confirmationRequests
        if (confRequestDto != null) {
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
                    ConfirmationRequestSource.BUYER -> {
                        confRequest.id = buyerTemplate.id + confRequest.relatedItem
                        confRequest.description = buyerTemplate.description
                        confRequest.title = buyerTemplate.title
                        confRequest.type = buyerTemplate.type
                        confRequest.relatesTo = buyerTemplate.relatesTo
                        confRequest.requestGroups = listOf(
                                RequestGroup(
                                        id = buyerTemplate.id + confRequest.relatedItem + "-" + dto.buyer.id,
                                        requests = listOf(Request(
                                                id = buyerTemplate.id + confRequest.relatedItem + "-" + buyerAuthority.id,
                                                title = buyerTemplate.requestTitle + buyerAuthority.name,
                                                description = buyerTemplate.requestDescription,
                                                relatedPerson = buyerAuthority
                                        ))
                                )
                        )
                    }
                    ConfirmationRequestSource.TENDERER -> {
                        confRequest.id = tendererTemplate.id + confRequest.relatedItem
                        confRequest.description = tendererTemplate.description
                        confRequest.title = tendererTemplate.title
                        confRequest.type = tendererTemplate.type
                        confRequest.relatesTo = tendererTemplate.relatesTo
                        confRequest.requestGroups = listOf(
                                RequestGroup(
                                        id = tendererTemplate.id + confRequest.relatedItem + "-" + awardSupplier.id,
                                        requests = listOf(Request(
                                                relatedPerson = tendererAuthority,
                                                id = tendererTemplate.id + confRequest.relatedItem + "-" + tendererAuthority.id,
                                                title = tendererTemplate.requestTitle + tendererAuthority.name,
                                                description = tendererTemplate.requestDescription
                                        ))
                                )
                        )
                    }
                    ConfirmationRequestSource.APPROVE_BODY -> {
                        TODO()
                    }
                }
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
        if (awardItemIds.size != relatedItemIds.size) throw ErrorException(BA_ITEM_ID)
        if (!awardItemIds.containsAll(relatedItemIds)) throw ErrorException(BA_ITEM_ID)
        return dto.planning
    }

    private fun updateAwardValue(dto: UpdateAcRq, contractProcess: ContractProcess): ValueTax {
        return contractProcess.award.value.copy(
                amount = dto.award.value.amount,
                amountNet = dto.award.value.amountNet,
                valueAddedTaxIncluded = dto.award.value.valueAddedTaxIncluded)
    }

    private fun updateAwardSuppliers(dto: UpdateAcRq, contractProcess: ContractProcess): List<OrganizationReferenceSupplier> {
        val suppliersDb = contractProcess.award.suppliers
        val suppliersDto = dto.award.suppliers
        //validation
        val suppliersDbIds: Set<OrganizationId> = suppliersDb.asSequence().map { it.id }.toSet()
        val suppliersDtoIds: Set<OrganizationId> = suppliersDto.asSequence().map { it.id }.toSet()
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

    private fun validateValueItems(dto: UpdateAcRq) {
        val award = dto.award
        award.items.forEach { item ->
            val value = item.unit.value
            when {
                value.amount > value.amountNet -> if (!value.valueAddedTaxIncluded) throw ErrorException(ITEM_AMOUNT)
                value.amount == value.amountNet -> if (value.valueAddedTaxIncluded) throw ErrorException(ITEM_AMOUNT)
                value.amount < value.amountNet -> throw ErrorException(ITEM_AMOUNT)
            }

            if (item.unit.value.currency != award.value.currency) throw ErrorException(ITEM_CURRENCY)
        }
    }

    private fun validateAwards(dto: UpdateAcRq, contractProcess: ContractProcess) {
        val award = dto.award
        if (award.id != contractProcess.contract.awardId) throw ErrorException(AWARD_ID) //VR-9.2.3
        if (award.value.currency != contractProcess.award.value.currency) throw ErrorException(INVALID_AWARD_CURRENCY)

        // VR-9.2.10(1)
        if(award.value.valueAddedTaxIncluded) {
            if(allValueAddedTaxIncludedIsFalse(award)) throw ErrorException(AWARD_VALUE)
        } else {
            if(anyValueAddedTaxIncludedIsTrue(award)) throw ErrorException(AWARD_VALUE)
        }

        // VR-9.2.10(2)
        if(award.value.amount > award.value.amountNet) {
            if(!award.value.valueAddedTaxIncluded) throw ErrorException(AWARD_VALUE)
        } else if(award.value.amount == award.value.amountNet){
            if(award.value.valueAddedTaxIncluded) throw ErrorException(AWARD_VALUE)
        } else{
            throw ErrorException(AWARD_VALUE)
        }

        // VR-9.2.10(3)
        val planningAmount = dto.planning.budget.budgetSource.asSequence()
            .map { it.amount }
            .reduce { acc, amount ->  acc + amount}
        if (award.value.amountNet > planningAmount) throw ErrorException(AWARD_VALUE)
    }

    private fun allValueAddedTaxIncludedIsFalse(award: AwardUpdate) = award.items.all {
        !it.unit.value.valueAddedTaxIncluded
    }

    private fun anyValueAddedTaxIncludedIsTrue(award: AwardUpdate) = award.items.any {
        it.unit.value.valueAddedTaxIncluded
    }

    private fun validateDocsRelatedLots(dto: UpdateAcRq, contractProcess: ContractProcess) {
        val awardRelatedLotsDb = contractProcess.award.relatedLots.toHashSet()
        val awardDocumentsDto = dto.award.documents
        if (awardDocumentsDto != null) {
            val lotsFromAwardDocuments = awardDocumentsDto.asSequence()
                    .filter { it.relatedLots != null }
                    .flatMap { it.relatedLots!!.asSequence() }
                    .toHashSet()
            if (awardRelatedLotsDb.isNotEmpty()) {
                if (!awardRelatedLotsDb.containsAll(lotsFromAwardDocuments)) throw ErrorException(INVALID_DOCS_RELATED_LOTS)
            }
        }
        val contractDocumentsDto = dto.contract.documents
        if (contractDocumentsDto != null) {
            val lotsFromContractDocuments = contractDocumentsDto.asSequence()
                    .filter { it.relatedLots != null }
                    .flatMap { it.relatedLots!!.asSequence() }
                    .toHashSet()
            if (lotsFromContractDocuments.isNotEmpty()) {
                if (!awardRelatedLotsDb.containsAll(lotsFromContractDocuments)) throw ErrorException(INVALID_DOCS_RELATED_LOTS)
            }
        }
    }
}
