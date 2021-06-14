package com.procurement.contracting.domain.model

import com.procurement.contracting.domain.model.EnumElementProvider.Companion.keysAsStrings
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.bid.BidId
import com.procurement.contracting.domain.model.bid.BusinessFunctionType
import com.procurement.contracting.domain.model.bid.PersonTitle
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.document.type.DocumentTypeAward
import com.procurement.contracting.domain.model.document.type.DocumentTypeBF
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.item.ItemId
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.organization.OrganizationRole
import com.procurement.contracting.domain.model.organization.Scale
import com.procurement.contracting.domain.model.organization.TypeOfSupplier
import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.domain.model.process.ProcessInitiator
import com.procurement.contracting.domain.util.extension.toLocalDateTime
import com.procurement.contracting.domain.util.extension.tryUUID
import com.procurement.contracting.extension.UUID_PATTERN
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.infrastructure.fail.error.DataTimeError
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess
import com.procurement.contracting.model.dto.ocds.PersonId
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

fun parseFCId(value: String, attributeName: String): Result<FrameworkContractId, DataErrors.Validation.DataFormatMismatch> =
    FrameworkContractId.orNull(value)
        ?.asSuccess()
        ?: DataErrors.Validation.DataFormatMismatch(
            name = attributeName,
            expectedFormat = FrameworkContractId.pattern,
            actualValue = value
        ).asFailure()

fun parsePACId(value: String, attributeName: String): Result<PacId, DataErrors.Validation.DataFormatMismatch> =
    PacId.orNull(value)
        ?.asSuccess()
        ?: DataErrors.Validation.DataFormatMismatch(
            name = attributeName,
            expectedFormat = PacId.pattern,
            actualValue = value
        ).asFailure()

fun parseCANId(value: String, attributeName: String): Result<CANId, DataErrors.Validation.DataFormatMismatch> =
    CANId.orNull(value)
        ?.asSuccess()
        ?: DataErrors.Validation.DataFormatMismatch(
            name = attributeName,
            expectedFormat = CANId.pattern,
            actualValue = value
        ).asFailure()

fun parsePersonId(value: String, attributeName: String): Result<PersonId, DataErrors.Validation.DataFormatMismatch> =
    PersonId.parse(value)
        ?.asSuccess()
        ?: DataErrors.Validation.DataFormatMismatch(
            name = attributeName,
            expectedFormat = "string",
            actualValue = value
        ).asFailure()

fun parseItemId(value: String, attributeName: String): Result<ItemId, DataErrors.Validation.DataFormatMismatch> =
    value.tryUUID()
        .mapFailure {
            DataErrors.Validation.DataFormatMismatch(
                name = attributeName,
                expectedFormat = UUID_PATTERN,
                actualValue = value
            )
        }

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

fun parsePmd(
    value: String, allowedValues: Set<ProcurementMethodDetails>
): Result<ProcurementMethodDetails, DataErrors.Validation.UnknownValue> =
    parseEnum(
        value = value,
        allowedEnums = allowedValues,
        attributeName = "pmd",
        target = ProcurementMethodDetails
    )

fun parseOperationType(
    value: String, allowedValues: Set<OperationType>
): Result<OperationType, DataErrors.Validation.UnknownValue> =
    parseEnum(value = value, allowedEnums = allowedValues, attributeName = "operationType", target = OperationType)

fun parseProcessInitiator(
    statusDetails: String, allowedStatuses: Set<ProcessInitiator>
): Result<ProcessInitiator, DataErrors.Validation.UnknownValue> =
    parseEnum(
        value = statusDetails, allowedEnums = allowedStatuses, attributeName = "processInitiator", target = ProcessInitiator
    )

fun parseAwardDocumentType(
    value: String, allowedValues: Set<DocumentTypeAward>, attributeName: String
): Result<DocumentTypeAward, DataErrors.Validation.UnknownValue> =
    parseEnum(value = value, allowedEnums = allowedValues, attributeName = attributeName, target = DocumentTypeAward)

fun parseBFDocumentType(
    value: String, allowedValues: Set<DocumentTypeBF>, attributeName: String
): Result<DocumentTypeBF, DataErrors.Validation.UnknownValue> =
    parseEnum(value = value, allowedEnums = allowedValues, attributeName = attributeName, target = DocumentTypeBF)

fun parseOrganizationRole(
    value: String, allowedValues: Set<OrganizationRole>, attributeName: String
): Result<OrganizationRole, DataErrors.Validation.UnknownValue> =
    parseEnum(value = value, allowedEnums = allowedValues, attributeName = attributeName, target = OrganizationRole)

fun parseTypeOfSupplier(
    value: String, allowedValues: Set<TypeOfSupplier>, attributeName: String
): Result<TypeOfSupplier, DataErrors.Validation.UnknownValue> =
    parseEnum(value = value, allowedEnums = allowedValues, attributeName = attributeName, target = TypeOfSupplier)

fun parseScale(
    value: String, allowedValues: Set<Scale>, attributeName: String
): Result<Scale, DataErrors.Validation.UnknownValue> =
    parseEnum(value = value, allowedEnums = allowedValues, attributeName = attributeName, target = Scale)

fun parsePersonTitle(
    value: String, allowedValues: Set<PersonTitle>, attributeName: String
): Result<PersonTitle, DataErrors.Validation.UnknownValue> =
    parseEnum(value = value, allowedEnums = allowedValues, attributeName = attributeName, target = PersonTitle)

fun parseBusinessFunctionType(
    value: String, allowedValues: Set<BusinessFunctionType>, attributeName: String
): Result<BusinessFunctionType, DataErrors.Validation.UnknownValue> =
    parseEnum(value = value, allowedEnums = allowedValues, attributeName = attributeName, target = BusinessFunctionType)

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
