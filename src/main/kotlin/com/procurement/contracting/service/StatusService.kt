package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType.*
import com.procurement.contracting.model.dto.ContractProcess
import com.procurement.contracting.model.dto.GetActualBsRs
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.ocds.ContractStatus
import com.procurement.contracting.model.dto.ocds.ContractStatusDetails
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service
import java.util.*

@Service
class StatusService(private val acDao: AcDao) {

    fun getActualBudgetSources(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val ocId = cm.context.ocid ?: throw ErrorException(CONTEXT)
        val token = cm.context.token ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)

        val entity = acDao.getByCpIdAndToken(cpId, UUID.fromString(token))
        if (entity.owner != owner) throw ErrorException(OWNER)
        val contractProcess = toObject(ContractProcess::class.java, entity.jsonData)
        val contract = contractProcess.contracts
        if (contract.id != ocId) throw ErrorException(CONTRACT_ID)
        if (!(contract.status == ContractStatus.PENDING &&
                        contract.statusDetails == ContractStatusDetails.CONTRACT_PROJECT ||
                        contract.statusDetails == ContractStatusDetails.ISSUED)) {
            throw ErrorException(CONTEXT)
        }
        val actualBudgetSource = contractProcess.planning?.budget?.budgetSource?.asSequence()?.toSet()
        return ResponseDto(data = GetActualBsRs(language = entity.language, actualBudgetSource = actualBudgetSource))
    }
}
