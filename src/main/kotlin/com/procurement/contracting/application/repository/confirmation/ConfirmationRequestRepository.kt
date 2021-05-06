package com.procurement.contracting.application.repository.confirmation

import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result

interface ConfirmationRequestRepository {
    fun save(entity: ConfirmationRequestEntity): Result<Boolean, Fail.Incident.Database>
    fun findBy(cpid: Cpid, ocid: Ocid): Result<List<ConfirmationRequestEntity>, Fail.Incident.Database>
    fun findBy(cpid: Cpid, ocid: Ocid, contractId: String): Result<List<ConfirmationRequestEntity>, Fail.Incident.Database>
}
