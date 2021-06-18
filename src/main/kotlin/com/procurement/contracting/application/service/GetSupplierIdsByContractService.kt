package com.procurement.contracting.application.service

import com.procurement.contracting.application.repository.pac.PacRepository
import com.procurement.contracting.application.service.errors.GetSupplierIdsByContractErrors
import com.procurement.contracting.application.service.model.GetSupplierIdsByContractParams
import com.procurement.contracting.domain.model.fc.PacEntity
import com.procurement.contracting.domain.model.process.Stage
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.v2.model.response.GetSupplierIdsByContractResponse
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.ValidationResult
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess
import com.procurement.contracting.lib.functional.asValidationError
import org.springframework.stereotype.Service

interface GetSupplierIdsByContractService {
    fun get(params: GetSupplierIdsByContractParams): Result<GetSupplierIdsByContractResponse, Fail>
}

@Service
class GetSupplierIdsByContractServiceImpl(
    private val transform: Transform,
    private val pacRepository: PacRepository
) : GetSupplierIdsByContractService {

    override fun get(params: GetSupplierIdsByContractParams): Result<GetSupplierIdsByContractResponse, Fail> {
        checkStage(params.ocid.stage)
            .doOnError { return it.asFailure() }

        val pac = pacRepository.findBy(params.cpid, params.ocid, params.contracts.first().id)
            .onFailure { return it }
            ?.let { transform.tryDeserialization(it.jsonData, PacEntity::class.java) }
            ?.onFailure { return it }
            ?.toDomain()
            ?: return GetSupplierIdsByContractErrors.ContractNotFound(
                params.cpid, params.ocid, params.contracts.first().id
            ).asFailure()

        return GetSupplierIdsByContractResponse(
            contracts = listOf(
                GetSupplierIdsByContractResponse.Contract(
                    id = params.contracts.first().id.underlying,
                    suppliers = pac.suppliers.map { supplier ->
                        GetSupplierIdsByContractResponse.Contract.Supplier(supplier.id)
                    }
                )
            )
        ).asSuccess()
    }

    private fun checkStage(stage: Stage): ValidationResult<Fail> =
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
            Stage.TP -> GetSupplierIdsByContractErrors.InvalidStage(stage).asValidationError()
        }
}