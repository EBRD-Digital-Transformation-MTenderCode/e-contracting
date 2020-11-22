package com.procurement.contracting.application.service

import com.datastax.driver.core.utils.UUIDs
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.util.extension.nowDefaultUTC
import com.procurement.contracting.domain.util.extension.toMilliseconds

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

    fun contractId(cpid: Cpid): String {
        return cpid.underlying + "-AC-" + (nowDefaultUTC().toMilliseconds() + Random().nextInt())
    }

    fun canId(): CANId = CANId.generate()

    fun awardId(): AwardId = UUID.randomUUID()

    fun token(): Token = Token.generate()
}
