package com.procurement.contracting.application.repository.pac

import com.procurement.contracting.application.repository.pac.model.PacEntity
import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result

interface PacRepository {
    fun findBy(cpid: Cpid, ocid: Ocid): Result<List<PacEntity>, Fail.Incident.Database>

    fun findBy(cpid: Cpid, ocid: Ocid, contractId: PacId): Result<PacEntity?, Fail.Incident.Database>

    fun saveNew(entity: PacEntity): Result<Boolean, Fail.Incident.Database>

    fun update(entity: PacEntity): Result<Boolean, Fail.Incident.Database>
}
