package com.procurement.contracting.application.repository.ac

import com.procurement.contracting.domain.entity.ACEntity
import com.procurement.contracting.domain.model.contract.status.ContractStatus
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result

interface ACRepository {
    fun findBy(cpid: Cpid, contractId: String): Result<ACEntity?, Fail.Incident.Database>

    fun saveNew(entity: ACEntity): Result<Boolean, Fail.Incident.Database>

    fun saveCancelledAC(
        cpid: Cpid,
        id: String,
        status: ContractStatus,
        statusDetails: ContractStatusDetails,
        jsonData: String
    ): Result<Boolean, Fail.Incident.Database>

    fun updateStatusesAC(
        cpid: Cpid,
        id: String,
        status: ContractStatus,
        statusDetails: ContractStatusDetails,
        jsonData: String
    ): Result<Boolean, Fail.Incident.Database>
}
