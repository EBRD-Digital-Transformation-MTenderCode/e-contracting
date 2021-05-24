package com.procurement.contracting.application.service

import com.procurement.contracting.application.repository.fc.FrameworkContractRepository
import com.procurement.contracting.application.repository.fc.model.FrameworkContractEntity
import com.procurement.contracting.application.repository.pac.PacRepository
import com.procurement.contracting.application.service.converter.convert
import com.procurement.contracting.application.service.errors.AddGeneratedDocumentToContractErrors
import com.procurement.contracting.application.service.errors.AddSupplierReferencesInFCErrors
import com.procurement.contracting.application.service.errors.CheckContractStateErrors
import com.procurement.contracting.application.service.errors.CheckExistenceSupplierReferencesInFCErrors
import com.procurement.contracting.application.service.model.AddGeneratedDocumentToContractParams
import com.procurement.contracting.application.service.model.AddSupplierReferencesInFCParams
import com.procurement.contracting.application.service.model.CheckContractStateParams
import com.procurement.contracting.application.service.model.CheckExistenceSupplierReferencesInFCParams
import com.procurement.contracting.application.service.model.CreateFrameworkContractParams
import com.procurement.contracting.application.service.model.CreateFrameworkContractResult
import com.procurement.contracting.application.service.rule.RulesService
import com.procurement.contracting.application.service.rule.model.ValidContractStatesRule
import com.procurement.contracting.domain.model.OperationType
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.document.type.DocumentTypeContract.X_FRAMEWORK_PROJECT
import com.procurement.contracting.domain.model.fc.FrameworkContract
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatus.PENDING
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatusDetails.CONTRACT_PROJECT
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatusDetails.ISSUED
import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.process.Stage
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.v2.model.response.AddGeneratedDocumentToContractResponse
import com.procurement.contracting.infrastructure.handler.v2.model.response.AddSupplierReferencesInFCResponse
import com.procurement.contracting.infrastructure.handler.v2.model.response.fromDomain
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.ValidationResult
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess
import com.procurement.contracting.lib.functional.asValidationError
import org.springframework.stereotype.Service

interface FrameworkContractService {
    fun create(params: CreateFrameworkContractParams): Result<CreateFrameworkContractResult, Fail>
    fun addSupplierReferences(params: AddSupplierReferencesInFCParams): Result<AddSupplierReferencesInFCResponse, Fail>
    fun addGeneratedDocumentToContract(params: AddGeneratedDocumentToContractParams): Result<AddGeneratedDocumentToContractResponse, Fail>
    fun checkContractState(params: CheckContractStateParams): ValidationResult<Fail>
    fun checkExistenceSupplierReferencesInFC(params: CheckExistenceSupplierReferencesInFCParams): ValidationResult<Fail>
}

