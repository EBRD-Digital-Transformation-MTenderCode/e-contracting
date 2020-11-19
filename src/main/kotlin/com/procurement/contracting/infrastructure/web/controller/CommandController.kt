package com.procurement.contracting.infrastructure.web.controller

import com.procurement.contracting.infrastructure.api.command.id.CommandId
import com.procurement.contracting.infrastructure.api.v1.ApiResponseV1
import com.procurement.contracting.infrastructure.configuration.properties.GlobalProperties
import com.procurement.contracting.infrastructure.handler.v1.CommandMessage
import com.procurement.contracting.infrastructure.handler.v1.CommandService
import com.procurement.contracting.infrastructure.handler.v1.errorResponse
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toObject
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/command")
class CommandController(private val commandService: CommandService) {

    companion object {
        private val log = LoggerFactory.getLogger(CommandService::class.java)
    }

    @PostMapping
    fun command(@RequestBody requestBody: String): ResponseEntity<ApiResponseV1> {
        if (log.isDebugEnabled)
            log.debug("RECEIVED COMMAND: '$requestBody'.")

        val cm: CommandMessage = try {
            toObject(CommandMessage::class.java, requestBody)
        } catch (expected: Exception) {
            log.debug("Error.", expected)
            val response =
                errorResponse(
                    exception = expected,
                    id = CommandId.NaN,
                    version = GlobalProperties.App.apiVersion
                )
            return ResponseEntity(response, HttpStatus.OK)
        }

        val response = try {
            commandService.execute(cm)
                .also { response ->
                    if (log.isDebugEnabled)
                        log.debug("RESPONSE (operation-id: '${cm.context.operationId}'): '${toJson(response)}'.")
                }
        } catch (expected: Exception) {
            log.debug("Error.", expected)
            errorResponse(
                exception = expected,
                id = cm.id,
                version = cm.version
            )
        }
        return ResponseEntity(response, HttpStatus.OK)
    }
}



