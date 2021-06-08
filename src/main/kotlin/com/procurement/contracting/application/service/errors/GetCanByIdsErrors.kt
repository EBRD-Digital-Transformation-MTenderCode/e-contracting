package com.procurement.contracting.application.service.errors

import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.infrastructure.fail.error.ValidationCommandError

sealed class GetCanByIdsErrors(
    override val description: String,
    numberError: String,
) : ValidationCommandError() {

    override val code: String = prefix + numberError

    class CanNotFound(cpid: Cpid, canIds: Collection<CANId>) : GetCanByIdsErrors(
        numberError = "6.21.1",
        description = "CAN not found by cpid '$cpid' and id(s) '${canIds.joinToString()}'."
    )
}
