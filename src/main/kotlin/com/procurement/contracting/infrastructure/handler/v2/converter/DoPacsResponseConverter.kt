package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.pacs.DoPacsResult
import com.procurement.contracting.domain.util.extension.asString
import com.procurement.contracting.infrastructure.handler.v2.model.response.DoPacsResponse

fun DoPacsResult.convert() = DoPacsResponse(
    contracts = contracts.map { pac ->
        DoPacsResponse.Contract(
            id = pac.id.underlying,
            status = pac.status.key,
            date = pac.date.asString(),
            token = pac.token.underlying.toString(),
            relatedLots = pac.relatedLots.map { it.underlying },
            awardId = pac.awardId.toString(),
            suppliers = pac.suppliers
                .map { supplier ->
                    DoPacsResponse.Contract.Supplier(
                        id = supplier.id,
                        name = supplier.name
                    )
                },
            agreedMetrics = pac.agreedMetrics
                ?.map { agreedMetric ->
                    DoPacsResponse.Contract.AgreedMetric(
                        id = agreedMetric.id,
                        title = agreedMetric.title,
                        observations = agreedMetric.observations.map { observation ->
                            DoPacsResponse.Contract.AgreedMetric.Observation(
                                id = observation.id,
                                notes = observation.notes,
                                measure = observation.measure,
                                relatedRequirementId = observation.relatedRequirementId,
                                period = observation.period
                                    ?.let { period ->
                                        DoPacsResponse.Contract.AgreedMetric.Observation.Period(
                                            startDate = period.startDate.asString(),
                                            endDate = period.endDate.asString()
                                        )
                                    },
                                unit = observation.unit
                                    ?.let { unit ->
                                        DoPacsResponse.Contract.AgreedMetric.Observation.Unit(
                                            id = unit.id,
                                            name = unit.name
                                        )
                                    }
                            )
                        }
                    )
                }
        )
    }
)
