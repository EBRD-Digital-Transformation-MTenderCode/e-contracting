package com.procurement.contracting.application.repository.fc

import com.procurement.contracting.application.repository.fc.model.FrameworkContractEntity
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result

interface FrameworkContractRepository {
    fun findBy(cpid: Cpid, ocid: Ocid): Result<List<FrameworkContractEntity>, Fail.Incident.Database>

    fun findBy(cpid: Cpid, ocid: Ocid, contractId: FrameworkContractId): Result<FrameworkContractEntity?, Fail.Incident.Database>

    fun saveNew(entity: FrameworkContractEntity): Result<Boolean, Fail.Incident.Database>

    fun update(entity: FrameworkContractEntity): Result<Boolean, Fail.Incident.Database>
}
