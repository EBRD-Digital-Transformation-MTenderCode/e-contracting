package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType.CONTEXT
import com.procurement.contracting.model.dto.ContractProcess
import com.procurement.contracting.model.dto.UpdateAcRq
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.ocds.Award
import com.procurement.contracting.model.dto.ocds.Contract
import com.procurement.contracting.model.entity.AcEntity
import com.procurement.contracting.model.entity.CanEntity
import com.procurement.contracting.utils.toDate
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toLocalDateTime
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class UpdateAcService(private val acDao: AcDao) {

    fun updateAC(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val token = cm.context.token ?: throw ErrorException(CONTEXT)
        val dateTime = cm.context.startDate?.toLocalDateTime() ?: throw ErrorException(CONTEXT)
        val dto = toObject(UpdateAcRq::class.java, cm.data)

        val entity = acDao.getByCpIdAndToken(cpId, UUID.fromString(token))
        val contractProcesses = toObject(ContractProcess::class.java, entity.jsonData)

        validateAwards(dto.awards) //VR-9.2.3 VR-9.2.10
        contractProcesses.awards.apply {
            items = dto.awards.items//  BR-9.2.3;
            documents = dto.awards.documents  //BR-9.2.2;
            value = dto.awards.value
            suppliers = dto.awards.suppliers// BR-9.2.21;
        }
        contractProcesses.planning.apply {
//        Checks and proceeds Implementation object of Request by rule BR-9.2.6;
//        Checks and proceeds Budget object of Request by rule BR-9.2.7;
//        Checks and proceeds budgetSource object of Request by rule BR-9.2.8;
//        Includes updated Planning object for Response;
        }
        contractProcesses.contracts.apply {
//        Finds saved version of Contract object by OCID from parameter of Request (OCID == Contract.ID);
//        Validates the Contract.Period object from Request by rule VR-9.2.18 and save it to DB;
//        Checks and proceeds Contract.Documents object of Request by rule BR-9.2.10;
//        Checks and proceeds Contract.Milestones object of Request by rule BR-9.2.11;
//        Checks and proceeds Contract.confirmationRequests object of Request by rule BR-9.2.16;
//        Updates saved version of Contract in DB using next fields of Contract from Request:
//        Updates or adds contract.title;
//        Updates or adds contract.description;
//        Calculates Contract.Value object by rule BR-9.2.19 and save it to DB;
//        Sets Contract.statusDetails by rule BR-9.2.25;
        }
//        eContracting сохраняет Buyer, treasuryBudgetSources objects по правилам BR-9.2.20, BR-9.2.24
        contractProcesses.buyer = TODO()

        return ResponseDto(data = contractProcesses)
    }

    private fun validateAwards(awards: Award) {
        TODO()
    }


    private fun convertContractToEntity(cpId: String,
                                        stage: String,
                                        dateTime: LocalDateTime,
                                        language: String,
                                        mainProcurementCategory: String,
                                        contract: Contract,
                                        contractProcess: ContractProcess,
                                        canEntity: CanEntity): AcEntity {
        return AcEntity(
                cpId = cpId,
                stage = stage,
                token = UUID.fromString(contract.token!!),
                owner = canEntity.owner,
                createdDate = dateTime.toDate(),
                canId = canEntity.canId.toString(),
                status = contract.status.value,
                statusDetails = contract.statusDetails.value,
                mainProcurementCategory = mainProcurementCategory,
                language = language,
                jsonData = toJson(contractProcess))
    }
}
