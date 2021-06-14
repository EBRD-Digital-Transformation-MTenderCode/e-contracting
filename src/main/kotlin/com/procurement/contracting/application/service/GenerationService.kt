package com.procurement.contracting.application.service

import com.datastax.driver.core.utils.UUIDs
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.ac.id.AwardContractId
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.related.process.RelatedProcessId
import com.procurement.contracting.domain.model.tender.TenderId
import org.springframework.stereotype.Service
import java.util.*

@Service
class GenerationService {

    fun generateTimeBasedUUID(): UUID {
        return UUIDs.timeBased()
    }

    fun getTimeBasedUUID(): String {
        return generateTimeBasedUUID().toString()
    }

    fun awardContractId(cpid: Cpid): AwardContractId = AwardContractId.generate(cpid)

    fun awardContractId(): AwardContractId = AwardContractId.generate()

    fun fcId(): FrameworkContractId = FrameworkContractId.generate()

    fun canId(): CANId = CANId.generate()

    fun awardId(): AwardId = AwardId.generate()

    fun token(): Token = Token.generate()

    fun pacId(): PacId = PacId.generate()

    fun tenderId(): TenderId = UUID.randomUUID().toString()

    fun relatedProcessesId(): RelatedProcessId = UUID.randomUUID().toString()
}
