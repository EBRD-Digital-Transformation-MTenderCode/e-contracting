package com.procurement.contracting.application.service

import com.procurement.contracting.application.exception.repository.SaveEntityException
import com.procurement.contracting.application.repository.ac.AwardContractRepository
import com.procurement.contracting.application.repository.ac.model.AwardContractEntity
import com.procurement.contracting.application.repository.model.ContractProcess
import com.procurement.contracting.domain.model.ac.id.asAwardContractId
import com.procurement.contracting.domain.model.ac.status.AwardContractStatusDetails
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.infrastructure.handler.v1.CommandMessage
import com.procurement.contracting.infrastructure.handler.v1.cpid
import com.procurement.contracting.infrastructure.handler.v1.model.request.ContractVerifiedAcRs
import com.procurement.contracting.infrastructure.handler.v1.model.request.VerificationAcRs
import com.procurement.contracting.infrastructure.handler.v1.ocid
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service

@Service
class VerificationAwardContractService(
    private val acRepository: AwardContractRepository
) {

    fun verificationAc(cm: CommandMessage): VerificationAcRs {
        val cpid = cm.cpid
        val ocid = cm.ocid

        val awardContractId = ocid.asAwardContractId()
        val entity: AwardContractEntity = acRepository.findBy(cpid, awardContractId)
            .orThrow { it.exception }
            ?: throw ErrorException(ErrorType.CONTRACT_NOT_FOUND)
        val contractProcess = toObject(ContractProcess::class.java, entity.jsonData)

//        if (contractProcess.contract.status != ContractStatus.PENDING) throw ErrorException(CONTRACT_STATUS)
//        if (contractProcess.contract.statusDetails != ContractStatusDetails.SIGNED) throw ErrorException(CONTRACT_STATUS_DETAILS)

        contractProcess.contract.statusDetails = AwardContractStatusDetails.VERIFICATION
        val updatedContractEntity = entity.copy(
            status = contractProcess.contract.status,
            statusDetails = contractProcess.contract.statusDetails,
            jsonData = toJson(contractProcess)
        )

        val wasApplied = acRepository
            .updateStatusesAC(
                cpid = cpid,
                id = updatedContractEntity.id,
                status = updatedContractEntity.status,
                statusDetails = updatedContractEntity.statusDetails,
                jsonData = updatedContractEntity.jsonData
            )
            .orThrow { it.exception }
        if (!wasApplied)
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the save updated AC by cpid '${cpid}' and id '${updatedContractEntity.id}' with status '${updatedContractEntity.status}' and status details '${updatedContractEntity.statusDetails}' to the database. Record is not exists.")

        return VerificationAcRs(ContractVerifiedAcRs(contractProcess.contract.statusDetails))
    }
}
