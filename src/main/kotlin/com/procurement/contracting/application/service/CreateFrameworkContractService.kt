package com.procurement.contracting.application.service

import com.procurement.contracting.application.repository.fc.FrameworkContractRepository
import com.procurement.contracting.application.repository.fc.model.FrameworkContractEntity
import com.procurement.contracting.application.service.converter.convert
import com.procurement.contracting.application.service.model.CreateFrameworkContractParams
import com.procurement.contracting.application.service.model.CreateFrameworkContractResult
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.fc.FrameworkContract
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatus
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatusDetails
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess
import org.springframework.stereotype.Service

interface CreateFrameworkContractService {
    fun create(params: CreateFrameworkContractParams): Result<CreateFrameworkContractResult, Fail>
}

@Service
class CreateFrameworkContractServiceImpl(
    private val generationService: GenerationService,
    private val transform: Transform,
    private val fcRepository: FrameworkContractRepository
) : CreateFrameworkContractService {

    override fun create(params: CreateFrameworkContractParams): Result<CreateFrameworkContractResult, Fail> {
        val fc = FrameworkContract(
            id = generationService.fcId(),
            token = Token.generate(),
            owner = params.owner,
            date = params.date,
            status = FrameworkContractStatus.PENDING,
            statusDetails = FrameworkContractStatusDetails.CONTRACT_PROJECT,
            isFrameworkOrDynamic = false
        )

        val entity = FrameworkContractEntity
            .of(cpid = params.cpid, ocid = params.ocid, fc = fc, transform = transform)
            .onFailure { return it }

        fcRepository.saveNew(entity).onFailure { return it }
        return fc.convert().asSuccess()
    }
}
