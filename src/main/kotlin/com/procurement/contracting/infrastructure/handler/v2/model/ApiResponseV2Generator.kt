package com.procurement.contracting.infrastructure.handler.v2.model

import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.domain.util.extension.nowDefaultUTC
import com.procurement.contracting.domain.util.extension.toListOrEmpty
import com.procurement.contracting.infrastructure.api.ApiVersion
import com.procurement.contracting.infrastructure.api.command.id.CommandId
import com.procurement.contracting.infrastructure.api.v2.ApiResponseV2
import com.procurement.contracting.infrastructure.configuration.properties.GlobalProperties
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.infrastructure.fail.error.ValidationError
import java.util.*

object ApiResponseV2Generator {

    fun generateResponseOnFailure(
        fail: Fail,
        version: ApiVersion,
        id: CommandId,
        logger: Logger
    ): ApiResponseV2 {
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
        dataError: DataErrors.Validation,
        version: ApiVersion,
        id: CommandId
    ) =
        ApiResponseV2.Error(
            version = version,
            id = id,
            result = listOf(
                ApiResponseV2.Error.Result(
                    code = getFullErrorCode(dataError.code),
                    description = dataError.description,
                    details = ApiResponseV2.Error.Result.Detail.tryCreateOrNull(name = dataError.name).toListOrEmpty()
                )
            )
        )

    private fun generateValidationErrorResponse(
        validationError: ValidationError,
        version: ApiVersion,
        id: CommandId
    ) =
        ApiResponseV2.Error(
            version = version,
            id = id,
            result = listOf(
                ApiResponseV2.Error.Result(
                    code = getFullErrorCode(validationError.code),
                    description = validationError.description,
                    details = ApiResponseV2.Error.Result.Detail.tryCreateOrNull(id = validationError.id).toListOrEmpty()

                )
            )
        )

    private fun generateErrorResponse(version: ApiVersion, id: CommandId, error: Fail.Error) =
        ApiResponseV2.Error(
            version = version,
            id = id,
            result = listOf(
                ApiResponseV2.Error.Result(
                    code = getFullErrorCode(error.code),
                    description = error.description
                )
            )
        )

    private fun generateIncidentResponse(incident: Fail.Incident, version: ApiVersion, id: CommandId) =
        ApiResponseV2.Incident(
            version = version,
            id = id,
            result = ApiResponseV2.Incident.Result(
                date = nowDefaultUTC(),
                id = UUID.randomUUID().toString(),
                level = incident.level,
                service = ApiResponseV2.Incident.Result.Service(
                    id = GlobalProperties.service.id,
                    version = GlobalProperties.service.version,
                    name = GlobalProperties.service.name
                ),
                details = listOf(
                    ApiResponseV2.Incident.Result.Detail(
                        code = getFullErrorCode(incident.code),
                        description = incident.description,
                        metadata = null
                    )
                )
            )
        )

    fun getFullErrorCode(code: String): String = "${code}/${GlobalProperties.service.id}"
}
