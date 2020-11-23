package com.procurement.contracting.application.service

import com.datastax.driver.core.utils.UUIDs
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.contract.id.ContractId
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.process.Cpid
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

    fun contractId(cpid: Cpid): ContractId = ContractId.generate(cpid)

    fun fcId(): FrameworkContractId = FrameworkContractId.generate()

    fun canId(): CANId = CANId.generate()

    fun awardId(): AwardId = AwardId.generate()

    fun token(): Token = Token.generate()
}
