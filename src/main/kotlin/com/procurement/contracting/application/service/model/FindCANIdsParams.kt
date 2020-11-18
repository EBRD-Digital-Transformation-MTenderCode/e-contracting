package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.functional.Result
import com.procurement.contracting.domain.functional.asFailure
import com.procurement.contracting.domain.functional.asSuccess
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.parseCANStatus
import com.procurement.contracting.domain.model.parseCANStatusDetails
import com.procurement.contracting.domain.model.parseCpid
import com.procurement.contracting.domain.model.parseLotId
import com.procurement.contracting.domain.model.parseOcid
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.domain.util.extension.mapResult
import com.procurement.contracting.infrastructure.fail.error.DataErrors

class FindCANIdsParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val states: List<State>,
    val lotIds: List<LotId>
) {
    companion object {

        private const val STATES_ATTRIBUTE_NAME = "states"
        private const val LOT_IDS_ATTRIBUTE_NAME = "lotIds"

        fun tryCreate(
            cpid: String,
            ocid: String,
            states: List<State>?,
            lotIds: List<String>?
        ): Result<FindCANIdsParams, DataErrors> {
            val cpidParsed = parseCpid(value = cpid)
                .orForwardFail { error -> return error }

            val ocidParsed = parseOcid(value = ocid)
                .orForwardFail { error -> return error }

            if (states != null && states.isEmpty())
                return DataErrors.Validation.EmptyArray(name = STATES_ATTRIBUTE_NAME).asFailure()

            if (lotIds != null && lotIds.isEmpty())
                return DataErrors.Validation.EmptyArray(name = LOT_IDS_ATTRIBUTE_NAME).asFailure()

            val lotIdsParsed = lotIds
                ?.mapResult { lotId ->
                    parseLotId(value = lotId, attributeName = LOT_IDS_ATTRIBUTE_NAME)
                }
                ?.orForwardFail { error -> return error }
                ?: emptyList()

            val duplicateIds = lotIdsParsed
                .groupingBy { it }
                .eachCount()
                .filter { it.value > 1 }
                .map { it.key }

            if (duplicateIds.isNotEmpty())
                return DataErrors.Validation.UniquenessDataMismatch(
                    value = duplicateIds.joinToString(), name = LOT_IDS_ATTRIBUTE_NAME
                ).asFailure()

            return FindCANIdsParams(
                cpid = cpidParsed,
                ocid = ocidParsed,
                lotIds = lotIdsParsed,
                states = states ?: emptyList()
            ).asSuccess()
        }
    }

    class State private constructor(
        val status: CANStatus?,
        val statusDetails: CANStatusDetails?
    ) : Comparable<State> {
        companion object {

            private const val STATUS_ATTRIBUTE_NAME = "status"
            private const val STATUS_DETAILS_ATTRIBUTE_NAME = "statusDetails"

            val allowedStatuses = CANStatus.allowedElements
                .filter { value ->
                    when (value) {
                        CANStatus.ACTIVE,
                        CANStatus.CANCELLED,
                        CANStatus.PENDING,
                        CANStatus.UNSUCCESSFUL -> true
                    }
                }.toSet()

            val allowedStatusDetails = CANStatusDetails.allowedElements
                .filter { value ->
                    when (value) {
                        CANStatusDetails.ACTIVE,
                        CANStatusDetails.CONTRACT_PROJECT,
                        CANStatusDetails.EMPTY,
                        CANStatusDetails.UNSUCCESSFUL -> true
                        CANStatusDetails.TREASURY_REJECTION -> false
                    }
                }.toSet()

            fun tryCreate(
                status: String?,
                statusDetails: String?
            ): Result<State, DataErrors> {
                if (status == null && statusDetails == null)
                    return DataErrors.Validation.EmptyObject(name = STATES_ATTRIBUTE_NAME).asFailure()

                val statusParsed = status?.let {
                    parseCANStatus(
                        status = it, allowedStatuses = allowedStatuses, attributeName = STATUS_ATTRIBUTE_NAME
                    ).orForwardFail { error -> return error }
                }

                val statusDetailsParsed = statusDetails?.let {
                    parseCANStatusDetails(
                        statusDetails = it,
                        allowedStatuses = allowedStatusDetails,
                        attributeName = STATUS_DETAILS_ATTRIBUTE_NAME
                    ).orForwardFail { error -> return error }
                }

                return State(
                    status = statusParsed,
                    statusDetails = statusDetailsParsed
                ).asSuccess()
            }
        }

        override fun compareTo(other: State): Int {
            val result = compareStatus(status, other.status)
            return if (result == 0) {
                compareStatusDetails(statusDetails, other.statusDetails)
            } else
                result
        }

        private fun compareStatus(thisStatus: CANStatus?, otherStatus: CANStatus?): Int {
            return if (thisStatus != null) {
                if (otherStatus != null) {
                    thisStatus.key.compareTo(otherStatus.key)
                } else {
                    -1
                }
            } else {
                if (otherStatus != null) {
                    1
                } else {
                    0
                }
            }
        }

        private fun compareStatusDetails(
            thisStatusDetails: CANStatusDetails?,
            otherStatusDetail: CANStatusDetails?
        ): Int {
            return if (thisStatusDetails != null) {
                if (otherStatusDetail != null) {
                    thisStatusDetails.key.compareTo(otherStatusDetail.key)
                } else {
                    -1
                }
            } else {
                if (otherStatusDetail != null) {
                    1
                } else {
                    0
                }
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as State

            if (status != other.status) return false
            if (statusDetails != other.statusDetails) return false

            return true
        }

        override fun hashCode(): Int {
            var result = status?.hashCode() ?: 0
            result = 31 * result + (statusDetails?.hashCode() ?: 0)
            return result
        }
    }
}