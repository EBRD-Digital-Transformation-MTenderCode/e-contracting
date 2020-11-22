package com.procurement.contracting.infrastructure.handler.v1

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.procurement.contracting.domain.model.MainProcurementCategory
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.ProcurementMethod
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.domain.util.extension.toLocalDateTime
import com.procurement.contracting.exception.EnumException
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.infrastructure.api.Action
import com.procurement.contracting.infrastructure.api.ApiVersion
import com.procurement.contracting.infrastructure.api.command.id.CommandId
import com.procurement.contracting.infrastructure.api.v1.ApiResponseV1
import com.procurement.contracting.infrastructure.api.v1.CommandTypeV1
import com.procurement.contracting.infrastructure.configuration.properties.GlobalProperties
import java.time.LocalDateTime

data class CommandMessage @JsonCreator constructor(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: CommandId,
    @field:JsonProperty("command") @param:JsonProperty("command") val command: CommandTypeV1,
    @field:JsonProperty("context") @param:JsonProperty("context") val context: Context,
    @field:JsonProperty("data") @param:JsonProperty("data") val data: JsonNode,
    @field:JsonProperty("version") @param:JsonProperty("version") val version: ApiVersion
)

val CommandMessage.commandId: CommandId
    get() = this.id

val CommandMessage.action: Action
    get() = this.command

val CommandMessage.cpid: Cpid
    get() = this.context.cpid
        ?.let {
            Cpid.orNull(it)
                ?: throw ErrorException(
                    error = ErrorType.INVALID_ATTRIBUTE,
                    message = "Cannot parse 'cpid' attribute '${it}'."
                )
        }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'cpid' attribute in context.")

val CommandMessage.ocid: Ocid
    get() = this.context.ocid
        ?.let {
            Ocid.orNull(it)
                ?: throw ErrorException(
                    error = ErrorType.INVALID_ATTRIBUTE,
                    message = "Cannot parse 'ocid' attribute '${it}'."
                )
        }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'ocid' attribute in context.")

val CommandMessage.token: Token
    get() = this.context.token
        ?.let {
            Token.orNull(it)
                ?: throw ErrorException(
                    error = ErrorType.INVALID_ATTRIBUTE,
                    message = "Cannot parse 'token' attribute '${it}'."
                )
        }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'token' attribute in context.")

val CommandMessage.owner: Owner
    get() = this.context.owner
        ?.let {
            Owner.orNull(it)
                ?: throw ErrorException(
                    error = ErrorType.INVALID_ATTRIBUTE,
                    message = "Cannot parse 'owner' attribute '${it}'."
                )
        }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'owner' attribute in context.")

val CommandMessage.startDate: LocalDateTime
    get() = this.context.startDate
        ?.toLocalDateTime()
        ?.orThrow { it.reason }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'startDate' attribute in context.")

val CommandMessage.pmd: ProcurementMethod
    get() = this.context.pmd?.let { ProcurementMethod.fromString(it) }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'pmd' attribute in context.")

val CommandMessage.mainProcurementCategory: MainProcurementCategory
    get() = this.context.mainProcurementCategory
        ?.let { value ->
            MainProcurementCategory.creator(value)
        }
        ?: throw ErrorException(
            error = ErrorType.CONTEXT,
            message = "Missing the 'mainProcurementCategory' attribute in context."
        )

val CommandMessage.lotId: LotId
    get() = this.context.id
        ?.let { value ->
            LotId.orNull(value)
                ?: throw ErrorException(error = ErrorType.INVALID_FORMAT_OF_ATTRIBUTE)
        }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'id' attribute in context.")

val CommandMessage.canId: CANId
    get() = this.context.id
        ?.let { value ->
            CANId.orNull(value)
                ?: throw ErrorException(error = ErrorType.INVALID_FORMAT_OF_ATTRIBUTE)
        }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'id' attribute in context.")

val CommandMessage.country: String
    get() = this.context.country
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'country' attribute in context.")

val CommandMessage.language: String
    get() = this.context.language
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'language' attribute in context.")

data class Context @JsonCreator constructor(
    val operationId: String,
    val requestId: String?,
    val cpid: String?,
    val ocid: String?,
    val stage: String?,
    val prevStage: String?,
    val processType: String?,
    val operationType: String?,
    val phase: String?,
    val owner: String?,
    val country: String?,
    val language: String?,
    val pmd: String?,
    val token: String?,
    val startDate: String?,
    val endDate: String?,
    val id: String?,
    val mainProcurementCategory: String?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ResponseErrorDto(

    val code: String,

    val description: String?
)

fun errorResponse(exception: Exception, id: CommandId, version: ApiVersion): ApiResponseV1.Failure =
    when (exception) {
        is ErrorException -> getApiErrorResponse(
            id = id,
            version = version,
            code = exception.code,
            message = exception.message!!
        )
        is EnumException -> getApiErrorResponse(
            id = id,
            version = version,
            code = exception.code,
            message = exception.message!!
        )
        else -> getApiErrorResponse(
            id = id,
            version = version,
            code = "00.00",
            message = exception.message ?: "Internal server error."
        )
    }

private fun getApiErrorResponse(
    id: CommandId,
    version: ApiVersion,
    code: String,
    message: String
): ApiResponseV1.Failure {
    return ApiResponseV1.Failure(
        errors = listOf(
            ApiResponseV1.Failure.Error(
                code = "400.${GlobalProperties.service.id}." + code,
                description = message
            )
        ),
        id = id,
        version = version
    )
}
