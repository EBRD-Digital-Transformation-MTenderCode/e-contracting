package com.procurement.contracting.controller

import com.procurement.contracting.exception.EnumException
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.model.dto.bpe.*
import com.procurement.contracting.service.CommandService
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toObject
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/command")
class CommandController(private val commandService: CommandService) {

    companion object {
        private val log = LoggerFactory.getLogger(CommandService::class.java)
    }

    @PostMapping
    fun command(@RequestBody requestBody: String): ResponseEntity<ResponseDto> {
        if (log.isDebugEnabled)
            log.debug("RECEIVED COMMAND: '$requestBody'.")
        val cm: CommandMessage = toObject(CommandMessage::class.java, requestBody)

        val response = commandService.execute(cm)

        if (log.isDebugEnabled)
            log.debug("RESPONSE (operation-id: '${cm.context.operationId}'): '${toJson(response)}'.")
        return ResponseEntity(response, HttpStatus.OK)
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception::class)
    fun exception(ex: Exception): ResponseDto {
        log.error("Error.", ex)

        return when (ex) {
            is ErrorException -> getErrorExceptionResponseDto(ex)
            is EnumException -> getEnumExceptionResponseDto(ex)
            else -> getExceptionResponseDto(ex)
        }
    }
}



