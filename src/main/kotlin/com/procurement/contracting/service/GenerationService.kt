package com.procurement.contracting.service

import com.datastax.driver.core.utils.UUIDs
import com.procurement.contracting.utils.milliNowUTC
import org.springframework.stereotype.Service
import java.util.*

@Service
class GenerationService {

    fun generateRandomUUID(): UUID {
        return UUIDs.random()
    }

    fun generateTimeBasedUUID(): UUID {
        return UUIDs.timeBased()
    }

    fun getRandomUUID(): String {
        return generateRandomUUID().toString()
    }

    fun getTimeBasedUUID(): String {
        return generateTimeBasedUUID().toString()
    }

    fun newOcId(cpId: String): String {
        return cpId + "-AC-" + (milliNowUTC() + Random().nextInt())
    }

    fun canId(): UUID = UUID.randomUUID()

    fun awardId(): UUID = UUID.randomUUID()

    fun token(): UUID = UUID.randomUUID()
}
