package com.procurement.contracting.application.model.can

import com.procurement.contracting.domain.model.Cpid
import com.procurement.contracting.domain.model.Ocid
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.lot.LotId

class FindCANIdsParams(
    val cpid: Cpid,
    val ocid: Ocid,
    val states: List<State>,
    val lotIds: List<LotId>
) {
    data class State(
         val status: CANStatus?,
         val statusDetails: CANStatusDetails?
    )
}