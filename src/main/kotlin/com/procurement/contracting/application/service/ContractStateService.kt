package com.procurement.contracting.application.service

import com.procurement.contracting.application.repository.fc.FrameworkContractRepository
import com.procurement.contracting.application.repository.pac.PacRepository
import com.procurement.contracting.application.service.errors.GetContractStateErrors
import com.procurement.contracting.application.service.model.GetContractStateParams
import com.procurement.contracting.domain.model.fc.FrameworkContract
import com.procurement.contracting.domain.model.fc.PacEntity
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.process.Stage
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.v2.model.response.GetContractStateResponse
import com.procurement.contracting.infrastructure.handler.v2.model.response.fromDomain
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.Result.Companion.failure
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess
import org.springframework.stereotype.Service

interface ContractStateService {
    fun getState(params: GetContractStateParams): Result<GetContractStateResponse, Fail>
}

@Service
class ContractStateServiceImpl(
    private val frameworkContractRepository: FrameworkContractRepository,
    private val pacRepository: PacRepository,
    private val transform: Transform,
) : ContractStateService {

    override fun getState(params: GetContractStateParams): Result<GetContractStateResponse, Fail> {
        val receivedContract = params.contracts.first()
        val stage = params.ocid.stage
        val targetContract = when (stage) {
            Stage.FE -> {
                val frameworkContractId = FrameworkContractId.orNull(receivedContract.id)
                    ?: return GetContractStateErrors.InvalidContractId(id = receivedContract.id, pattern = FrameworkContractId.pattern).asFailure()

                val targetFrameworkContract = frameworkContractRepository
                    .findBy(params.cpid, params.ocid, frameworkContractId).onFailure { return it }
                    ?.let { transform.tryDeserialization(it.jsonData, FrameworkContract::class.java).onFailure { return it } }
                    ?: return GetContractStateErrors.ContractNotFound(params.cpid, params.ocid, receivedContract.id).asFailure()

                GetContractStateResponse.Contract.fromDomain(targetFrameworkContract)
            }

            Stage.PC -> {
                val pacId = PacId.orNull(receivedContract.id)
                    ?: return GetContractStateErrors.InvalidContractId(id = receivedContract.id, pattern = PacId.pattern).asFailure()

                val targetPac = pacRepository
                    .findBy(params.cpid, params.ocid, pacId).onFailure { return it }
                    ?.let { transform.tryDeserialization(it.jsonData, PacEntity::class.java).onFailure { return it } }
                    ?: return GetContractStateErrors.ContractNotFound(params.cpid, params.ocid, receivedContract.id).asFailure()

                GetContractStateResponse.Contract.fromDomain(targetPac)
            }

            Stage.AC,
            Stage.EV,
            Stage.NP,
            Stage.TP,
            Stage.EI,
            Stage.FS,
            Stage.PN,
            Stage.PO,
            Stage.RQ -> return failure(GetContractStateErrors.UnexpectedStage(stage))
        }

        return GetContractStateResponse(contracts = listOf(targetContract)).asSuccess()
    }

}
