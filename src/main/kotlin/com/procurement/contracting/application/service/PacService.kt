package com.procurement.contracting.application.service

import com.procurement.contracting.application.repository.can.CANRepository
import com.procurement.contracting.application.repository.can.model.CANEntity
import com.procurement.contracting.application.repository.fc.FrameworkContractRepository
import com.procurement.contracting.application.repository.fc.model.FrameworkContractEntity
import com.procurement.contracting.application.repository.pac.PacRepository
import com.procurement.contracting.application.repository.pac.model.PacRecord
import com.procurement.contracting.application.service.errors.GetPacErrors
import com.procurement.contracting.application.service.errors.SetStateForContractsErrors
import com.procurement.contracting.application.service.model.FindPacsByLotIdsParams
import com.procurement.contracting.application.service.model.FindPacsByLotIdsResult
import com.procurement.contracting.application.service.model.GetPacParams
import com.procurement.contracting.application.service.model.SetStateForContractsParams
import com.procurement.contracting.application.service.model.SetStateForContractsParams.OperationType.APPLY_CONFIRMATIONS
import com.procurement.contracting.application.service.model.SetStateForContractsParams.OperationType.COMPLETE_SOURCING
import com.procurement.contracting.application.service.model.SetStateForContractsParams.OperationType.CREATE_CONTRACT
import com.procurement.contracting.application.service.model.SetStateForContractsParams.OperationType.ISSUING_FRAMEWORK_CONTRACT
import com.procurement.contracting.application.service.model.SetStateForContractsParams.OperationType.NEXT_STEP_AFTER_BUYERS_CONFIRMATION
import com.procurement.contracting.application.service.model.SetStateForContractsParams.OperationType.NEXT_STEP_AFTER_INVITED_CANDIDATES_CONFIRMATION
import com.procurement.contracting.application.service.model.SetStateForContractsParams.OperationType.NEXT_STEP_AFTER_SUPPLIERS_CONFIRMATION
import com.procurement.contracting.application.service.model.pacs.DoPacsParams
import com.procurement.contracting.application.service.model.pacs.DoPacsResult
import com.procurement.contracting.application.service.rule.RulesService
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.CAN
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.fc.FrameworkContract
import com.procurement.contracting.domain.model.fc.Pac
import com.procurement.contracting.domain.model.fc.PacEntity
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatus
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatusDetails
import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.pac.PacStatus
import com.procurement.contracting.domain.model.pac.PacStatusDetails
import com.procurement.contracting.domain.model.process.Stage
import com.procurement.contracting.domain.util.extension.mapResult
import com.procurement.contracting.domain.util.extension.toSetBy
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.fail.Fail.Incident
import com.procurement.contracting.infrastructure.handler.v2.model.response.GetPacResponse
import com.procurement.contracting.infrastructure.handler.v2.model.response.SetStateForContractsResponse
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.ValidationResult
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess
import com.procurement.contracting.lib.functional.asValidationError
import org.springframework.stereotype.Service

interface PacService {
    fun create(params: DoPacsParams): Result<DoPacsResult?, Fail>
    fun findPacsByLotIds(params: FindPacsByLotIdsParams): Result<FindPacsByLotIdsResult, Fail>
    fun setState(params: SetStateForContractsParams): Result<SetStateForContractsResponse, Fail>
    fun getPac(params: GetPacParams): Result<GetPacResponse, Fail>
}

