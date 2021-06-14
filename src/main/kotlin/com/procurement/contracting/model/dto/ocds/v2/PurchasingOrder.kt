package com.procurement.contracting.model.dto.ocds.v2

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.ac.id.AwardContractId
import com.procurement.contracting.domain.model.ac.status.AwardContractStatus
import com.procurement.contracting.domain.model.ac.status.AwardContractStatusDetails
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.bid.BusinessFunctionType
import com.procurement.contracting.domain.model.bid.PersonTitle
import com.procurement.contracting.domain.model.document.type.DocumentTypeAward
import com.procurement.contracting.domain.model.document.type.DocumentTypeBF
import com.procurement.contracting.domain.model.item.ItemId
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.organization.OrganizationRole
import com.procurement.contracting.domain.model.organization.Scale
import com.procurement.contracting.domain.model.organization.TypeOfSupplier
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.domain.model.related.process.RelatedProcessType
import com.procurement.contracting.infrastructure.bind.amount.AmountDeserializer
import com.procurement.contracting.infrastructure.bind.amount.AmountSerializer
import com.procurement.contracting.model.dto.ocds.PersonId
import java.math.BigDecimal
import java.time.LocalDateTime

data class PurchasingOrder(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
    @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>,
    @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender,
    @param:JsonProperty("awards") @field:JsonProperty("awards") val awards: List<Award>,
    @param:JsonProperty("token") @field:JsonProperty("token") val token: Token,
    @param:JsonProperty("owner") @field:JsonProperty("owner") val owner: Owner,
    @param:JsonProperty("relatedProcesses") @field:JsonProperty("relatedProcesses") val relatedProcesses: List<RelatedProcesse>,
    @param:JsonProperty("parties") @field:JsonProperty("parties") val parties: List<Party>
) {
    data class Contract(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: AwardContractId,
        @param:JsonProperty("status") @field:JsonProperty("status") val status: AwardContractStatus,
        @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: AwardContractStatusDetails,
        @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,
        @param:JsonProperty("awardId") @field:JsonProperty("awardId") val awardId: AwardId
    )

    data class Tender(
        @param:JsonProperty("classification") @field:JsonProperty("classification") val classification: Classification,
        @param:JsonProperty("procurementMethod") @field:JsonProperty("procurementMethod") val procurementMethod: String,
        @param:JsonProperty("procurementMethodDetails") @field:JsonProperty("procurementMethodDetails") val procurementMethodDetails: String,
        @param:JsonProperty("mainProcurementCategory") @field:JsonProperty("mainProcurementCategory") val mainProcurementCategory: String,
        @param:JsonProperty("lots") @field:JsonProperty("lots") val lots: List<Lot>,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("additionalProcurementCategories") @field:JsonProperty("additionalProcurementCategories") val additionalProcurementCategories: List<String>?,

        @param:JsonProperty("id") @field:JsonProperty("id") val id: String
    ) {
        data class Classification(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
            @param:JsonProperty("description") @field:JsonProperty("description") val description: String
        )

        data class Lot(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: LotId,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("internalId") @field:JsonProperty("internalId") val internalId: String?,

            @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

            @param:JsonProperty("placeOfPerformance") @field:JsonProperty("placeOfPerformance") val placeOfPerformance: PlaceOfPerformance
        ) {
            data class PlaceOfPerformance(
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                @param:JsonProperty("address") @field:JsonProperty("address") val address: Address
            ) {
                data class Address(
                    @param:JsonProperty("streetAddress") @field:JsonProperty("streetAddress") val streetAddress: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("postalCode") @field:JsonProperty("postalCode") val postalCode: String?,

                    @param:JsonProperty("addressDetails") @field:JsonProperty("addressDetails") val addressDetails: AddressDetails
                ) {
                    data class AddressDetails(
                        @param:JsonProperty("country") @field:JsonProperty("country") val country: Country,
                        @param:JsonProperty("region") @field:JsonProperty("region") val region: Region,
                        @param:JsonProperty("locality") @field:JsonProperty("locality") val locality: Locality
                    ) {
                        data class Country(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                            @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                            @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
                        )

                        data class Region(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                            @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                            @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
                        )

                        data class Locality(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                            @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                        )
                    }
                }
            }
        }
    }

    data class Award(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: AwardId,
        @param:JsonProperty("value") @field:JsonProperty("value") val value: Value,
        @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<String>,
        @param:JsonProperty("suppliers") @field:JsonProperty("suppliers") val suppliers: List<Supplier>,
        @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>,
        @param:JsonProperty("items") @field:JsonProperty("items") val items: List<Item>
    ) {
        data class Value(
            @JsonDeserialize(using = AmountDeserializer::class)
            @JsonSerialize(using = AmountSerializer::class)
            @param:JsonProperty("amount") @field:JsonProperty("amount") val amount: BigDecimal,
            @param:JsonProperty("currency") @field:JsonProperty("currency") val currency: String
        )

        data class Supplier(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("name") @field:JsonProperty("name") val name: String
        )

        data class Document(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: DocumentTypeAward,
            @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<LotId>?
        )

        data class Item(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: ItemId,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("internalId") @field:JsonProperty("internalId") val internalId: String?,

            @param:JsonProperty("classification") @field:JsonProperty("classification") val classification: Classification,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("additionalClassifications") @field:JsonProperty("additionalClassifications") val additionalClassifications: List<AdditionalClassification>?,

            @param:JsonProperty("quantity") @field:JsonProperty("quantity") val quantity: BigDecimal,
            @param:JsonProperty("unit") @field:JsonProperty("unit") val unit: Unit,
            @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
            @param:JsonProperty("relatedLot") @field:JsonProperty("relatedLot") val relatedLot: LotId
        ) {
            data class Classification(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String
            )

            data class AdditionalClassification(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String
            )

            data class Unit(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String
            )
        }
    }

    data class RelatedProcesse(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
        @param:JsonProperty("relationship") @field:JsonProperty("relationship") val relationship: List<RelatedProcessType>,
        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
        @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: String,
        @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
    )

    data class Party(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
        @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
        @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("additionalIdentifiers") @field:JsonProperty("additionalIdentifiers") val additionalIdentifiers: List<AdditionalIdentifier>?,

        @param:JsonProperty("address") @field:JsonProperty("address") val address: Address,
        @param:JsonProperty("contactPoint") @field:JsonProperty("contactPoint") val contactPoint: ContactPoint,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("persones") @field:JsonProperty("persones") val persones: List<Persone>?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("details") @field:JsonProperty("details") val details: Details?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("roles") @field:JsonProperty("roles") val roles: List<OrganizationRole>?
    ) {
        data class Identifier(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("legalName") @field:JsonProperty("legalName") val legalName: String,
            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
        )

        data class AdditionalIdentifier(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("legalName") @field:JsonProperty("legalName") val legalName: String,
            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
        )

        data class Address(
            @param:JsonProperty("streetAddress") @field:JsonProperty("streetAddress") val streetAddress: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("postalCode") @field:JsonProperty("postalCode") val postalCode: String?,

            @param:JsonProperty("addressDetails") @field:JsonProperty("addressDetails") val addressDetails: AddressDetails
        ) {
            data class AddressDetails(
                @param:JsonProperty("country") @field:JsonProperty("country") val country: Country,
                @param:JsonProperty("region") @field:JsonProperty("region") val region: Region,
                @param:JsonProperty("locality") @field:JsonProperty("locality") val locality: Locality
            ) {
                data class Country(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
                )

                data class Region(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
                )

                data class Locality(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                )
            }
        }

        data class ContactPoint(
            @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
            @param:JsonProperty("email") @field:JsonProperty("email") val email: String,
            @param:JsonProperty("telephone") @field:JsonProperty("telephone") val telephone: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("faxNumber") @field:JsonProperty("faxNumber") val faxNumber: String?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("url") @field:JsonProperty("url") val url: String?
        )

        data class Persone(
            @param:JsonProperty("title") @field:JsonProperty("title") val title: PersonTitle,
            @param:JsonProperty("id") @field:JsonProperty("id") val id: PersonId,
            @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
            @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier,
            @param:JsonProperty("businessFunctions") @field:JsonProperty("businessFunctions") val businessFunctions: List<BusinessFunction>
        ) {
            data class Identifier(
                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
            )

            data class BusinessFunction(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("type") @field:JsonProperty("type") val type: BusinessFunctionType,
                @param:JsonProperty("jobTitle") @field:JsonProperty("jobTitle") val jobTitle: String,
                @param:JsonProperty("period") @field:JsonProperty("period") val period: Period,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?
            ) {
                data class Period(
                    @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime
                )

                data class Document(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: DocumentTypeBF,
                    @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String?
                )
            }
        }

        data class Details(
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("typeOfSupplier") @field:JsonProperty("typeOfSupplier") val typeOfSupplier: TypeOfSupplier?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("mainEconomicActivities") @field:JsonProperty("mainEconomicActivities") val mainEconomicActivities: List<MainEconomicActivity>?,

            @param:JsonProperty("scale") @field:JsonProperty("scale") val scale: Scale,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("permits") @field:JsonProperty("permits") val permits: List<Permit>?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("bankAccounts") @field:JsonProperty("bankAccounts") val bankAccounts: List<BankAccount>?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("legalForm") @field:JsonProperty("legalForm") val legalForm: LegalForm?
        ) {
            data class MainEconomicActivity(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
            )

            data class Permit(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("url") @field:JsonProperty("url") val url: String?,

                @param:JsonProperty("permitDetails") @field:JsonProperty("permitDetails") val permitDetails: PermitDetails
            ) {
                data class PermitDetails(
                    @param:JsonProperty("issuedBy") @field:JsonProperty("issuedBy") val issuedBy: IssuedBy,
                    @param:JsonProperty("issuedThought") @field:JsonProperty("issuedThought") val issuedThought: IssuedThought,
                    @param:JsonProperty("validityPeriod") @field:JsonProperty("validityPeriod") val validityPeriod: ValidityPeriod
                ) {
                    data class IssuedBy(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                    )

                    data class IssuedThought(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                    )

                    data class ValidityPeriod(
                        @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime?
                    )
                }
            }

            data class BankAccount(
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                @param:JsonProperty("bankName") @field:JsonProperty("bankName") val bankName: String,
                @param:JsonProperty("address") @field:JsonProperty("address") val address: Address,
                @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier,
                @param:JsonProperty("accountIdentification") @field:JsonProperty("accountIdentification") val accountIdentification: AccountIdentification,
                @param:JsonProperty("additionalAccountIdentifiers") @field:JsonProperty("additionalAccountIdentifiers") val additionalAccountIdentifiers: List<AdditionalAccountIdentifier>
            ) {
                data class Address(
                    @param:JsonProperty("streetAddress") @field:JsonProperty("streetAddress") val streetAddress: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("postalCode") @field:JsonProperty("postalCode") val postalCode: String?,

                    @param:JsonProperty("addressDetails") @field:JsonProperty("addressDetails") val addressDetails: AddressDetails
                ) {
                    data class AddressDetails(
                        @param:JsonProperty("country") @field:JsonProperty("country") val country: Country,
                        @param:JsonProperty("region") @field:JsonProperty("region") val region: Region,
                        @param:JsonProperty("locality") @field:JsonProperty("locality") val locality: Locality
                    ) {
                        data class Country(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                            @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String
                        )

                        data class Region(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                            @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String
                        )

                        data class Locality(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                            @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String
                        )
                    }
                }

                data class Identifier(
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                )

                data class AccountIdentification(
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                )

                data class AdditionalAccountIdentifier(
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                )
            }

            data class LegalForm(
                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
            )
        }
    }
}