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
                     private val statusService: StatusService) {


    fun execute(cm: CommandMessage): ResponseDto {
        var historyEntity = historyDao.getHistory(cm.id, cm.command.value())
        if (historyEntity != null) {
            return toObject(ResponseDto::class.java, historyEntity.jsonData)
        }
        val response = when (cm.command) {
            CommandType.CREATE_CAN -> canService.createCAN(cm)
            CommandType.CREATE_AC -> createAcService.createAC(cm)
            CommandType.UPDATE_AC -> updateAcService.updateAC(cm)
            CommandType.GET_BUDGET_SOURCES -> statusService.getActualBudgetSources(cm)
            CommandType.CHECK_STATUS_DETAILS -> statusService.contractingCheckStatusDetails(cm)
        }
        historyEntity = historyDao.saveHistory(cm.id, cm.command.value(), response)
        return toObject(ResponseDto::class.java, historyEntity.jsonData)
    }
}