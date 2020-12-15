package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.CreatePacsParams
import com.procurement.contracting.domain.model.parseAwardId
import com.procurement.contracting.domain.model.parseBidId
import com.procurement.contracting.domain.model.parseCpid
import com.procurement.contracting.domain.model.parseDate
import com.procurement.contracting.domain.model.parseLotId
import com.procurement.contracting.domain.model.parseOcid
import com.procurement.contracting.domain.model.parseOwner
import com.procurement.contracting.domain.util.extension.mapResult
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.infrastructure.handler.v2.converter.rule.notEmptyRule
import com.procurement.contracting.infrastructure.handler.v2.model.request.CreatePacsRequest
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess
import com.procurement.contracting.lib.functional.flatMap
import com.procurement.contracting.lib.functional.validate

fun CreatePacsRequest.convert(): Result<CreatePacsParams, DataErrors> {
    val cpid = parseCpid(cpid)
        .onFailure { return it }

    val ocid = parseOcid(ocid)
        .onFailure { return it }

    val owner = parseOwner(owner)
        .onFailure { return it }

    val date = parseDate(date)
        .onFailure { return it }

    val tender = tender.convert("tender")
        .onFailure { return it }

    val bids = bids?.convert("bids")
        ?.onFailure { return it }

    val awards = awards.validate(notEmptyRule("awards"))
        .flatMap { it.orEmpty().mapResult { award -> award.convert("awards") } }
        .onFailure { return it }

    return CreatePacsParams(
        cpid = cpid,
        ocid = ocid,
        date = date,
        owner = owner,
        tender = tender,
        bids = bids,
        awards = awards

    ).asSuccess()
}

private fun CreatePacsRequest.Award.convert(path: String): Result<CreatePacsParams.Award, DataErrors> {
    val id = parseAwardId(id, "$path.id")
        .onFailure { return it }

    val suppliers = suppliers.validate(notEmptyRule("$path.suppliers"))
        .flatMap { it.mapResult { supplier -> supplier.convert() } }
        .onFailure { return it }

    return CreatePacsParams.Award(
        id = id,
        suppliers = suppliers
    ).asSuccess()
}

private fun CreatePacsRequest.Award.Supplier.convert(): Result<CreatePacsParams.Award.Supplier, DataErrors> {
    return CreatePacsParams.Award.Supplier(
        id = id,
        name = name
    ).asSuccess()
}

private fun CreatePacsRequest.Bids.convert(path: String): Result<CreatePacsParams.Bids, DataErrors> {
    val details = details.validate(notEmptyRule("$path.details"))
        .flatMap { it.mapResult { detail -> detail.convert("$path.details") } }
        .onFailure { return it }

    return CreatePacsParams.Bids(
        details = details
    ).asSuccess()
}

private fun CreatePacsRequest.Bids.Detail.convert(path: String): Result<CreatePacsParams.Bids.Detail, DataErrors> {
    val id = parseBidId(id, "$path.id")
        .onFailure { return it }

    val tenderers = tenderers.validate(notEmptyRule("$path.tenderers"))
        .flatMap { it.mapResult { tenderer -> tenderer.convert() } }
        .onFailure { return it }

    val requirementResponses = requirementResponses.validate(notEmptyRule("$path.requirementResponses"))
        .flatMap {
            it.orEmpty()
                .mapResult { requirementResponse -> requirementResponse.convert("$path.requirementResponses") }
        }
        .onFailure { return it }

    return CreatePacsParams.Bids.Detail(
        id = id,
        tenderers = tenderers,
        requirementResponses = requirementResponses
    ).asSuccess()
}

private fun CreatePacsRequest.Bids.Detail.RequirementResponse.convert(path: String): Result<CreatePacsParams.Bids.Detail.RequirementResponse, DataErrors> {
    val period = period?.convert(path)
        ?.onFailure { return it }

    return CreatePacsParams.Bids.Detail.RequirementResponse(
        id = id,
        value = value,
        requirement = CreatePacsParams.Bids.Detail.RequirementResponse.Requirement(requirement.id),
        period = period
    ).asSuccess()
}

