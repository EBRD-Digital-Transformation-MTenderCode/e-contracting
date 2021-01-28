package com.procurement.contracting.application.service

import com.procurement.contracting.application.repository.pac.PacRepository
import com.procurement.contracting.application.repository.pac.model.PacEntity
import com.procurement.contracting.application.service.model.pacs.DoPacsParams
import com.procurement.contracting.application.service.model.pacs.DoPacsResult
import com.procurement.contracting.domain.model.fc.Pac
import com.procurement.contracting.domain.model.pac.PacStatus
import com.procurement.contracting.domain.model.pac.PacStatusDetails
import com.procurement.contracting.domain.util.extension.toSetBy
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess
import org.springframework.stereotype.Service

interface PacService {
    fun create(params: DoPacsParams): Result<DoPacsResult, Fail>
}

@Service
class PacServiceImpl(
    private val generationService: GenerationService,
    private val transform: Transform,
    private val pacRepository: PacRepository
) : PacService {

    override fun create(params: DoPacsParams): Result<DoPacsResult, Fail> {
        val createdPacs = if (params.awards.isNotEmpty())
            createPacsByAwards(params)
        else
            listOf(createPac(params))

        val pacEntities = createdPacs.map { pac ->
            PacEntity.of(params.cpid, params.ocid, pac, transform = transform)
                .onFailure { return it }
        }
        pacRepository.save(pacEntities)
            .doOnFail { return it.asFailure() }

        return convertToPacResult(createdPacs).asSuccess()
    }

    private fun convertToPacResult(createdPacs: List<Pac>): DoPacsResult {
        return DoPacsResult(
            contracts = createdPacs.map { pac ->
                DoPacsResult.Contract(
                    id = pac.id,
                    status = pac.status,
                    statusDetails = pac.statusDetails,
                    date = pac.date,
                    relatedLots = pac.relatedLots,
                    awardId = pac.awardId,
                    suppliers = pac.suppliers.map { supplier ->
                        DoPacsResult.Contract.Supplier(
                            id = supplier.id,
                            name = supplier.name
                        )
                    },
                    agreedMetrics = pac.agreedMetrics.map { agreedMetric ->
                        DoPacsResult.Contract.AgreedMetric(
                            id = agreedMetric.id,
                            title = agreedMetric.title,
                            observations = agreedMetric.observations.map { observation ->
                                DoPacsResult.Contract.AgreedMetric.Observation(
                                    id = observation.id,
                                    notes = observation.notes,
                                    measure = observation.measure,
                                    relatedRequirementId = observation.relatedRequirementId,
                                    period = observation.period?.let { period ->
                                        DoPacsResult.Contract.AgreedMetric.Observation.Period(
                                            startDate = period.startDate,
                                            endDate = period.endDate
                                        )
                                    },
                                    unit = observation.unit?.let { unit ->
                                        DoPacsResult.Contract.AgreedMetric.Observation.Unit(
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
            token = createdPacs.first().token
        )
    }

    private fun createPac(params: DoPacsParams) = Pac(
        id = generationService.pacId(),
        date = params.date,
        owner = params.owner,
        status = PacStatus.PENDING,
        statusDetails = PacStatusDetails.ALL_REJECTED,
        relatedLots = listOf(params.tender.lots.first().id),
        token = generationService.token()
    )


    private fun createPacsByAwards(params: DoPacsParams) =
        params.awards.map { award ->
            val suppliers = createSuppliers(award)
            Pac(
                id = generationService.pacId(),
                date = params.date,
                owner = params.owner,
                awardId = award.id,
                status = PacStatus.PENDING,
                statusDetails = PacStatusDetails.CONCLUDED,
                suppliers = suppliers,
                relatedLots = listOf(params.tender.lots.first().id),
                agreedMetrics = createAgreedMetrics(params, suppliers),
            )
        }

    private fun createSuppliers(award: DoPacsParams.Award) =
        award.suppliers.map { supplier ->
            Pac.Supplier(
                id = supplier.id,
                name = supplier.name
            )
        }

    private fun createAgreedMetrics(
        params: DoPacsParams,
        suppliers: List<Pac.Supplier>
    ): List<Pac.AgreedMetric> =
        params.tender.criteria.map { criterion ->
            Pac.AgreedMetric(
                id = criterion.id,
                title = criterion.title,
                observations = createObservations(params, suppliers)
            )
        }

    private fun createObservations(
        params: DoPacsParams,
        suppliers: List<Pac.Supplier>
    ): List<Pac.AgreedMetric.Observation> {
        val responsesByRequirementIds = params.bids?.details.orEmpty()
            .asSequence()
            .filter { bid -> belongsToSuppliers(bid, suppliers) }
            .flatMap { it.requirementResponses }
            .associateBy { it.requirement.id }

        val requirements = params.tender.criteria.asSequence()
            .flatMap { it.requirementGroups }
            .flatMap { it.requirements }
            .filter { requirement -> requirement.id in responsesByRequirementIds.keys }
            .toList()

        val observationsByRequirementId = params.tender.targets
            .flatMap { it.observations }
            .associateBy { it.relatedRequirementId }

        return requirements.map { requirement ->
            requirement.toObservation(responsesByRequirementIds, observationsByRequirementId)
        }
    }

    private fun DoPacsParams.Tender.Criteria.RequirementGroup.Requirement.toObservation(
        responsesByRequirementIds: Map<String, DoPacsParams.Bids.Detail.RequirementResponse>,
        observationsByRequirementId: Map<String?, DoPacsParams.Tender.Target.Observation>
    ): Pac.AgreedMetric.Observation {

        val requirementResponse = responsesByRequirementIds.getValue(id)
        val unit = observationsByRequirementId[id]?.unit

        return Pac.AgreedMetric.Observation(
            id = requirementResponse.id,
            notes = title,
            measure = requirementResponse.value,
            relatedRequirementId = id,
            period = requirementResponse.period?.let { period ->
                Pac.AgreedMetric.Observation.Period(
                    startDate = period.startDate,
                    endDate = period.endDate
                )
            },
            unit = unit?.let {
                Pac.AgreedMetric.Observation.Unit(
                    id = unit.id,
                    name = unit.name
                )
            }
        )
    }

    private fun belongsToSuppliers(
        detail: DoPacsParams.Bids.Detail,
        suppliers: List<Pac.Supplier>
    ): Boolean {
        val tenderersIds = detail.tenderers.toSetBy { tenderer -> tenderer.id }
        val supplierIds = suppliers.toSetBy { supplier -> supplier.id }
        return tenderersIds == supplierIds
    }
}
