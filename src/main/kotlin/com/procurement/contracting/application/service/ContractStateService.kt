package com.procurement.contracting.application.service

import com.procurement.contracting.application.repository.ac.AwardContractRepository
import com.procurement.contracting.application.repository.fc.FrameworkContractRepository
import com.procurement.contracting.application.repository.model.ContractProcess
import com.procurement.contracting.application.service.errors.GetContractStateErrors
import com.procurement.contracting.application.service.model.GetContractStateParams
import com.procurement.contracting.domain.model.ac.id.AwardContractId
import com.procurement.contracting.domain.model.fc.FrameworkContract
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
    private val awardContractRepository: AwardContractRepository,
    private val frameworkContractRepository: FrameworkContractRepository,
    private val transform: Transform,
) : ContractStateService {

    override fun getState(params: GetContractStateParams): Result<GetContractStateResponse, Fail> {
        val stage = params.ocid.stage
        val targetContract = when (stage) {
            Stage.FE -> {
                val frameworkContracts = frameworkContractRepository
                    .findBy(params.cpid, params.ocid).onFailure { return it }
                    .map { transform.tryDeserialization(it.jsonData, FrameworkContract::class.java).onFailure { return it } }

                val targetFrameworkContract = frameworkContracts
                    .maxByOrNull { fc -> fc.date }
                    ?: return GetContractStateErrors.ContractNotFound(params.cpid, params.ocid).asFailure()

                GetContractStateResponse.Contract.fromDomain(targetFrameworkContract)
            }

            Stage.AC -> {
                val targetAwardedContract = awardContractRepository
                    .findBy(params.cpid, AwardContractId.orNull(params.ocid.underlying)!!).onFailure { return it }
                    ?.let { transform.tryDeserialization(it.jsonData, ContractProcess::class.java).onFailure { return it } }
                    ?: return GetContractStateErrors.ContractNotFound(params.cpid, params.ocid).asFailure()

                GetContractStateResponse.Contract.fromDomain(targetAwardedContract.contract)
            }

            Stage.EV,
            Stage.NP,
            Stage.TP, // temporally not allowed

            Stage.EI,
            Stage.FS,
            Stage.PC,
            Stage.PN,
            Stage.RQ -> return failure(GetContractStateErrors.UnexpectedStage(stage))
        }

        return GetContractStateResponse(contracts = listOf(targetContract)).asSuccess()
    }

}