private fun CreatePacsRequest.Bids.Detail.RequirementResponse.Period.convert(path: String): Result<CreatePacsParams.Bids.Detail.RequirementResponse.Period, DataErrors> {
    val startDate = parseDate(startDate, "$path.startDate")
        .onFailure { return it }

    val endDate = parseDate(endDate, "$path.endDate")
        .onFailure { return it }

    return CreatePacsParams.Bids.Detail.RequirementResponse.Period(
        startDate = startDate,
        endDate = endDate
    ).asSuccess()
}

private fun CreatePacsRequest.Bids.Detail.Tenderer.convert(): Result<CreatePacsParams.Bids.Detail.Tenderer, DataErrors> {
    return CreatePacsParams.Bids.Detail.Tenderer(
        id = id,
        name = name
    ).asSuccess()
}

private fun CreatePacsRequest.Tender.convert(path: String): Result<CreatePacsParams.Tender, DataErrors> {
    val lots = lots.validate(notEmptyRule("$path.lots"))
        .flatMap { it.mapResult { lot -> lot.convert("$path.lots") } }
        .onFailure { return it }

    val targets = targets.validate(notEmptyRule("$path.targets"))
        .flatMap { it.orEmpty().mapResult { target -> target.convert("$path.targets") } }
        .onFailure { return it }

    val criteria = criteria.validate(notEmptyRule("$path.criteria"))
        .flatMap { it.orEmpty().mapResult { criterion -> criterion.convert("$path.criteria") } }
        .onFailure { return it }

    return CreatePacsParams.Tender(
        lots = lots,
        targets = targets,
        criteria = criteria
    ).asSuccess()
}

private fun CreatePacsRequest.Tender.Criteria.convert(path: String): Result<CreatePacsParams.Tender.Criteria, DataErrors> {
    val requirementGroups = requirementGroups.validate(notEmptyRule("$path.requirementGroups"))
        .flatMap { it.mapResult { requirementGroup -> requirementGroup.convert("$path.requirementGroups") } }
        .onFailure { return it }

    return CreatePacsParams.Tender.Criteria(
        id = id,
        title = title,
        relatesTo = relatesTo,
        relatedItem = relatedItem,
        requirementGroups = requirementGroups
    ).asSuccess()
}

private fun CreatePacsRequest.Tender.Criteria.RequirementGroup.convert(path: String): Result<CreatePacsParams.Tender.Criteria.RequirementGroup, DataErrors> {
    val requirements = requirements.validate(notEmptyRule("$path.requirements"))
        .flatMap { it.mapResult { requirement -> requirement.convert() } }
        .onFailure { return it }

    return CreatePacsParams.Tender.Criteria.RequirementGroup(
        id = id,
        requirements = requirements
    ).asSuccess()
}

private fun CreatePacsRequest.Tender.Criteria.RequirementGroup.Requirement.convert(): Result<CreatePacsParams.Tender.Criteria.RequirementGroup.Requirement, DataErrors> {
    return CreatePacsParams.Tender.Criteria.RequirementGroup.Requirement(
        id = id,
        title = title
    ).asSuccess()
}

private fun CreatePacsRequest.Tender.Target.convert(path: String): Result<CreatePacsParams.Tender.Target, DataErrors> {
    val observations = observations.validate(notEmptyRule("$path.observations"))
        .flatMap { it.mapResult { observation -> observation.convert() } }
        .onFailure { return it }

    return CreatePacsParams.Tender.Target(
        id = id,
        observations = observations
    ).asSuccess()
}

private fun CreatePacsRequest.Tender.Target.Observation.convert(): Result<CreatePacsParams.Tender.Target.Observation, DataErrors> {
    return CreatePacsParams.Tender.Target.Observation(
        id = id,
        relatedRequirementId = relatedRequirementId,
        unit = CreatePacsParams.Tender.Target.Observation.Unit(
            id = unit.id,
            name = unit.name
        )
    ).asSuccess()
}

private fun CreatePacsRequest.Tender.Lot.convert(path: String): Result<CreatePacsParams.Tender.Lot, DataErrors> {
    val id = parseLotId(id, "$path.id")
        .onFailure { return it }

    return CreatePacsParams.Tender.Lot(
        id = id
    ).asSuccess()
}
