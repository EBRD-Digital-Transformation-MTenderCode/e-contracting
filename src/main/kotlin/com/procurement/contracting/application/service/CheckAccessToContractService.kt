package com.procurement.contracting.application.service

import com.procurement.contracting.application.repository.can.CANRepository
import com.procurement.contracting.application.repository.fc.FrameworkContractRepository
import com.procurement.contracting.application.repository.pac.PacRepository
import com.procurement.contracting.application.service.errors.CheckAccessToContractErrors
import com.procurement.contracting.application.service.errors.GetContractStateErrors
import com.procurement.contracting.application.service.model.CheckAccessToContractParams
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.process.Stage
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.ValidationResult
import com.procurement.contracting.lib.functional.asValidationError
import org.springframework.stereotype.Service

interface CheckAccessToContractService {
    fun check(params: CheckAccessToContractParams): ValidationResult<Fail>
}

@Service
class CheckAccessToContractServiceImpl(
    private val frameworkContractRepository: FrameworkContractRepository,
    private val canRepository: CANRepository,
    private val pacRepository: PacRepository,
) : CheckAccessToContractService {

    override fun check(params: CheckAccessToContractParams): ValidationResult<Fail> {
        val receivedContract = params.contracts.first()
        val stage = params.ocid.stage
        when (stage) {
            Stage.FE -> checkFrameworkContract(receivedContract, params)
                .doOnError { return it.asValidationError() }
            Stage.EV,
            Stage.NP,
            Stage.TP -> checkCAN(receivedContract, params)
                .doOnError { return it.asValidationError() }

            Stage.PC -> checkPAC(receivedContract, params)
                .doOnError { return it.asValidationError() }
            Stage.AC,
            Stage.EI,
            Stage.FS,
            Stage.PN,
            Stage.RQ -> return CheckAccessToContractErrors.UnexpectedStage(stage).asValidationError()
        }

        return ValidationResult.ok()
    }

    private fun checkFrameworkContract(
        receivedContract: CheckAccessToContractParams.Contract,
        params: CheckAccessToContractParams
    ): ValidationResult<Fail> {
        val frameworkContractId = FrameworkContractId.orNull(receivedContract.id)
            ?: return CheckAccessToContractErrors
                .InvalidContractId(id = receivedContract.id, pattern = FrameworkContractId.pattern).asValidationError()

        val frameworkContract = frameworkContractRepository
            .findBy(params.cpid, params.ocid, frameworkContractId)
            .onFailure { return it.reason.asValidationError() }
            ?: return CheckAccessToContractErrors.ContractNotFound(params.cpid, params.ocid, receivedContract.id)
                .asValidationError()

        if (params.token != frameworkContract.token)
            return CheckAccessToContractErrors.TokenMismatch()
                .asValidationError()

        if (params.owner != frameworkContract.owner)
            return CheckAccessToContractErrors.OwnerMismatch()
                .asValidationError()

        return ValidationResult.ok()
    }

    private fun checkCAN(
        receivedContract: CheckAccessToContractParams.Contract,
        params: CheckAccessToContractParams
    ): ValidationResult<Fail> {
        val canId = CANId.orNull(receivedContract.id)
            ?: return CheckAccessToContractErrors
                .InvalidContractId(id = receivedContract.id, pattern = CANId.pattern).asValidationError()

        val can = canRepository
            .findBy(params.cpid, canId)
            .onFailure { return it.reason.asValidationError() }
            ?: return GetContractStateErrors.ContractNotFound(params.cpid, params.ocid, receivedContract.id)
                .asValidationError()

        if (params.token != can.token)
            return CheckAccessToContractErrors.TokenMismatch()
                .asValidationError()

        if (params.owner != can.owner)
            return CheckAccessToContractErrors.OwnerMismatch()
                .asValidationError()

        return ValidationResult.ok()
    }

    private fun checkPAC(
        receivedContract: CheckAccessToContractParams.Contract,
        params: CheckAccessToContractParams
    ): ValidationResult<Fail> {
        val pacId = PacId.orNull(receivedContract.id)
            ?: return CheckAccessToContractErrors
                .InvalidContractId(id = receivedContract.id, pattern = PacId.pattern).asValidationError()

        val pac = pacRepository
            .findBy(params.cpid, params.ocid, pacId)
            .onFailure { return it.reason.asValidationError() }
            ?: return CheckAccessToContractErrors.ContractNotFound(params.cpid, params.ocid, receivedContract.id)
                .asValidationError()

        if (params.token != pac.token)
            return CheckAccessToContractErrors.TokenMismatch()
                .asValidationError()

        if (params.owner != pac.owner)
            return CheckAccessToContractErrors.OwnerMismatch()
                .asValidationError()

        return ValidationResult.ok()
    }
}
