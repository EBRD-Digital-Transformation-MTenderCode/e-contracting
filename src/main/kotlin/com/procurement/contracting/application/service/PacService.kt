package com.procurement.contracting.application.service

import com.procurement.contracting.application.repository.pac.PacRepository
import com.procurement.contracting.application.repository.pac.model.PacEntity
import com.procurement.contracting.application.service.model.FindPacsByLotIdsParams
import com.procurement.contracting.application.service.model.FindPacsByLotIdsResult
import com.procurement.contracting.application.service.model.pacs.DoPacsParams
import com.procurement.contracting.application.service.model.pacs.DoPacsResult
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.fc.Pac
import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.pac.PacStatus
import com.procurement.contracting.domain.util.extension.mapResult
import com.procurement.contracting.domain.util.extension.toSetBy
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess
import org.springframework.stereotype.Service

interface PacService {
    fun create(params: DoPacsParams): Result<DoPacsResult, Fail>
    fun findPacsByLotIds(params: FindPacsByLotIdsParams): Result<FindPacsByLotIdsResult, Fail>
}

@Service
class PacServiceImpl(
    private val generationService: GenerationService,
    private val transform: Transform,
    private val pacRepository: PacRepository
) : PacService {

    override fun create(params: DoPacsParams): Result<DoPacsResult, Fail> {
        val activePacByAwardId = pacRepository.findBy(params.cpid, params.ocid)
            .onFailure { return it }
            .mapResult { transform.tryDeserialization(it.jsonData, Pac::class.java) }
            .onFailure { return it }
            .filter { it.status == PacStatus.PENDING && it.awardId != null} // find active PAC's created on award
            .associateBy { it.awardId!! }

        // create PACs for new awards from request
        val createdPacs = createPacsByAwards(params, activePacByAwardId).onFailure { return it }

        val receivedAwardsId = params.awards.toSetBy { it.id }
        val canceledPacs = activePacByAwardId
            .filter { (id, _) -> id !in receivedAwardsId }
            .map { (_, pac) -> pac.copy(status = PacStatus.CANCELLED) }

        val createdPacEntities = (createdPacs)
            .mapResult { pac -> PacEntity.of(params.cpid, params.ocid, pac, transform = transform) }
            .onFailure { return it }

        pacRepository.save(createdPacEntities)
            .doOnFail { return it.asFailure() }

        canceledPacs
            .mapResult { pac -> PacEntity.of(params.cpid, params.ocid, pac, transform = transform) }
            .onFailure { return it }
            .mapResult { canceledPac -> pacRepository.update(canceledPac) }
            .onFailure { return it }

        return convertToPacResult(createdPacs).asSuccess()
    }

    override fun findPacsByLotIds(params: FindPacsByLotIdsParams): Result<FindPacsByLotIdsResult, Fail> {
        val receivedLots = params.tender.lots.toSetBy { it.id }

        return pacRepository.findBy(params.cpid, params.ocid)
            .onFailure { return it }
            .mapResult { transform.tryDeserialization(it.jsonData, Pac::class.java) }
            .onFailure { return it }
            .filter { it.isActive() && it.isForLot() && it.hasRelationWithLots(receivedLots) }
            .map { FindPacsByLotIdsResult.fromDomain(it) }
            .let { FindPacsByLotIdsResult(it) }
            .asSuccess()
    }

    private fun Pac.isActive(): Boolean = this.status == PacStatus.PENDING
    private fun Pac.isForLot(): Boolean = this.relatedLots.isNotEmpty()
    private fun Pac.hasRelationWithLots(lots: Collection<String>): Boolean = this.relatedLots.any { it.underlying in lots }

    private fun convertToPacResult(createdPacs: List<Pac>): DoPacsResult {
        return DoPacsResult(
            contracts = createdPacs.map { pac ->
                DoPacsResult.Contract(
                    id = pac.id,
                    status = pac.status,
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
            }
        )
    }

    private fun createPacsByAwards(params: DoPacsParams, activePacByAwardId: Map<AwardId, Pac>): Result<List<Pac>, Fail.Incident> {
        return params.awards
            .filter { award -> activePacByAwardId[award.id] == null } // find awards for creating new PAC
            .map { award -> createPac(generationService.pacId(), award, params) }
            .asSuccess()
    }

    private fun createSuppliers(award: DoPacsParams.Award) =
        award.suppliers.map { supplier ->
            Pac.Supplier(
                id = supplier.id,
                name = supplier.name
            )
        }

    private fun createPac(pacId: PacId, award: DoPacsParams.Award, params: DoPacsParams): Pac {
        val suppliers = createSuppliers(award)
        return Pac(
            id = pacId,
            date = params.date,
            owner = params.owner,
            awardId = award.id,
            status = PacStatus.PENDING,
            statusDetails = null,
            suppliers = suppliers,
            relatedLots = listOf(params.tender.lots.first().id),
            agreedMetrics = createAgreedMetrics(params, suppliers),
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
