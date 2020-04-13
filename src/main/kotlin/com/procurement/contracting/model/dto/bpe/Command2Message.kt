package com.procurement.contracting.model.dto.bpe

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.config.properties.GlobalProperties2
import com.procurement.contracting.domain.functional.Result
import com.procurement.contracting.domain.functional.bind
import com.procurement.contracting.domain.model.EnumElementProvider
import com.procurement.contracting.domain.util.extension.nowDefaultUTC
import com.procurement.contracting.domain.util.extension.toListOrEmpty
import com.procurement.contracting.domain.util.extension.tryUUID
import com.procurement.contracting.infrastructure.extension.tryGetAttribute
import com.procurement.contracting.infrastructure.extension.tryGetAttributeAsEnum
import com.procurement.contracting.infrastructure.extension.tryGetTextAttribute
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.fail.error.BadRequest
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.infrastructure.fail.error.ValidationError
import com.procurement.contracting.infrastructure.web.dto.Action
import com.procurement.contracting.infrastructure.web.dto.ApiErrorResponse2
import com.procurement.contracting.infrastructure.web.dto.ApiIncidentResponse2
import com.procurement.contracting.infrastructure.web.dto.ApiResponse2
import com.procurement.contracting.infrastructure.web.dto.ApiVersion2
import com.procurement.contracting.utils.tryToNode
import com.procurement.contracting.utils.tryToObject
import java.util.*

enum class Command2Type(@JsonValue override val key: String) : Action, EnumElementProvider.Key {

    REPLACE_LATER(""); //TODO

    override fun toString(): String = key

    companion object : EnumElementProvider<Command2Type>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = Command2Type.orThrow(name)
    }
}

fun generateResponseOnFailure(
    fail: Fail, version: ApiVersion2, id: UUID, logger: Logger
): ApiResponse2 {
    fail.logging(logger)
    return when (fail) {
        is Fail.Error -> {
            when (fail) {
                is DataErrors.Validation ->
                    generateDataErrorResponse(id = id, version = version, dataError = fail)
                is ValidationError ->
                    generateValidationErrorResponse(id = id, version = version, validationError = fail)
                else -> generateErrorResponse(id = id, version = version, error = fail)
            }
        }
        is Fail.Incident -> generateIncidentResponse(id = id, version = version, incident = fail)
    }
}

private fun generateDataErrorResponse(
    dataError: DataErrors.Validation, version: ApiVersion2, id: UUID
) =
    ApiErrorResponse2(
        version = version,
        id = id,
        result = listOf(
            ApiErrorResponse2.Error(
                code = getFullErrorCode(dataError.code),
                description = dataError.description,
                details = ApiErrorResponse2.Error.Detail.tryCreateOrNull(name = dataError.name).toListOrEmpty()
            )
        )
    )

private fun generateValidationErrorResponse(
    validationError: ValidationError, version: ApiVersion2, id: UUID
) =
    ApiErrorResponse2(
        version = version,
        id = id,
        result = listOf(
            ApiErrorResponse2.Error(
                code = getFullErrorCode(validationError.code),
                description = validationError.description,
                details = ApiErrorResponse2.Error.Detail.tryCreateOrNull(id = validationError.id).toListOrEmpty()

            )
        )
    )

private fun generateErrorResponse(version: ApiVersion2, id: UUID, error: Fail.Error) =
    ApiErrorResponse2(
        version = version,
        id = id,
        result = listOf(
            ApiErrorResponse2.Error(
                code = getFullErrorCode(error.code),
                description = error.description
            )
        )
    )

private fun generateIncidentResponse(incident: Fail.Incident, version: ApiVersion2, id: UUID) =
    ApiIncidentResponse2(
        version = version,
        id = id,
        result = ApiIncidentResponse2.Incident(
            date = nowDefaultUTC(),
            id = UUID.randomUUID(),
            service = ApiIncidentResponse2.Incident.Service(
                id = GlobalProperties2.service.id,
                version = GlobalProperties2.service.version,
                name = GlobalProperties2.service.name
            ),
            details = listOf(
                ApiIncidentResponse2.Incident.Details(
                    code = getFullErrorCode(incident.code),
                    description = incident.description,
                    metadata = null
                )
            )
        )
    )

fun getFullErrorCode(code: String): String = "${code}/${GlobalProperties2.service.id}"

val NaN: UUID
    get() = UUID(0, 0)

fun JsonNode.tryGetVersion(): Result<ApiVersion2, DataErrors> {
    val name = "version"
    return tryGetTextAttribute(name).bind {
        when (val result = ApiVersion2.tryValueOf(it)) {
            is Result.Success -> result
            is Result.Failure -> Result.failure(
                DataErrors.Validation.DataFormatMismatch(
                    name = name,
                    expectedFormat = "00.00.00",
                    actualValue = it
                )
            )
        }
    }
}

fun JsonNode.tryGetAction(): Result<Command2Type, DataErrors> =
    tryGetAttributeAsEnum("action", Command2Type)

fun <T : Any> JsonNode.tryGetParams(target: Class<T>): Result<T, Fail.Error> {
    val name = "params"
    return tryGetAttribute(name).bind {
        when (val result = it.tryToObject(target)) {
            is Result.Success -> result
            is Result.Failure -> Result.failure(
                BadRequest("Error parsing '$name'", result.error.exception)
            )
        }
    }
}

fun JsonNode.tryGetId(): Result<UUID, DataErrors> {
    val name = "id"
    return tryGetTextAttribute(name)
        .bind {
            when (val result = it.tryUUID()) {
                is Result.Success -> result
                is Result.Failure -> Result.failure(
                    DataErrors.Validation.DataFormatMismatch(
                        name = name,
                        actualValue = it,
                        expectedFormat = "uuid"
                    )
                )
            }
        }
}

fun String.tryGetNode(): Result<JsonNode, BadRequest> =
    when (val result = this.tryToNode()) {
        is Result.Success -> result
        is Result.Failure -> Result.failure(BadRequest(exception = result.error.exception))
    }

