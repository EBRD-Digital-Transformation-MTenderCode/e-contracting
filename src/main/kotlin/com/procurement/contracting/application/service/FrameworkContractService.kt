package com.procurement.contracting.application.service

import com.procurement.contracting.application.repository.fc.FrameworkContractRepository
import com.procurement.contracting.application.repository.fc.model.FrameworkContractEntity
import com.procurement.contracting.application.service.converter.convert
import com.procurement.contracting.application.service.errors.AddSupplierReferencesInFCErrors
import com.procurement.contracting.application.service.errors.CheckContractStateErrors
import com.procurement.contracting.application.service.errors.CheckExistenceSupplierReferencesInFCErrors
import com.procurement.contracting.application.service.model.AddSupplierReferencesInFCParams
import com.procurement.contracting.application.service.model.CheckContractStateParams
import com.procurement.contracting.application.service.model.CheckExistenceSupplierReferencesInFCParams
import com.procurement.contracting.application.service.model.CreateFrameworkContractParams
import com.procurement.contracting.application.service.model.CreateFrameworkContractResult
import com.procurement.contracting.application.service.rule.RulesService
import com.procurement.contracting.application.service.rule.model.ValidFCStatesRule
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.fc.FrameworkContract
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatus
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatusDetails
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.v2.model.response.AddSupplierReferencesInFCResponse
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.ValidationResult
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess
import com.procurement.contracting.lib.functional.asValidationError
import org.springframework.stereotype.Service

interface FrameworkContractService {
    fun create(params: CreateFrameworkContractParams): Result<CreateFrameworkContractResult, Fail>
    fun addSupplierReferences(params: AddSupplierReferencesInFCParams): Result<AddSupplierReferencesInFCResponse, Fail>
    fun checkContractState(params: CheckContractStateParams): ValidationResult<Fail>
    fun checkExistenceSupplierReferencesInFC(params: CheckExistenceSupplierReferencesInFCParams): ValidationResult<Fail>
}

@Service
class FrameworkContractServiceImpl(
    private val generationService: GenerationService,
    private val transform: Transform,
    private val fcRepository: FrameworkContractRepository,
    private val rulesService: RulesService
) : FrameworkContractService {

    override fun create(params: CreateFrameworkContractParams): Result<CreateFrameworkContractResult, Fail> {
        val fc = FrameworkContract(
            id = generationService.fcId(),
            token = Token.generate(),
            owner = params.owner,
            date = params.date,
            status = FrameworkContractStatus.PENDING,
            statusDetails = FrameworkContractStatusDetails.CONTRACT_PROJECT,
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
            .find { it.status == FrameworkContractStatus.PENDING }
            ?: return AddSupplierReferencesInFCErrors.ContractNotFound(params.cpid, params.ocid).asFailure()

        val newSuppliers = params.parties.map { it.toDomain() }

        val updatedFrameworkContract = frameworkContract.copy(suppliers = frameworkContract.suppliers + newSuppliers)
        val updatedFrameworkContractRecord = FrameworkContractEntity.of(params.cpid, params.ocid, updatedFrameworkContract, transform)
            .onFailure { return it }

        val wapAssplied = fcRepository.update(updatedFrameworkContractRecord).onFailure { return it }
        if (!wapAssplied)
            return Fail.Incident.Database.ConsistencyIncident("Cannot update FC (id = ${updatedFrameworkContractRecord.id}) " +
                "by cpid = ${params.cpid} and ocid = ${params.ocid}.").asFailure()

        return AddSupplierReferencesInFCResponse.fromDomain(updatedFrameworkContract).asSuccess()
    }

    override fun checkContractState(params: CheckContractStateParams): ValidationResult<Fail> {
        val frameworkContracts = fcRepository.findBy(params.cpid, params.ocid)
            .onFailure { return it.reason.asValidationError() }
            .map { transform
                .tryDeserialization(it.jsonData, FrameworkContract::class.java)
                .onFailure { return it.reason.asValidationError() }
            }

        if (frameworkContracts.isEmpty())
            return CheckContractStateErrors.ContractNotFound(params.cpid, params.ocid).asValidationError()

        // TEMP. At the moment of implementation is predicted only one FC record by cpid and ocid
        val frameworkContract = frameworkContracts.first()

        val currentState = ValidFCStatesRule.State(frameworkContract.status, frameworkContract.statusDetails);
        val validStates = rulesService.getValidFCStates(params.country, params.pmd, params.operationType)
            .onFailure { return it.reason.asValidationError() }

        if (currentState !in validStates)
            return CheckContractStateErrors.InvalidContractState(currentState, validStates).asValidationError()

        return ValidationResult.ok()
    }

    override fun checkExistenceSupplierReferencesInFC(params: CheckExistenceSupplierReferencesInFCParams): ValidationResult<Fail> {
        val frameworkContracts = fcRepository.findBy(params.cpid, params.ocid)
            .onFailure { return it.reason.asValidationError() }
            .map { transform
                .tryDeserialization(it.jsonData, FrameworkContract::class.java)
                .onFailure { return it.reason.asValidationError() }
            }

        if (frameworkContracts.isEmpty())
            return CheckExistenceSupplierReferencesInFCErrors.ContractNotFound(params.cpid, params.ocid).asValidationError()

        // TEMP. At the moment of implementation is predicted only one FC record by cpid and ocid
        val frameworkContract = frameworkContracts.first()

        if (frameworkContract.suppliers.isEmpty())
            return CheckExistenceSupplierReferencesInFCErrors.SuppliersNotFound().asValidationError()

        return ValidationResult.ok()
    }
}
