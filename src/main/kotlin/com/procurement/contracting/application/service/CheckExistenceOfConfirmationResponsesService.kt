package com.procurement.contracting.application.service

import com.procurement.contracting.application.repository.confirmation.ConfirmationRequestRepository
import com.procurement.contracting.application.repository.confirmation.ConfirmationResponseRepository
import com.procurement.contracting.application.service.errors.CheckExistenceOfConfirmationResponsesErrors
import com.procurement.contracting.application.service.model.CheckExistenceOfConfirmationResponsesParams
import com.procurement.contracting.application.service.rule.RulesService
import com.procurement.contracting.application.service.rule.model.MinReceivedConfResponsesRule
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequest
import com.procurement.contracting.domain.model.confirmation.response.ConfirmationResponse
import com.procurement.contracting.domain.util.extension.mapResult
import com.procurement.contracting.domain.util.extension.toSetBy
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.ValidationResult
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

        val contractReceived = params.contracts.first()
        val confirmationRequests = confirmationRequestRepository
            .findBy(params.cpid, params.ocid, contractReceived.id)
            .onFailure { return it.reason.asValidationError() }
            .mapResult { transform.tryDeserialization(it.jsonData, ConfirmationRequest::class.java) }
            .onFailure { return it.reason.asValidationError() }

        val targetConfirmationRequest = confirmationRequests
            .firstOrNull { it.source == sourceOfConfirmationRequest.role }
            ?: return CheckExistenceOfConfirmationResponsesErrors.ConfirmationRequestNotFound(
                params.cpid, params.ocid, contractReceived.id, sourceOfConfirmationRequest.role
            ).asValidationError()

        val requestsIds = targetConfirmationRequest.requests.toSetBy { it.id }

        val confirmationResponses = confirmationResponseRepository
            .findBy(params.cpid, params.ocid, contractReceived.id)
            .onFailure { return it.reason.asValidationError() }
            .mapResult { transform.tryDeserialization(it.jsonData, ConfirmationResponse::class.java) }
            .onFailure { return it.reason.asValidationError() }

        val confirmationResponsesLinkedToConfirmationRequest = confirmationResponses
            .filter { requestsIds.contains(it.requestId.underlying.toString()) }

        val minReceivedConfResponses = rulesService
            .getMinReceivedConfResponses(params.country, params.pmd, params.operationType)
            .onFailure { return it.reason.asValidationError() }

        when (minReceivedConfResponses) {
            is MinReceivedConfResponsesRule.Number -> {
                if (confirmationResponsesLinkedToConfirmationRequest.size < minReceivedConfResponses.quantity)
                    CheckExistenceOfConfirmationResponsesErrors.IncorrectNumberOfConfirmatonResponses(
                        minimumQuantity = minReceivedConfResponses.quantity,
                        actualQuantity = confirmationResponsesLinkedToConfirmationRequest.size
                    ).asValidationError()
            }
            is MinReceivedConfResponsesRule.String -> {
                val numberOfRequests = targetConfirmationRequest.requests.size
                if (confirmationResponsesLinkedToConfirmationRequest.size != numberOfRequests)
                    CheckExistenceOfConfirmationResponsesErrors.IncorrectNumberOfConfirmatonResponses(
                        minimumQuantity = numberOfRequests,
                        actualQuantity = confirmationResponsesLinkedToConfirmationRequest.size
                    ).asValidationError()
            }
        }

        return ValidationResult.ok()
    }
}
