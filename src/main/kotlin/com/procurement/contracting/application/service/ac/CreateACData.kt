package com.procurement.contracting.application.service.ac

import com.procurement.contracting.domain.model.MainProcurementCategory
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.document.type.DocumentTypeAward
import com.procurement.contracting.domain.model.lot.LotId
import java.math.BigDecimal
import java.util.*

data class CreateACData(
    val cans: List<CAN>,
    val awards: List<Award>,
    val contractedTender: ContractedTender
) {

    data class CAN(
        val id: CANId
    )

    data class Award(
        val id: AwardId,
        val value: Value,
        val relatedLots: List<LotId>,
        val relatedBid: UUID?,

        val suppliers: List<Supplier>,
        val documents: List<Document>
    ) {

        data class Value(
            val amount: BigDecimal,
            val currency: String
        )

        data class Supplier(
            val id: String,
            val name: String,
            val identifier: Identifier,
            val additionalIdentifiers: List<AdditionalIdentifier>?,
            val address: Address,
            val contactPoint: ContactPoint
        ) {

            data class Identifier(
                val scheme: String,
                val id: String,
                val legalName: String,
                val uri: String
            )

            data class AdditionalIdentifier(
                val scheme: String,
                val id: String,
                val legalName: String,
                val uri: String
            )

            data class Address(
                val streetAddress: String,
                val postalCode: String?,
                val addressDetails: AddressDetails
            ) {

                data class AddressDetails(
                    val country: Country,
                    val region: Region,
                    val locality: Locality
                ) {

                    data class Country(
                        val scheme: String,
                        val id: String,
                        val description: String,
                        val uri: String
                    )

                    data class Region(
                        val scheme: String,
                        val id: String,
                        val description: String,
                        val uri: String
                    )

                    data class Locality(
                        val scheme: String,
                        val id: String,
                        val description: String,
                        val uri: String?
                    )
                }
            }

            data class ContactPoint(
                val name: String,
                val email: String,
                val telephone: String,
                val faxNumber: String?,
                val url: String?
            )
        }

        data class Document(
            val documentType: DocumentTypeAward,
            val id: String,
            val title: String,
            val description: String,
            val relatedLots: List<LotId>
        )
    }

    data class ContractedTender(
        val mainProcurementCategory: MainProcurementCategory,
        val items: List<Item>
    ) {

        data class Item(
            val id: UUID,
            val classification: Classification,
            val additionalClassifications: List<AdditionalClassification>?,
            val quantity: BigDecimal,
            val unit: Unit,
            val description: String,
            val relatedLot: LotId
        ) {

            data class Classification(
                val scheme: String,
                val description: String,
                val id: String
            )

            data class AdditionalClassification(
                val scheme: String,
                val description: String,
                val id: String
            )

            data class Unit(
                val id: String,
                val name: String
            )
        }
    }
}
