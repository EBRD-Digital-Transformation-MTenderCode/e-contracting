package com.procurement.contracting.application.service.errors

import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.domain.model.process.Stage
import com.procurement.contracting.infrastructure.fail.error.ValidationCommandError

sealed class SetStateForContractsErrors(
    override val description: String,
    numberError: String,
) : ValidationCommandError() {

    override val code: String = prefix + numberError

    class PacRelatedToLotNotFound(relatedLot: String) : SetStateForContractsErrors(
        numberError = "6.6.1",
        description = "Cannot find pac with related lot $relatedLot."
    )

    class TenderMissing() : SetStateForContractsErrors(
        numberError = "6.6.2",
        description = "Tender is missing from request."
    )

    class ContractsMissing() : SetStateForContractsErrors(
        numberError = "6.6.3",
        description = "Contracts are missing from request."
    )

    class FCNotFound(cpid: Cpid, ocid: Ocid, id: FrameworkContractId) : SetStateForContractsErrors(
        numberError = "6.6.4",
        description = "Framework Contract not found by cpid '$cpid', ocid '$ocid' and id '$id'."
    )

    class PacNotFound(cpid: Cpid, ocid: Ocid, contractId: String) : SetStateForContractsErrors(
        numberError = "6.6.5",
        description = "Cannot find pac by by cpid '$cpid', ocid '$ocid' and id '$contractId'."
    )

    class InvalidStage(stage: Stage) : SetStateForContractsErrors(
        numberError = "6.6.6",
        description = "Stage '$stage' is invalid."
    )
}
