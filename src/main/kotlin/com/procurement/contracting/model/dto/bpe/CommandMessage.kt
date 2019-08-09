package com.procurement.contracting.model.dto.bpe

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.procurement.contracting.exception.EnumException
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.utils.toLocalDateTime
import java.time.LocalDateTime
import java.util.*

data class CommandMessage @JsonCreator constructor(

        val id: String,

        val command: CommandType,

        val context: Context,

        val data: JsonNode,

        val version: ApiVersion
)

val CommandMessage.cpid: String
    get() = this.context.cpid
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'cpid' attribute in context.")

val CommandMessage.ocid: String
    get() = this.context.ocid
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'ocid' attribute in context.")

val CommandMessage.startDate: LocalDateTime
    get() = this.context.startDate?.toLocalDateTime()
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'startDate' attribute in context.")

val CommandMessage.owner: String
    get() = this.context.owner
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'owner' attribute in context.")

val CommandMessage.lotId: UUID
    get() = this.context.id
        ?.let { id ->
            try {
                UUID.fromString(id)
            } catch (exception: Exception) {
                throw ErrorException(error = ErrorType.INVALID_FORMAT_LOT_ID)
            }
        }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'lotId' attribute in context.")

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

enum class CommandType(private val value: String) {

    CHECK_CAN("checkCan"),
    CHECK_CAN_BY_AWARD("checkCanBiAwardId"),
    CREATE_CAN("createCan"),
    GET_CANS("getCans"),
    UPDATE_CAN_DOCS("updateCanDocs"),
    CANCEL_CAN("cancelCan"),
    CONFIRMATION_CAN("confirmationCan"),
    CREATE_AC("createAC"),
    UPDATE_AC("updateAC"),
    GET_BUDGET_SOURCES("getActualBudgetSources"),
    CHECK_STATUS_DETAILS("contractingCheckStatusDetails"),
    GET_RELATED_BID_ID("getRelatedBidId"),
    ISSUING_AC("issuingAC"),
    FINAL_UPDATE("finalUpdateAC"),
    BUYER_SIGNING_AC("buyerSigningAC"),
    SUPPLIER_SIGNING_AC("supplierSigningAC"),
    VERIFICATION_AC("verificationAC"),
    TREASURY_RESPONSE_PROCESSING("treasuryResponseProcessing"),
    ACTIVATION_AC("activationAC");

    @JsonValue
    fun value(): String {
        return this.value
    }

    override fun toString(): String {
        return this.value
    }
}

enum class ApiVersion(private val value: String) {
    V_0_0_1("0.0.1");

    @JsonValue
    fun value(): String {
        return this.value
    }

    override fun toString(): String {
        return this.value
    }
}


@JsonInclude(JsonInclude.Include.NON_NULL)
data class ResponseDto(

        val errors: List<ResponseErrorDto>? = null,

        val data: Any? = null,

        val id: String? = null
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ResponseErrorDto(

        val code: String,

        val description: String?
)

fun getExceptionResponseDto(exception: Exception): ResponseDto {
    return ResponseDto(
            errors = listOf(ResponseErrorDto(
                    code = "400.09.00",
                    description = exception.message
            )))
}

fun getErrorExceptionResponseDto(exception: ErrorException, id: String? = null): ResponseDto {
    return ResponseDto(
            errors = listOf(ResponseErrorDto(
                    code = "400.09." + exception.error.code,
                    description = exception.message
            )),
            id = id)
}

fun getEnumExceptionResponseDto(error: EnumException, id: String? = null): ResponseDto {
    return ResponseDto(
            errors = listOf(ResponseErrorDto(
                    code = "400.09." + error.code,
                    description = error.msg
            )),
            id = id)
}