@Service
class PacServiceImpl(
    private val generationService: GenerationService,
    private val transform: Transform,
    private val pacRepository: PacRepository,
    private val canRepository: CANRepository,
    private val frameworkContractRepository: FrameworkContractRepository,
    private val rulesService: RulesService
) : PacService {

    override fun create(params: DoPacsParams): Result<DoPacsResult?, Fail> {
        val activePacByAwardId = pacRepository.findBy(params.cpid, params.ocid)
            .onFailure { return it }
            .mapResult { transform.tryDeserialization(it.jsonData, PacEntity::class.java) }
            .onFailure { return it }
            .map { it.toDomain() }
            .filter { it.status == PacStatus.PENDING && it.awardId != null } // find active PAC's created on award
            .associateBy { it.awardId!! }

        // create PACs for new awards from request
        val createdPacs = createPacsByAwards(params, activePacByAwardId).onFailure { return it }

        val receivedAwardsId = params.awards.toSetBy { it.id }
        val canceledPacs = activePacByAwardId
            .filter { (id, _) -> id !in receivedAwardsId }
            .map { (_, pac) -> pac.copy(status = PacStatus.CANCELLED) }

        val createdPacEntities = (createdPacs)
            .mapResult { pac -> PacRecord.of(params.cpid, params.ocid, pac, transform = transform) }
            .onFailure { return it }

        pacRepository.save(createdPacEntities)
            .doOnFail { return it.asFailure() }

        canceledPacs
            .mapResult { pac -> PacRecord.of(params.cpid, params.ocid, pac, transform = transform) }
            .onFailure { return it }
            .mapResult { canceledPac -> pacRepository.update(canceledPac) }
            .onFailure { return it }

        val pacsForResponse = createdPacs + canceledPacs

        return if (pacsForResponse.isNotEmpty())
            convertToPacResult(pacsForResponse).asSuccess()
        else
            null.asSuccess()
    }

    override fun findPacsByLotIds(params: FindPacsByLotIdsParams): Result<FindPacsByLotIdsResult, Fail> {
        val receivedLots = params.tender.lots.toSetBy { it.id }

        return pacRepository.findBy(params.cpid, params.ocid)
            .onFailure { return it }
            .mapResult { transform.tryDeserialization(it.jsonData, PacEntity::class.java) }
            .onFailure { return it }
            .map { it.toDomain() }
            .filter { it.isActive() && it.isForLot() && it.hasRelationWithLots(receivedLots) }
            .map { FindPacsByLotIdsResult.fromDomain(it) }
            .let { FindPacsByLotIdsResult(it) }
            .asSuccess()
    }

    private fun Pac.isActive(): Boolean = this.status == PacStatus.PENDING
    private fun Pac.isForLot(): Boolean = this.relatedLots.isNotEmpty()
    private fun Pac.hasRelationWithLots(lots: Collection<String>): Boolean =
        this.relatedLots.any { it.underlying in lots }

    override fun setState(params: SetStateForContractsParams): Result<SetStateForContractsResponse, Fail> =
        when (params.operationType) {
            COMPLETE_SOURCING -> setStateForPACLinkedToLot(params)
            NEXT_STEP_AFTER_SUPPLIERS_CONFIRMATION -> {
                checkStageForPACState(params.ocid.stage)
                    .doOnError { return it.asFailure() }
                setStateForPAC(params)
            }
            APPLY_CONFIRMATIONS -> setStateForApplyConfirmations(params)
            NEXT_STEP_AFTER_BUYERS_CONFIRMATION,
            NEXT_STEP_AFTER_INVITED_CANDIDATES_CONFIRMATION,
            ISSUING_FRAMEWORK_CONTRACT -> {
                checkStageForFCState(params.ocid.stage)
                    .doOnError { return it.asFailure() }
                setStateForFC(params)
            }
            CREATE_CONTRACT -> setStateForCAN(params)
                .doOnError { return it.asFailure() }
        }

    override fun getPac(params: GetPacParams): Result<GetPacResponse, Fail> {
        val contractId = params.contracts.first().id
        val pacRecord = pacRepository.findBy(params.cpid, params.ocid, contractId)
            .onFailure { return it }
            ?: return GetPacErrors.PacNotFound(cpid = params.cpid, ocid = params.ocid, contractId = contractId)
                .asFailure()

        val pacEntity = transform.tryDeserialization(pacRecord.jsonData, PacEntity::class.java)
            .onFailure { return it }
            .toDomain()

        return GetPacResponse.ResponseConverter.fromDomain(pacEntity = pacEntity).asSuccess()
    }

    private fun checkStageForPACState(stage: Stage): ValidationResult<Fail> =
        when (stage) {
            Stage.PC -> ValidationResult.ok()
            Stage.AC,
            Stage.EI,
            Stage.EV,
            Stage.FE,
            Stage.FS,
            Stage.NP,
            Stage.PN,
            Stage.PO,
            Stage.RQ,
            Stage.TP -> SetStateForContractsErrors.InvalidStage(stage).asValidationError()
        }

    private fun checkStageForFCState(stage: Stage): ValidationResult<Fail> =
        when (stage) {
            Stage.FE -> ValidationResult.ok()
            Stage.AC,
            Stage.EI,
            Stage.EV,
            Stage.FS,
            Stage.NP,
            Stage.PC,
            Stage.PN,
            Stage.RQ,
            Stage.PO,
            Stage.TP -> SetStateForContractsErrors.InvalidStage(stage).asValidationError()
        }

    private fun setStateForApplyConfirmations(params: SetStateForContractsParams): Result<SetStateForContractsResponse, Fail> =
        when (params.ocid.stage) {
            Stage.FE -> setStateForFC(params)
            Stage.PC -> setStateForPAC(params)
            Stage.AC,
            Stage.EI,
            Stage.EV,
            Stage.FS,
            Stage.NP,
            Stage.PN,
            Stage.PO,
            Stage.RQ,
            Stage.TP -> SetStateForContractsErrors.InvalidStage(params.ocid.stage).asFailure()
        }

    private fun setStateForFC(params: SetStateForContractsParams): Result<SetStateForContractsResponse, Fail> {
        if (params.contracts.isEmpty())
            return SetStateForContractsErrors.ContractsMissing().asFailure()

        val frameworkContractId = FrameworkContractId.orNull(params.contracts.first().id)!!

        val frameworkContract = frameworkContractRepository
            .findBy(params.cpid, params.ocid, frameworkContractId)
            .onFailure { return it }
            ?.let { transform.tryDeserialization(it.jsonData, FrameworkContract::class.java) }
            ?.onFailure { return it }
            ?: return SetStateForContractsErrors.FCNotFound(params.cpid, params.ocid, frameworkContractId).asFailure()

        val stateForSetting = rulesService.getStateForSetting(
            country = params.country,
            pmd = params.pmd.base,
            operationType = params.operationType.base,
            stage = params.ocid.stage
        ).onFailure { return it }

        val updatedFrameworkContract = frameworkContract.copy(
            status = FrameworkContractStatus.orNull(stateForSetting.status)!!,
            statusDetails = FrameworkContractStatusDetails.orNull(stateForSetting.statusDetails)!!
        )

        val updatedEntity = FrameworkContractEntity.of(params.cpid, params.ocid, updatedFrameworkContract, transform)
            .onFailure { return it }

        val wasApplied = frameworkContractRepository.update(updatedEntity).onFailure { return it }
        if (!wasApplied)
            return Incident.Database.ConsistencyIncident(message = "Cannot update FC (id = ${updatedFrameworkContract.id})")
                .asFailure()

        return updatedFrameworkContract
            .let { SetStateForContractsResponse.fromDomain(it) }
            .let { SetStateForContractsResponse(listOf(it)) }
            .asSuccess()
    }

    private fun setStateForPACLinkedToLot(params: SetStateForContractsParams): Result<SetStateForContractsResponse, Fail> {
        if (params.tender == null)
            return SetStateForContractsErrors.TenderMissing().asFailure()

        val receivedLots = params.tender.lots.toSetBy { it.id }
        val stateForSetting = rulesService.getStateForSetting(
            country = params.country,
            pmd = params.pmd.base,
            operationType = params.operationType.base,
            stage = params.ocid.stage
        ).onFailure { return it }

        val targetPacs = pacRepository
            .findBy(params.cpid, params.ocid)
            .onFailure { return it }
            .mapResult { transform.tryDeserialization(it.jsonData, PacEntity::class.java) }
            .onFailure { return it }
            .map { it.toDomain() }
            .filter { it.hasRelationWithLots(receivedLots) }

        if (targetPacs.size < receivedLots.size) {
            val unknownLots = receivedLots - targetPacs.flatMap { it.relatedLots }
                .map { it.underlying }
            return SetStateForContractsErrors.PacRelatedToLotNotFound(unknownLots.first()).asFailure()
        }

        val updatedPacs = targetPacs
            .map {
                it.copy(
                    status = PacStatus.orNull(stateForSetting.status)!!,
                    statusDetails = PacStatusDetails.orNull(stateForSetting.statusDetails)
                )
            }

        updatedPacs
            .mapResult { pac -> PacRecord.of(params.cpid, params.ocid, pac, transform = transform) }
            .onFailure { return it }
            .forEach { updatedPac ->
                val wasApplied = pacRepository.update(updatedPac).onFailure { return it }
                if (!wasApplied)
                    return Incident.Database.ConsistencyIncident(message = "Cannot update PAC (id = ${updatedPac.id})")
                        .asFailure()
            }

        return updatedPacs
            .map { SetStateForContractsResponse.fromDomain(it) }
            .let { SetStateForContractsResponse(it) }
            .asSuccess()
    }

    private fun setStateForPAC(params: SetStateForContractsParams): Result<SetStateForContractsResponse, Fail> {
        if (params.contracts.isEmpty())
            return SetStateForContractsErrors.ContractsMissing().asFailure()

        val pacId = PacId.orNull(params.contracts.first().id)!!

        val targetPac = pacRepository
            .findBy(params.cpid, params.ocid, pacId)
            .onFailure { return it }
            ?.let { transform.tryDeserialization(it.jsonData, PacEntity::class.java) }
            ?.onFailure { return it }
            ?.toDomain()
            ?: return SetStateForContractsErrors.PacNotFound(params.cpid, params.ocid, params.contracts[0].id)
                .asFailure()

        val stateForSetting = rulesService.getStateForSetting(
            country = params.country,
            pmd = params.pmd.base,
            operationType = params.operationType.base,
            stage = params.ocid.stage
        ).onFailure { return it }

        val updatedPac = targetPac.copy(
            status = PacStatus.creator(stateForSetting.status),
            statusDetails = PacStatusDetails.creator(stateForSetting.statusDetails)
        )

        val updatedEntity = PacRecord.of(params.cpid, params.ocid, updatedPac, transform)
            .onFailure { return it }

        val wasApplied = pacRepository.update(updatedEntity).onFailure { return it }
        if (!wasApplied)
            return Incident.Database.ConsistencyIncident(message = "Cannot update PAC (id = ${pacId})").asFailure()

        return updatedPac
            .let { SetStateForContractsResponse.fromDomain(it) }
            .let { SetStateForContractsResponse(listOf(it)) }
            .asSuccess()
    }

    private fun setStateForCAN(params: SetStateForContractsParams): Result<SetStateForContractsResponse, Fail> {
        if (params.contracts.isEmpty())
            return SetStateForContractsErrors.ContractsMissing().asFailure()

        val canIds = params.contracts.map { CANId.orNull(it.id)!! }

        val canEntities = canRepository
            .findBy(params.cpid, canIds)
            .onFailure { return it }

        val missingCans = canIds.toSet() - canEntities.toSetBy { it.id }
        if (missingCans.isNotEmpty())
            return SetStateForContractsErrors.CanNotFound(params.cpid, params.ocid, missingCans)
                .asFailure()


        val stateForSetting = rulesService.getStateForSetting(
            country = params.country,
            pmd = params.pmd.base,
            operationType = params.operationType.base,
            stage = params.ocid.stage
        ).onFailure { return it }

        val updatedCans = canEntities
            .associateBy { transform.tryDeserialization(it.jsonData, CAN::class.java).onFailure { return it } }
            .mapKeys { (can, _) ->
                can.copy(
                    status = CANStatus.creator(stateForSetting.status),
                    statusDetails = CANStatusDetails.creator(stateForSetting.statusDetails)
                )
            }

        val updatedEntities = updatedCans.map { (can, entity) ->
            CANEntity.of(
                cpid = entity.cpid,
                can = can,
                owner = entity.owner,
                awardContractId = entity.awardContractId,
                transform = transform
            ).onFailure { return it }
        }

        val wasApplied = canRepository.update(params.cpid, updatedEntities).onFailure { return it }
        if (!wasApplied)
            return Incident.Database.ConsistencyIncident(message = "Cannot update CANs (id = ${updatedEntities.joinToString()})").asFailure()

        return updatedCans
            .keys
            .map { SetStateForContractsResponse.fromDomain(it) }
            .let { SetStateForContractsResponse(it) }
            .asSuccess()
    }


    private fun convertToPacResult(createdPacs: List<Pac>): DoPacsResult {
        return DoPacsResult(
            contracts = createdPacs.map { pac ->
                DoPacsResult.Contract(
                    id = pac.id,
                    status = pac.status,
                    date = pac.date,
                    token = pac.token,
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

    private fun createPacsByAwards(
        params: DoPacsParams,
        activePacByAwardId: Map<AwardId, Pac>
    ): Result<List<Pac>, Fail.Incident> {
        return params.awards
            .filter { award -> activePacByAwardId[award.id] == null } // find awards for creating new PAC
            .map { award -> createPac(award, params) }
            .asSuccess()
    }

    private fun createSuppliers(award: DoPacsParams.Award) =
        award.suppliers.map { supplier ->
            Pac.Supplier(
                id = supplier.id,
                name = supplier.name
            )
        }

    private fun createPac(award: DoPacsParams.Award, params: DoPacsParams): Pac {
        val suppliers = createSuppliers(award)
        return Pac(
            id = generationService.pacId(),
            date = params.date,
            owner = params.owner,
            token = generationService.token(),
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
                observations = createObservations(params, suppliers, criterion)
            )
        }

    private fun createObservations(
        params: DoPacsParams,
        suppliers: List<Pac.Supplier>,
        criterion: DoPacsParams.Tender.Criteria
    ): List<Pac.AgreedMetric.Observation> {
        val responsesByRequirementIds = params.bids?.details.orEmpty()
            .asSequence()
            .filter { bid -> belongsToSuppliers(bid, suppliers) }
            .flatMap { it.requirementResponses }
            .associateBy { it.requirement.id }

        val requirements = criterion.requirementGroups
            .flatMap { it.requirements }
            .filter { requirement -> requirement.id in responsesByRequirementIds }

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
