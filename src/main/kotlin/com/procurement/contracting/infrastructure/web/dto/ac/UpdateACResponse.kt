package com.procurement.contracting.infrastructure.web.dto.ac

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestSource
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestType
import com.procurement.contracting.domain.model.contract.status.ContractStatus
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails
import com.procurement.contracting.domain.model.document.type.DocumentTypeAward
import com.procurement.contracting.domain.model.document.type.DocumentTypeBF
import com.procurement.contracting.domain.model.document.type.DocumentTypeContract
import com.procurement.contracting.domain.model.milestone.status.MilestoneStatus
import com.procurement.contracting.domain.model.milestone.type.MilestoneType
import com.procurement.contracting.domain.model.transaction.type.TransactionType
import com.procurement.contracting.infrastructure.amount.Amount3Deserializer
import com.procurement.contracting.infrastructure.amount.Amount3Serializer
import com.procurement.contracting.infrastructure.amount.AmountDeserializer
import com.procurement.contracting.infrastructure.amount.AmountSerializer
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class UpdateACResponse (
    @field:JsonProperty("planning") @param:JsonProperty("planning") val planning: Planning,
    @field:JsonProperty("contract") @param:JsonProperty("contract") val contract: Contract,
    @field:JsonProperty("contractedAward") @param:JsonProperty("contractedAward") val contractedAward: ContractedAward
) {

    data class Planning(
        @field:JsonProperty("implementation") @param:JsonProperty("implementation") val implementation: Implementation,
        @field:JsonProperty("budget") @param:JsonProperty("budget") val budget: Budget
    ) {
        data class Implementation(
            @field:JsonProperty("transactions") @param:JsonProperty("transactions") val transactions: List<Transaction>
        ) {
            data class Transaction(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,
                @field:JsonProperty("type") @param:JsonProperty("type") val type: TransactionType,
                @field:JsonProperty("value") @param:JsonProperty("value") val value: Value,
                @field:JsonProperty("executionPeriod") @param:JsonProperty("executionPeriod") val executionPeriod: ExecutionPeriod,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("relatedContractMilestone") @param:JsonProperty("relatedContractMilestone") val relatedContractMilestone: String?
            ) {
                data class Value(

                    @JsonDeserialize(using = AmountDeserializer::class)
                    @JsonSerialize(using = AmountSerializer::class)
                    @field:JsonProperty("amount") @param:JsonProperty("amount") val amount: BigDecimal,

                    @field:JsonProperty("currency") @param:JsonProperty("currency") val currency: String
                )

                data class ExecutionPeriod(
                    @field:JsonProperty("durationInDays") @param:JsonProperty("durationInDays") val durationInDays: Int
                )
            }
        }

        data class Budget(
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
            @field:JsonProperty("budgetAllocation") @param:JsonProperty("budgetAllocation") val budgetAllocation: List<BudgetAllocation>,
            @field:JsonProperty("budgetSource") @param:JsonProperty("budgetSource") val budgetSource: List<BudgetSource>
        ) {
            data class BudgetAllocation(
                @field:JsonProperty("budgetBreakdownID") @param:JsonProperty("budgetBreakdownID") val budgetBreakdownID: String,
                @field:JsonProperty("period") @param:JsonProperty("period") val period: Period,

                @JsonDeserialize(using = AmountDeserializer::class)
                @JsonSerialize(using = AmountSerializer::class)
                @field:JsonProperty("amount") @param:JsonProperty("amount") val amount: BigDecimal,

                @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: UUID
            ) {
                data class Period(
                    @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime,
                    @field:JsonProperty("endDate") @param:JsonProperty("endDate") val endDate: LocalDateTime
                )
            }

            data class BudgetSource(
                @field:JsonProperty("budgetBreakdownID") @param:JsonProperty("budgetBreakdownID") val budgetBreakdownID: String,

                @JsonDeserialize(using = AmountDeserializer::class)
                @JsonSerialize(using = AmountSerializer::class)
                @field:JsonProperty("amount") @param:JsonProperty("amount") val amount: BigDecimal,

                @field:JsonProperty("currency") @param:JsonProperty("currency") val currency: String
            )
        }
    }

    data class Contract(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("awardID") @param:JsonProperty("awardID") val awardID: UUID,
        @field:JsonProperty("status") @param:JsonProperty("status") val status: ContractStatus,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: ContractStatusDetails,
        @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
        @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
        @field:JsonProperty("period") @param:JsonProperty("period") val period: Period,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>?,

        @field:JsonProperty("milestones") @param:JsonProperty("milestones") val milestones: List<Milestone>,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("confirmationRequests") @param:JsonProperty("confirmationRequests") val confirmationRequests: List<ConfirmationRequest>?,
        @field:JsonProperty("value") @param:JsonProperty("value") val value: Value
    ) {


        data class Period(
            @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime,
            @field:JsonProperty("endDate") @param:JsonProperty("endDate") val endDate: LocalDateTime
        )

        data class Document(
            @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentTypeContract,
            @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

            @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<UUID>
        )

        data class Milestone(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("relatedItems") @param:JsonProperty("relatedItems") val relatedItems: List<UUID>,
            @field:JsonProperty("status") @param:JsonProperty("status") val status: MilestoneStatus,
            @field:JsonProperty("additionalInformation") @param:JsonProperty("additionalInformation") val additionalInformation: String,
            @field:JsonProperty("dueDate") @param:JsonProperty("dueDate") val dueDate: LocalDateTime,
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
            @field:JsonProperty("type") @param:JsonProperty("type") val type: MilestoneType,
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
            @field:JsonProperty("relatedParties") @param:JsonProperty("relatedParties") val relatedParties: List<RelatedParty>
        ) {
            data class RelatedParty(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("name") @param:JsonProperty("name") val name: String
            )
        }

        data class ConfirmationRequest(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("type") @param:JsonProperty("type") val type: ConfirmationRequestType,
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
            @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: String,
            @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: UUID,
            @field:JsonProperty("source") @param:JsonProperty("source") val source: ConfirmationRequestSource,
            @field:JsonProperty("requestGroups") @param:JsonProperty("requestGroups") val requestGroups: List<RequestGroup>
        ) {
            data class RequestGroup(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("requests") @param:JsonProperty("requests") val requests: List<Request>
            ) {
                data class Request(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
                    @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                    @field:JsonProperty("relatedPerson") @param:JsonProperty("relatedPerson") val relatedPerson: RelatedPerson
                ) {
                    data class RelatedPerson(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,
                        @field:JsonProperty("name") @param:JsonProperty("name") val name: String
                    )
                }
            }
        }

        data class Value(

            @JsonDeserialize(using = AmountDeserializer::class)
            @JsonSerialize(using = AmountSerializer::class)
            @field:JsonProperty("amount") @param:JsonProperty("amount") val amount: BigDecimal,

            @field:JsonProperty("currency") @param:JsonProperty("currency") val currency: String,

            @JsonDeserialize(using = AmountDeserializer::class)
            @JsonSerialize(using = AmountSerializer::class)
            @field:JsonProperty("amountNet") @param:JsonProperty("amountNet") val amountNet: BigDecimal,

            @field:JsonProperty("valueAddedTaxIncluded") @param:JsonProperty("valueAddedTaxIncluded") val valueAddedTaxIncluded: Boolean
        )
    }

    data class ContractedAward(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,
        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

        @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<UUID>,
        @field:JsonProperty("relatedBid") @param:JsonProperty("relatedBid") val relatedBid: UUID,
        @field:JsonProperty("suppliers") @param:JsonProperty("suppliers") val suppliers: List<Supplier>,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>?,

        @field:JsonProperty("value") @param:JsonProperty("value") val value: Value,
        @field:JsonProperty("items") @param:JsonProperty("items") val items: List<Item>
    ) {

        data class Supplier(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
            @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier,
            @field:JsonProperty("address") @param:JsonProperty("address") val address: Address,
            @field:JsonProperty("contactPoint") @param:JsonProperty("contactPoint") val contactPoint: ContactPoint,
            @field:JsonProperty("additionalIdentifiers") @param:JsonProperty("additionalIdentifiers") val additionalIdentifiers: List<AdditionalIdentifier>,
            @field:JsonProperty("persones") @param:JsonProperty("persones") val persones: List<Persone>,
            @field:JsonProperty("details") @param:JsonProperty("details") val details: Details
        ) {

            data class Identifier(
                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("legalName") @param:JsonProperty("legalName") val legalName: String,
                @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String
            )

            data class Address(
                @field:JsonProperty("streetAddress") @param:JsonProperty("streetAddress") val streetAddress: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("postalCode") @param:JsonProperty("postalCode") val postalCode: String?,

                @field:JsonProperty("addressDetails") @param:JsonProperty("addressDetails") val addressDetails: AddressDetails
            ) {
                data class AddressDetails(
                    @field:JsonProperty("country") @param:JsonProperty("country") val country: Country,
                    @field:JsonProperty("region") @param:JsonProperty("region") val region: Region,
                    @field:JsonProperty("locality") @param:JsonProperty("locality") val locality: Locality
                ) {
                    data class Country(
                        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                        @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                        @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String
                    )

                    data class Region(
                        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                        @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                        @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String
                    )

                    data class Locality(
                        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                        @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
                    )
                }
            }

            data class ContactPoint(
                @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
                @field:JsonProperty("email") @param:JsonProperty("email") val email: String,
                @field:JsonProperty("telephone") @param:JsonProperty("telephone") val telephone: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("faxNumber") @param:JsonProperty("faxNumber") val faxNumber: String?,

                @field:JsonProperty("url") @param:JsonProperty("url") val url: String
            )

            data class AdditionalIdentifier(
                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("legalName") @param:JsonProperty("legalName") val legalName: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
            )

            data class Persone(
                @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
                @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
                @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier,
                @field:JsonProperty("businessFunctions") @param:JsonProperty("businessFunctions") val businessFunctions: List<BusinessFunction>
            ) {
                data class Identifier(
                    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
                )

                data class BusinessFunction(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("type") @param:JsonProperty("type") val type: String,
                    @field:JsonProperty("jobTitle") @param:JsonProperty("jobTitle") val jobTitle: String,
                    @field:JsonProperty("period") @param:JsonProperty("period") val period: Period,
                    @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>
                ) {
                    data class Period(
                        @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime
                    )

                    data class Document(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,
                        @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentTypeBF,
                        @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("description") @param:JsonProperty("description") val description: String?
                    )
                }
            }

            data class Details(
                @field:JsonProperty("typeOfSupplier") @param:JsonProperty("typeOfSupplier") val typeOfSupplier: String,
                @field:JsonProperty("mainEconomicActivities") @param:JsonProperty("mainEconomicActivities") val mainEconomicActivities: List<String>,
                @field:JsonProperty("scale") @param:JsonProperty("scale") val scale: String,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @field:JsonProperty("permits") @param:JsonProperty("permits") val permits: List<Permit>?,

                @field:JsonProperty("bankAccounts") @param:JsonProperty("bankAccounts") val bankAccounts: List<BankAccount>,
                @field:JsonProperty("legalForm") @param:JsonProperty("legalForm") val legalForm: LegalForm
            ) {
                data class Permit(
                    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("url") @param:JsonProperty("url") val url: String,
                    @field:JsonProperty("permitDetails") @param:JsonProperty("permitDetails") val permitDetails: PermitDetails
                ) {
                    data class PermitDetails(
                        @field:JsonProperty("issuedBy") @param:JsonProperty("issuedBy") val issuedBy: IssuedBy,
                        @field:JsonProperty("issuedThought") @param:JsonProperty("issuedThought") val issuedThought: IssuedThought,
                        @field:JsonProperty("validityPeriod") @param:JsonProperty("validityPeriod") val validityPeriod: ValidityPeriod
                    ) {
                        data class IssuedThought(
                            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                            @field:JsonProperty("name") @param:JsonProperty("name") val name: String
                        )

                        data class IssuedBy(
                            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                            @field:JsonProperty("name") @param:JsonProperty("name") val name: String
                        )

                        data class ValidityPeriod(
                            @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @field:JsonProperty("endDate") @param:JsonProperty("endDate") val endDate: LocalDateTime?
                        )
                    }
                }

                data class BankAccount(
                    @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                    @field:JsonProperty("bankName") @param:JsonProperty("bankName") val bankName: String,
                    @field:JsonProperty("address") @param:JsonProperty("address") val address: Address,
                    @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier,
                    @field:JsonProperty("accountIdentification") @param:JsonProperty("accountIdentification") val accountIdentification: AccountIdentification,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @field:JsonProperty("additionalAccountIdentifiers") @param:JsonProperty("additionalAccountIdentifiers") val additionalAccountIdentifiers: List<AdditionalAccountIdentifier>?
                ) {
                    data class Address(
                        @field:JsonProperty("streetAddress") @param:JsonProperty("streetAddress") val streetAddress: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("postalCode") @param:JsonProperty("postalCode") val postalCode: String?,

                        @field:JsonProperty("addressDetails") @param:JsonProperty("addressDetails") val addressDetails: AddressDetails
                    ) {
                        data class AddressDetails(
                            @field:JsonProperty("country") @param:JsonProperty("country") val country: Country,
                            @field:JsonProperty("region") @param:JsonProperty("region") val region: Region,
                            @field:JsonProperty("locality") @param:JsonProperty("locality") val locality: Locality
                        ) {
                            data class Country(
                                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                                @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                                @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String
                            )

                            data class Region(
                                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                                @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                                @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String
                            )

                            data class Locality(
                                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                                @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
                            )
                        }
                    }

                    data class Identifier(
                        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String
                    )

                    data class AccountIdentification(
                        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String
                    )

                    data class AdditionalAccountIdentifier(
                        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String
                    )

                }

                data class LegalForm(
                    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
                )

            }

        }

        data class Document(
            @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentTypeAward,
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<UUID>?
        )

        data class Value(

            @JsonDeserialize(using = AmountDeserializer::class)
            @JsonSerialize(using = AmountSerializer::class)
            @field:JsonProperty("amount") @param:JsonProperty("amount") val amount: BigDecimal,

            @field:JsonProperty("currency") @param:JsonProperty("currency") val currency: String,

            @JsonDeserialize(using = AmountDeserializer::class)
            @JsonSerialize(using = AmountSerializer::class)
            @field:JsonProperty("amountNet") @param:JsonProperty("amountNet") val amountNet: BigDecimal,

            @field:JsonProperty("valueAddedTaxIncluded") @param:JsonProperty("valueAddedTaxIncluded") val valueAddedTaxIncluded: Boolean
        )

        data class Item(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,
            @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("additionalClassifications") @param:JsonProperty("additionalClassifications") val additionalClassifications: List<AdditionalClassification>?,

            @JsonDeserialize(using = Amount3Deserializer::class)
            @JsonSerialize(using = Amount3Serializer::class)
            @field:JsonProperty("quantity") @param:JsonProperty("quantity") val quantity: BigDecimal,

            @field:JsonProperty("unit") @param:JsonProperty("unit") val unit: Unit,
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
            @field:JsonProperty("relatedLot") @param:JsonProperty("relatedLot") val relatedLot: UUID,
            @field:JsonProperty("deliveryAddress") @param:JsonProperty("deliveryAddress") val deliveryAddress: DeliveryAddress
        ) {
            data class Classification(
                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String
            )

            data class AdditionalClassification(
                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String
            )

            data class Unit(
                @field:JsonProperty("value") @param:JsonProperty("value") val value: Value,
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("name") @param:JsonProperty("name") val name: String
            ) {
                data class Value(

                    @JsonDeserialize(using = AmountDeserializer::class)
                    @JsonSerialize(using = AmountSerializer::class)
                    @field:JsonProperty("amount") @param:JsonProperty("amount") val amount: BigDecimal,

                    @JsonDeserialize(using = AmountDeserializer::class)
                    @JsonSerialize(using = AmountSerializer::class)
                    @field:JsonProperty("amountNet") @param:JsonProperty("amountNet") val amountNet: BigDecimal,

                    @field:JsonProperty("valueAddedTaxIncluded") @param:JsonProperty("valueAddedTaxIncluded") val valueAddedTaxIncluded: Boolean,
                    @field:JsonProperty("currency") @param:JsonProperty("currency") val currency: String
                )
            }

            data class DeliveryAddress(
                @field:JsonProperty("streetAddress") @param:JsonProperty("streetAddress") val streetAddress: String,
                @field:JsonProperty("postalCode") @param:JsonProperty("postalCode") val postalCode: String,
                @field:JsonProperty("addressDetails") @param:JsonProperty("addressDetails") val addressDetails: AddressDetails
            ) {
                data class AddressDetails(
                    @field:JsonProperty("country") @param:JsonProperty("country") val country: Country,
                    @field:JsonProperty("region") @param:JsonProperty("region") val region: Region,
                    @field:JsonProperty("locality") @param:JsonProperty("locality") val locality: Locality
                ) {
                    data class Country(
                        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                        @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                        @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String
                    )

                    data class Region(
                        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                        @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                        @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String
                    )

                    data class Locality(
                        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                        @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
                    )
                }
            }

        }
    }

}