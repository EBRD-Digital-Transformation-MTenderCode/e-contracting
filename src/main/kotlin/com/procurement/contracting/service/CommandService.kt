package com.procurement.contracting.service

import com.procurement.contracting.dao.HistoryDao
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.CommandType
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service

@Service
class CommandService(private val historyDao: HistoryDao,
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
                     private val cancelService: CancelCanService) {


    fun execute(cm: CommandMessage): ResponseDto {
        var historyEntity = historyDao.getHistory(cm.id, cm.command.value())
        if (historyEntity != null) {
            return toObject(ResponseDto::class.java, historyEntity.jsonData)
        }
        val response = when (cm.command) {
            CommandType.CHECK_CAN -> canService.checkCan(cm)
            CommandType.CREATE_CAN -> canService.createCAN(cm)
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
            CommandType.UPDATE_CAN_DOCS -> updateDocumentsService.updateCanDocs(cm)
            CommandType.CANCEL_CAN -> cancelService.cancelCan(cm)
        }
        historyEntity = historyDao.saveHistory(cm.id, cm.command.value(), response)
        return toObject(ResponseDto::class.java, historyEntity.jsonData)
    }
}