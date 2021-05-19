package com.procurement.contracting.application.service

import com.procurement.contracting.application.repository.confirmation.ConfirmationRequestRepository
import com.procurement.contracting.application.repository.confirmation.ConfirmationResponseRepository
import com.procurement.contracting.application.service.errors.CheckExistenceOfConfirmationResponsesErrors
import com.procurement.contracting.application.service.model.CheckExistenceOfConfirmationResponsesParams
import com.procurement.contracting.application.service.rule.RulesService
import com.procurement.contracting.application.service.rule.model.MinReceivedConfResponsesRule
import com.procurement.contracting.application.service.rule.model.SourceOfConfirmationRequestRule
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequest
import com.procurement.contracting.domain.model.confirmation.response.ConfirmationResponse
import com.procurement.contracting.domain.util.extension.mapResult
import com.procurement.contracting.domain.util.extension.toSetBy
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.ValidationResult
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess
import com.procurement.contracting.lib.functional.asValidationError
import org.springframework.stereotype.Service

interface CheckExistenceOfConfirmationResponsesService {
    fun check(params: CheckExistenceOfConfirmationResponsesParams): ValidationResult<Fail>
}

@Service
class CheckExistenceOfConfirmationResponsesServiceImpl(
    private val transform: Transform,
    private val rulesService: RulesService,
    private val confirmationRequestRepository: ConfirmationRequestRepository,
    private val confirmationResponseRepository: ConfirmationResponseRepository
) : CheckExistenceOfConfirmationResponsesService {

    override fun check(params: CheckExistenceOfConfirmationResponsesParams): ValidationResult<Fail> {
        val sourceOfConfirmationRequest = rulesService
            .getSourceOfConfirmationRequest(params.country, params.pmd, params.operationType)
            .onFailure { return it.reason.asValidationError() }

        val contract = params.contracts.first()

        val confirmationRequest = getConfirmationRequest(params, contract, sourceOfConfirmationRequest)
            .onFailure { return it.reason.asValidationError() }

        val confirmationResponses = confirmationRequest.getLinkedConfirmationResponses(params, contract)
            .onFailure { return it.reason.asValidationError() }

        val minReceivedConfResponses = rulesService
            .getMinReceivedConfResponses(params.country, params.pmd, params.operationType)
            .onFailure { return it.reason.asValidationError() }

        checkConfirmationResponsesQuantity(minReceivedConfResponses, confirmationResponses, confirmationRequest)
            .doOnError { return it.asValidationError() }

        return ValidationResult.ok()
    }

    fun getConfirmationRequest(
        params: CheckExistenceOfConfirmationResponsesParams,
        contractReceived: CheckExistenceOfConfirmationResponsesParams.Contract,
        sourceOfConfirmationRequest: SourceOfConfirmationRequestRule,
    ): Result<ConfirmationRequest, Fail> =
        confirmationRequestRepository
            .findBy(params.cpid, params.ocid, contractReceived.id)
            .onFailure { return it }
            .mapResult { transform.tryDeserialization(it.jsonData, ConfirmationRequest::class.java) }
            .onFailure { return it }
            .firstOrNull { it.source == sourceOfConfirmationRequest.role }
            ?.asSuccess()
            ?: CheckExistenceOfConfirmationResponsesErrors.ConfirmationRequestNotFound(
                params.cpid, params.ocid, contractReceived.id, sourceOfConfirmationRequest.role
            ).asFailure()

    private fun ConfirmationRequest.getLinkedConfirmationResponses(
        params: CheckExistenceOfConfirmationResponsesParams,
        contractReceived: CheckExistenceOfConfirmationResponsesParams.Contract
    ): Result<List<ConfirmationResponse>, Fail> {
        val requestsIds = requests.toSetBy { it.id }
        val confirmationResponses = confirmationResponseRepository
            .findBy(params.cpid, params.ocid, contractReceived.id)
            .onFailure { return it }
            .mapResult { transform.tryDeserialization(it.jsonData, ConfirmationResponse::class.java) }
            .onFailure { return it }

        return confirmationResponses
            .filter { requestsIds.contains(it.requestId.underlying.toString()) }
            .asSuccess()
    }

    private fun checkConfirmationResponsesQuantity(
        minReceivedConfResponses: MinReceivedConfResponsesRule,
        confirmationResponses: List<ConfirmationResponse>,
        confirmationRequest: ConfirmationRequest
    ): ValidationResult<CheckExistenceOfConfirmationResponsesErrors> {
        when (val quantity = minReceivedConfResponses.quantity) {
            is MinReceivedConfResponsesRule.Quantity.Number -> {
                if (confirmationResponses.size < quantity.underlying)
                    CheckExistenceOfConfirmationResponsesErrors.IncorrectNumberOfConfirmatonResponses(
                        minimumQuantity = quantity.underlying,
                        actualQuantity = confirmationResponses.size
                    ).asValidationError()
            }
            is MinReceivedConfResponsesRule.Quantity.All -> {
                val numberOfRequests = confirmationRequest.requests.size
                if (confirmationResponses.size != numberOfRequests)
                    CheckExistenceOfConfirmationResponsesErrors.IncorrectNumberOfConfirmatonResponses(
                        minimumQuantity = numberOfRequests,
                        actualQuantity = confirmationResponses.size
                    ).asValidationError()
            }
        }
        return ValidationResult.ok()
    }
}
