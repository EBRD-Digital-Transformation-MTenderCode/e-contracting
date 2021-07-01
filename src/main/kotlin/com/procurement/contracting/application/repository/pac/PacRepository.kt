package com.procurement.contracting.application.repository.pac

import com.procurement.contracting.application.repository.pac.model.PacRecord
import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.MaybeFail
import com.procurement.contracting.lib.functional.Result

interface PacRepository {
    fun findBy(cpid: Cpid, ocid: Ocid): Result<List<PacRecord>, Fail.Incident.Database>

    fun findBy(cpid: Cpid, ocid: Ocid, contractId: PacId): Result<PacRecord?, Fail.Incident.Database>

    fun findBy(cpid: Cpid, ocid: Ocid, pacIds: List<PacId>): Result<List<PacRecord>, Fail.Incident.Database>

    fun saveNew(record: PacRecord): Result<Boolean, Fail.Incident.Database>

    fun update(record: PacRecord): Result<Boolean, Fail.Incident.Database>

    fun save(records: Collection<PacRecord>): MaybeFail<Fail.Incident.Database>
}
