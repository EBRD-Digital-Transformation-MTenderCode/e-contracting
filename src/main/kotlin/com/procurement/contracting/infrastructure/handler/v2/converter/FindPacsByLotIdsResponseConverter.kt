package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.FindPacsByLotIdsResult
import com.procurement.contracting.domain.util.extension.asString
import com.procurement.contracting.infrastructure.handler.v2.model.response.FindPacsByLotIdsResponse

fun FindPacsByLotIdsResult.convert() = FindPacsByLotIdsResponse(
    contracts = contracts.map { pac -> pac.convert() }
)

fun FindPacsByLotIdsResult.Contract.convert() = FindPacsByLotIdsResponse.Contract(
    id = id.underlying,
    status = status.key,
    date = date.asString(),
    relatedLots = relatedLots.map { it.underlying },
    awardId = awardId.toString(),
    suppliers = suppliers.map { supplier -> supplier.convert() },
    agreedMetrics = agreedMetrics?.map { agreedMetric -> agreedMetric.convert() }
)

fun FindPacsByLotIdsResult.Contract.Supplier.convert(): FindPacsByLotIdsResponse.Contract.Supplier =
    FindPacsByLotIdsResponse.Contract.Supplier(
        id = id,
        name = name
    )

fun FindPacsByLotIdsResult.Contract.AgreedMetric.convert(): FindPacsByLotIdsResponse.Contract.AgreedMetric =
    FindPacsByLotIdsResponse.Contract.AgreedMetric(
        id = id,
        title = title,
        observations = observations.map { observation -> observation.convert() }
    )

fun FindPacsByLotIdsResult.Contract.AgreedMetric.Observation.convert(): FindPacsByLotIdsResponse.Contract.AgreedMetric.Observation =
    FindPacsByLotIdsResponse.Contract.AgreedMetric.Observation(
        id = id,
        notes = notes,
        measure = measure,
        relatedRequirementId = relatedRequirementId,
        period = period?.convert(),
        unit = unit?.convert()
    )

fun FindPacsByLotIdsResult.Contract.AgreedMetric.Observation.Period.convert(): FindPacsByLotIdsResponse.Contract.AgreedMetric.Observation.Period =
    FindPacsByLotIdsResponse.Contract.AgreedMetric.Observation.Period(
        startDate = startDate.asString(),
        endDate = endDate.asString()
    )

fun FindPacsByLotIdsResult.Contract.AgreedMetric.Observation.Unit.convert(): FindPacsByLotIdsResponse.Contract.AgreedMetric.Observation.Unit =
    FindPacsByLotIdsResponse.Contract.AgreedMetric.Observation.Unit(
        id = id,
        name = name
    )