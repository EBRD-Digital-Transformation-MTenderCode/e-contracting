package com.procurement.contracting.service

import com.procurement.contracting.application.service.CancelCANContext
import com.procurement.contracting.application.service.CancelCANData
import com.procurement.contracting.application.service.CancelCANService
import com.procurement.contracting.dao.HistoryDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.infrastructure.dto.can.cancel.CancelCANRequest
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.CommandType
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class CommandService(
    private val historyDao: HistoryDao,
    private val canService: CreateCanService,
    private val createAcService: CreateAcService,
    private val updateAcService: UpdateAcService,
    private val issuingAcService: IssuingAcService,
    private val statusService: StatusService,
    private val finalUpdateService: FinalUpdateService,
    private val verificationAcService: VerificationAcService,
    private val treasuryAcService: TreasuryAcService,
    private val signingAcService: SigningAcService,
    private val acService: ActivationAcService,
    private val updateDocumentsService: UpdateDocumentsService,
    private val cancelService: CancelCANService
) {

    companion object {
        private val log = LoggerFactory.getLogger(CommandService::class.java)
    }

    fun execute(cm: CommandMessage): ResponseDto {
        var historyEntity = historyDao.getHistory(cm.id, cm.command.value())
        if (historyEntity != null) {
            return toObject(ResponseDto::class.java, historyEntity.jsonData)
        }
        val response = when (cm.command) {
            CommandType.CHECK_CAN -> canService.checkCan(cm)
            CommandType.CHECK_CAN_BY_AWARD -> canService.checkCanByAwardId(cm)
            CommandType.CREATE_CAN -> canService.createCan(cm)
            CommandType.GET_CANS -> canService.getCans(cm)
            CommandType.UPDATE_CAN_DOCS -> updateDocumentsService.updateCanDocs(cm)
            CommandType.CANCEL_CAN -> {
                val context = CancelCANContext(
                    cpid = getCPID(cm),
                    token = getToken(cm),
                    owner = getOwner(cm),
                    canId = getCANId(cm)
                )
                val request = toObject(CancelCANRequest::class.java, cm.data)
                val data = CancelCANData(
                    amendment = request.contract.amendment.let { amendment ->
                        CancelCANData.Amendment(
                            rationale = amendment.rationale,
                            description = amendment.description,
                            documents = amendment.documents?.map { document ->
                                CancelCANData.Amendment.Document(
                                    id = document.id,
                                    documentType = document.documentType,
                                    title = document.title,
                                    description = document.description
                                )
                            }
                        )
                    }
                )
                val result = cancelService.cancel(context = context, data = data)
                if(log.isDebugEnabled)
                    log.debug("CANs were cancelled. Response: ${toJson(result)}")
                ResponseDto(data = result)
            }
            CommandType.CONFIRMATION_CAN -> canService.confirmationCan(cm)
            CommandType.CREATE_AC -> createAcService.createAC(cm)
            CommandType.UPDATE_AC -> updateAcService.updateAC(cm)
            CommandType.CHECK_STATUS_DETAILS -> TODO()
            CommandType.GET_BUDGET_SOURCES -> statusService.getActualBudgetSources(cm)
            CommandType.GET_RELATED_BID_ID -> statusService.getRelatedBidId(cm)
            CommandType.ISSUING_AC -> issuingAcService.issuingAc(cm)
            CommandType.FINAL_UPDATE -> finalUpdateService.finalUpdate(cm)
            CommandType.BUYER_SIGNING_AC -> signingAcService.buyerSigningAC(cm)
            CommandType.SUPPLIER_SIGNING_AC -> signingAcService.supplierSigningAC(cm)
            CommandType.VERIFICATION_AC -> verificationAcService.verificationAc(cm)
            CommandType.TREASURY_APPROVING_AC -> treasuryAcService.treasuryApprovingAC(cm)
            CommandType.ACTIVATION_AC -> acService.activateAc(cm)
        }
        historyEntity = historyDao.saveHistory(cm.id, cm.command.value(), response)
        return toObject(ResponseDto::class.java, historyEntity.jsonData)
    }

    private fun getCPID(cm: CommandMessage): String = cm.context.cpid
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'cpid' attribute in context.")

    private fun getToken(cm: CommandMessage): UUID = cm.context.token
        ?.let { token ->
            try {
                UUID.fromString(token)
            } catch (exception: Exception) {
                throw ErrorException(error = ErrorType.INVALID_FORMAT_TOKEN)
            }
        }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'token' attribute in context.")

    private fun getOwner(cm: CommandMessage): String = cm.context.owner
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'owner' attribute in context.")

    private fun getCANId(cm: CommandMessage): UUID = cm.context.id
        ?.let { id ->
            try {
                UUID.fromString(id)
            } catch (exception: Exception) {
                throw ErrorException(error = ErrorType.INVALID_FORMAT_CAN_ID)
            }
        }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'id' attribute in context.")
}