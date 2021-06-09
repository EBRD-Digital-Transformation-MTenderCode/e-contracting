package com.procurement.contracting.application.repository.can.model

import com.procurement.contracting.application.service.Transform
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.ac.id.AwardContractId
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.CAN
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess
import java.time.LocalDateTime

data class CANEntity(
    val cpid: Cpid,
    val id: CANId,
    val token: Token,
    val owner: Owner,
    val createdDate: LocalDateTime,
    val awardId: AwardId?,
    val lotId: LotId,
    val awardContractId: AwardContractId?,
    val status: CANStatus,
    val statusDetails: CANStatusDetails,
    val jsonData: String
){
    companion object {
        fun of(
            cpid: Cpid,
            can: CAN,
            owner: Owner,
            awardContractId: AwardContractId?,
            transform: Transform
        ): Result<CANEntity, Fail> {
            val json = transform.trySerialization(can).onFailure { return it }
            return CANEntity(
                cpid = cpid,
                id = can.id,
                token = can.token,
                owner = owner,
                createdDate = can.date,
                status = can.status,
                statusDetails = can.statusDetails,
                lotId = can.lotId,
                awardId = can.awardId,
                awardContractId = awardContractId,
                jsonData = json
            ).asSuccess()
        }
    }
}
