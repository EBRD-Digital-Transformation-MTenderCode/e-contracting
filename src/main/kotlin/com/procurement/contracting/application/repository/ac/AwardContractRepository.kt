package com.procurement.contracting.application.repository.ac

import com.procurement.contracting.application.repository.ac.model.AwardContractEntity
import com.procurement.contracting.domain.model.ac.id.AwardContractId
import com.procurement.contracting.domain.model.ac.status.AwardContractStatus
import com.procurement.contracting.domain.model.ac.status.AwardContractStatusDetails
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result

interface AwardContractRepository {
    fun findBy(cpid: Cpid, id: AwardContractId): Result<AwardContractEntity?, Fail.Incident.Database>

    fun saveNew(entity: AwardContractEntity): Result<Boolean, Fail.Incident.Database>

    fun saveCancelledAC(
        cpid: Cpid,
        id: AwardContractId,
        status: AwardContractStatus,
        statusDetails: AwardContractStatusDetails,
        jsonData: String
    ): Result<Boolean, Fail.Incident.Database>

    fun updateStatusesAC(
        cpid: Cpid,
        id: AwardContractId,
        status: AwardContractStatus,
        statusDetails: AwardContractStatusDetails,
        jsonData: String
    ): Result<Boolean, Fail.Incident.Database>
}
