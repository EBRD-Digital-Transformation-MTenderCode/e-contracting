package com.procurement.contracting.domain.model

import com.procurement.contracting.domain.model.EnumElementProvider.Companion.keysAsStrings
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.lot.tryLotId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess

fun parseCpid(value: String): Result<Cpid, DataErrors.Validation.DataMismatchToPattern> =
    Cpid.tryCreateOrNull(value = value)
        ?.asSuccess()
        ?: Result.failure(
            DataErrors.Validation.DataMismatchToPattern(
                name = "cpid",
                pattern = Cpid.pattern,
                actualValue = value
            )
        )

fun parseOcid(value: String): Result<Ocid, DataErrors.Validation.DataMismatchToPattern> =
    Ocid.tryCreateOrNull(value = value)
        ?.asSuccess()
        ?: Result.failure(
            DataErrors.Validation.DataMismatchToPattern(
                name = "ocid",
                pattern = Ocid.pattern,
                actualValue = value
            )
        )

fun parseLotId(value: String, attributeName: String): Result<LotId, DataErrors.Validation.DataFormatMismatch> =
    value.tryLotId()
        .doReturn {
            return Result.failure(
                DataErrors.Validation.DataFormatMismatch(
                    name = attributeName,
                    expectedFormat = "uuid",
                    actualValue = value
                )
            )
        }
        .asSuccess()

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

private fun <T> parseEnum(
    value: String, allowedEnums: Set<T>, attributeName: String, target: EnumElementProvider<T>
): Result<T, DataErrors.Validation.UnknownValue> where T : Enum<T>,
                                                       T : EnumElementProvider.Key =
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