package com.procurement.contracting.application.service

import com.procurement.contracting.application.repository.can.CANRepository
import com.procurement.contracting.application.repository.fc.FrameworkContractRepository
import com.procurement.contracting.application.repository.pac.PacRepository
import com.procurement.contracting.application.service.errors.CheckAccessToContractErrors
import com.procurement.contracting.application.service.model.FindContractDocumentIdParams
import com.procurement.contracting.domain.model.can.CAN
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.document.type.DocumentTypeContract
import com.procurement.contracting.domain.model.fc.FrameworkContract
import com.procurement.contracting.domain.model.fc.PacEntity
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.process.Stage
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.v2.model.response.FindContractDocumentIdResponse
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess
import org.springframework.stereotype.Service

interface FindContractDocumentIdService {
    fun find(params: FindContractDocumentIdParams): Result<FindContractDocumentIdResponse, Fail>
}

@Service
class CheckExistenceOfConfirmationResponsesServiceImpl(
    private val transform: Transform,
    private val frameworkContractRepository: FrameworkContractRepository,
    private val canRepository: CANRepository,
    private val pacRepository: PacRepository,
) : FindContractDocumentIdService {

    override fun find(params: FindContractDocumentIdParams): Result<FindContractDocumentIdResponse, Fail> {
        val receivedContract = params.contracts.first()
        val stage = params.ocid.stage
        val documentId = when (stage) {
            Stage.FE -> getFEDocumentOrNull(receivedContract, params)
                .onFailure { return it }
            Stage.EV,
            Stage.NP,
            Stage.TP -> getCANDocumentOrNull(receivedContract, params)
                .onFailure { return it }

            Stage.PC -> getPACDocumentOrNull(receivedContract, params)
                .onFailure { return it }
            Stage.AC,
            Stage.EI,
            Stage.FS,
            Stage.PN,
            Stage.RQ -> return CheckAccessToContractErrors.UnexpectedStage(stage).asFailure()
        }

        return documentId
    }

    private fun getFEDocumentOrNull(
        receivedContract: FindContractDocumentIdParams.Contract,
        params: FindContractDocumentIdParams
    ): Result<String?, Fail> {
        val frameworkContractId = FrameworkContractId.orNull(receivedContract.id)
            ?: return CheckAccessToContractErrors
                .InvalidContractId(id = receivedContract.id, pattern = FrameworkContractId.pattern).asFailure()

        val frameworkContract = frameworkContractRepository
            .findBy(params.cpid, params.ocid, frameworkContractId)
            .onFailure { return it }
            ?.let {
                transform.tryDeserialization(it.jsonData, FrameworkContract::class.java)
                    .onFailure { return it }
            }
            ?: return CheckAccessToContractErrors.ContractNotFound(params.cpid, params.ocid, receivedContract.id)
                .asFailure()

        return frameworkContract.documents
            .firstOrNull { it.documentType == DocumentTypeContract.X_FRAMEWORK_CONTRACT }?.id
            .asSuccess()
    }

    private fun getCANDocumentOrNull(
        receivedContract: FindContractDocumentIdParams.Contract,
        params: FindContractDocumentIdParams
    ): Result<String?, Fail> {
        val canId = CANId.orNull(receivedContract.id)
            ?: return CheckAccessToContractErrors
                .InvalidContractId(id = receivedContract.id, pattern = CANId.pattern).asFailure()

        val can = canRepository
            .findBy(params.cpid, canId)
            .onFailure { return it }
            ?.let {
                transform.tryDeserialization(it.jsonData, CAN::class.java)
                    .onFailure { return it }
            }
            ?: return CheckAccessToContractErrors.ContractNotFound(params.cpid, params.ocid, receivedContract.id)
                .asFailure()

        return can.documents
            ?.firstOrNull { it.documentType == DocumentTypeContract.X_FRAMEWORK_CONTRACT }?.id
            .asSuccess()
    }

    private fun getPACDocumentOrNull(
        receivedContract: FindContractDocumentIdParams.Contract,
        params: FindContractDocumentIdParams
    ): Result<String?, Fail> {
        val pacId = PacId.orNull(receivedContract.id)
            ?: return CheckAccessToContractErrors
                .InvalidContractId(id = receivedContract.id, pattern = PacId.pattern).asFailure()

        val pac = pacRepository
            .findBy(params.cpid, params.ocid, pacId)
            .onFailure { return it.reason.asFailure() }
            ?.let {
                transform.tryDeserialization(it.jsonData, PacEntity::class.java)
                    .onFailure { return it }
            }
            ?: return CheckAccessToContractErrors.ContractNotFound(params.cpid, params.ocid, receivedContract.id)
                .asFailure()

        return null.asSuccess()
    }
}
