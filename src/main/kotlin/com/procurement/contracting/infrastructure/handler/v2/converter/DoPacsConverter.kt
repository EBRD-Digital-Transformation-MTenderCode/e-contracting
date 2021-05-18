package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.pacs.DoPacsParams
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
import com.procurement.contracting.infrastructure.handler.v2.model.request.DoPacsRequest
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess
import com.procurement.contracting.lib.functional.flatMap
import com.procurement.contracting.lib.functional.validate

fun DoPacsRequest.convert(): Result<DoPacsParams, DataErrors> {
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

    return DoPacsParams(
        cpid = cpid,
        ocid = ocid,
        date = date,
        owner = owner,
        tender = tender,
        bids = bids,
        awards = awards

    ).asSuccess()
}

private fun DoPacsRequest.Award.convert(path: String): Result<DoPacsParams.Award, DataErrors> {
    val id = parseAwardId(id, "$path.id")
        .onFailure { return it }

    val suppliers = suppliers.validate(notEmptyRule("$path.suppliers"))
        .flatMap { it.mapResult { supplier -> supplier.convert() } }
        .onFailure { return it }

    return DoPacsParams.Award(
        id = id,
        suppliers = suppliers
    ).asSuccess()
}

private fun DoPacsRequest.Award.Supplier.convert(): Result<DoPacsParams.Award.Supplier, DataErrors> {
    return DoPacsParams.Award.Supplier(
        id = id,
        name = name
    ).asSuccess()
}

private fun DoPacsRequest.Bids.convert(path: String): Result<DoPacsParams.Bids, DataErrors> {
    val details = details.validate(notEmptyRule("$path.details"))
        .flatMap { it.mapResult { detail -> detail.convert("$path.details") } }
        .onFailure { return it }

    return DoPacsParams.Bids(
        details = details
    ).asSuccess()
}

private fun DoPacsRequest.Bids.Detail.convert(path: String): Result<DoPacsParams.Bids.Detail, DataErrors> {
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

    return DoPacsParams.Bids.Detail(
        id = id,
        tenderers = tenderers,
        requirementResponses = requirementResponses
    ).asSuccess()
}

private fun DoPacsRequest.Bids.Detail.RequirementResponse.convert(path: String): Result<DoPacsParams.Bids.Detail.RequirementResponse, DataErrors> {
    val period = period?.convert(path)
        ?.onFailure { return it }

    return DoPacsParams.Bids.Detail.RequirementResponse(
        id = id,
        value = value,
        requirement = DoPacsParams.Bids.Detail.RequirementResponse.Requirement(requirement.id),
        period = period
    ).asSuccess()
}

private fun DoPacsRequest.Bids.Detail.RequirementResponse.Period.convert(path: String): Result<DoPacsParams.Bids.Detail.RequirementResponse.Period, DataErrors> {
    val startDate = parseDate(startDate, "$path.startDate")
        .onFailure { return it }

    val endDate = parseDate(endDate, "$path.endDate")
        .onFailure { return it }

    return DoPacsParams.Bids.Detail.RequirementResponse.Period(
        startDate = startDate,
        endDate = endDate
    ).asSuccess()
}

private fun DoPacsRequest.Bids.Detail.Tenderer.convert(): Result<DoPacsParams.Bids.Detail.Tenderer, DataErrors> {
    return DoPacsParams.Bids.Detail.Tenderer(
        id = id,
        name = name
    ).asSuccess()
}

private fun DoPacsRequest.Tender.convert(path: String): Result<DoPacsParams.Tender, DataErrors> {
    val lots = lots.validate(notEmptyRule("$path.lots"))
        .flatMap { it.mapResult { lot -> lot.convert("$path.lots") } }
        .onFailure { return it }

    val targets = targets.validate(notEmptyRule("$path.targets"))
        .flatMap { it.orEmpty().mapResult { target -> target.convert("$path.targets") } }
        .onFailure { return it }

    val criteria = criteria.validate(notEmptyRule("$path.criteria"))
        .flatMap { it.orEmpty().mapResult { criterion -> criterion.convert("$path.criteria") } }
        .onFailure { return it }

    return DoPacsParams.Tender(
        lots = lots,
        targets = targets,
        criteria = criteria
    ).asSuccess()
}

private fun DoPacsRequest.Tender.Criteria.convert(path: String): Result<DoPacsParams.Tender.Criteria, DataErrors> {
    val requirementGroups = requirementGroups.validate(notEmptyRule("$path.requirementGroups"))
        .flatMap { it.mapResult { requirementGroup -> requirementGroup.convert("$path.requirementGroups") } }
        .onFailure { return it }

    return DoPacsParams.Tender.Criteria(
        id = id,
        title = title,
        relatesTo = relatesTo,
        relatedItem = relatedItem,
        requirementGroups = requirementGroups
    ).asSuccess()
}

private fun DoPacsRequest.Tender.Criteria.RequirementGroup.convert(path: String): Result<DoPacsParams.Tender.Criteria.RequirementGroup, DataErrors> {
    val requirements = requirements.validate(notEmptyRule("$path.requirements"))
        .flatMap { it.mapResult { requirement -> requirement.convert() } }
        .onFailure { return it }

    return DoPacsParams.Tender.Criteria.RequirementGroup(
        id = id,
        requirements = requirements
    ).asSuccess()
}

private fun DoPacsRequest.Tender.Criteria.RequirementGroup.Requirement.convert(): Result<DoPacsParams.Tender.Criteria.RequirementGroup.Requirement, DataErrors> {
    return DoPacsParams.Tender.Criteria.RequirementGroup.Requirement(
        id = id,
        title = title
    ).asSuccess()
}

private fun DoPacsRequest.Tender.Target.convert(path: String): Result<DoPacsParams.Tender.Target, DataErrors> {
    val observations = observations.validate(notEmptyRule("$path.observations"))
        .flatMap { it.mapResult { observation -> observation.convert() } }
        .onFailure { return it }

    return DoPacsParams.Tender.Target(
        id = id,
        observations = observations
    ).asSuccess()
}

private fun DoPacsRequest.Tender.Target.Observation.convert(): Result<DoPacsParams.Tender.Target.Observation, DataErrors> {
    return DoPacsParams.Tender.Target.Observation(
        id = id,
        relatedRequirementId = relatedRequirementId,
        unit = DoPacsParams.Tender.Target.Observation.Unit(
            id = unit.id,
            name = unit.name
        )
    ).asSuccess()
}

private fun DoPacsRequest.Tender.Lot.convert(path: String): Result<DoPacsParams.Tender.Lot, DataErrors> {
    val id = parseLotId(id, "$path.id")
        .onFailure { return it }

    return DoPacsParams.Tender.Lot(
        id = id
    ).asSuccess()
}
