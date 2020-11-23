package com.procurement.contracting.application.service

import com.procurement.contracting.application.exception.repository.ReadEntityException
import com.procurement.contracting.application.exception.repository.SaveEntityException
import com.procurement.contracting.application.repository.ac.AwardContractRepository
import com.procurement.contracting.application.repository.ac.model.AwardContractEntity
import com.procurement.contracting.application.repository.can.CANRepository
import com.procurement.contracting.application.repository.can.model.CANEntity
import com.procurement.contracting.application.repository.model.ContractProcess
import com.procurement.contracting.domain.model.ac.id.asAwardContractId
import com.procurement.contracting.domain.model.ac.status.AwardContractStatus
import com.procurement.contracting.domain.model.ac.status.AwardContractStatusDetails
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.milestone.status.MilestoneStatus
import com.procurement.contracting.domain.model.milestone.type.MilestoneSubType
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.exception.ErrorType.CANS_NOT_FOUND
import com.procurement.contracting.exception.ErrorType.INVALID_OWNER
import com.procurement.contracting.exception.ErrorType.INVALID_TOKEN
import com.procurement.contracting.infrastructure.handler.v1.CommandMessage
import com.procurement.contracting.infrastructure.handler.v1.cpid
import com.procurement.contracting.infrastructure.handler.v1.model.request.ActivationAcRs
import com.procurement.contracting.infrastructure.handler.v1.model.request.ActivationCan
import com.procurement.contracting.infrastructure.handler.v1.model.request.ActivationContract
import com.procurement.contracting.infrastructure.handler.v1.ocid
import com.procurement.contracting.infrastructure.handler.v1.owner
import com.procurement.contracting.infrastructure.handler.v1.startDate
import com.procurement.contracting.infrastructure.handler.v1.token
import com.procurement.contracting.model.dto.ocds.Can
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service

@Service
class ActivationAwardContractService(
    private val canRepository: CANRepository,
    private val acRepository: AwardContractRepository
) {

    fun activateAc(cm: CommandMessage): ActivationAcRs {
        val cpid = cm.cpid
        val ocid = cm.ocid
        val token = cm.token
        val owner = cm.owner
        val startDate = cm.startDate

        val awardContractId = ocid.asAwardContractId()
        val entity: AwardContractEntity = acRepository.findBy(cpid, awardContractId)
            .orThrow { it.exception }
            ?: throw ErrorException(ErrorType.CONTRACT_NOT_FOUND)
        if (entity.owner != owner) throw ErrorException(error = INVALID_OWNER)
        if (entity.token != token) throw ErrorException(INVALID_TOKEN)
        val contractProcess = toObject(ContractProcess::class.java, entity.jsonData)

//        if (contractProcess.contract.status != ContractStatus.PENDING) throw ErrorException(CONTRACT_STATUS)
//        if (contractProcess.contract.statusDetails != ContractStatusDetails.VERIFIED) throw ErrorException(CONTRACT_STATUS_DETAILS)

        contractProcess.contract.milestones?.asSequence()
                ?.filter { it.subtype == MilestoneSubType.CONTRACT_ACTIVATION }
                ?.forEach { milestone ->
                    milestone.apply {
                        dateModified = startDate
                        dateMet = startDate
                        status = MilestoneStatus.MET
                    }
                }
        contractProcess.contract.apply {
            status = AwardContractStatus.ACTIVE
            statusDetails = AwardContractStatusDetails.EXECUTION

        }
        val relatedLots = contractProcess.award.relatedLots

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

        val canEntities = canRepository.findBy(cpid)
            .orThrow {
                ReadEntityException(message = "Error read CAN(s) from the database.", cause = it.exception)
            }
        if (canEntities.isEmpty()) throw ErrorException(CANS_NOT_FOUND)
        val updatedCanEntities = ArrayList<CANEntity>()
        val cans = ArrayList<Can>()
        for (canEntity in canEntities) {
            if (canEntity.awardContractId == entity.id) {
                val can = toObject(Can::class.java, canEntity.jsonData)
                can.status = CANStatus.ACTIVE
                can.statusDetails = CANStatusDetails.EMPTY
                val updatedCANEntity = canEntity.copy(
                    status = can.status,
                    statusDetails = can.statusDetails,
                    jsonData = toJson(can)
                )

                updatedCanEntities.add(updatedCANEntity)
                cans.add(can)
            }
        }

        val wasAppliedCAN = canRepository.update(cpid = cpid, entities = updatedCanEntities)
            .orThrow { it.exception }
        if (!wasAppliedCAN)
            throw SaveEntityException(message = "An error occurred when writing a record(s) of CAN by cpid '$cpid' to the database. Record is already.")

        val cansRs = cans.asSequence()
            .map { ActivationCan(id = it.id, status = it.status, statusDetails = it.statusDetails) }.toList()
        return ActivationAcRs(
            relatedLots = relatedLots,
            contract = ActivationContract(
                id = contractProcess.contract.id,
                status = contractProcess.contract.status,
                statusDetails = contractProcess.contract.statusDetails,
                milestones = contractProcess.contract.milestones
            ),
            cans = cansRs
        )
    }
}
