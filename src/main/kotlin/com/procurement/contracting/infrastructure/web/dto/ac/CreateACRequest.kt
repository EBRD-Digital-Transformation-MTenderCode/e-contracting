package com.procurement.contracting.infrastructure.web.dto.ac

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.contracting.domain.model.document.type.DocumentTypeAward
import com.procurement.contracting.infrastructure.amount.AmountDeserializer
import com.procurement.contracting.infrastructure.amount.AmountSerializer
import com.procurement.contracting.infrastructure.bind.JsonDateTimeDeserializer
import com.procurement.contracting.infrastructure.bind.JsonDateTimeSerializer
import com.procurement.contracting.infrastructure.quantity.QuantitySerializer
import com.procurement.contracting.model.dto.databinding.QuantityDeserializer
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class CreateACRequest(
    @field:JsonProperty("contracts") @param:JsonProperty("contracts") val contracts: List<Contract>,
    @field:JsonProperty("awards") @param:JsonProperty("awards") val awards: List<Award>,
    @field:JsonProperty("contractedTender") @param:JsonProperty("contractedTender") val contractedTender: ContractedTender
) {
    data class Contract(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String
    )

    data class Award(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,

        @JsonDeserialize(using = JsonDateTimeDeserializer::class)
        @JsonSerialize(using = JsonDateTimeSerializer::class)
        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

        @field:JsonProperty("value") @param:JsonProperty("value") val value: Value,
        @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<UUID>,
        @field:JsonProperty("relatedBid") @param:JsonProperty("relatedBid") val relatedBid: UUID,
        @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>,
        @field:JsonProperty("suppliers") @param:JsonProperty("suppliers") val suppliers: List<Supplier>
    ) {
        data class Value(
            @JsonDeserialize(using = AmountDeserializer::class)
            @JsonSerialize(using = AmountSerializer::class)
            @field:JsonProperty("amount") @param:JsonProperty("amount") val amount: BigDecimal,

            @field:JsonProperty("currency") @param:JsonProperty("currency") val currency: String
        )

        data class Document(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,
            @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentTypeAward,
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
            @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<UUID>
        )

        data class Supplier(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
            @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("additionalIdentifiers") @param:JsonProperty("additionalIdentifiers") val additionalIdentifiers: List<AdditionalIdentifier>?,

            @field:JsonProperty("address") @param:JsonProperty("address") val address: Address,
            @field:JsonProperty("contactPoint") @param:JsonProperty("contactPoint") val contactPoint: ContactPoint
        ) {

            data class Identifier(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                @field:JsonProperty("legalName") @param:JsonProperty("legalName") val legalName: String,
                @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String
            )

            data class AdditionalIdentifier(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
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

        }
    }

    data class ContractedTender(
        @field:JsonProperty("mainProcurementCategory") @param:JsonProperty("mainProcurementCategory") val mainProcurementCategory: String,
        @field:JsonProperty("items") @param:JsonProperty("items") val items: List<Item>
    ) {
        data class Item(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
            @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("additionalClassifications") @param:JsonProperty("additionalClassifications") val additionalClassifications: List<AdditionalClassification>?,

            @JsonDeserialize(using = QuantityDeserializer::class)
            @JsonSerialize(using = QuantitySerializer::class)
            @field:JsonProperty("quantity") @param:JsonProperty("quantity") val quantity: BigDecimal,
            @field:JsonProperty("unit") @param:JsonProperty("unit") val unit: Unit,
            @field:JsonProperty("relatedLot") @param:JsonProperty("relatedLot") val relatedLot: UUID
        ) {
            data class AdditionalClassification(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String
            )

            data class Classification(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String
            )

            data class Unit(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,
                @field:JsonProperty("name") @param:JsonProperty("name") val name: String
            )
        }
    }
}
