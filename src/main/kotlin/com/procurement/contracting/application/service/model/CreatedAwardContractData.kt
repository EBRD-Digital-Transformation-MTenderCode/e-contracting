package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.ac.id.AwardContractId
import com.procurement.contracting.domain.model.ac.status.AwardContractStatus
import com.procurement.contracting.domain.model.ac.status.AwardContractStatusDetails
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.document.type.DocumentTypeAward
import com.procurement.contracting.domain.model.item.ItemId
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.organization.OrganizationId
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreatedAwardContractData(
    val token: Token,
    val cans: List<CAN>,
    val contract: Contract,
    val contractedAward: ContractedAward
) {

    data class CAN(
        val id: CANId,
        val status: CANStatus,
        val statusDetails: CANStatusDetails
    )

    data class Contract(
        val id: AwardContractId,
        val awardId: AwardId,
        val status: AwardContractStatus,
        val statusDetails: AwardContractStatusDetails
    )

    data class ContractedAward(
        val id: AwardId,
        val date: LocalDateTime,
        val value: Value,
        val relatedLots: List<LotId>,
        val suppliers: List<Supplier>,
        val items: List<Item>,
        val documents: List<Document>
    ) {

        data class Value(
            val amount: BigDecimal,
            val currency: String
        )

        data class Supplier(
            val id: OrganizationId,
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
                val uri: String?
            )

            data class AdditionalIdentifier(
                val scheme: String,
                val id: String,
                val legalName: String,
                val uri: String?
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

        data class Item(
            val id: ItemId,
            val internalId: String?,
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

            data class Unit(
                val id: String,
                val name: String
            )

            data class AdditionalClassification(
                val scheme: String,
                val description: String,
                val id: String
            )
        }

        data class Document(
            val documentType: DocumentTypeAward,
            val id: String,
            val title: String?,
            val description: String?,
            val relatedLots: List<LotId>?
        )
    }
}
