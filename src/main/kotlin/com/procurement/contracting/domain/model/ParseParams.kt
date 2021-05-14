package com.procurement.contracting.domain.model

import com.procurement.contracting.domain.model.EnumElementProvider.Companion.keysAsStrings
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.bid.BidId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.domain.util.extension.toLocalDateTime
import com.procurement.contracting.domain.util.extension.tryUUID
import com.procurement.contracting.extension.UUID_PATTERN
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.infrastructure.fail.error.DataTimeError
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess
import java.time.LocalDateTime

fun parseCpid(value: String): Result<Cpid, DataErrors.Validation.DataMismatchToPattern> =
    Cpid.orNull(value = value)
        ?.asSuccess()
        ?: Result.failure(
            DataErrors.Validation.DataMismatchToPattern(
                name = "cpid",
                pattern = Cpid.pattern,
                actualValue = value
            )
        )

fun parseOcid(value: String): Result<Ocid, DataErrors.Validation.DataMismatchToPattern> =
    Ocid.orNull(value = value)
        ?.asSuccess()
        ?: Result.failure(
            DataErrors.Validation.DataMismatchToPattern(
                name = "ocid",
                pattern = Ocid.pattern,
                actualValue = value
            )
        )

fun parseLotId(value: String, attributeName: String): Result<LotId, DataErrors.Validation.DataFormatMismatch> =
    LotId.orNull(value)
        ?.asSuccess()
        ?: DataErrors.Validation.DataFormatMismatch(
            name = attributeName,
            expectedFormat = LotId.pattern,
            actualValue = value
        ).asFailure()

fun parseBidId(value: String, attributeName: String): Result<BidId, DataErrors.Validation.DataFormatMismatch> =
    value.tryUUID()
        .mapFailure {
            DataErrors.Validation.DataFormatMismatch(
                name = attributeName,
                expectedFormat = UUID_PATTERN,
                actualValue = value
            )
        }

fun parseAwardId(value: String, attributeName: String): Result<AwardId, DataErrors.Validation.DataFormatMismatch> =
    AwardId.orNull(value)
        ?.asSuccess()
        ?: DataErrors.Validation.DataFormatMismatch(
            name = attributeName,
            expectedFormat = AwardId.pattern,
            actualValue = value
        ).asFailure()


fun parseIdFC(value: String, attributeName: String): Result<FrameworkContractId, DataErrors.Validation.DataFormatMismatch> =
    FrameworkContractId.orNull(value)
        ?.asSuccess()
        ?: DataErrors.Validation.DataFormatMismatch(
            name = attributeName,
            expectedFormat = LotId.pattern,
            actualValue = value
        ).asFailure()

fun parseCANStatus(
    status: String, allowedStatuses: Set<CANStatus>, attributeName: String
): Result<CANStatus, DataErrors.Validation.UnknownValue> =
    parseEnum(value = status, allowedEnums = allowedStatuses, attributeName = attributeName, target = CANStatus)

fun parseCANStatusDetails(
    statusDetails: String, allowedStatuses: Set<CANStatusDetails>, attributeName: String
): Result<CANStatusDetails, DataErrors.Validation.UnknownValue> =
    parseEnum(
        value = statusDetails, allowedEnums = allowedStatuses, attributeName = attributeName, target = CANStatusDetails
    )

fun <T> parseEnum(
    value: String, allowedEnums: Set<T>, attributeName: String, target: EnumElementProvider<T>
): Result<T, DataErrors.Validation.UnknownValue> where T : Enum<T>,
                                                       T : EnumElementProvider.Element =
    target.orNull(value)
        ?.takeIf { it in allowedEnums }
        ?.asSuccess()
        ?: Result.failure(
            DataErrors.Validation.UnknownValue(
                name = attributeName,
                expectedValues = allowedEnums.keysAsStrings(),
                actualValue = value
            )
        )

fun parseDate(value: String, attributeName: String = "date"): Result<LocalDateTime, DataErrors.Validation> =
    value.toLocalDateTime()
        .mapFailure { fail ->
            when (fail) {
                is DataTimeError.InvalidFormat -> DataErrors.Validation.DataFormatMismatch(
                    name = attributeName,
                    actualValue = value,
                    expectedFormat = fail.pattern
                )

                is DataTimeError.InvalidDateTime ->
                    DataErrors.Validation.InvalidDateTime(name = attributeName, actualValue = value)
            }
        }

fun parseOwner(value: String): Result<Owner, DataErrors.Validation.DataFormatMismatch> =
    Owner.orNull(value)
        ?.asSuccess()
        ?: DataErrors.Validation.DataFormatMismatch(
                    name = "owner",
                    actualValue = value,
                    expectedFormat = "uuid"
                ).asFailure()

fun parseToken(value: String): Result<Token, DataErrors.Validation.DataFormatMismatch> =
    Token.orNull(value)
        ?.asSuccess()
        ?: DataErrors.Validation.DataFormatMismatch(
            name = "token",
            actualValue = value,
            expectedFormat = "uuid"
        ).asFailure()
