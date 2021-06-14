package com.procurement.contracting.application.service

import com.procurement.contracting.application.repository.v2.AwardContractRepository
import com.procurement.contracting.application.repository.v2.PurchasingOrderRepository
import com.procurement.contracting.application.service.errors.CreateContractErrors
import com.procurement.contracting.application.service.model.CreateContractParams
import com.procurement.contracting.domain.model.ProcurementMethodDetails
import com.procurement.contracting.domain.model.ac.status.AwardContractStatus
import com.procurement.contracting.domain.model.ac.status.AwardContractStatusDetails
import com.procurement.contracting.domain.model.po.status.PurchasingOrderStatus
import com.procurement.contracting.domain.model.po.status.PurchasingOrderStatusDetails
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.domain.model.related.process.RelatedProcessType
import com.procurement.contracting.domain.util.extension.nowDefaultUTC
import com.procurement.contracting.domain.util.extension.toMilliseconds
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.infrastructure.configuration.properties.UriProperties
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.v2.model.response.CreateContractResponse
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess
import com.procurement.contracting.model.dto.ocds.v2.AwardContract
import com.procurement.contracting.model.dto.ocds.v2.PurchasingOrder
import org.springframework.stereotype.Service
import java.util.*

interface CreateContractService {
    fun create(params: CreateContractParams): Result<CreateContractResponse, Fail>
}

