package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.CreateContractParams
import com.procurement.contracting.domain.model.ProcurementMethodDetails
import com.procurement.contracting.domain.model.bid.BusinessFunctionType
import com.procurement.contracting.domain.model.bid.PersonTitle
import com.procurement.contracting.domain.model.document.type.DocumentTypeAward
import com.procurement.contracting.domain.model.document.type.DocumentTypeBF
import com.procurement.contracting.domain.model.organization.OrganizationRole
import com.procurement.contracting.domain.model.organization.Scale
import com.procurement.contracting.domain.model.organization.TypeOfSupplier
import com.procurement.contracting.domain.model.parseAwardDocumentType
import com.procurement.contracting.domain.model.parseAwardId
import com.procurement.contracting.domain.model.parseBFDocumentType
import com.procurement.contracting.domain.model.parseBusinessFunctionType
import com.procurement.contracting.domain.model.parseCpid
import com.procurement.contracting.domain.model.parseDate
import com.procurement.contracting.domain.model.parseItemId
import com.procurement.contracting.domain.model.parseLotId
import com.procurement.contracting.domain.model.parseOcid
import com.procurement.contracting.domain.model.parseOrganizationRole
import com.procurement.contracting.domain.model.parseOwner
import com.procurement.contracting.domain.model.parsePersonId
import com.procurement.contracting.domain.model.parsePersonTitle
import com.procurement.contracting.domain.model.parsePmd
import com.procurement.contracting.domain.model.parseScale
import com.procurement.contracting.domain.model.parseTypeOfSupplier
import com.procurement.contracting.domain.util.extension.mapResult
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.infrastructure.handler.v2.converter.rule.notEmptyRule
import com.procurement.contracting.infrastructure.handler.v2.model.request.CreateContractRequest
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess
import com.procurement.contracting.lib.functional.flatMap
import com.procurement.contracting.lib.functional.validate

private val allowedPmds = ProcurementMethodDetails.allowedElements.filter {
    when (it) {
        ProcurementMethodDetails.RFQ, ProcurementMethodDetails.TEST_RFQ,
        ProcurementMethodDetails.OT, ProcurementMethodDetails.TEST_OT,
        ProcurementMethodDetails.MV, ProcurementMethodDetails.TEST_MV,
        ProcurementMethodDetails.SV, ProcurementMethodDetails.TEST_SV,
        ProcurementMethodDetails.GPA, ProcurementMethodDetails.TEST_GPA,
        ProcurementMethodDetails.RT, ProcurementMethodDetails.TEST_RT,
        ProcurementMethodDetails.DA, ProcurementMethodDetails.TEST_DA,
        ProcurementMethodDetails.NP, ProcurementMethodDetails.TEST_NP,
        ProcurementMethodDetails.CD, ProcurementMethodDetails.TEST_CD,
        ProcurementMethodDetails.DC, ProcurementMethodDetails.TEST_DC,
        ProcurementMethodDetails.IP, ProcurementMethodDetails.TEST_IP -> true

        ProcurementMethodDetails.CF, ProcurementMethodDetails.TEST_CF,
        ProcurementMethodDetails.OF, ProcurementMethodDetails.TEST_OF,
        ProcurementMethodDetails.FA, ProcurementMethodDetails.TEST_FA,
        ProcurementMethodDetails.OP, ProcurementMethodDetails.TEST_OP -> false
    }
}.toSet()

fun CreateContractRequest.convert(): Result<CreateContractParams, DataErrors> {
    val cpid = parseCpid(cpid)
        .onFailure { return it }

    val relatedOcid = parseOcid(relatedOcid)
        .onFailure { return it }

    val owner = parseOwner(owner)
        .onFailure { return it }

    val date = parseDate(date)
        .onFailure { return it }

    val pmd = parsePmd(pmd, allowedPmds)
        .onFailure { return it }

    val tender = tender.convert("tender")
        .onFailure { return it }

    val awards = awards.validate(notEmptyRule("awards"))
        .flatMap { it.mapResult { award -> award.convert("awards") } }
        .onFailure { return it }

    val parties = parties.validate(notEmptyRule("parties"))
        .flatMap { it.mapResult { party -> party.convert("parties") } }
        .onFailure { return it }

    return CreateContractParams(
        cpid = cpid,
        relatedOcid = relatedOcid,
        owner = owner,
        date = date,
        tender = tender,
        awards = awards,
        pmd = pmd,
        parties = parties
    ).asSuccess()
}

