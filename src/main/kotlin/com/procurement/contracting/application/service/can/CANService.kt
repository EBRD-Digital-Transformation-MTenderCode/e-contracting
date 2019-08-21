package com.procurement.contracting.application.service.can

import com.procurement.contracting.application.repository.CANRepository
import com.procurement.contracting.domain.entity.CANEntity
import com.procurement.contracting.domain.model.can.CAN
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.service.GenerationService
import com.procurement.contracting.utils.toJson
import org.springframework.stereotype.Service
import java.util.*

interface CANService {
    fun create(context: CreateCANContext, data: CreateCANData): CreatedCANData
}

@Service
class CANServiceImpl(
    private val canRepository: CANRepository,
    private val generationService: GenerationService
) : CANService {

    /**
     * BR-9.14.1 CAN
     *
     * eContracting executes next operations:
     * 1. Checks the availability of award.ID in Request:
     *   a. IF [there is award.ID] then:
     *      eContracting generates CAN object according to the next order:
     *     i.   Generates unique can.ID and adds it to can object (UUID type);
     *     ii.  Sets can.status by rule BR-9.14.2 and adds it to can object;
     *     iii. Sets can.statusDetails by rule BR-9.14.2 and adds it to can object;
     *     iv.  Sets can.date == startDate value from the context of Request and adds it to can object;
     *     v.   Sets can.awardId == award.ID value from Request and adds it to can object;
     *     vi.  Sets can.lotId == ID value from the context of Request and adds it to can object;
     *   b. ELSE [no award.ID in Request] { then: eContracting generates CAN object according to the next order:
     *     i.   Generates unique can.ID and adds it to can object (UUID type);
     *     ii.  Sets can.status by rule BR-9.14.3 and adds it to can object;
     *     iii. Sets can.statusDetails by rule BR-9.14.3 and adds it to can object;
     *     iv.  Sets can.date == startDate value from the context of Request and adds it to can object;
     *     v.   Sets can.lotId == ID value from the context of Request and adds it to can object;
     * 2. Generates token for created can object;
     * 3. Saves in DB created can object with values of CPID && OCID && Owner && ID (lot.ID)
     *    from the context of Request && with Token got before;
     * 4. Returns created can object and token value for Response;
     */
    override fun create(context: CreateCANContext, data: CreateCANData): CreatedCANData {
        val can = if (data.award != null)
            createCANByAward(context = context, data = data)
        else
            createCANWithoutAward(context = context)

        val canEntity = CANEntity(
            cpid = context.cpid,
            id = can.id,
            token = can.token,
            owner = context.owner,
            createdDate = context.startDate,
            awardId = can.awardId,
            lotId = can.lotId,
            contractId = null,
            status = can.status.value,
            statusDetails = can.statusDetails.value,
            jsonData = toJson(can)
        )
        canRepository.saveNewCAN(cpid = context.cpid, entity = canEntity)

        return CreatedCANData(
            token = can.token,
            can = CreatedCANData.CAN(
                id = can.id,
                awardId = can.awardId,
                lotId = can.lotId,
                date = can.date,
                status = can.status,
                statusDetails = can.statusDetails
            )
        )
    }

    private fun createCANByAward(context: CreateCANContext, data: CreateCANData): CAN {
        return CAN(
            id = generationService.canId(),
            token = generationService.token(),
            awardId = data.award!!.id,
            lotId = context.lotId,
            date = context.startDate,
            //BR-9.14.2
            status = CANStatus.PENDING,
            //BR-9.14.2
            statusDetails = CANStatusDetails.CONTRACT_PROJECT,
            documents = null,
            amendment = null
        )
    }

    private fun createCANWithoutAward(context: CreateCANContext): CAN {
        return CAN(
            id = generationService.canId(),
            token = generationService.token(),
            awardId = null,
            lotId = context.lotId,
            date = context.startDate,
            //BR-9.14.3
            status = CANStatus.PENDING,
            //BR-9.14.3
            statusDetails = CANStatusDetails.UNSUCCESSFUL,
            documents = null,
            amendment = null
        )
    }
}
