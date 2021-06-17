package com.procurement.contracting.application.repository.v2

import com.procurement.contracting.domain.model.ac.id.AwardContractId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.model.dto.ocds.v2.AwardContract

interface AwardContractRepository {
    fun findBy(cpid: Cpid, ocid: Ocid, id: AwardContractId): Result<AwardContractEntity?, Fail.Incident.Database>

    fun save(awardContract: AwardContract): Result<Boolean, Fail.Incident.Database>
}
