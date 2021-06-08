package com.procurement.contracting.application.service

import com.procurement.contracting.application.repository.fc.FrameworkContractRepository
import com.procurement.contracting.application.service.model.CancelFrameworkContractParams
import com.procurement.contracting.application.service.model.CancelFrameworkContractResult
import com.procurement.contracting.application.service.rule.RulesService
import com.procurement.contracting.domain.model.fc.FrameworkContract
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatus
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatusDetails
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.fail.error.ValidationError
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess
import org.springframework.stereotype.Service

interface CancelFrameworkContractService {
    fun cancel(params: CancelFrameworkContractParams): Result<CancelFrameworkContractResult, Fail>
}

@Service
class CancelFrameworkContractServiceImpl(
    private val transform: Transform,
    private val fcRepository: FrameworkContractRepository,
    private val rulesService: RulesService
) : CancelFrameworkContractService {

    override fun cancel(params: CancelFrameworkContractParams): Result<CancelFrameworkContractResult, Fail> {

        val entity = fcRepository.findBy(cpid = params.cpid, ocid = params.ocid)
            .onFailure { return it }
            .find { it.status == FrameworkContractStatus.PENDING }
            ?: return ValidationError.ActiveFrameworkContractNotFound(cpid = params.cpid, ocid = params.ocid)
                .asFailure()

        val stateRule = rulesService
            .getStateForSetting(
                country = params.country,
                pmd = params.pmd,
                operationType = params.operationType,
                stage = params.ocid.stage
            )
            .onFailure { return it }

        val contract = entity.jsonData
            .tryDeserialization<FrameworkContract>(transform)
            .mapFailure {
                Fail.Incident.Database.DatabaseInteractionIncident(it.exception)
            }
            .onFailure { return it }

        val updatedContract = contract.copy(
            status = FrameworkContractStatus.creator(stateRule.status),
            statusDetails = FrameworkContractStatusDetails.creator(stateRule.statusDetails),
        )

        val json = transform.trySerialization(updatedContract)
            .onFailure { return it }
        val updatedEntity = entity.copy(
            status = updatedContract.status,
            statusDetails = updatedContract.statusDetails,
            jsonData = json
        )
        fcRepository.update(updatedEntity)
            .onFailure { return it }

        return CancelFrameworkContractResult(
            id = updatedContract.id,
            status = updatedContract.status,
            statusDetails = updatedContract.statusDetails
        ).asSuccess()
    }
}