@Service
class CreateContractServiceImpl(
    private val acRepository: AwardContractRepository,
    private val poRepository: PurchasingOrderRepository,
    private val generationService: GenerationService,
    private val uriProperties: UriProperties

) : CreateContractService {

    override fun create(params: CreateContractParams): Result<CreateContractResponse, Fail> {
        if (awardsCurrenciesDoNotMatch(params))
            return CreateContractErrors.UnmatchingCurrency().asFailure()

        when (params.pmd) {
            ProcurementMethodDetails.OT, ProcurementMethodDetails.TEST_OT,
            ProcurementMethodDetails.MV, ProcurementMethodDetails.TEST_MV,
            ProcurementMethodDetails.SV, ProcurementMethodDetails.TEST_SV,
            ProcurementMethodDetails.GPA, ProcurementMethodDetails.TEST_GPA,
            ProcurementMethodDetails.RT, ProcurementMethodDetails.TEST_RT,
            ProcurementMethodDetails.DA, ProcurementMethodDetails.TEST_DA,
            ProcurementMethodDetails.NP, ProcurementMethodDetails.TEST_NP,
            ProcurementMethodDetails.CD, ProcurementMethodDetails.TEST_CD,
            ProcurementMethodDetails.DC, ProcurementMethodDetails.TEST_DC,
            ProcurementMethodDetails.IP, ProcurementMethodDetails.TEST_IP -> {
                val awardContract = generateAwardContract(params)
                acRepository.save(awardContract)
                    .onFailure { return it }

                return mapToResult(awardContract).asSuccess()
            }

            ProcurementMethodDetails.RFQ, ProcurementMethodDetails.TEST_RFQ -> {
                val purchasingOrder = generatePurchasingOrder(params)
                poRepository.save(purchasingOrder)
                    .onFailure { return it }

                return mapToResult(purchasingOrder).asSuccess()
            }

            ProcurementMethodDetails.CF, ProcurementMethodDetails.TEST_CF,
            ProcurementMethodDetails.OF, ProcurementMethodDetails.TEST_OF,
            ProcurementMethodDetails.FA, ProcurementMethodDetails.TEST_FA,
            ProcurementMethodDetails.OP, ProcurementMethodDetails.TEST_OP -> throw ErrorException(ErrorType.INVALID_PMD)
        }
    }

    private fun awardsCurrenciesDoNotMatch(params: CreateContractParams) =
        params.awards.asSequence()
            .map { it.value.currency }
            .any { it != params.awards.first().value.currency }

    private fun generateAwardContract(
        params: CreateContractParams,
    ): AwardContract {
        val awardId = generationService.awardId()

        return AwardContract(
            awards = listOf(
                AwardContract.Award(
                    id = awardId,
                    value = AwardContract.Award.Value(
                        currency = params.awards.first().value.currency,
                        amount = params.awards.sumOf { it.value.amount }
                    ),
                    relatedLots = params.awards.flatMap { it.relatedLots },
                    suppliers = params.awards.first().suppliers.map { supplier ->
                        AwardContract.Award.Supplier(
                            id = supplier.id,
                            name = supplier.name
                        )
                    },
                    documents = params.awards.flatMap { it.documents }
                        .map { document ->
                            AwardContract.Award.Document(
                                id = document.id,
                                title = document.title,
                                relatedLots = document.relatedLots,
                                description = document.description,
                                documentType = document.documentType
                            )
                        },
                    items = params.tender.items.map { item ->
                        AwardContract.Award.Item(
                            id = item.id,
                            internalId = item.internalId,
                            relatedLot = item.relatedLot,
                            description = item.description,
                            quantity = item.quantity,
                            unit = AwardContract.Award.Item.Unit(
                                id = item.unit.id,
                                name = item.unit.name
                            ),
                            classification = AwardContract.Award.Item.Classification(
                                id = item.classification.id,
                                scheme = item.classification.scheme,
                                description = item.classification.description
                            ),
                            additionalClassifications = item.additionalClassifications.orEmpty()
                                .map { additionalClassification ->
                                    AwardContract.Award.Item.AdditionalClassification(
                                        id = additionalClassification.id,
                                        scheme = additionalClassification.scheme,
                                        description = additionalClassification.description
                                    )
                                }

                        )
                    }

                )

            ),
            contracts = listOf(
                AwardContract.Contract(
                    id = generationService.awardContractId(),
                    status = AwardContractStatus.PENDING,
                    statusDetails = AwardContractStatusDetails.CONTRACT_PROJECT,
                    awardId = awardId,
                    date = params.date
                )
            ),
            tender = params.tender.let { tender ->
                AwardContract.Tender(
                    id = generationService.tenderId(),
                    classification = AwardContract.Tender.Classification(
                        id = tender.classification.id,
                        scheme = tender.classification.scheme,
                        description = tender.classification.description
                    ),
                    procurementMethod = tender.procurementMethod,
                    procurementMethodDetails = tender.procurementMethodDetails,
                    mainProcurementCategory = tender.mainProcurementCategory,
                    additionalProcurementCategories = tender.additionalProcurementCategories,
                    lots = tender.lots.map { lot ->
                        AwardContract.Tender.Lot(
                            id = lot.id,
                            description = lot.description,
                            title = lot.title,
                            internalId = lot.internalId,
                            placeOfPerformance = AwardContract.Tender.Lot.PlaceOfPerformance(
                                description = lot.placeOfPerformance.description,
                                address = lot.placeOfPerformance.address.let { address ->
                                    AwardContract.Tender.Lot.PlaceOfPerformance.Address(
                                        streetAddress = address.streetAddress,
                                        postalCode = address.postalCode,
                                        addressDetails = address.addressDetails.let { addressDetails ->
                                            AwardContract.Tender.Lot.PlaceOfPerformance.Address.AddressDetails(
                                                country = addressDetails.country.let { country ->
                                                    AwardContract.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Country(
                                                        scheme = country.scheme,
                                                        id = country.id,
                                                        description = country.description,
                                                        uri = country.uri
                                                    )
                                                },
                                                region = addressDetails.region.let { region ->
                                                    AwardContract.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Region(
                                                        scheme = region.scheme,
                                                        id = region.id,
                                                        description = region.description,
                                                        uri = region.uri
                                                    )
                                                },
                                                locality = addressDetails.locality.let { locality ->
                                                    AwardContract.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Locality(
                                                        scheme = locality.scheme,
                                                        id = locality.id,
                                                        description = locality.description,
                                                        uri = locality.uri
                                                    )
                                                }
                                            )

                                        }
                                    )
                                }
                            )
                        )
                    }
                )
            },
            relatedProcesses = listOf(
                AwardContract.RelatedProcesse(
                    id = generationService.relatedProcessesId(),
                    relationship = listOf(RelatedProcessType.PARENT),
                    scheme = "cpid",
                    identifier = params.cpid.underlying,
                    uri = "${uriProperties.tender}/tenders/${params.cpid.underlying}/${params.cpid.underlying}"
                ),
                AwardContract.RelatedProcesse(
                    id = generationService.relatedProcessesId(),
                    relationship = listOf(RelatedProcessType.X_EVALUATION),
                    scheme = "ocid",
                    identifier = params.cpid.underlying,
                    uri = "${uriProperties.tender}/tenders/${params.cpid.underlying}/${params.relatedOcid.underlying}"
                )
            ),
            parties = params.parties.map { party ->
                AwardContract.Party(
                    id = party.id,
                    name = party.name,
                    identifier = party.identifier.let { identifier ->
                        AwardContract.Party.Identifier(
                            id = identifier.id,
                            scheme = identifier.scheme,
                            uri = identifier.uri,
                            legalName = identifier.legalName
                        )
                    },
                    additionalIdentifiers = party.additionalIdentifiers.map { additionalIdentifier ->
                        AwardContract.Party.AdditionalIdentifier(
                            id = additionalIdentifier.id,
                            legalName = additionalIdentifier.legalName,
                            scheme = additionalIdentifier.scheme,
                            uri = additionalIdentifier.uri
                        )
                    },
                    contactPoint = party.contactPoint.let { contactPoint ->
                        AwardContract.Party.ContactPoint(
                            name = contactPoint.name,
                            url = contactPoint.url,
                            telephone = contactPoint.telephone,
                            faxNumber = contactPoint.faxNumber,
                            email = contactPoint.email
                        )
                    },
                    roles = party.roles,
                    details = party.details.let { details ->
                        AwardContract.Party.Details(
                            typeOfSupplier = details.typeOfSupplier,
                            scale = details.scale,
                            legalForm = details.legalForm?.let { legalForm ->
                                AwardContract.Party.Details.LegalForm(
                                    scheme = legalForm.scheme,
                                    uri = legalForm.uri,
                                    description = legalForm.description,
                                    id = legalForm.id
                                )
                            },
                            bankAccounts = details.bankAccounts.map { bankAccount ->
                                AwardContract.Party.Details.BankAccount(
                                    description = bankAccount.description,
                                    identifier = AwardContract.Party.Details.BankAccount.Identifier(
                                        scheme = bankAccount.identifier.scheme,
                                        id = bankAccount.identifier.id
                                    ),
                                    bankName = bankAccount.bankName,
                                    address = bankAccount.address.let { address ->
                                        AwardContract.Party.Details.BankAccount.Address(
                                            streetAddress = address.streetAddress,
                                            postalCode = address.postalCode,
                                            addressDetails = address.addressDetails.let { addressDetails ->
                                                AwardContract.Party.Details.BankAccount.Address.AddressDetails(
                                                    country = addressDetails.country.let { country ->
                                                        AwardContract.Party.Details.BankAccount.Address.AddressDetails.Country(
                                                            scheme = country.scheme,
                                                            id = country.id,
                                                            description = country.description
                                                        )
                                                    },
                                                    region = addressDetails.region.let { region ->
                                                        AwardContract.Party.Details.BankAccount.Address.AddressDetails.Region(
                                                            scheme = region.scheme,
                                                            id = region.id,
                                                            description = region.description
                                                        )
                                                    },
                                                    locality = addressDetails.locality.let { locality ->
                                                        AwardContract.Party.Details.BankAccount.Address.AddressDetails.Locality(
                                                            scheme = locality.scheme,
                                                            id = locality.id,
                                                            description = locality.description
                                                        )
                                                    }
                                                )

                                            }
                                        )
                                    },
                                    accountIdentification = AwardContract.Party.Details.BankAccount.AccountIdentification(
                                        id = bankAccount.accountIdentification.id,
                                        scheme = bankAccount.accountIdentification.scheme
                                    ),
                                    additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                        .map { additionalAccountIdentifier ->
                                            AwardContract.Party.Details.BankAccount.AdditionalAccountIdentifier(
                                                id = additionalAccountIdentifier.id,
                                                scheme = additionalAccountIdentifier.scheme
                                            )
                                        }
                                )
                            },
                            mainEconomicActivities = details.mainEconomicActivities.map { mainEconomicActivity ->
                                AwardContract.Party.Details.MainEconomicActivity(
                                    id = mainEconomicActivity.id,
                                    description = mainEconomicActivity.description,
                                    scheme = mainEconomicActivity.scheme,
                                    uri = mainEconomicActivity.uri
                                )
                            },
                            permits = details.permits.map { permit ->
                                AwardContract.Party.Details.Permit(
                                    id = permit.id,
                                    scheme = permit.scheme,
                                    url = permit.url,
                                    permitDetails = permit.permitDetails.let { permitDetails ->
                                        AwardContract.Party.Details.Permit.PermitDetails(
                                            issuedBy = permitDetails.issuedBy.let { issuedBy ->
                                                AwardContract.Party.Details.Permit.PermitDetails.IssuedBy(
                                                    id = issuedBy.id,
                                                    name = issuedBy.name
                                                )
                                            },
                                            validityPeriod = permitDetails.validityPeriod.let { validityPeriod ->
                                                AwardContract.Party.Details.Permit.PermitDetails.ValidityPeriod(
                                                    startDate = validityPeriod.startDate,
                                                    endDate = validityPeriod.endDate
                                                )
                                            },
                                            issuedThought = permitDetails.issuedThought.let { issuedThought ->
                                                AwardContract.Party.Details.Permit.PermitDetails.IssuedThought(
                                                    id = issuedThought.id,
                                                    name = issuedThought.name
                                                )
                                            }
                                        )
                                    }
                                )
                            }
                        )
                    },
                    persones = party.persones.map { persone ->
                        AwardContract.Party.Persone(
                            id = persone.id,
                            name = persone.name,
                            title = persone.title,
                            identifier = persone.identifier.let { identifier ->
                                AwardContract.Party.Persone.Identifier(
                                    scheme = identifier.scheme,
                                    uri = identifier.uri,
                                    id = identifier.id
                                )
                            },
                            businessFunctions = persone.businessFunctions.map { businessFunction ->
                                AwardContract.Party.Persone.BusinessFunction(
                                    id = businessFunction.id,
                                    type = businessFunction.type,
                                    jobTitle = businessFunction.jobTitle,
                                    period = AwardContract.Party.Persone.BusinessFunction.Period(
                                        businessFunction.period.startDate
                                    ),
                                    documents = businessFunction.documents.map { document ->
                                        AwardContract.Party.Persone.BusinessFunction.Document(
                                            id = document.id,
                                            documentType = document.documentType,
                                            description = document.description,
                                            title = document.title
                                        )
                                    }
                                )
                            }
                        )
                    },
                    address = party.address.let { address ->
                        AwardContract.Party.Address(
                            streetAddress = address.streetAddress,
                            postalCode = address.postalCode,
                            addressDetails = address.addressDetails.let { addressDetails ->
                                AwardContract.Party.Address.AddressDetails(
                                    country = addressDetails.country.let { country ->
                                        AwardContract.Party.Address.AddressDetails.Country(
                                            scheme = country.scheme,
                                            id = country.id,
                                            description = country.description,
                                            uri = country.uri
                                        )
                                    },
                                    region = addressDetails.region.let { region ->
                                        AwardContract.Party.Address.AddressDetails.Region(
                                            scheme = region.scheme,
                                            id = region.id,
                                            description = region.description,
                                            uri = region.uri
                                        )
                                    },
                                    locality = addressDetails.locality.let { locality ->
                                        AwardContract.Party.Address.AddressDetails.Locality(
                                            scheme = locality.scheme,
                                            id = locality.id,
                                            description = locality.description,
                                            uri = locality.uri
                                        )
                                    }
                                )

                            }
                        )
                    }
                )
            },
            token = generationService.token(),
            ocid = Ocid.orNull(params.cpid.underlying + "-AC-" + (nowDefaultUTC().toMilliseconds() + Random().nextInt()))!!,
            cpid = params.cpid,
            owner = params.owner
        )
    }

    private fun mapToResult(awardContract: AwardContract): CreateContractResponse =
        CreateContractResponse(
            awards = awardContract.awards.map { award ->
                CreateContractResponse.Award(
                    id = award.id,
                    value = CreateContractResponse.Award.Value(
                        currency = award.value.currency,
                        amount = award.value.amount
                    ),
                    relatedLots = award.relatedLots,
                    suppliers = award.suppliers.map { supplier ->
                        CreateContractResponse.Award.Supplier(
                            id = supplier.id,
                            name = supplier.name
                        )
                    },
                    documents = award.documents.map { document ->
                        CreateContractResponse.Award.Document(
                            id = document.id,
                            title = document.title,
                            relatedLots = document.relatedLots,
                            description = document.description,
                            documentType = document.documentType
                        )
                    },
                    items = award.items.map { item ->
                        CreateContractResponse.Award.Item(
                            id = item.id,
                            internalId = item.internalId,
                            relatedLot = item.relatedLot,
                            description = item.description,
                            quantity = item.quantity,
                            unit = CreateContractResponse.Award.Item.Unit(
                                id = item.unit.id,
                                name = item.unit.name
                            ),
                            classification = CreateContractResponse.Award.Item.Classification(
                                id = item.classification.id,
                                scheme = item.classification.scheme,
                                description = item.classification.description
                            ),
                            additionalClassifications = item.additionalClassifications
                                ?.map { additionalClassification ->
                                    CreateContractResponse.Award.Item.AdditionalClassification(
                                        id = additionalClassification.id,
                                        scheme = additionalClassification.scheme,
                                        description = additionalClassification.description
                                    )
                                }

                        )
                    }

                )
            },
            contracts = awardContract.contracts.map { contract ->
                CreateContractResponse.Contract(
                    id = contract.id.underlying,
                    status = contract.status.key,
                    statusDetails = contract.statusDetails.key,
                    awardId = contract.awardId,
                    date = contract.date
                )

            },
            tender = awardContract.tender.let { tender ->
                CreateContractResponse.Tender(
                    id = tender.id,
                    classification = CreateContractResponse.Tender.Classification(
                        id = tender.classification.id,
                        scheme = tender.classification.scheme,
                        description = tender.classification.description
                    ),
                    procurementMethod = tender.procurementMethod,
                    procurementMethodDetails = tender.procurementMethodDetails,
                    mainProcurementCategory = tender.mainProcurementCategory,
                    additionalProcurementCategories = tender.additionalProcurementCategories,
                    lots = tender.lots.map { lot ->
                        CreateContractResponse.Tender.Lot(
                            id = lot.id,
                            description = lot.description,
                            title = lot.title,
                            internalId = lot.internalId,
                            placeOfPerformance = CreateContractResponse.Tender.Lot.PlaceOfPerformance(
                                description = lot.placeOfPerformance.description,
                                address = lot.placeOfPerformance.address.let { address ->
                                    CreateContractResponse.Tender.Lot.PlaceOfPerformance.Address(
                                        streetAddress = address.streetAddress,
                                        postalCode = address.postalCode,
                                        addressDetails = address.addressDetails.let { addressDetails ->
                                            CreateContractResponse.Tender.Lot.PlaceOfPerformance.Address.AddressDetails(
                                                country = addressDetails.country.let { country ->
                                                    CreateContractResponse.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Country(
                                                        scheme = country.scheme,
                                                        id = country.id,
                                                        description = country.description,
                                                        uri = country.uri
                                                    )
                                                },
                                                region = addressDetails.region.let { region ->
                                                    CreateContractResponse.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Region(
                                                        scheme = region.scheme,
                                                        id = region.id,
                                                        description = region.description,
                                                        uri = region.uri
                                                    )
                                                },
                                                locality = addressDetails.locality.let { locality ->
                                                    CreateContractResponse.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Locality(
                                                        scheme = locality.scheme,
                                                        id = locality.id,
                                                        description = locality.description,
                                                        uri = locality.uri
                                                    )
                                                }
                                            )

                                        }
                                    )
                                }
                            )
                        )
                    }
                )
            },
            relatedProcesses = awardContract.relatedProcesses.map { relatedProcess ->
                CreateContractResponse.RelatedProcesse(
                    id = relatedProcess.id,
                    relationship = relatedProcess.relationship,
                    scheme = relatedProcess.scheme,
                    identifier = relatedProcess.identifier,
                    uri = relatedProcess.uri
                )
            },
            parties = awardContract.parties.map { party ->
                CreateContractResponse.Party(
                    id = party.id,
                    name = party.name,
                    identifier = party.identifier.let { identifier ->
                        CreateContractResponse.Party.Identifier(
                            id = identifier.id,
                            scheme = identifier.scheme,
                            uri = identifier.uri,
                            legalName = identifier.legalName
                        )
                    },
                    additionalIdentifiers = party.additionalIdentifiers?.map { additionalIdentifier ->
                        CreateContractResponse.Party.AdditionalIdentifier(
                            id = additionalIdentifier.id,
                            legalName = additionalIdentifier.legalName,
                            scheme = additionalIdentifier.scheme,
                            uri = additionalIdentifier.uri
                        )
                    },
                    contactPoint = party.contactPoint.let { contactPoint ->
                        CreateContractResponse.Party.ContactPoint(
                            name = contactPoint.name,
                            url = contactPoint.url,
                            telephone = contactPoint.telephone,
                            faxNumber = contactPoint.faxNumber,
                            email = contactPoint.email
                        )
                    },
                    roles = party.roles,
                    details = party.details?.let { details ->
                        CreateContractResponse.Party.Details(
                            typeOfSupplier = details.typeOfSupplier,
                            scale = details.scale,
                            legalForm = details.legalForm?.let { legalForm ->
                                CreateContractResponse.Party.Details.LegalForm(
                                    scheme = legalForm.scheme,
                                    uri = legalForm.uri,
                                    description = legalForm.description,
                                    id = legalForm.id
                                )
                            },
                            bankAccounts = details.bankAccounts?.map { bankAccount ->
                                CreateContractResponse.Party.Details.BankAccount(
                                    description = bankAccount.description,
                                    identifier = CreateContractResponse.Party.Details.BankAccount.Identifier(
                                        scheme = bankAccount.identifier.scheme,
                                        id = bankAccount.identifier.id
                                    ),
                                    bankName = bankAccount.bankName,
                                    address = bankAccount.address.let { address ->
                                        CreateContractResponse.Party.Details.BankAccount.Address(
                                            streetAddress = address.streetAddress,
                                            postalCode = address.postalCode,
                                            addressDetails = address.addressDetails.let { addressDetails ->
                                                CreateContractResponse.Party.Details.BankAccount.Address.AddressDetails(
                                                    country = addressDetails.country.let { country ->
                                                        CreateContractResponse.Party.Details.BankAccount.Address.AddressDetails.Country(
                                                            scheme = country.scheme,
                                                            id = country.id,
                                                            description = country.description
                                                        )
                                                    },
                                                    region = addressDetails.region.let { region ->
                                                        CreateContractResponse.Party.Details.BankAccount.Address.AddressDetails.Region(
                                                            scheme = region.scheme,
                                                            id = region.id,
                                                            description = region.description
                                                        )
                                                    },
                                                    locality = addressDetails.locality.let { locality ->
                                                        CreateContractResponse.Party.Details.BankAccount.Address.AddressDetails.Locality(
                                                            scheme = locality.scheme,
                                                            id = locality.id,
                                                            description = locality.description
                                                        )
                                                    }
                                                )

                                            }
                                        )
                                    },
                                    accountIdentification = CreateContractResponse.Party.Details.BankAccount.AccountIdentification(
                                        id = bankAccount.accountIdentification.id,
                                        scheme = bankAccount.accountIdentification.scheme
                                    ),
                                    additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                        .map { additionalAccountIdentifier ->
                                            CreateContractResponse.Party.Details.BankAccount.AdditionalAccountIdentifier(
                                                id = additionalAccountIdentifier.id,
                                                scheme = additionalAccountIdentifier.scheme
                                            )
                                        }
                                )
                            },
                            mainEconomicActivities = details.mainEconomicActivities?.map { mainEconomicActivity ->
                                CreateContractResponse.Party.Details.MainEconomicActivity(
                                    id = mainEconomicActivity.id,
                                    description = mainEconomicActivity.description,
                                    scheme = mainEconomicActivity.scheme,
                                    uri = mainEconomicActivity.uri
                                )
                            },
                            permits = details.permits?.map { permit ->
                                CreateContractResponse.Party.Details.Permit(
                                    id = permit.id,
                                    scheme = permit.scheme,
                                    url = permit.url,
                                    permitDetails = permit.permitDetails.let { permitDetails ->
                                        CreateContractResponse.Party.Details.Permit.PermitDetails(
                                            issuedBy = permitDetails.issuedBy.let { issuedBy ->
                                                CreateContractResponse.Party.Details.Permit.PermitDetails.IssuedBy(
                                                    id = issuedBy.id,
                                                    name = issuedBy.name
                                                )
                                            },
                                            validityPeriod = permitDetails.validityPeriod.let { validityPeriod ->
                                                CreateContractResponse.Party.Details.Permit.PermitDetails.ValidityPeriod(
                                                    startDate = validityPeriod.startDate,
                                                    endDate = validityPeriod.endDate
                                                )
                                            },
                                            issuedThought = permitDetails.issuedThought.let { issuedThought ->
                                                CreateContractResponse.Party.Details.Permit.PermitDetails.IssuedThought(
                                                    id = issuedThought.id,
                                                    name = issuedThought.name
                                                )
                                            }
                                        )
                                    }
                                )
                            }
                        )
                    },
                    persones = party.persones?.map { persone ->
                        CreateContractResponse.Party.Persone(
                            id = persone.id,
                            name = persone.name,
                            title = persone.title,
                            identifier = persone.identifier.let { identifier ->
                                CreateContractResponse.Party.Persone.Identifier(
                                    scheme = identifier.scheme,
                                    uri = identifier.uri,
                                    id = identifier.id
                                )
                            },
                            businessFunctions = persone.businessFunctions.map { businessFunction ->
                                CreateContractResponse.Party.Persone.BusinessFunction(
                                    id = businessFunction.id,
                                    type = businessFunction.type,
                                    jobTitle = businessFunction.jobTitle,
                                    period = CreateContractResponse.Party.Persone.BusinessFunction.Period(
                                        businessFunction.period.startDate
                                    ),
                                    documents = businessFunction.documents?.map { document ->
                                        CreateContractResponse.Party.Persone.BusinessFunction.Document(
                                            id = document.id,
                                            documentType = document.documentType,
                                            description = document.description,
                                            title = document.title
                                        )
                                    }
                                )
                            }
                        )
                    },
                    address = party.address.let { address ->
                        CreateContractResponse.Party.Address(
                            streetAddress = address.streetAddress,
                            postalCode = address.postalCode,
                            addressDetails = address.addressDetails.let { addressDetails ->
                                CreateContractResponse.Party.Address.AddressDetails(
                                    country = addressDetails.country.let { country ->
                                        CreateContractResponse.Party.Address.AddressDetails.Country(
                                            scheme = country.scheme,
                                            id = country.id,
                                            description = country.description,
                                            uri = country.uri
                                        )
                                    },
                                    region = addressDetails.region.let { region ->
                                        CreateContractResponse.Party.Address.AddressDetails.Region(
                                            scheme = region.scheme,
                                            id = region.id,
                                            description = region.description,
                                            uri = region.uri
                                        )
                                    },
                                    locality = addressDetails.locality.let { locality ->
                                        CreateContractResponse.Party.Address.AddressDetails.Locality(
                                            scheme = locality.scheme,
                                            id = locality.id,
                                            description = locality.description,
                                            uri = locality.uri
                                        )
                                    }
                                )

                            }
                        )
                    }
                )
            },
            token = awardContract.token,
            ocid = awardContract.ocid

        )

    private fun generatePurchasingOrder(
        params: CreateContractParams,
    ): PurchasingOrder {
        val awardId = generationService.awardId()

        return PurchasingOrder(
            awards = listOf(
                    PurchasingOrder.Award(
                        id = awardId,
                        value = PurchasingOrder.Award.Value(
                            currency = params.awards.first().value.currency,
                            amount = params.awards.sumOf { it.value.amount }
                        ),
                        relatedLots =  params.awards.flatMap { it.relatedLots },
                        suppliers = params.awards.first().suppliers.map { supplier ->
                            PurchasingOrder.Award.Supplier(
                                id = supplier.id,
                                name = supplier.name
                            )
                        },
                        documents = params.awards.flatMap { it.documents }
                            .map { document ->
                            PurchasingOrder.Award.Document(
                                id = document.id,
                                title = document.title,
                                relatedLots = document.relatedLots,
                                description = document.description,
                                documentType = document.documentType
                            )
                        },
                        items = params.tender.items.map { item ->
                            PurchasingOrder.Award.Item(
                                id = item.id,
                                internalId = item.internalId,
                                relatedLot = item.relatedLot,
                                description = item.description,
                                quantity = item.quantity,
                                unit = PurchasingOrder.Award.Item.Unit(
                                    id = item.unit.id,
                                    name = item.unit.name
                                ),
                                classification = PurchasingOrder.Award.Item.Classification(
                                    id = item.classification.id,
                                    scheme = item.classification.scheme,
                                    description = item.classification.description
                                ),
                                additionalClassifications = item.additionalClassifications.orEmpty()
                                    .map { additionalClassification ->
                                        PurchasingOrder.Award.Item.AdditionalClassification(
                                            id = additionalClassification.id,
                                            scheme = additionalClassification.scheme,
                                            description = additionalClassification.description
                                        )
                                    }

                            )
                        }

                    )

            ),
            contracts = listOf(
                PurchasingOrder.Contract(
                    id = generationService.purchasingOrderId(),
                    status = PurchasingOrderStatus.PENDING,
                    statusDetails = PurchasingOrderStatusDetails.CONTRACT_PROJECT,
                    awardId = awardId,
                    date = params.date
                )
            ),
            tender = params.tender.let { tender ->
                PurchasingOrder.Tender(
                    id = generationService.tenderId(),
                    classification = PurchasingOrder.Tender.Classification(
                        id = tender.classification.id,
                        scheme = tender.classification.scheme,
                        description = tender.classification.description
                    ),
                    procurementMethod = tender.procurementMethod,
                    procurementMethodDetails = tender.procurementMethodDetails,
                    mainProcurementCategory = tender.mainProcurementCategory,
                    additionalProcurementCategories = tender.additionalProcurementCategories,
                    lots = tender.lots.map { lot ->
                        PurchasingOrder.Tender.Lot(
                            id = lot.id,
                            description = lot.description,
                            title = lot.title,
                            internalId = lot.internalId,
                            placeOfPerformance = PurchasingOrder.Tender.Lot.PlaceOfPerformance(
                                description = lot.placeOfPerformance.description,
                                address = lot.placeOfPerformance.address.let { address ->
                                    PurchasingOrder.Tender.Lot.PlaceOfPerformance.Address(
                                        streetAddress = address.streetAddress,
                                        postalCode = address.postalCode,
                                        addressDetails = address.addressDetails.let { addressDetails ->
                                            PurchasingOrder.Tender.Lot.PlaceOfPerformance.Address.AddressDetails(
                                                country = addressDetails.country.let { country ->
                                                    PurchasingOrder.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Country(
                                                        scheme = country.scheme,
                                                        id = country.id,
                                                        description = country.description,
                                                        uri = country.uri
                                                    )
                                                },
                                                region = addressDetails.region.let { region ->
                                                    PurchasingOrder.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Region(
                                                        scheme = region.scheme,
                                                        id = region.id,
                                                        description = region.description,
                                                        uri = region.uri
                                                    )
                                                },
                                                locality = addressDetails.locality.let { locality ->
                                                    PurchasingOrder.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Locality(
                                                        scheme = locality.scheme,
                                                        id = locality.id,
                                                        description = locality.description,
                                                        uri = locality.uri
                                                    )
                                                }
                                            )

                                        }
                                    )
                                }
                            )
                        )
                    }
                )
            },
            relatedProcesses = listOf(
                PurchasingOrder.RelatedProcesse(
                    id = generationService.relatedProcessesId(),
                    relationship = listOf(RelatedProcessType.PARENT),
                    scheme = "cpid",
                    identifier = params.cpid.underlying,
                    uri = "${uriProperties.tender}/tenders/${params.cpid.underlying}/${params.cpid.underlying}"
                ),
                PurchasingOrder.RelatedProcesse(
                    id = generationService.relatedProcessesId(),
                    relationship = listOf(RelatedProcessType.X_EVALUATION),
                    scheme = "ocid",
                    identifier = params.cpid.underlying,
                    uri = "${uriProperties.tender}/tenders/${params.cpid.underlying}/${params.relatedOcid.underlying}"
                )
            ),
            parties = params.parties.map { party ->
                PurchasingOrder.Party(
                    id = party.id,
                    name = party.name,
                    identifier = party.identifier.let { identifier ->
                        PurchasingOrder.Party.Identifier(
                            id = identifier.id,
                            scheme = identifier.scheme,
                            uri = identifier.uri,
                            legalName = identifier.legalName
                        )
                    },
                    additionalIdentifiers = party.additionalIdentifiers.map { additionalIdentifier ->
                        PurchasingOrder.Party.AdditionalIdentifier(
                            id = additionalIdentifier.id,
                            legalName = additionalIdentifier.legalName,
                            scheme = additionalIdentifier.scheme,
                            uri = additionalIdentifier.uri
                        )
                    },
                    contactPoint = party.contactPoint.let { contactPoint ->
                        PurchasingOrder.Party.ContactPoint(
                            name = contactPoint.name,
                            url = contactPoint.url,
                            telephone = contactPoint.telephone,
                            faxNumber = contactPoint.faxNumber,
                            email = contactPoint.email
                        )
                    },
                    roles = party.roles,
                    details = party.details.let { details ->
                        PurchasingOrder.Party.Details(
                            typeOfSupplier = details.typeOfSupplier,
                            scale = details.scale,
                            legalForm = details.legalForm?.let { legalForm ->
                                PurchasingOrder.Party.Details.LegalForm(
                                    scheme = legalForm.scheme,
                                    uri = legalForm.uri,
                                    description = legalForm.description,
                                    id = legalForm.id
                                )
                            },
                            bankAccounts = details.bankAccounts.map { bankAccount ->
                                PurchasingOrder.Party.Details.BankAccount(
                                    description = bankAccount.description,
                                    identifier = PurchasingOrder.Party.Details.BankAccount.Identifier(
                                        scheme = bankAccount.identifier.scheme,
                                        id = bankAccount.identifier.id
                                    ),
                                    bankName = bankAccount.bankName,
                                    address = bankAccount.address.let { address ->
                                        PurchasingOrder.Party.Details.BankAccount.Address(
                                            streetAddress = address.streetAddress,
                                            postalCode = address.postalCode,
                                            addressDetails = address.addressDetails.let { addressDetails ->
                                                PurchasingOrder.Party.Details.BankAccount.Address.AddressDetails(
                                                    country = addressDetails.country.let { country ->
                                                        PurchasingOrder.Party.Details.BankAccount.Address.AddressDetails.Country(
                                                            scheme = country.scheme,
                                                            id = country.id,
                                                            description = country.description
                                                        )
                                                    },
                                                    region = addressDetails.region.let { region ->
                                                        PurchasingOrder.Party.Details.BankAccount.Address.AddressDetails.Region(
                                                            scheme = region.scheme,
                                                            id = region.id,
                                                            description = region.description
                                                        )
                                                    },
                                                    locality = addressDetails.locality.let { locality ->
                                                        PurchasingOrder.Party.Details.BankAccount.Address.AddressDetails.Locality(
                                                            scheme = locality.scheme,
                                                            id = locality.id,
                                                            description = locality.description
                                                        )
                                                    }
                                                )

                                            }
                                        )
                                    },
                                    accountIdentification = PurchasingOrder.Party.Details.BankAccount.AccountIdentification(
                                        id = bankAccount.accountIdentification.id,
                                        scheme = bankAccount.accountIdentification.scheme
                                    ),
                                    additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                        .map { additionalAccountIdentifier ->
                                            PurchasingOrder.Party.Details.BankAccount.AdditionalAccountIdentifier(
                                                id = additionalAccountIdentifier.id,
                                                scheme = additionalAccountIdentifier.scheme
                                            )
                                        }
                                )
                            },
                            mainEconomicActivities = details.mainEconomicActivities.map { mainEconomicActivity ->
                                PurchasingOrder.Party.Details.MainEconomicActivity(
                                    id = mainEconomicActivity.id,
                                    description = mainEconomicActivity.description,
                                    scheme = mainEconomicActivity.scheme,
                                    uri = mainEconomicActivity.uri
                                )
                            },
                            permits = details.permits.map { permit ->
                                PurchasingOrder.Party.Details.Permit(
                                    id = permit.id,
                                    scheme = permit.scheme,
                                    url = permit.url,
                                    permitDetails = permit.permitDetails.let { permitDetails ->
                                        PurchasingOrder.Party.Details.Permit.PermitDetails(
                                            issuedBy = permitDetails.issuedBy.let { issuedBy ->
                                                PurchasingOrder.Party.Details.Permit.PermitDetails.IssuedBy(
                                                    id = issuedBy.id,
                                                    name = issuedBy.name
                                                )
                                            },
                                            validityPeriod = permitDetails.validityPeriod.let { validityPeriod ->
                                                PurchasingOrder.Party.Details.Permit.PermitDetails.ValidityPeriod(
                                                    startDate = validityPeriod.startDate,
                                                    endDate = validityPeriod.endDate
                                                )
                                            },
                                            issuedThought = permitDetails.issuedThought.let { issuedThought ->
                                                PurchasingOrder.Party.Details.Permit.PermitDetails.IssuedThought(
                                                    id = issuedThought.id,
                                                    name = issuedThought.name
                                                )
                                            }
                                        )
                                    }
                                )
                            }
                        )
                    },
                    persones = party.persones.map { persone ->
                        PurchasingOrder.Party.Persone(
                            id = persone.id,
                            name = persone.name,
                            title = persone.title,
                            identifier = persone.identifier.let { identifier ->
                                PurchasingOrder.Party.Persone.Identifier(
                                    scheme = identifier.scheme,
                                    uri = identifier.uri,
                                    id = identifier.id
                                )
                            },
                            businessFunctions = persone.businessFunctions.map { businessFunction ->
                                PurchasingOrder.Party.Persone.BusinessFunction(
                                    id = businessFunction.id,
                                    type = businessFunction.type,
                                    jobTitle = businessFunction.jobTitle,
                                    period = PurchasingOrder.Party.Persone.BusinessFunction.Period(
                                        businessFunction.period.startDate
                                    ),
                                    documents = businessFunction.documents.map { document ->
                                        PurchasingOrder.Party.Persone.BusinessFunction.Document(
                                            id = document.id,
                                            documentType = document.documentType,
                                            description = document.description,
                                            title = document.title
                                        )
                                    }
                                )
                            }
                        )
                    },
                    address = party.address.let { address ->
                        PurchasingOrder.Party.Address(
                            streetAddress = address.streetAddress,
                            postalCode = address.postalCode,
                            addressDetails = address.addressDetails.let { addressDetails ->
                                PurchasingOrder.Party.Address.AddressDetails(
                                    country = addressDetails.country.let { country ->
                                        PurchasingOrder.Party.Address.AddressDetails.Country(
                                            scheme = country.scheme,
                                            id = country.id,
                                            description = country.description,
                                            uri = country.uri
                                        )
                                    },
                                    region = addressDetails.region.let { region ->
                                        PurchasingOrder.Party.Address.AddressDetails.Region(
                                            scheme = region.scheme,
                                            id = region.id,
                                            description = region.description,
                                            uri = region.uri
                                        )
                                    },
                                    locality = addressDetails.locality.let { locality ->
                                        PurchasingOrder.Party.Address.AddressDetails.Locality(
                                            scheme = locality.scheme,
                                            id = locality.id,
                                            description = locality.description,
                                            uri = locality.uri
                                        )
                                    }
                                )

                            }
                        )
                    }
                )
            },
            token = generationService.token(),
            ocid = Ocid.orNull(params.cpid.underlying + "-AC-" + (nowDefaultUTC().toMilliseconds() + Random().nextInt()))!!,
            cpid = params.cpid,
            owner = params.owner
        )
    }

    private fun mapToResult(purchasingOrder: PurchasingOrder): CreateContractResponse =
        CreateContractResponse(
            awards = purchasingOrder.awards.map { award ->
                CreateContractResponse.Award(
                    id = award.id,
                    value = CreateContractResponse.Award.Value(
                        currency = award.value.currency,
                        amount = award.value.amount
                    ),
                    relatedLots = award.relatedLots,
                    suppliers = award.suppliers.map { supplier ->
                        CreateContractResponse.Award.Supplier(
                            id = supplier.id,
                            name = supplier.name
                        )
                    },
                    documents = award.documents.map { document ->
                        CreateContractResponse.Award.Document(
                            id = document.id,
                            title = document.title,
                            relatedLots = document.relatedLots,
                            description = document.description,
                            documentType = document.documentType
                        )
                    },
                    items = award.items.map { item ->
                        CreateContractResponse.Award.Item(
                            id = item.id,
                            internalId = item.internalId,
                            relatedLot = item.relatedLot,
                            description = item.description,
                            quantity = item.quantity,
                            unit = CreateContractResponse.Award.Item.Unit(
                                id = item.unit.id,
                                name = item.unit.name
                            ),
                            classification = CreateContractResponse.Award.Item.Classification(
                                id = item.classification.id,
                                scheme = item.classification.scheme,
                                description = item.classification.description
                            ),
                            additionalClassifications = item.additionalClassifications
                                ?.map { additionalClassification ->
                                    CreateContractResponse.Award.Item.AdditionalClassification(
                                        id = additionalClassification.id,
                                        scheme = additionalClassification.scheme,
                                        description = additionalClassification.description
                                    )
                                }

                        )
                    }

                )
            },
            contracts = purchasingOrder.contracts.map { contract ->
                CreateContractResponse.Contract(
                    id = contract.id.underlying.toString(),
                    status = contract.status.key,
                    statusDetails = contract.statusDetails.key,
                    awardId = contract.awardId,
                    date = contract.date
                )

            },
            tender = purchasingOrder.tender.let { tender ->
                CreateContractResponse.Tender(
                    id = tender.id,
                    classification = CreateContractResponse.Tender.Classification(
                        id = tender.classification.id,
                        scheme = tender.classification.scheme,
                        description = tender.classification.description
                    ),
                    procurementMethod = tender.procurementMethod,
                    procurementMethodDetails = tender.procurementMethodDetails,
                    mainProcurementCategory = tender.mainProcurementCategory,
                    additionalProcurementCategories = tender.additionalProcurementCategories,
                    lots = tender.lots.map { lot ->
                        CreateContractResponse.Tender.Lot(
                            id = lot.id,
                            description = lot.description,
                            title = lot.title,
                            internalId = lot.internalId,
                            placeOfPerformance = CreateContractResponse.Tender.Lot.PlaceOfPerformance(
                                description = lot.placeOfPerformance.description,
                                address = lot.placeOfPerformance.address.let { address ->
                                    CreateContractResponse.Tender.Lot.PlaceOfPerformance.Address(
                                        streetAddress = address.streetAddress,
                                        postalCode = address.postalCode,
                                        addressDetails = address.addressDetails.let { addressDetails ->
                                            CreateContractResponse.Tender.Lot.PlaceOfPerformance.Address.AddressDetails(
                                                country = addressDetails.country.let { country ->
                                                    CreateContractResponse.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Country(
                                                        scheme = country.scheme,
                                                        id = country.id,
                                                        description = country.description,
                                                        uri = country.uri
                                                    )
                                                },
                                                region = addressDetails.region.let { region ->
                                                    CreateContractResponse.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Region(
                                                        scheme = region.scheme,
                                                        id = region.id,
                                                        description = region.description,
                                                        uri = region.uri
                                                    )
                                                },
                                                locality = addressDetails.locality.let { locality ->
                                                    CreateContractResponse.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Locality(
                                                        scheme = locality.scheme,
                                                        id = locality.id,
                                                        description = locality.description,
                                                        uri = locality.uri
                                                    )
                                                }
                                            )

                                        }
                                    )
                                }
                            )
                        )
                    }
                )
            },
            relatedProcesses = purchasingOrder.relatedProcesses.map { relatedProcess ->
                CreateContractResponse.RelatedProcesse(
                    id = relatedProcess.id,
                    relationship = relatedProcess.relationship,
                    scheme = relatedProcess.scheme,
                    identifier = relatedProcess.identifier,
                    uri = relatedProcess.uri
                )
            },
            parties = purchasingOrder.parties.map { party ->
                CreateContractResponse.Party(
                    id = party.id,
                    name = party.name,
                    identifier = party.identifier.let { identifier ->
                        CreateContractResponse.Party.Identifier(
                            id = identifier.id,
                            scheme = identifier.scheme,
                            uri = identifier.uri,
                            legalName = identifier.legalName
                        )
                    },
                    additionalIdentifiers = party.additionalIdentifiers?.map { additionalIdentifier ->
                        CreateContractResponse.Party.AdditionalIdentifier(
                            id = additionalIdentifier.id,
                            legalName = additionalIdentifier.legalName,
                            scheme = additionalIdentifier.scheme,
                            uri = additionalIdentifier.uri
                        )
                    },
                    contactPoint = party.contactPoint.let { contactPoint ->
                        CreateContractResponse.Party.ContactPoint(
                            name = contactPoint.name,
                            url = contactPoint.url,
                            telephone = contactPoint.telephone,
                            faxNumber = contactPoint.faxNumber,
                            email = contactPoint.email
                        )
                    },
                    roles = party.roles,
                    details = party.details?.let { details ->
                        CreateContractResponse.Party.Details(
                            typeOfSupplier = details.typeOfSupplier,
                            scale = details.scale,
                            legalForm = details.legalForm?.let { legalForm ->
                                CreateContractResponse.Party.Details.LegalForm(
                                    scheme = legalForm.scheme,
                                    uri = legalForm.uri,
                                    description = legalForm.description,
                                    id = legalForm.id
                                )
                            },
                            bankAccounts = details.bankAccounts?.map { bankAccount ->
                                CreateContractResponse.Party.Details.BankAccount(
                                    description = bankAccount.description,
                                    identifier = CreateContractResponse.Party.Details.BankAccount.Identifier(
                                        scheme = bankAccount.identifier.scheme,
                                        id = bankAccount.identifier.id
                                    ),
                                    bankName = bankAccount.bankName,
                                    address = bankAccount.address.let { address ->
                                        CreateContractResponse.Party.Details.BankAccount.Address(
                                            streetAddress = address.streetAddress,
                                            postalCode = address.postalCode,
                                            addressDetails = address.addressDetails.let { addressDetails ->
                                                CreateContractResponse.Party.Details.BankAccount.Address.AddressDetails(
                                                    country = addressDetails.country.let { country ->
                                                        CreateContractResponse.Party.Details.BankAccount.Address.AddressDetails.Country(
                                                            scheme = country.scheme,
                                                            id = country.id,
                                                            description = country.description
                                                        )
                                                    },
                                                    region = addressDetails.region.let { region ->
                                                        CreateContractResponse.Party.Details.BankAccount.Address.AddressDetails.Region(
                                                            scheme = region.scheme,
                                                            id = region.id,
                                                            description = region.description
                                                        )
                                                    },
                                                    locality = addressDetails.locality.let { locality ->
                                                        CreateContractResponse.Party.Details.BankAccount.Address.AddressDetails.Locality(
                                                            scheme = locality.scheme,
                                                            id = locality.id,
                                                            description = locality.description
                                                        )
                                                    }
                                                )

                                            }
                                        )
                                    },
                                    accountIdentification = CreateContractResponse.Party.Details.BankAccount.AccountIdentification(
                                        id = bankAccount.accountIdentification.id,
                                        scheme = bankAccount.accountIdentification.scheme
                                    ),
                                    additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                        .map { additionalAccountIdentifier ->
                                            CreateContractResponse.Party.Details.BankAccount.AdditionalAccountIdentifier(
                                                id = additionalAccountIdentifier.id,
                                                scheme = additionalAccountIdentifier.scheme
                                            )
                                        }
                                )
                            },
                            mainEconomicActivities = details.mainEconomicActivities?.map { mainEconomicActivity ->
                                CreateContractResponse.Party.Details.MainEconomicActivity(
                                    id = mainEconomicActivity.id,
                                    description = mainEconomicActivity.description,
                                    scheme = mainEconomicActivity.scheme,
                                    uri = mainEconomicActivity.uri
                                )
                            },
                            permits = details.permits?.map { permit ->
                                CreateContractResponse.Party.Details.Permit(
                                    id = permit.id,
                                    scheme = permit.scheme,
                                    url = permit.url,
                                    permitDetails = permit.permitDetails.let { permitDetails ->
                                        CreateContractResponse.Party.Details.Permit.PermitDetails(
                                            issuedBy = permitDetails.issuedBy.let { issuedBy ->
                                                CreateContractResponse.Party.Details.Permit.PermitDetails.IssuedBy(
                                                    id = issuedBy.id,
                                                    name = issuedBy.name
                                                )
                                            },
                                            validityPeriod = permitDetails.validityPeriod.let { validityPeriod ->
                                                CreateContractResponse.Party.Details.Permit.PermitDetails.ValidityPeriod(
                                                    startDate = validityPeriod.startDate,
                                                    endDate = validityPeriod.endDate
                                                )
                                            },
                                            issuedThought = permitDetails.issuedThought.let { issuedThought ->
                                                CreateContractResponse.Party.Details.Permit.PermitDetails.IssuedThought(
                                                    id = issuedThought.id,
                                                    name = issuedThought.name
                                                )
                                            }
                                        )
                                    }
                                )
                            }
                        )
                    },
                    persones = party.persones?.map { persone ->
                        CreateContractResponse.Party.Persone(
                            id = persone.id,
                            name = persone.name,
                            title = persone.title,
                            identifier = persone.identifier.let { identifier ->
                                CreateContractResponse.Party.Persone.Identifier(
                                    scheme = identifier.scheme,
                                    uri = identifier.uri,
                                    id = identifier.id
                                )
                            },
                            businessFunctions = persone.businessFunctions.map { businessFunction ->
                                CreateContractResponse.Party.Persone.BusinessFunction(
                                    id = businessFunction.id,
                                    type = businessFunction.type,
                                    jobTitle = businessFunction.jobTitle,
                                    period = CreateContractResponse.Party.Persone.BusinessFunction.Period(
                                        businessFunction.period.startDate
                                    ),
                                    documents = businessFunction.documents?.map { document ->
                                        CreateContractResponse.Party.Persone.BusinessFunction.Document(
                                            id = document.id,
                                            documentType = document.documentType,
                                            description = document.description,
                                            title = document.title
                                        )
                                    }
                                )
                            }
                        )
                    },
                    address = party.address.let { address ->
                        CreateContractResponse.Party.Address(
                            streetAddress = address.streetAddress,
                            postalCode = address.postalCode,
                            addressDetails = address.addressDetails.let { addressDetails ->
                                CreateContractResponse.Party.Address.AddressDetails(
                                    country = addressDetails.country.let { country ->
                                        CreateContractResponse.Party.Address.AddressDetails.Country(
                                            scheme = country.scheme,
                                            id = country.id,
                                            description = country.description,
                                            uri = country.uri
                                        )
                                    },
                                    region = addressDetails.region.let { region ->
                                        CreateContractResponse.Party.Address.AddressDetails.Region(
                                            scheme = region.scheme,
                                            id = region.id,
                                            description = region.description,
                                            uri = region.uri
                                        )
                                    },
                                    locality = addressDetails.locality.let { locality ->
                                        CreateContractResponse.Party.Address.AddressDetails.Locality(
                                            scheme = locality.scheme,
                                            id = locality.id,
                                            description = locality.description,
                                            uri = locality.uri
                                        )
                                    }
                                )

                            }
                        )
                    }
                )
            },
            token = purchasingOrder.token,
            ocid = purchasingOrder.ocid
        )
}
