package com.procurement.contracting.application.repository.can

import com.procurement.contracting.application.repository.can.model.CANEntity
import com.procurement.contracting.application.repository.can.model.DataCancelCAN
import com.procurement.contracting.application.repository.can.model.DataRelatedCAN
import com.procurement.contracting.application.repository.can.model.DataResetCAN
import com.procurement.contracting.application.repository.can.model.RelatedContract
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result

interface CANRepository {
    fun findBy(cpid: Cpid, canId: CANId): Result<CANEntity?, Fail.Incident.Database>

    fun findBy(cpid: Cpid): Result<List<CANEntity>, Fail.Incident.Database>

    fun findBy(cpid: Cpid, canIds: List<CANId>): Result<List<CANEntity>, Fail.Incident.Database>

    fun saveNewCAN(cpid: Cpid, entity: CANEntity): Result<Boolean, Fail.Incident.Database>

    fun saveCancelledCANs(
        cpid: Cpid,
        dataCancelledCAN: DataCancelCAN,
        dataRelatedCANs: List<DataRelatedCAN>
    ): Result<Boolean, Fail.Incident.Database>

    fun update(cpid: Cpid, entity: CANEntity): Result<Boolean, Fail.Incident.Database>

    fun update(cpid: Cpid, entities: Collection<CANEntity>): Result<Boolean, Fail.Incident.Database>

    fun resetCANs(cpid: Cpid, cans: List<DataResetCAN>): Result<Boolean, Fail.Incident.Database>

    fun relateContract(cpid: Cpid, cans: List<RelatedContract>): Result<Boolean, Fail.Incident.Database>
}
