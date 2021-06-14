package com.procurement.contracting.application.repository.v2

import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.ac.status.AwardContractStatus
import com.procurement.contracting.domain.model.ac.status.AwardContractStatusDetails
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.model.dto.ocds.v2.AwardContract
import java.time.LocalDateTime

data class AwardContractEntity(
    val cpid: Cpid,
    val ocid: Ocid,
    val token: Token,
    val owner: Owner,
    val createdDate: LocalDateTime,
    val status: AwardContractStatus,
    val statusDetails: AwardContractStatusDetails,
    val awardContract: AwardContract
)