@Service
class FrameworkContractServiceImpl(
    private val generationService: GenerationService,
    private val transform: Transform,
    private val fcRepository: FrameworkContractRepository,
    private val pacRepository: PacRepository,
    private val rulesService: RulesService
) : FrameworkContractService {

    override fun create(params: CreateFrameworkContractParams): Result<CreateFrameworkContractResult, Fail> {
        val fc = FrameworkContract(
            id = generationService.fcId(),
            token = Token.generate(),
            owner = params.owner,
            date = params.date,
            status = PENDING,
            statusDetails = CONTRACT_PROJECT,
            isFrameworkOrDynamic = false,
            suppliers = emptyList()
        )

        val entity = FrameworkContractEntity
            .of(cpid = params.cpid, ocid = params.ocid, fc = fc, transform = transform)
            .onFailure { return it }

        fcRepository.saveNew(entity).onFailure { return it }
        return fc.convert().asSuccess()
    }

    override fun addSupplierReferences(params: AddSupplierReferencesInFCParams): Result<AddSupplierReferencesInFCResponse, Fail> {
        val frameworkContract = fcRepository.findBy(params.cpid, params.ocid)
            .onFailure { return it }
            .map { transform.tryDeserialization(it.jsonData, FrameworkContract::class.java).onFailure { return it } }
            .find { it.status == PENDING }
            ?: return AddSupplierReferencesInFCErrors.ContractNotFound(params.cpid, params.ocid).asFailure()

        val newSuppliers = params.parties.map { it.toDomain() }

        val updatedFrameworkContract = frameworkContract.copy(suppliers = frameworkContract.suppliers + newSuppliers)
        val updatedFrameworkContractRecord = FrameworkContractEntity.of(params.cpid, params.ocid, updatedFrameworkContract, transform)
            .onFailure { return it }

        val wasApplied = fcRepository.update(updatedFrameworkContractRecord).onFailure { return it }
        if (!wasApplied)
            return Fail.Incident.Database.ConsistencyIncident("Cannot update FC (id = ${updatedFrameworkContractRecord.id}) " +
                "by cpid = ${params.cpid} and ocid = ${params.ocid}.").asFailure()

        return AddSupplierReferencesInFCResponse.fromDomain(updatedFrameworkContract).asSuccess()
    }

    override fun addGeneratedDocumentToContract(params: AddGeneratedDocumentToContractParams): Result<AddGeneratedDocumentToContractResponse, Fail> {
        val receivedContractId = params.contracts.first().id
        val updatedFrameworkContract = when (params.processInitiator) {
            OperationType.ISSUING_FRAMEWORK_CONTRACT -> {
                val frameworkContract = fcRepository.findBy(params.cpid, params.ocid, receivedContractId)
                    .onFailure { return it }
                    ?.let { transform
                            .tryDeserialization(it.jsonData, FrameworkContract::class.java)
                            .onFailure { return it }
                    }
                    ?: return AddGeneratedDocumentToContractErrors.ContractNotFound(params.cpid, params.ocid, receivedContractId).asFailure()

                val receivedDocuments = params.contracts
                    .flatMap { it.documents }
                    .map { FrameworkContract.Document(id = it.id, documentType = X_FRAMEWORK_PROJECT) }

                val updatedDocuments = frameworkContract.documents + receivedDocuments
                frameworkContract.copy(documents = updatedDocuments, statusDetails = ISSUED)
            }

            OperationType.COMPLETE_SOURCING,
            OperationType.CREATE_CONFIRMATION_RESPONSE_BY_BUYER,
            OperationType.CREATE_CONFIRMATION_RESPONSE_BY_INVITED_CANDIDATE,
            OperationType.CREATE_CONFIRMATION_RESPONSE_BY_SUPPLIER,
            OperationType.NEXT_STEP_AFTER_BUYERS_CONFIRMATION,
            OperationType.NEXT_STEP_AFTER_INVITED_CANDIDATES_CONFIRMATION,
            OperationType.NEXT_STEP_AFTER_SUPPLIERS_CONFIRMATION,
            OperationType.WITHDRAW_QUALIFICATION_PROTOCOL -> throw NotImplementedError()
        }

        val updatedFrameworkContractRecord = FrameworkContractEntity.of(params.cpid, params.ocid, updatedFrameworkContract, transform)
            .onFailure { return it }

        val wasApplied = fcRepository.update(updatedFrameworkContractRecord).onFailure { return it }
        if (!wasApplied)
            return Fail.Incident.Database.ConsistencyIncident("Cannot update FC (id = ${updatedFrameworkContractRecord.id}) " +
                "by cpid = ${params.cpid} and ocid = ${params.ocid}.").asFailure()

        return AddGeneratedDocumentToContractResponse.Contract
            .fromDomain(updatedFrameworkContract)
            .let { AddGeneratedDocumentToContractResponse(listOf(it)) }
            .asSuccess()
    }

    override fun checkContractState(params: CheckContractStateParams): ValidationResult<Fail> {
        val validStates = rulesService.getValidContractStates(params.country, params.pmd, params.operationType)
            .onFailure { return it.reason.asValidationError() }

        return when (val stage = params.ocid.stage) {
            Stage.FE -> checkContractStateForFE(params, validStates)
            Stage.PC -> checkContractStateForPAC(params, validStates)
            Stage.TP,
            Stage.RQ,
            Stage.PN,
            Stage.NP,
            Stage.FS,
            Stage.EV,
            Stage.EI,
            Stage.AC -> CheckContractStateErrors.InvalidStage(stage).asValidationError()
        }
    }

    private fun checkContractStateForPAC(
        params: CheckContractStateParams,
        validStates: ValidContractStatesRule
    ): ValidationResult<Fail> {
        val pacId = PacId.orNull(params.contracts.first().id)
            ?: return CheckContractStateErrors.InvalidContractId(params.contracts.first().id, PacId.pattern).asValidationError()

        val pac = pacRepository
            .findBy(params.cpid, params.ocid, pacId)
            .onFailure { return it.reason.asValidationError() }
            ?: return CheckContractStateErrors.ContractNotFound(params.cpid, params.ocid, pacId.underlying)
                .asValidationError()

        val currentState = ValidContractStatesRule.State(pac.status.key, pac.statusDetails?.key)

        validStates.firstOrNull { currentState.matches(expected = it) }
            ?: return CheckContractStateErrors.InvalidContractState(
                pac.status.key, pac.statusDetails?.key, validStates
            ).asValidationError()

        return ValidationResult.ok()
    }

    private fun checkContractStateForFE(
        params: CheckContractStateParams,
        validStates: ValidContractStatesRule
    ): ValidationResult<Fail> {
        val frameworkContractId = FrameworkContractId.orNull(params.contracts.first().id)
            ?: return CheckContractStateErrors.InvalidContractId(params.contracts.first().id, FrameworkContractId.pattern)
                .asValidationError()

        val frameworkContract = fcRepository.findBy(params.cpid, params.ocid, frameworkContractId)
            .onFailure { return it.reason.asValidationError() }
            ?: return CheckContractStateErrors.ContractNotFound(
                params.cpid, params.ocid, frameworkContractId.underlying
            ).asValidationError()

        val currentState = ValidContractStatesRule.State(
            frameworkContract.status.key,
            frameworkContract.statusDetails.key
        )
        validStates.firstOrNull { currentState.matches(expected = it) }
            ?: return CheckContractStateErrors.InvalidContractState(
                currentState.status, currentState.statusDetails, validStates
            ).asValidationError()

        return ValidationResult.ok()
    }

    override fun checkExistenceSupplierReferencesInFC(params: CheckExistenceSupplierReferencesInFCParams): ValidationResult<Fail> {
        val receivedContractId = params.contracts.first().id
        val frameworkContract = fcRepository.findBy(params.cpid, params.ocid, receivedContractId)
            .onFailure { return it.reason.asValidationError() }
            ?.let { transform
                .tryDeserialization(it.jsonData, FrameworkContract::class.java)
                .onFailure { return it.reason.asValidationError() }
            }
            ?: return CheckExistenceSupplierReferencesInFCErrors.ContractNotFound(params.cpid, params.ocid, receivedContractId).asValidationError()

        if (frameworkContract.suppliers.isEmpty())
            return CheckExistenceSupplierReferencesInFCErrors.SuppliersNotFound().asValidationError()

        return ValidationResult.ok()
    }
}
