package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.ProcurementMethodDetails
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.bid.BusinessFunctionType
import com.procurement.contracting.domain.model.bid.PersonTitle
import com.procurement.contracting.domain.model.document.type.DocumentTypeAward
import com.procurement.contracting.domain.model.document.type.DocumentTypeBF
import com.procurement.contracting.domain.model.item.ItemId
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.organization.OrganizationId
import com.procurement.contracting.domain.model.organization.OrganizationRole
import com.procurement.contracting.domain.model.organization.Scale
import com.procurement.contracting.domain.model.organization.TypeOfSupplier
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.model.dto.ocds.PersonId
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateContractParams(
    val cpid: Cpid,
    val tender: Tender,
    val awards: List<Award>,
    val date: LocalDateTime,
    val pmd: ProcurementMethodDetails,
    val parties: List<Party>
) {
    data class Tender(
        val classification: Classification,
        val procurementMethod: String,
        val procurementMethodDetails: String,
        val mainProcurementCategory: String,
        val lots: List<Lot>,
        val items: List<Item>,
        val additionalProcurementCategories: List<String>
    ) {
        data class Classification(
            val id: String,
            val scheme: String,
            val description: String
        )

        data class Lot(
            val id: LotId,
            val internalId: String?,
            val title: String,
            val description: String?,
            val placeOfPerformance: PlaceOfPerformance
        ) {
            data class PlaceOfPerformance(
                val description: String?,
                val address: Address
            ) {
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
                            val id: String,
                            val description: String,
                            val scheme: String,
                            val uri: String
                        )

                        data class Region(
                            val id: String,
                            val description: String,
                            val scheme: String,
                            val uri: String
                        )

                        data class Locality(
                            val id: String,
                            val description: String,
                            val scheme: String,
                            val uri: String?
                        )
                    }
                }
            }
        }

        data class Item(
            val id: ItemId,
            val internalId: String?,
            val classification: Classification,
            val additionalClassifications: List<AdditionalClassification>,
            val quantity: BigDecimal,
            val unit: Unit,
            val description: String,
            val relatedLot: LotId
        ) {
            data class Classification(
                val id: String,
                val scheme: String,
                val description: String
            )

            data class AdditionalClassification(
                val id: String,
                val scheme: String,
                val description: String
            )

            data class Unit(
                val id: String,
                val name: String
            )
        }
    }

    data class Award(
        val id: AwardId,
        val value: Value,
        val relatedLots: List<String>,
        val suppliers: List<Supplier>,
        val documents: List<Document>
    ) {
        data class Value(
            val amount: BigDecimal,
            val currency: String
        )

        data class Supplier(
            val id: String,
            val name: String
        )

        data class Document(
            val id: String,
            val documentType: DocumentTypeAward,
            val title: String,
            val description: String?,
            val relatedLots: List<LotId>
        )
    }

    data class Party(
        val id: OrganizationId,
        val name: String,
        val identifier: Identifier,
        val additionalIdentifiers: List<AdditionalIdentifier>,
        val address: Address,
        val contactPoint: ContactPoint,
        val persones: List<Persone>,
        val details: Details,
        val roles: List<OrganizationRole>
    ) {
        data class Identifier(
            val id: String,
            val legalName: String,
            val scheme: String,
            val uri: String?
        )

        data class AdditionalIdentifier(
            val id: String,
            val legalName: String,
            val scheme: String,
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
                    val id: String,
                    val description: String,
                    val scheme: String,
                    val uri: String
                )

                data class Region(
                    val id: String,
                    val description: String,
                    val scheme: String,
                    val uri: String
                )

                data class Locality(
                    val id: String,
                    val description: String,
                    val scheme: String,
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

        data class Persone(
            val title: PersonTitle,
            val id: PersonId,
            val name: String,
            val identifier: Identifier,
            val businessFunctions: List<BusinessFunction>
        ) {
            data class Identifier(
                val scheme: String,
                val id: String,
                val uri: String?
            )

            data class BusinessFunction(
                val id: String,
                val type: BusinessFunctionType,
                val jobTitle: String,
                val period: Period,
                val documents: List<Document>
            ) {
                data class Period(
                    val startDate: LocalDateTime
                )

                data class Document(
                    val id: String,
                    val documentType: DocumentTypeBF,
                    val title: String,
                    val description: String?
                )
            }
        }

        data class Details(
            val typeOfSupplier: TypeOfSupplier?,
            val mainEconomicActivities: List<MainEconomicActivity>,
            val scale: Scale,
            val permits: List<Permit>,
            val bankAccounts: List<BankAccount>,
            val legalForm: LegalForm?
        ) {
            data class MainEconomicActivity(
                val id: String,
                val scheme: String,
                val description: String,
                val uri: String?
            )

            data class Permit(
                val id: String,
                val scheme: String,
                val url: String,
                val permitDetails: PermitDetails
            ) {
                data class PermitDetails(
                    val issuedBy: IssuedBy,
                    val issuedThought: IssuedThought,
                    val validityPeriod: ValidityPeriod
                ) {
                    data class IssuedBy(
                        val id: String,
                        val name: String
                    )

                    data class IssuedThought(
                        val id: String,
                        val name: String
                    )

                    data class ValidityPeriod(
                        val startDate: LocalDateTime,
                        val endDate: LocalDateTime?
                    )
                }
            }

            data class BankAccount(
                val description: String,
                val bankName: String,
                val address: Address,
                val identifier: Identifier,
                val accountIdentification: AccountIdentification,

                val additionalAccountIdentifiers: List<AdditionalAccountIdentifier>
            ) {
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
                            val id: String,
                            val description: String,
                            val scheme: String
                        )

                        data class Region(
                            val id: String,
                            val description: String,
                            val scheme: String
                        )

                        data class Locality(
                            val id: String,
                            val description: String,
                            val scheme: String
                        )
                    }
                }

                data class Identifier(
                    val scheme: String,
                    val id: String
                )

                data class AccountIdentification(
                    val scheme: String,
                    val id: String
                )

                data class AdditionalAccountIdentifier(
                    val scheme: String,
                    val id: String
                )
            }

            data class LegalForm(
                val scheme: String,
                val id: String,
                val description: String,

                val uri: String?
            )
        }
    }
}