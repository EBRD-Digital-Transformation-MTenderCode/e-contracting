package com.procurement.contracting.service

import com.datastax.driver.core.utils.UUIDs
import com.procurement.contracting.utils.milliNowUTC
import org.springframework.stereotype.Service
import java.util.*

interface GenerationService {


    fun generateRandomUUID(): UUID

    fun generateTimeBasedUUID(): UUID

    fun newOcId(cpId: String, stage: String): String

}

@Service
class GenerationServiceImpl : GenerationService {

    override fun generateRandomUUID(): UUID {
        return UUIDs.random()
    }

    override fun generateTimeBasedUUID(): UUID {
        return UUIDs.timeBased()
    }

    override fun newOcId(cpId: String, stage: String): String {
        return cpId + "-" + stage.toUpperCase() + "-" + milliNowUTC()
    }
}