package com.procurement.contracting.infrastructure.web.controller

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.application.service.Transform
import com.procurement.contracting.infrastructure.api.ApiVersion
import com.procurement.contracting.infrastructure.api.command.id.CommandId
import com.procurement.contracting.infrastructure.api.v2.ApiResponseV2
import com.procurement.contracting.infrastructure.api.v2.CommandTypeV2
import com.procurement.contracting.infrastructure.extension.tryGetAttributeAsEnum
import com.procurement.contracting.infrastructure.extension.tryGetNode
import com.procurement.contracting.infrastructure.extension.tryGetTextAttribute
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.infrastructure.handler.v2.CommandServiceV2
import com.procurement.contracting.infrastructure.handler.v2.model.ApiResponseV2Generator.generateResponseOnFailure
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess
import com.procurement.contracting.lib.functional.flatMap
import com.procurement.contracting.utils.toJson
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/command2")
class Command2Controller(
    private val commandService: CommandServiceV2,
    private val transform: Transform,
    private val logger: Logger
) {

    @PostMapping
    fun command(@RequestBody requestBody: String): ResponseEntity<ApiResponseV2> {
        if (logger.isDebugEnabled)
            logger.debug("RECEIVED COMMAND: '$requestBody'.")

        val node = requestBody.tryGetNode(transform)
            .onFailure { return generateResponse(fail = it.reason, id = CommandId.NaN, version = ApiVersion.NaN) }

        val version = node.tryGetVersion()
            .onFailure {
                val id = node.tryGetId().getOrElse(CommandId.NaN)
                return generateResponse(fail = it.reason, version = ApiVersion.NaN, id = id)
            }

        val id = node.tryGetId()
            .onFailure { return generateResponse(fail = it.reason, version = version, id = CommandId.NaN) }

        val action = node.tryGetAction()
            .onFailure { return generateResponse(fail = it.reason, version = version, id = id) }

        val description = CommandDescriptor(
            version = version,
            id = id,
            action = action,
            body = CommandDescriptor.Body(asString = requestBody, asJsonNode = node)
        )

        val response =
            commandService.execute(description)
                .also { response ->
                    if (logger.isDebugEnabled)
                        logger.debug("RESPONSE (id: '${id}'): '${toJson(response)}'.")
                }

        return ResponseEntity(response, HttpStatus.OK)
    }

    fun JsonNode.tryGetVersion(): Result<ApiVersion, DataErrors> {
        val name = "version"
        return tryGetTextAttribute(name)
            .flatMap { version ->
                ApiVersion.orNull(version)
                    ?.asSuccess<ApiVersion, DataErrors>()
                    ?: DataErrors.Validation.DataFormatMismatch(
                        name = name,
                        expectedFormat = ApiVersion.pattern,
                        actualValue = version
                    ).asFailure()
            }
    }

    fun JsonNode.tryGetAction(): Result<CommandTypeV2, DataErrors> = tryGetAttributeAsEnum("action", CommandTypeV2)

    fun JsonNode.tryGetId(): Result<CommandId, DataErrors> = tryGetTextAttribute("id").map { CommandId(it) }

    private fun generateResponse(
        fail: Fail,
        version: ApiVersion,
        id: CommandId = CommandId.NaN
    ): ResponseEntity<ApiResponseV2> {
        val response = generateResponseOnFailure(fail = fail, id = id, version = version, logger = logger)
        return ResponseEntity(response, HttpStatus.OK)
    }
}