private val allowedOrganizationRoles = OrganizationRole.allowedElements
    .filter {
        when (it) {
            OrganizationRole.SUPPLIER,
            OrganizationRole.PAYEE -> true
            OrganizationRole.BUYER,
            OrganizationRole.INVITED_CANDIDATE,
            OrganizationRole.PROCURING_ENTITY -> false
        }
    }.toSet()

private fun CreateContractRequest.Party.convert(path: String): Result<CreateContractParams.Party, DataErrors> {
    additionalIdentifiers.validate(notEmptyRule("$path.additionalIdentifiers"))
        .onFailure { return it }

    val roles = roles.validate(notEmptyRule("$path.roles"))
        .flatMap { it.mapResult { role -> parseOrganizationRole(role, allowedOrganizationRoles, "$path.roles") } }
        .onFailure { return it }

    val details = details.convert("$path.details")
        .onFailure { return it }

    val persones = persones.validate(notEmptyRule("persones"))
        .flatMap { it.orEmpty().mapResult { person -> person.convert("persones") } }
        .onFailure { return it }

    return CreateContractParams.Party(
        id = id,
        name = name,
        identifier = CreateContractParams.Party.Identifier(
            id = identifier.id,
            scheme = identifier.scheme,
            uri = identifier.uri,
            legalName = identifier.legalName
        ),
        additionalIdentifiers = additionalIdentifiers.map { additionalIdentifier ->
            CreateContractParams.Party.AdditionalIdentifier(
                id = additionalIdentifier.id,
                legalName = additionalIdentifier.legalName,
                scheme = additionalIdentifier.scheme,
                uri = additionalIdentifier.uri
            )
        },
        contactPoint = CreateContractParams.Party.ContactPoint(
            name = contactPoint.name,
            url = contactPoint.url,
            telephone = contactPoint.telephone,
            faxNumber = contactPoint.faxNumber,
            email = contactPoint.email
        ),
        roles = roles,
        details = details,
        persones = persones,
        address = address.let { address ->
            CreateContractParams.Party.Address(
                streetAddress = address.streetAddress,
                postalCode = address.postalCode,
                addressDetails = address.addressDetails.let { addressDetails ->
                    CreateContractParams.Party.Address.AddressDetails(
                        country = addressDetails.country.let { country ->
                            CreateContractParams.Party.Address.AddressDetails.Country(
                                scheme = country.scheme,
                                id = country.id,
                                description = country.description,
                                uri = country.uri
                            )
                        },
                        region = addressDetails.region.let { region ->
                            CreateContractParams.Party.Address.AddressDetails.Region(
                                scheme = region.scheme,
                                id = region.id,
                                description = region.description,
                                uri = region.uri
                            )
                        },
                        locality = addressDetails.locality.let { locality ->
                            CreateContractParams.Party.Address.AddressDetails.Locality(
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
    ).asSuccess()
}

private val allowedPersonTitles = PersonTitle.allowedElements.filter {
    when (it) {
        PersonTitle.MR,
        PersonTitle.MRS,
        PersonTitle.MS -> true
    }
}.toSet()

private fun CreateContractRequest.Party.Persone.convert(path: String): Result<CreateContractParams.Party.Persone, DataErrors> {
    val id = parsePersonId(id, "$path.id")
        .onFailure { return it }

    val title = parsePersonTitle(title, allowedPersonTitles, "$path.title")
        .onFailure { return it }

    val businessFunctions = businessFunctions.validate(notEmptyRule("businessFunctions"))
        .flatMap { it.mapResult { businessFunction -> businessFunction.convert("businessFunction") } }
        .onFailure { return it }


    return CreateContractParams.Party.Persone(
        id = id,
        name = name,
        title = title,
        identifier = CreateContractParams.Party.Persone.Identifier(
            scheme = identifier.scheme,
            uri = identifier.uri,
            id = identifier.id
        ),
        businessFunctions = businessFunctions
    ).asSuccess()
}

private val allowedBusinessFunctionTypes = BusinessFunctionType.allowedElements.filter {
    when (it) {
        BusinessFunctionType.CHAIRMAN,
        BusinessFunctionType.CONTACT_POINT,
        BusinessFunctionType.TECHNICAL_OPENER,
        BusinessFunctionType.PRICE_OPENER,
        BusinessFunctionType.PRICE_EVALUATOR,
        BusinessFunctionType.TECHNICAL_EVALUATOR,
        BusinessFunctionType.PROCUREMENT_OFFICER -> true

        BusinessFunctionType.AUTHORITY -> false
    }
}.toSet()

private fun CreateContractRequest.Party.Persone.BusinessFunction.convert(path: String): Result<CreateContractParams.Party.Persone.BusinessFunction, DataErrors> {
    val businessFunctionType = parseBusinessFunctionType(type, allowedBusinessFunctionTypes, "$path.type")
        .onFailure { return it }

    val startDate = parseDate(period.startDate, "$path.period.startDate")
        .onFailure { return it }

    val documents = documents.orEmpty().validate(notEmptyRule("$path.documents"))
        .flatMap { it.mapResult { document -> document.convert("$path.documents") } }
        .onFailure { return it }

    return CreateContractParams.Party.Persone.BusinessFunction(
        id = id,
        type = businessFunctionType,
        jobTitle = jobTitle,
        period = CreateContractParams.Party.Persone.BusinessFunction.Period(startDate),
        documents = documents
    ).asSuccess()
}

private val allowedBFDocuments = DocumentTypeBF.allowedElements
    .filter {
        when (it) {
            DocumentTypeBF.REGULATORY_DOCUMENT -> true
        }
    }.toSet()

private fun CreateContractRequest.Party.Persone.BusinessFunction.Document.convert(path: String): Result<CreateContractParams.Party.Persone.BusinessFunction.Document, DataErrors> {
    val parsedType = parseBFDocumentType(documentType, allowedBFDocuments, "$path.documentType")
        .onFailure { return it }

    return CreateContractParams.Party.Persone.BusinessFunction.Document(
        id = id,
        documentType = parsedType,
        description = description,
        title = title
    ).asSuccess()
}

private val allowedTypeOfSupplier = TypeOfSupplier.allowedElements
    .filter {
        when (it) {
            TypeOfSupplier.COMPANY,
            TypeOfSupplier.INDIVIDUAL -> true
        }
    }.toSet()

private val allowedScale = Scale.allowedElements
    .filter {
        when (it) {
            Scale.LARGE,
            Scale.MICRO,
            Scale.SME -> true
            Scale.EMPTY -> false
        }
    }.toSet()

private fun CreateContractRequest.Party.Details.convert(path: String): Result<CreateContractParams.Party.Details, DataErrors> {
    val typeOfSupplier = typeOfSupplier
        ?.let { parseTypeOfSupplier(it, allowedTypeOfSupplier, "$path.typeOfSupplier") }
        ?.onFailure { return it }

    val scale = parseScale(scale, allowedScale, "$path.scale")
        .onFailure { return it }

    val bankAccounts = bankAccounts.validate(notEmptyRule("$path.bankAccounts"))
        .flatMap { it.orEmpty().mapResult { bankAccount -> bankAccount.convert("$path.bankAccounts") } }
        .onFailure { return it }

    val permits = permits.validate(notEmptyRule("$path.permits"))
        .flatMap { it.orEmpty().mapResult { permit -> permit.convert("$path.permits") } }
        .onFailure { return it }

    mainEconomicActivities.validate(notEmptyRule("$path.mainEconomicActivities"))
        .onFailure { return it }

    return CreateContractParams.Party.Details(
        typeOfSupplier = typeOfSupplier,
        scale = scale,
        legalForm = legalForm?.let { legalForm ->
            CreateContractParams.Party.Details.LegalForm(
                scheme = legalForm.scheme,
                uri = legalForm.uri,
                description = legalForm.description,
                id = legalForm.id
            )
        },
        bankAccounts = bankAccounts,
        mainEconomicActivities = mainEconomicActivities.orEmpty().map { mainEconomicActivity ->
            CreateContractParams.Party.Details.MainEconomicActivity(
                id = mainEconomicActivity.id,
                description = mainEconomicActivity.description,
                scheme = mainEconomicActivity.scheme,
                uri = mainEconomicActivity.uri
            )
        },
        permits = permits
    )
        .asSuccess()
}

private fun CreateContractRequest.Party.Details.Permit.convert(path: String): Result<CreateContractParams.Party.Details.Permit, DataErrors> {
    return CreateContractParams.Party.Details.Permit(
        id = id,
        scheme = scheme,
        url = url,
        permitDetails = CreateContractParams.Party.Details.Permit.PermitDetails(
            issuedBy = permitDetails.issuedBy.let { issuedBy ->
                CreateContractParams.Party.Details.Permit.PermitDetails.IssuedBy(
                    id = issuedBy.id,
                    name = issuedBy.name
                )
            },
            validityPeriod = permitDetails.validityPeriod.let { validityPeriod ->
                CreateContractParams.Party.Details.Permit.PermitDetails.ValidityPeriod(
                    startDate = parseDate(validityPeriod.startDate, "$path.permitDetails.validityPeriod.startDate")
                        .onFailure { return it },
                    endDate = validityPeriod.endDate?.let {
                        parseDate(it, "$path.permitDetails.validityPeriod.endDate")
                            .onFailure { return it }
                    }
                )
            },
            issuedThought = permitDetails.issuedThought.let { issuedThought ->
                CreateContractParams.Party.Details.Permit.PermitDetails.IssuedThought(
                    id = issuedThought.id,
                    name = issuedThought.name
                )
            }
        )
    ).asSuccess()
}

private fun CreateContractRequest.Party.Details.BankAccount.convert(path: String): Result<CreateContractParams.Party.Details.BankAccount, DataErrors> {
    additionalAccountIdentifiers
        .validate(notEmptyRule("$path.additionalAccountIdentifiers"))
        .onFailure { return it }

    return CreateContractParams.Party.Details.BankAccount(
        description = description,
        identifier = CreateContractParams.Party.Details.BankAccount.Identifier(
            scheme = identifier.scheme,
            id = identifier.id
        ),
        bankName = bankName,
        address = address.let { address ->
            CreateContractParams.Party.Details.BankAccount.Address(
                streetAddress = address.streetAddress,
                postalCode = address.postalCode,
                addressDetails = address.addressDetails.let { addressDetails ->
                    CreateContractParams.Party.Details.BankAccount.Address.AddressDetails(
                        country = addressDetails.country.let { country ->
                            CreateContractParams.Party.Details.BankAccount.Address.AddressDetails.Country(
                                scheme = country.scheme,
                                id = country.id,
                                description = country.description
                            )
                        },
                        region = addressDetails.region.let { region ->
                            CreateContractParams.Party.Details.BankAccount.Address.AddressDetails.Region(
                                scheme = region.scheme,
                                id = region.id,
                                description = region.description
                            )
                        },
                        locality = addressDetails.locality.let { locality ->
                            CreateContractParams.Party.Details.BankAccount.Address.AddressDetails.Locality(
                                scheme = locality.scheme,
                                id = locality.id,
                                description = locality.description
                            )
                        }
                    )

                }
            )
        },
        accountIdentification = CreateContractParams.Party.Details.BankAccount.AccountIdentification(
            id = accountIdentification.id,
            scheme = accountIdentification.scheme
        ),
        additionalAccountIdentifiers = additionalAccountIdentifiers.orEmpty()
            .map { additionalAccountIdentifier ->
                CreateContractParams.Party.Details.BankAccount.AdditionalAccountIdentifier(
                    id = additionalAccountIdentifier.id,
                    scheme = additionalAccountIdentifier.scheme
                )
            }
    )
        .asSuccess()
}

private fun CreateContractRequest.Award.convert(path: String): Result<CreateContractParams.Award, DataErrors> {
    val id = parseAwardId(id, "$path.id")
        .onFailure { return it }

    val suppliers = suppliers.validate(notEmptyRule("$path.suppliers"))
        .flatMap { it.mapResult { supplier -> supplier.convert() } }
        .onFailure { return it }

    relatedLots.validate(notEmptyRule("$path.relatedLots"))
        .onFailure { return it }

    val documents = documents.validate(notEmptyRule("$path.documents"))
        .flatMap { it.orEmpty().mapResult { document -> document.convert("$path.documents") } }
        .onFailure { return it }

    return CreateContractParams.Award(
        id = id,
        suppliers = suppliers,
        relatedLots = relatedLots,
        documents = documents,
        value = CreateContractParams.Award.Value(
            amount = value.amount,
            currency = value.currency
        )
    ).asSuccess()
}

private val allowedAwardDocuments = DocumentTypeAward.allowedElements
    .filter {
        when (it) {
            DocumentTypeAward.AWARD_NOTICE,
            DocumentTypeAward.EVALUATION_REPORTS,
            DocumentTypeAward.CONTRACT_DRAFT,
            DocumentTypeAward.WINNING_BID,
            DocumentTypeAward.COMPLAINTS,
            DocumentTypeAward.BIDDERS,
            DocumentTypeAward.CONFLICT_OF_INTEREST,
            DocumentTypeAward.CANCELLATION_DETAILS,
            DocumentTypeAward.SUBMISSION_DOCUMENTS,
            DocumentTypeAward.CONTRACT_ARRANGEMENTS,
            DocumentTypeAward.CONTRACT_SCHEDULE,
            DocumentTypeAward.SHORTLISTED_FIRMS -> true
        }
    }.toSet()

private fun CreateContractRequest.Award.Document.convert(path: String): Result<CreateContractParams.Award.Document, DataErrors> {
    val parsedType = parseAwardDocumentType(documentType, allowedAwardDocuments, "$path.documentType")
        .onFailure { return it }

    val relatedLots = relatedLots.validate(notEmptyRule("$path.relatedLots"))
        .onFailure { return it }
        .orEmpty()
        .map { parseLotId(it, "$path.relatedLots").onFailure { return it } }


    return CreateContractParams.Award.Document(
        id = id,
        documentType = parsedType,
        description = description,
        title = title,
        relatedLots = relatedLots
    ).asSuccess()
}

private fun CreateContractRequest.Award.Supplier.convert(): Result<CreateContractParams.Award.Supplier, DataErrors> {
    return CreateContractParams.Award.Supplier(
        id = id,
        name = name
    ).asSuccess()
}

private fun CreateContractRequest.Tender.convert(path: String): Result<CreateContractParams.Tender, DataErrors> {
    val lots = lots.validate(notEmptyRule("$path.lots"))
        .flatMap { it.mapResult { lot -> lot.convert("$path.lots") } }
        .onFailure { return it }

    val items = items.validate(notEmptyRule("$path.items"))
        .flatMap { it.mapResult { item -> item.convert("$path.items") } }
        .onFailure { return it }

    additionalProcurementCategories
        .validate(notEmptyRule("$path.additionalProcurementCategories"))

    return CreateContractParams.Tender(
        lots = lots,
        items = items,
        classification = CreateContractParams.Tender.Classification(
            id = classification.id,
            description = classification.description,
            scheme = classification.scheme
        ),
        additionalProcurementCategories = additionalProcurementCategories.orEmpty(),
        mainProcurementCategory = mainProcurementCategory,
        procurementMethod = procurementMethod,
        procurementMethodDetails = procurementMethodDetails
    ).asSuccess()
}

private fun CreateContractRequest.Tender.Lot.convert(path: String): Result<CreateContractParams.Tender.Lot, DataErrors> {
    val id = parseLotId(id, "$path.id")
        .onFailure { return it }

    return CreateContractParams.Tender.Lot(
        id = id,
        description = description,
        title = title,
        internalId = internalId,
        placeOfPerformance = CreateContractParams.Tender.Lot.PlaceOfPerformance(
            description = placeOfPerformance.description,
            address = placeOfPerformance.address.let { address ->
                CreateContractParams.Tender.Lot.PlaceOfPerformance.Address(
                    streetAddress = address.streetAddress,
                    postalCode = address.postalCode,
                    addressDetails = address.addressDetails.let { addressDetails ->
                        CreateContractParams.Tender.Lot.PlaceOfPerformance.Address.AddressDetails(
                            country = addressDetails.country.let { country ->
                                CreateContractParams.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Country(
                                    scheme = country.scheme,
                                    id = country.id,
                                    description = country.description,
                                    uri = country.uri
                                )
                            },
                            region = addressDetails.region.let { region ->
                                CreateContractParams.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Region(
                                    scheme = region.scheme,
                                    id = region.id,
                                    description = region.description,
                                    uri = region.uri
                                )
                            },
                            locality = addressDetails.locality.let { locality ->
                                CreateContractParams.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Locality(
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
    ).asSuccess()
}

private fun CreateContractRequest.Tender.Item.convert(path: String): Result<CreateContractParams.Tender.Item, DataErrors> {
    val id = parseItemId(id, "$path.id")
        .onFailure { return it }

    val parsedRelatedLotId = parseLotId(relatedLot, "$path.relatedLot")
        .onFailure { return it }

    additionalClassifications.validate(notEmptyRule("$path.additionalClassifications"))

    return CreateContractParams.Tender.Item(
        id = id,
        internalId = internalId,
        relatedLot = parsedRelatedLotId,
        description = description,
        quantity = quantity,
        unit = CreateContractParams.Tender.Item.Unit(
            id = unit.id,
            name = unit.name
        ),
        classification = CreateContractParams.Tender.Item.Classification(
            id = classification.id,
            scheme = classification.scheme,
            description = classification.description
        ),
        additionalClassifications = additionalClassifications.orEmpty()
            .map { additionalClassification ->
                CreateContractParams.Tender.Item.AdditionalClassification(
                    id = additionalClassification.id,
                    scheme = additionalClassification.scheme,
                    description = additionalClassification.description
                )
            }
    )
        .asSuccess()
}

