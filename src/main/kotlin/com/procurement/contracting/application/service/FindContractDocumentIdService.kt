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
import com.procurement.contracting.domain.model.process.ProcessInitiator
import com.procurement.contracting.domain.model.process.Stage
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.v2.model.response.FindContractDocumentIdResponse
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess
import org.springframework.stereotype.Service

interface FindContractDocumentIdService {
    fun find(params: FindContractDocumentIdParams): Result<FindContractDocumentIdResponse?, Fail>
}

@Service
class FindContractDocumentIdServiceImpl(
    private val transform: Transform,
    private val frameworkContractRepository: FrameworkContractRepository,
    private val canRepository: CANRepository,
    private val pacRepository: PacRepository,
) : FindContractDocumentIdService {

    override fun find(params: FindContractDocumentIdParams): Result<FindContractDocumentIdResponse?, Fail> {
        val stage = params.ocid.stage
        val documentId = when (stage) {
            Stage.FE -> getFEDocumentOrNull(params)
                .onFailure { return it }
            Stage.EV,
            Stage.NP,
            Stage.TP -> getCANDocumentOrNull(params)
                .onFailure { return it }

            Stage.PC -> getPACDocumentOrNull(params)
                .onFailure { return it }
            Stage.AC,
            Stage.EI,
            Stage.FS,
            Stage.PN,
            Stage.RQ -> return CheckAccessToContractErrors.UnexpectedStage(stage).asFailure()
        }

        return documentId?.let {
            FindContractDocumentIdResponse(
                contracts = listOf(
                    FindContractDocumentIdResponse.Contract(
                        id = params.contracts.first().id,
                        documents = listOf(
                            FindContractDocumentIdResponse.Contract.Document(documentId)
                        )
                    )
                )
            )
        }.asSuccess()
    }

    private fun getFEDocumentOrNull(
        params: FindContractDocumentIdParams
    ): Result<String?, Fail> {
        val frameworkContractId = FrameworkContractId.orNull(params.contracts.first().id)
            ?: return CheckAccessToContractErrors
                .InvalidContractId(id = params.contracts.first().id, pattern = FrameworkContractId.pattern).asFailure()

        val frameworkContract = frameworkContractRepository
            .findBy(params.cpid, params.ocid, frameworkContractId)
            .onFailure { return it }
            ?.let {
                transform.tryDeserialization(it.jsonData, FrameworkContract::class.java)
                    .onFailure { return it }
            }
            ?: return CheckAccessToContractErrors.ContractNotFound(params.cpid, params.ocid, frameworkContractId.underlying)
                .asFailure()

        return when (params.processInitiator) {
            ProcessInitiator.NEXT_STEP_AFTER_BUYERS_CONFIRMATION -> return frameworkContract.documents
                .firstOrNull { it.documentType == DocumentTypeContract.X_FRAMEWORK_PROJECT }?.id
                .asSuccess()
            ProcessInitiator.ISSUING_FRAMEWORK_CONTRACT -> null.asSuccess()
        }
    }

    private fun getCANDocumentOrNull(
        params: FindContractDocumentIdParams
    ): Result<String?, Fail> {
        val canId = CANId.orNull(params.contracts.first().id)
            ?: return CheckAccessToContractErrors
                .InvalidContractId(id = params.contracts.first().id, pattern = CANId.pattern).asFailure()

        val can = canRepository
            .findBy(params.cpid, canId)
            .onFailure { return it }
            ?.let {
                transform.tryDeserialization(it.jsonData, CAN::class.java)
                    .onFailure { return it }
            }
            ?: return CheckAccessToContractErrors.ContractNotFound(params.cpid, params.ocid, canId.underlying.toString())
                .asFailure()

        return when (params.processInitiator) {
            ProcessInitiator.NEXT_STEP_AFTER_BUYERS_CONFIRMATION -> return can.documents
                ?.firstOrNull { it.documentType == DocumentTypeContract.X_FRAMEWORK_CONTRACT }?.id
                .asSuccess()
            ProcessInitiator.ISSUING_FRAMEWORK_CONTRACT -> null.asSuccess()
        }
    }

    private fun getPACDocumentOrNull(
        params: FindContractDocumentIdParams
    ): Result<String?, Fail> {
        val pacId = PacId.orNull(params.contracts.first().id)
            ?: return CheckAccessToContractErrors
                .InvalidContractId(id = params.contracts.first().id, pattern = PacId.pattern).asFailure()

        pacRepository
            .findBy(params.cpid, params.ocid, pacId)
            .onFailure { return it.reason.asFailure() }
            ?.let {
                transform.tryDeserialization(it.jsonData, PacEntity::class.java)
                    .onFailure { return it }
            }
            ?: return CheckAccessToContractErrors.ContractNotFound(params.cpid, params.ocid, pacId.underlying)
                .asFailure()

        return null.asSuccess()
    }
}
