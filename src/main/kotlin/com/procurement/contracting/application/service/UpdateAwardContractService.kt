package com.procurement.contracting.application.service

import com.procurement.contracting.application.exception.repository.SaveEntityException
import com.procurement.contracting.application.repository.ac.AwardContractRepository
import com.procurement.contracting.application.repository.ac.model.AwardContractEntity
import com.procurement.contracting.application.repository.model.ContractProcess
import com.procurement.contracting.domain.model.MainProcurementCategory
import com.procurement.contracting.domain.model.ProcurementMethodDetails
import com.procurement.contracting.domain.model.ac.id.asAwardContractId
import com.procurement.contracting.domain.model.ac.status.AwardContractStatusDetails
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestSource
import com.procurement.contracting.domain.model.item.ItemId
import com.procurement.contracting.domain.model.milestone.status.MilestoneStatus
import com.procurement.contracting.domain.model.milestone.type.MilestoneType
import com.procurement.contracting.domain.model.organization.OrganizationId
import com.procurement.contracting.domain.model.transaction.type.TransactionType
import com.procurement.contracting.domain.util.extension.getDuplicate
import com.procurement.contracting.domain.util.extension.getElementsForUpdate
import com.procurement.contracting.domain.util.extension.getNewElements
import com.procurement.contracting.domain.util.extension.toSetBy
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.exception.ErrorType.ADDITIONAL_IDENTIFIERS_IN_SUPPLIER_IS_EMPTY_OR_MISSING
import com.procurement.contracting.exception.ErrorType.AWARD_ID
import com.procurement.contracting.exception.ErrorType.AWARD_VALUE
import com.procurement.contracting.exception.ErrorType.BANK_ACCOUNTS_IN_DETAILS_IN_SUPPLIER_IS_EMPTY_OR_MISSING
import com.procurement.contracting.exception.ErrorType.BA_ITEM_ID
import com.procurement.contracting.exception.ErrorType.BF
import com.procurement.contracting.exception.ErrorType.BS_CURRENCY
import com.procurement.contracting.exception.ErrorType.BUSINESS_FUNCTIONS_IN_PERSONES_IN_SUPPLIER_IS_EMPTY
import com.procurement.contracting.exception.ErrorType.CONFIRMATION_ITEM
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
import com.procurement.contracting.exception.ErrorType.PERSONES_IN_SUPPLIERS_IS_EMPTY
import com.procurement.contracting.exception.ErrorType.PERSON_NOT_FOUND
import com.procurement.contracting.exception.ErrorType.SUPPLIERS
import com.procurement.contracting.exception.ErrorType.TRANSACTIONS
import com.procurement.contracting.infrastructure.handler.v1.CommandMessage
import com.procurement.contracting.infrastructure.handler.v1.country
import com.procurement.contracting.infrastructure.handler.v1.cpid
import com.procurement.contracting.infrastructure.handler.v1.language
import com.procurement.contracting.infrastructure.handler.v1.mainProcurementCategory
import com.procurement.contracting.infrastructure.handler.v1.model.request.AwardUpdate
import com.procurement.contracting.infrastructure.handler.v1.model.request.DetailsSupplierUpdate
import com.procurement.contracting.infrastructure.handler.v1.model.request.ItemUpdate
import com.procurement.contracting.infrastructure.handler.v1.model.request.OrganizationReferenceSupplierUpdate
import com.procurement.contracting.infrastructure.handler.v1.model.request.UpdateAcRq
import com.procurement.contracting.infrastructure.handler.v1.model.request.UpdateAcRs
import com.procurement.contracting.infrastructure.handler.v1.ocid
import com.procurement.contracting.infrastructure.handler.v1.owner
import com.procurement.contracting.infrastructure.handler.v1.pmd
import com.procurement.contracting.infrastructure.handler.v1.startDate
import com.procurement.contracting.infrastructure.handler.v1.token
import com.procurement.contracting.lib.errorIfBlank
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
import com.procurement.contracting.model.dto.ocds.PersonId
import com.procurement.contracting.model.dto.ocds.Planning
import com.procurement.contracting.model.dto.ocds.RelatedParty
import com.procurement.contracting.model.dto.ocds.RelatedPerson
import com.procurement.contracting.model.dto.ocds.Request
import com.procurement.contracting.model.dto.ocds.RequestGroup
import com.procurement.contracting.model.dto.ocds.ValueTax
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class UpdateAwardContractService(
    private val acRepository: AwardContractRepository,
    private val generationService: GenerationService,
    private val templateService: TemplateService
) {

    fun updateAC(cm: CommandMessage): UpdateAcRs {
        val cpid = cm.cpid
        val ocid = cm.ocid
        val token = cm.token
        val owner = cm.owner
        val country = cm.country
        val language = cm.language
        val pmd = cm.pmd
        val dateTime = cm.startDate
        val mpc = cm.mainProcurementCategory
        val dto = toObject(UpdateAcRq::class.java, cm.data)

        dto.validateTextAttributes()
        dto.validateDuplicates()

        checkTransactionsValue(dto)
        checkAwardSupplierPersones(dto.award)
        checkAwardSupplierPersonesBusinessFunctionsType(dto.award)
        val awardContractId = ocid.asAwardContractId()
        val entity: AwardContractEntity = acRepository.findBy(cpid, awardContractId)
            .orThrow { it.exception }
            ?: throw ErrorException(ErrorType.CONTRACT_NOT_FOUND)
        if (entity.owner != owner) throw ErrorException(error = INVALID_OWNER)
        if (entity.token != token) throw ErrorException(INVALID_TOKEN)
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
            //BR-9.2.20 + BR-9.2.20.1
            buyer = dto.buyer
                .apply {
                    copy(persones = persones.map { person -> person.generateId() })
                }
            funders = dto.funders//BR-9.2.20
            payers = dto.payers//BR-9.2.20
            treasuryBudgetSources = dto.treasuryBudgetSources//BR-9.2.24
        }

        val updatedContractEntity = entity.copy(
            status = contractProcess.contract.status,
            statusDetails = contractProcess.contract.statusDetails,
            jsonData = toJson(contractProcess)
        )

        val wasApplied = acRepository
            .updateStatusesAC(
                cpid = cpid,
                id = updatedContractEntity.id,
                status = updatedContractEntity.status,
                statusDetails = updatedContractEntity.statusDetails,
                jsonData = updatedContractEntity.jsonData
            )
            .orThrow { it.exception }
        if (!wasApplied)
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the save updated AC by cpid '${cpid}' and id '${updatedContractEntity.id}' with status '${updatedContractEntity.status}' and status details '${updatedContractEntity.statusDetails}' to the database. Record is not exists.")

        return UpdateAcRs(
            planning = contractProcess.planning!!,
            contract = contractProcess.contract,
            award = contractProcess.award,
            buyer = contractProcess.buyer!!
        )
    }

    private fun UpdateAcRq.validateTextAttributes() {
        planning.budget.description.checkForBlank("planning.budget.description")

        planning.implementation.transactions
            .forEachIndexed { transactionIdx, transaction ->
                transaction.id.checkForBlank("planning.implementation.transactions[$transactionIdx].id")
                transaction.relatedContractMilestone.checkForBlank("planning.implementation.transactions[$transactionIdx].id")
            }

        award.documents
            ?.forEachIndexed { index, document ->
                document.description.checkForBlank("award.documents[$index].description")
                document.title.checkForBlank("award.documents[$index].title")
            }

        award.items
            .forEachIndexed { index, item ->
                item.apply {
                    deliveryAddress.postalCode.checkForBlank("award.items[$index].deliveryAddress.postalCode")
                    deliveryAddress.streetAddress.checkForBlank("award.items[$index].deliveryAddress.streetAddress")
                    deliveryAddress.addressDetails
                        .apply {
                            locality.scheme.checkForBlank("award.items[$index].deliveryAddress.addressDetails.locality.scheme")
                            locality.id.checkForBlank("award.items[$index].deliveryAddress.addressDetails.locality.id")
                            locality.description.checkForBlank("award.items[$index].deliveryAddress.addressDetails.locality.description")
                        }
                }
            }

        award.suppliers
            .forEachIndexed{supplierIdx, supplier ->
                supplier.additionalIdentifiers
                    .forEachIndexed { additionalIdentifierIdx, additionalIdentifier ->
                        additionalIdentifier.scheme.checkForBlank("award.suppliers[$supplierIdx].additionalIdentifiers[$additionalIdentifierIdx].scheme")
                        additionalIdentifier.id.checkForBlank("award.suppliers[$supplierIdx].additionalIdentifiers[$additionalIdentifierIdx].id")
                        additionalIdentifier.legalName.checkForBlank("award.suppliers[$supplierIdx].additionalIdentifiers[$additionalIdentifierIdx].legalName")
                        additionalIdentifier.uri.checkForBlank("award.suppliers[$supplierIdx].additionalIdentifiers[$additionalIdentifierIdx].uri")
                    }

                supplier.details
                    .apply {
                        bankAccounts.forEachIndexed { bankAccountIdx, bankAccount ->
                            bankAccount.bankName.checkForBlank("award.suppliers[$supplierIdx].details.bankAccounts[$bankAccountIdx].bankName")
                            bankAccount.description.checkForBlank("award.suppliers[$supplierIdx].details.bankAccounts[$bankAccountIdx].description")

                            bankAccount.identifier.id.checkForBlank("award.suppliers[$supplierIdx].details.bankAccounts[$bankAccountIdx].id")
                            bankAccount.identifier.scheme.checkForBlank("award.suppliers[$supplierIdx].details.bankAccounts[$bankAccountIdx].scheme")

                            bankAccount.accountIdentification.scheme.checkForBlank("award.suppliers[$supplierIdx].details.bankAccounts[$bankAccountIdx].accountIdentification.scheme")
                            bankAccount.accountIdentification.id.checkForBlank("award.suppliers[$supplierIdx].details.bankAccounts[$bankAccountIdx].accountIdentification.id")
                            bankAccount.additionalAccountIdentifiers
                                ?.forEachIndexed { additionalAccountIdentifierIdx, additionalAccountIdentifier ->
                                    additionalAccountIdentifier.scheme.checkForBlank("award.suppliers[$supplierIdx].details.bankAccounts[$bankAccountIdx].additionalAccountIdentifiers[$additionalAccountIdentifierIdx].scheme")
                                    additionalAccountIdentifier.id.checkForBlank("award.suppliers[$supplierIdx].details.bankAccounts[$bankAccountIdx].additionalAccountIdentifiers[$additionalAccountIdentifierIdx].id")
                                }

                            bankAccount.address
                                .apply {
                                    postalCode.checkForBlank("award.suppliers[$supplierIdx].details.bankAccounts[$bankAccountIdx].address.postalCode")
                                    streetAddress.checkForBlank("award.suppliers[$supplierIdx].details.bankAccounts[$bankAccountIdx].address.streetAddress")

                                    addressDetails.apply {
                                        locality.scheme.checkForBlank("award.suppliers[$supplierIdx].details.bankAccounts[$bankAccountIdx].address.addressDetails.locality.scheme")
                                        locality.id.checkForBlank("award.suppliers[$supplierIdx].details.bankAccounts[$bankAccountIdx].address.addressDetails.locality.id")
                                        locality.description.checkForBlank("award.suppliers[$supplierIdx].details.bankAccounts[$bankAccountIdx].address.addressDetails.locality.description")
                                        locality.uri.checkForBlank("award.suppliers[$supplierIdx].details.bankAccounts[$bankAccountIdx].address.addressDetails.locality.uri")
                                    }
                                }

                        }

                        legalForm.description.checkForBlank("award.suppliers[$supplierIdx].details.legalForm.description")
                        legalForm.id.checkForBlank("award.suppliers[$supplierIdx].details.legalForm.id")
                        legalForm.scheme.checkForBlank("award.suppliers[$supplierIdx].details.legalForm.scheme")
                        legalForm.uri.checkForBlank("award.suppliers[$supplierIdx].details.legalForm.uri")

                        mainEconomicActivities.forEachIndexed { mainEconomicActivityIdx, mainEconomicActivity ->
                            mainEconomicActivity.scheme.checkForBlank("award.suppliers[$supplierIdx].details.mainEconomicActivities[$mainEconomicActivityIdx].scheme")
                            mainEconomicActivity.id.checkForBlank("award.suppliers[$supplierIdx].details.mainEconomicActivities[$mainEconomicActivityIdx].id")
                            mainEconomicActivity.description.checkForBlank("award.suppliers[$supplierIdx].details.mainEconomicActivities[$mainEconomicActivityIdx].description")
                            mainEconomicActivity.uri.checkForBlank("award.suppliers[$supplierIdx].details.mainEconomicActivities[$mainEconomicActivityIdx].uri")
                        }

                        permits?.forEachIndexed { permitIdx, permit ->
                            permit.scheme.checkForBlank("award.suppliers[$supplierIdx].details.permits[$permitIdx].scheme")
                            permit.id.checkForBlank("award.suppliers[$supplierIdx].details.permits[$permitIdx].id")
                            permit.url.checkForBlank("award.suppliers[$supplierIdx].details.permits[$permitIdx].url")

                            permit.permitDetails
                                .apply {
                                    issuedBy.id.checkForBlank("award.suppliers[$supplierIdx].details.permits[$permitIdx].permitDetails.issuedBy.id")
                                    issuedBy.name.checkForBlank("award.suppliers[$supplierIdx].details.permits[$permitIdx].permitDetails.issuedBy.name")

                                    issuedThought.id.checkForBlank("award.suppliers[$supplierIdx].details.permits[$permitIdx].permitDetails.issuedThought.id")
                                    issuedThought.name.checkForBlank("award.suppliers[$supplierIdx].details.permits[$permitIdx].permitDetails.issuedThought.name")
                                }
                        }

                        scale.checkForBlank("award.suppliers[$supplierIdx].details.scale")
                        typeOfSupplier.checkForBlank("award.suppliers[$supplierIdx].details.typeOfSupplier")
                    }

                supplier.persones
                    .forEachIndexed{personIdx, person ->
                        person.name.checkForBlank("award.suppliers[$supplierIdx].persones[$personIdx].name")
                        person.title.checkForBlank("award.suppliers[$supplierIdx].persones[$personIdx].title")

                        person.identifier.id.checkForBlank("award.suppliers[$supplierIdx].persones[$personIdx].identifier.id")
                        person.identifier.scheme.checkForBlank("award.suppliers[$supplierIdx].persones[$personIdx].identifier.scheme")
                        person.identifier.uri.checkForBlank("award.suppliers[$supplierIdx].persones[$personIdx].identifier.uri")

                        person.businessFunctions
                            .forEachIndexed{businessFunctionIdx, businessFunction ->
                                businessFunction.id.checkForBlank("award.suppliers[$supplierIdx].persones[$personIdx].businessFunctions[$businessFunctionIdx].id")
                                businessFunction.jobTitle.checkForBlank("award.suppliers[$supplierIdx].persones[$personIdx].businessFunctions[$businessFunctionIdx].jobTitle")

                                businessFunction.documents
                                    .forEachIndexed { documentIdx, document ->
                                        document.description.checkForBlank("award.suppliers[$supplierIdx].persones[$personIdx].businessFunctions[$businessFunctionIdx].documents[$documentIdx].description")
                                        document.title.checkForBlank("award.suppliers[$supplierIdx].persones[$personIdx].businessFunctions[$businessFunctionIdx].documents[$documentIdx].title")
                                    }
                            }
                    }
            }

        buyer.apply {
            additionalIdentifiers
                .forEachIndexed {additionalIdentifierIdx, additionalIdentifier ->
                additionalIdentifier.scheme.checkForBlank("buyer.additionalIdentifiers[$additionalIdentifierIdx].scheme")
                additionalIdentifier.id.checkForBlank("buyer.additionalIdentifiers[$additionalIdentifierIdx].id")
                additionalIdentifier.legalName.checkForBlank("buyer.additionalIdentifiers[$additionalIdentifierIdx].legalName")
                additionalIdentifier.uri.checkForBlank("buyer.additionalIdentifiers[$additionalIdentifierIdx].uri")
            }

            details.apply {
                bankAccounts
                    .forEachIndexed { bankAccountIdx, bankAccount ->
                        bankAccount.bankName.checkForBlank("buyer.details.bankAccounts[$bankAccountIdx].bankName")
                        bankAccount.description.checkForBlank("buyer.details.bankAccounts[$bankAccountIdx].bankName")


                        bankAccount.accountIdentification.scheme.checkForBlank("buyer.details.bankAccounts[$bankAccountIdx].accountIdentification.scheme")
                        bankAccount.accountIdentification.id.checkForBlank("buyer.details.bankAccounts[$bankAccountIdx].accountIdentification.id")

                        bankAccount.additionalAccountIdentifiers
                            ?.forEachIndexed { accountIdentifierIdx, accountIdentifier ->
                                accountIdentifier.id.checkForBlank("buyer.details.bankAccounts[$bankAccountIdx].additionalAccountIdentifiers[$accountIdentifierIdx].id")
                                accountIdentifier.scheme.checkForBlank("buyer.details.bankAccounts[$bankAccountIdx].additionalAccountIdentifiers[$accountIdentifierIdx].scheme")
                        }

                        bankAccount.address
                            .apply {
                                postalCode.checkForBlank("buyer.details.bankAccounts[$bankAccountIdx].address.postalCode")
                                streetAddress.checkForBlank("buyer.details.bankAccounts[$bankAccountIdx].address.streetAddress")

                                addressDetails.apply {
                                    locality.scheme.checkForBlank("buyer.details.bankAccounts[$bankAccountIdx].address.addressDetails.locality.scheme")
                                    locality.id.checkForBlank("buyer.details.bankAccounts[$bankAccountIdx].address.addressDetails.locality.id")
                                    locality.description.checkForBlank("buyer.details.bankAccounts[$bankAccountIdx].address.addressDetails.locality.description")
                                    locality.uri.checkForBlank("buyer.details.bankAccounts[$bankAccountIdx].address.addressDetails.locality.uri")
                                }
                            }
                    }
            }

            persones
                .forEachIndexed {personIdx, person ->
                    person.name.checkForBlank("buyer.persones[$personIdx].name")
                    person.title.checkForBlank("buyer.persones[$personIdx].title")

                    person.identifier
                        .apply {
                            scheme.checkForBlank("buyer.persones[$personIdx].identifier.scheme")
                            id.checkForBlank("buyer.persones[$personIdx].identifier.id")
                            uri.checkForBlank("buyer.persones[$personIdx].identifier.uri")
                        }

                    person.businessFunctions
                        .forEachIndexed { businessFunctionIdx, businessFunction ->
                            businessFunction.id.checkForBlank("buyer.persones[$personIdx].businessFunctions[$businessFunctionIdx].id")
                            businessFunction.jobTitle.checkForBlank("buyer.persones[$personIdx].businessFunctions[$businessFunctionIdx].jobTitle")

                            businessFunction.documents
                                .forEachIndexed { documentIdx, document ->
                                    document.description.checkForBlank("buyer.persones[$personIdx].businessFunctions[$businessFunctionIdx].documents[$documentIdx].description")
                                    document.title.checkForBlank("buyer.persones[$personIdx].businessFunctions[$businessFunctionIdx].documents[$documentIdx].title")
                                }
                        }
                }


        }

        contract.apply {
            title.checkForBlank("contract.title")
            description.checkForBlank("contract.description")

            agreedMetrics.forEachIndexed { agreedMetricIdx, agreedMetric ->
                agreedMetric.observations
                    ?.forEachIndexed { observationIdx, observation ->
                        val measure = observation.measure
                            if(measure is String)
                                measure.checkForBlank("contract.agreedMetrics[$observationIdx].measure")
                    }
            }

            confirmationRequests?.forEach {  confirmationRequest->
                confirmationRequest.id.checkForBlank("contract.confirmationRequests")
            }

            documents?.forEachIndexed {documentIdx, document ->
                document.description.checkForBlank("contract.documents[$documentIdx].description")
                document.title.checkForBlank("contract.documents[$documentIdx].title")
            }

            milestones.forEachIndexed { milestoneIdx, milestone ->
                milestone.id.checkForBlank("contract.milestones[$milestoneIdx].id")
                milestone.title.checkForBlank("contract.milestones[$milestoneIdx].title")
                milestone.description.checkForBlank("contract.milestones[$milestoneIdx].description")
                milestone.additionalInformation.checkForBlank("contract.milestones[$milestoneIdx].additionalInformation")
            }
        }
    }

    private fun String?.checkForBlank(name: String) = this.errorIfBlank {
        ErrorException(
            error = ErrorType.INCORRECT_VALUE_ATTRIBUTE,
            message = "The attribute '$name' is empty or blank."
        )
    }

    private fun UpdateAcRq.validateDuplicates() {
        buyer.additionalIdentifiers
            .apply {
                val duplicate = getDuplicate { it.scheme.toUpperCase() + it.id.toUpperCase() }
                if (duplicate != null)
                    throw ErrorException(
                        error = ErrorType.DUPLICATE,
                        message = "Attribute 'buyer.additionalIdentifiers' has duplicate by scheme '${duplicate.scheme}' and id '${duplicate.id}'."
                    )
            }

        buyer.persones
            .apply {
                val duplicatePersons = getDuplicate { it.id }
                if (duplicatePersons != null)
                    throw ErrorException(
                        error = ErrorType.DUPLICATE,
                        message = "Attribute 'buyer.persones' has duplicate by id '${duplicatePersons.id}'."
                    )

                forEachIndexed { personIdx, person ->
                    val duplicateBusinessFunctions = person.businessFunctions.getDuplicate { it.id }
                    if (duplicateBusinessFunctions != null)
                        throw ErrorException(
                            error = ErrorType.DUPLICATE,
                            message = "Attribute 'buyer.persones[$personIdx].businessFunctions' has duplicate id '${duplicateBusinessFunctions.id}'."
                        )

                    person.businessFunctions
                        .forEachIndexed { businessFunctionIdx, businessFunction ->
                            val duplicateDocuments = businessFunction.documents.getDuplicate { it.id }
                            if (duplicateDocuments != null)
                                throw ErrorException(
                                    error = ErrorType.DUPLICATE,
                                    message = "Attribute 'buyer.persones[$personIdx].businessFunctions[$businessFunctionIdx].documents' has duplicate id '${duplicateDocuments.id}'."
                                )
                        }

                }
            }

        buyer.details
            .apply {
                val duplicateBankAccounts = buyer.details.bankAccounts
                    .getDuplicate { it.identifier.scheme.toUpperCase() + it.identifier.id.toUpperCase() }
                if (duplicateBankAccounts != null)
                    throw ErrorException(
                        error = ErrorType.DUPLICATE,
                        message = "Attribute 'buyer.details.bankAccounts' has duplicate by scheme '${duplicateBankAccounts.identifier.scheme}' and id '${duplicateBankAccounts.identifier.id}'."
                    )

                bankAccounts.forEachIndexed { bankAccountIdx, bankAccount ->
                    val duplicateAdditionalAccountIdentifiers =
                        bankAccount.additionalAccountIdentifiers.getDuplicate { it.scheme.toUpperCase() + it.id.toUpperCase() }
                    if (duplicateAdditionalAccountIdentifiers != null)
                        throw ErrorException(
                            error = ErrorType.DUPLICATE,
                            message = "Attribute 'buyer.details.bankAccounts[$bankAccountIdx].additionalAccountIdentifiers' has duplicate by scheme '${duplicateAdditionalAccountIdentifiers.scheme}' and id '${duplicateAdditionalAccountIdentifiers.id}'."
                        )
                }
            }

        contract.confirmationRequests
            .apply {
                val duplicate = getDuplicate { it.id.toUpperCase() }
                if (duplicate != null)
                    throw ErrorException(
                        error = ErrorType.DUPLICATE,
                        message = "Attribute 'contract.confirmationRequests' has duplicate by id '${duplicate.id}'."
                    )
            }

        award.suppliers
            .forEachIndexed { supplierIdx, supplier ->
                val duplicateAdditionalIdentifiers = supplier.additionalIdentifiers
                    .getDuplicate { it.scheme.toUpperCase() + it.id.toUpperCase() }
                if (duplicateAdditionalIdentifiers != null)
                    throw ErrorException(
                        error = ErrorType.DUPLICATE,
                        message = "Attribute 'award.suppliers[$supplierIdx].additionalIdentifiers' has duplicate by scheme '${duplicateAdditionalIdentifiers.scheme}' and id '${duplicateAdditionalIdentifiers.id}'."
                    )

                supplier.details
                    .apply {
                        val duplicateMainEconomicActivities =
                            mainEconomicActivities.getDuplicate { it.scheme.toUpperCase() + it.id.toUpperCase() }
                        if (duplicateMainEconomicActivities != null)
                            throw ErrorException(
                                error = ErrorType.DUPLICATE,
                                message = "Attribute 'award.suppliers[$supplierIdx].mainEconomicActivities' has duplicate by scheme '${duplicateMainEconomicActivities.scheme}' and id '${duplicateMainEconomicActivities.id}'."
                            )

                        val duplicatePermits = permits?.getDuplicate { it.scheme.toUpperCase() + it.id.toUpperCase() }
                        if (duplicatePermits != null)
                            throw ErrorException(
                                error = ErrorType.DUPLICATE,
                                message = "Attribute 'award.suppliers[$supplierIdx].permits' has duplicate by scheme '${duplicatePermits.scheme}' and id '${duplicatePermits.id}'."
                            )

                        val duplicateBankAccounts =
                            bankAccounts.getDuplicate { it.identifier.scheme.toUpperCase() + it.identifier.id.toUpperCase() }
                        if (duplicateBankAccounts != null)
                            throw ErrorException(
                                error = ErrorType.DUPLICATE,
                                message = "Attribute 'award.suppliers[$supplierIdx].bankAccounts' has duplicate by scheme '${duplicateBankAccounts.identifier.scheme}' and id '${duplicateBankAccounts.identifier.id}'."
                            )

                        bankAccounts.forEachIndexed { bankAccountIdx, bankAccount ->
                            val duplicateAdditionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                .getDuplicate { it.scheme.toUpperCase() + it.id.toUpperCase() }
                            if (duplicateAdditionalAccountIdentifiers != null)
                                throw ErrorException(
                                    error = ErrorType.DUPLICATE,
                                    message = "Attribute 'award.suppliers[$supplierIdx].bankAccounts[$bankAccountIdx].additionalAccountIdentifiers' has duplicate by scheme '${duplicateAdditionalAccountIdentifiers.scheme}' and id '${duplicateAdditionalAccountIdentifiers.id}'."
                                )
                        }
                    }

            }

        planning.budget.budgetAllocation
            .apply {
                val duplicate = getDuplicate { it.budgetBreakdownID }
                if (duplicate != null)
                    throw ErrorException(
                        error = ErrorType.DUPLICATE,
                        message = "Attribute 'planning.budget.budgetAllocation' has duplicate by budgetBreakdownID '${duplicate}'."
                    )
            }

        planning.budget.budgetSource
            .apply {
                val duplicate = getDuplicate { it.budgetBreakdownID }
                if (duplicate != null)
                    throw ErrorException(
                        error = ErrorType.DUPLICATE,
                        message = "Attribute 'planning.budget.budgetSource' has duplicate by budgetBreakdownID '${duplicate}'."
                    )
            }
    }

    /**
     * VR-9.2.13
     */
    private fun checkTransactionsValue(dto: UpdateAcRq) {
        val currency: String = dto.planning.budget.budgetSource
            .let { budgetSources ->
                val uniqueCurrency: Set<String> = budgetSources.asSequence()
                    .map { it.currency }
                    .toSet()
                if (uniqueCurrency.size != 1)
                    throw ErrorException(
                        error = ErrorType.INVALID_CURRENCY,
                        message = "Invalid currency of 'planning.budget.budgetSource' object."
                    )
                budgetSources[0].currency
            }



        dto.planning.implementation.transactions.forEach { transaction ->
            if (transaction.value.amount <= BigDecimal.ZERO)
                throw ErrorException(
                    error = ErrorType.INVALID_AMOUNT,
                    message = "Invalid amount of 'implementation.transaction.value' object."
                )
            if (transaction.value.currency != currency)
                throw ErrorException(
                    error = ErrorType.INVALID_CURRENCY,
                    message = "Invalid currency of 'implementation.transaction.value' object."
                )
        }
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
        val documentDtoIds = documentsDto.toSetBy { it.id }
        if (documentDtoIds.size != documentsDto.size) throw ErrorException(DOCUMENTS)
        //update
        val documentsDb = contractProcess.contract.documents ?: return documentsDto
        val documentsDbIds = documentsDb.toSetBy { it.id }
        documentsDb.forEach { docDb -> docDb.update(documentsDto.firstOrNull { it.id == docDb.id }) }
        val newDocumentsId = documentDtoIds - documentsDbIds
        val newDocuments = documentsDto.filter { it.id in newDocumentsId }
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
        val milestonesDtoIds = milestonesDto.toSetBy { it.id }
        val milestonesDbIds = milestonesDb.toSetBy { it.id }
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

        val milestonesIdSet = milestonesDto.toSetBy { it.id }
        if (milestonesIdSet.size != milestonesDto.size) throw ErrorException(MILESTONE_ID)

        val milestonesFromTrSet = transactions.asSequence()
                .filter { it.type != TransactionType.ADVANCE }
                .map { it.relatedContractMilestone ?: throw ErrorException(INVALID_TR_RELATED_MILESTONES) }.toSet()

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
        val awardItemIds: Set<ItemId> = dto.award.items.toSetBy { it.id }
        if (!awardItemIds.containsAll(relatedItemIds)) throw ErrorException(MILESTONE_RELATED_ITEMS)
    }

    private fun updateConfirmationRequests(dto: UpdateAcRq,
                                           documents: List<DocumentContract>?,
                                           country: String,
                                           pmd: ProcurementMethodDetails,
                                           language: String): MutableList<ConfirmationRequest>? {
        val confRequestDto = dto.contract.confirmationRequests
        if (confRequestDto != null) {
            //validation
            val relatedItemIds = confRequestDto.toSetBy { it.relatedItem }
            val documentIds = documents?.toSetBy { it.id }.orEmpty()
            if (!documentIds.containsAll(relatedItemIds)) throw ErrorException(CONFIRMATION_ITEM)

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

    private fun getPersonByBFType(persones: List<Person>, type: String): RelatedPerson? {
        for (person in persones) {
            if (person.businessFunctions.any { it.type == type }) {
                return RelatedPerson(id = person.identifier.id, name = person.name)
            }
        }
        return null
    }

    private fun setStatusDetails(contractStatusDetails: AwardContractStatusDetails): AwardContractStatusDetails {
        return when (contractStatusDetails) {
            AwardContractStatusDetails.CONTRACT_PROJECT -> AwardContractStatusDetails.CONTRACT_PREPARATION
            AwardContractStatusDetails.CONTRACT_PREPARATION -> AwardContractStatusDetails.CONTRACT_PREPARATION
            else -> throw ErrorException(CONTRACT_STATUS_DETAILS)
        }
    }

    private fun validateUpdatePlanning(dto: UpdateAcRq): Planning {
        //BR-9.2.6
        if (dto.planning.budget.budgetSource.any { it.currency != dto.award.value.currency }) throw ErrorException(BS_CURRENCY)
        val transactions = dto.planning.implementation.transactions
        if (transactions.isEmpty()) throw ErrorException(TRANSACTIONS)
        val transactionsId = transactions.toSetBy { it.id }
        if (transactionsId.size != transactions.size) throw ErrorException(TRANSACTIONS)
        transactions.forEach { it.id = generationService.getTimeBasedUUID() }
        //BR-9.2.7
        val relatedItemIds = dto.planning.budget.budgetAllocation.toSetBy { it.relatedItem }
        val awardItemIds = dto.award.items.toSetBy { it.id }
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
        val suppliersDbIds: Set<OrganizationId> = suppliersDb.toSetBy { it.id }
        val suppliersDtoIds: Set<OrganizationId> = suppliersDto.toSetBy { it.id }
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

    private fun updatePersones(savedPersons: List<Person>?, receivedPersons: List<Person>): List<Person> {
        val receivedPersonsWithId = receivedPersons.map { person -> person.generateId() }

        if (savedPersons == null || savedPersons.isEmpty())
            return receivedPersonsWithId

        val receivedPersonByIds = receivedPersonsWithId.associateBy { it.id!! }
        val receivedPersonIds = receivedPersonByIds.keys
        if (receivedPersonIds.size != receivedPersons.size) throw ErrorException(ErrorType.PERSONES)

        val savedPersonByIds = savedPersons.associateBy { it.id!! }
        val savedPersonIds = savedPersonByIds.keys

        val newPersons = getNewElements(received = receivedPersonIds, known = savedPersonIds)
            .map { id -> receivedPersonByIds.getValue(id) }

        val updatedPersons = getElementsForUpdate(received = receivedPersonIds, known = savedPersonIds)
            .map { id ->
                val receivedPerson = receivedPersonByIds.getValue(id)
                savedPersonByIds.getValue(id)
                    .apply { update(receivedPerson) }
            }

        return newPersons + updatedPersons
    }

    private fun Person.generateId(): Person = this.apply {
        id = PersonId.generate(
            scheme = identifier.scheme,
            id = identifier.id
        )
    }

    private fun Person.update(personDto: Person) {
        this.title = personDto.title
        this.name = personDto.name
        this.businessFunctions = updateBusinessFunctions(this.businessFunctions, personDto.businessFunctions)
    }

    private fun updateBusinessFunctions(bfDb: List<BusinessFunction>, bfDto: List<BusinessFunction>): List<BusinessFunction> {
        val bfDbIds = bfDb.toSetBy { it.id }
        val bfDtoIds = bfDto.toSetBy { it.id }
        if (bfDtoIds.size != bfDto.size) throw ErrorException(BF)
        //update
        bfDb.forEach { businessFunction -> businessFunction.update(bfDto.firstOrNull { it.id == businessFunction.id }) }
        val newBfId = bfDtoIds - bfDbIds
        val newBf = bfDto.asSequence().filter { it.id in newBfId }.toSet()
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
        val documentsDbIds = documentsDb.toSetBy { it.id }
        val documentDtoIds = documentsDto.toSetBy { it.id }
        if (documentDtoIds.size != documentsDto.size) throw ErrorException(DOCUMENTS)
        //update
        documentsDb.forEach { docDb -> docDb.update(documentsDto.firstOrNull { it.id == docDb.id }) }
        val newDocumentsId = documentDtoIds - documentsDbIds
        val newDocuments = documentsDto.filter { it.id in newDocumentsId }
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
            val documentDtoIds = documentsDto.toSetBy { it.id }
            if (documentDtoIds.size != documentsDto.size) throw ErrorException(DOCUMENTS)
            //update
            return if (documentsDb != null) {
                val documentsDbIds = documentsDb.toSetBy { it.id }
                documentsDb.forEach { docDb -> docDb.update(documentsDto.firstOrNull { it.id == docDb.id }) }
                val newDocumentsId = documentDtoIds - documentsDbIds
                val newDocuments: List<DocumentAward> = documentsDto.filter { it.id in newDocumentsId }
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
        val itemDbIds = itemsDb.toSetBy { it.id }
        val itemDtoIds = itemsDto.toSetBy { it.id }
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
        val awardRelatedLotsDb = contractProcess.award.relatedLots.toSet()
        val awardDocumentsDto = dto.award.documents
        if (awardDocumentsDto != null) {
            val lotsFromAwardDocuments = awardDocumentsDto.asSequence()
                    .filter { it.relatedLots != null }
                    .flatMap { it.relatedLots!!.asSequence() }
                    .toSet()
            if (awardRelatedLotsDb.isNotEmpty()) {
                if (!awardRelatedLotsDb.containsAll(lotsFromAwardDocuments)) throw ErrorException(INVALID_DOCS_RELATED_LOTS)
            }
        }
        val contractDocumentsDto = dto.contract.documents
        if (contractDocumentsDto != null) {
            val lotsFromContractDocuments = contractDocumentsDto.asSequence()
                    .filter { it.relatedLots != null }
                    .flatMap { it.relatedLots!!.asSequence() }
                    .toSet()
            if (lotsFromContractDocuments.isNotEmpty()) {
                if (!awardRelatedLotsDb.containsAll(lotsFromContractDocuments)) throw ErrorException(INVALID_DOCS_RELATED_LOTS)
            }
        }
    }
}
