package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.pacs.CreatePacsResult
import com.procurement.contracting.domain.util.extension.asString
import com.procurement.contracting.infrastructure.handler.v2.model.response.CreatePacsResponse

fun CreatePacsResult.convert() = CreatePacsResponse(
    contracts = contracts.map { pac ->
        CreatePacsResponse.Contract(
            id = pac.id.underlying,
            status = pac.status.key,
            statusDetails = pac.statusDetails.key,
            date = pac.date.asString(),
            relatedLots = pac.relatedLots.map { it.underlying },
            awardId = pac.awardId.toString(),
            suppliers = pac.suppliers
                ?.map { supplier ->
                    CreatePacsResponse.Contract.Supplier(
                        id = supplier.id,
                        name = supplier.name
                    )
                },
            agreedMetrics = pac.agreedMetrics
                ?.map { agreedMetric ->
                    CreatePacsResponse.Contract.AgreedMetric(
                        id = agreedMetric.id,
                        title = agreedMetric.title,
                        observations = agreedMetric.observations.map { observation ->
                            CreatePacsResponse.Contract.AgreedMetric.Observation(
                                id = observation.id,
                                notes = observation.notes,
                                measure = observation.measure,
                                relatedRequirementId = observation.relatedRequirementId,
                                period = observation.period
                                    ?.let { period ->
                                        CreatePacsResponse.Contract.AgreedMetric.Observation.Period(
                                            startDate = period.startDate.asString(),
                                            endDate = period.endDate.asString()
                                        )
                                    },
                                unit = observation.unit
                                    ?.let { unit ->
                                        CreatePacsResponse.Contract.AgreedMetric.Observation.Unit(
                                            id = unit.id,
                                            name = unit.name
                                        )
                                    }
                            )
                        }
                    )
                }
        )
    },
    token = token.toString()
)
