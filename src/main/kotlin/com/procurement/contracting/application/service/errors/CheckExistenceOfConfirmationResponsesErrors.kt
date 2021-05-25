package com.procurement.contracting.application.service.errors

import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestSource
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.error.ValidationCommandError

sealed class CheckExistenceOfConfirmationResponsesErrors(
    override val description: String,
    numberError: String,
) : ValidationCommandError() {

    override val code: String = prefix + numberError

    class ConfirmationRequestNotFound(cpid: Cpid, ocid: Ocid, requestId: String, sourceOfConfirmationRequest: ConfirmationRequestSource) :
        CheckExistenceOfConfirmationResponsesErrors(
            numberError = "6.19.1",
            description = "Confirmation Request not found by cpid '$cpid', ocid '$ocid' and request id '$requestId' and '$sourceOfConfirmationRequest'."
        )

    class IncorrectNumberOfConfirmatonResponses(minimumQuantity: Int, actualQuantity: Int) :
        CheckExistenceOfConfirmationResponsesErrors(
            numberError = "6.19.2",
            description = "Minimum required number of confirmation responses: '$minimumQuantity'. Actual confirmation responses number: '$actualQuantity'"
        )
}
