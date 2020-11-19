package com.procurement.contracting.infrastructure.web.controller

import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.domain.functional.Result
import com.procurement.contracting.infrastructure.api.ApiVersion
import com.procurement.contracting.infrastructure.api.command.id.CommandId
import com.procurement.contracting.infrastructure.api.v2.ApiResponse2
import com.procurement.contracting.infrastructure.configuration.properties.GlobalProperties2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.v2.Command2Service
import com.procurement.contracting.infrastructure.handler.v2.generateResponseOnFailure
import com.procurement.contracting.infrastructure.handler.v2.tryGetId
import com.procurement.contracting.infrastructure.handler.v2.tryGetNode
import com.procurement.contracting.infrastructure.handler.v2.tryGetVersion
import com.procurement.contracting.utils.toJson
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/command2")
class Command2Controller(private val commandService: Command2Service, private val logger: Logger) {

    @PostMapping
    fun command(@RequestBody requestBody: String): ResponseEntity<ApiResponse2> {
        if (logger.isDebugEnabled)
            logger.debug("RECEIVED COMMAND: '$requestBody'.")

        val node = requestBody.tryGetNode()
            .doReturn { error -> return generateResponse(fail = error) }

        val version = when (val versionResult = node.tryGetVersion()) {
            is Result.Success -> versionResult.get
            is Result.Failure -> {
                when (val idResult = node.tryGetId()) {
                    is Result.Success -> return generateResponse(fail = versionResult.error, id = idResult.get)
                    is Result.Failure -> return generateResponse(fail = versionResult.error)
                }
            }
        }

        val id = node.tryGetId()
            .doReturn { error -> return generateResponse(fail = error, version = version) }

        val response =
            commandService.execute(node)
                .also { response ->
                    if (logger.isDebugEnabled)
                        logger.debug("RESPONSE (id: '${id}'): '${toJson(response)}'.")
                }

        return ResponseEntity(response, HttpStatus.OK)
    }

    private fun generateResponse(
        fail: Fail,
        version: ApiVersion = GlobalProperties2.App.apiVersion,
        id: CommandId = CommandId.NaN
    ): ResponseEntity<ApiResponse2> {
        val response = generateResponseOnFailure(fail = fail, id = id, version = version, logger = logger)
        return ResponseEntity(response, HttpStatus.OK)
    }
}