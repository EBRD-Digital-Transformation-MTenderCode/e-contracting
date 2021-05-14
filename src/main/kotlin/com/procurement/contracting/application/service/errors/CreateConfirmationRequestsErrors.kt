package com.procurement.contracting.application.service.errors

import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.domain.model.process.Stage
import com.procurement.contracting.infrastructure.fail.error.ValidationCommandError

sealed class CreateConfirmationRequestsErrors(
    override val description: String,
    numberError: String,
) : ValidationCommandError() {

    override val code: String = prefix + numberError

    class InvalidStage(stage: Stage) : CreateConfirmationRequestsErrors(
        numberError = "6.12.1",
        description = "Unexpected stage '${stage.key}' for command."
    )

    class ContractNotFound(cpid: Cpid, ocid: Ocid, contractId: String) : CreateConfirmationRequestsErrors(
        numberError = "6.12.2",
        description = "Contract not found by cpid '$cpid', ocid '$ocid' and contract id '$contractId'."
    )

    class AttributeNotFound(attributeName: String) : CreateConfirmationRequestsErrors(
        numberError = "6.12.3",
        description = "Missing '$attributeName' attribute in request."
    )

    class TooMachDocuments : CreateConfirmationRequestsErrors(
        numberError = "6.12.4",
        description = "Contract must contains only one document."
    )
}
