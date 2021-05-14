package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.bid.BusinessFunctionType
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestId
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationResponseId
import com.procurement.contracting.domain.model.confirmation.response.ConfirmationResponseType
import com.procurement.contracting.domain.model.document.type.DocumentTypeBF
import com.procurement.contracting.domain.model.parseCpid
import com.procurement.contracting.domain.model.parseDate
import com.procurement.contracting.domain.model.parseEnum
import com.procurement.contracting.domain.model.parseOcid
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess
import java.time.LocalDateTime

class CreateConfirmationResponseParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val date: LocalDateTime,
    val contracts: List<Contract>
) {
    companion object {

        fun tryCreate(
            cpid: String,
            ocid: String,
            date: String,
            contracts: List<Contract>
        ): Result<CreateConfirmationResponseParams, DataErrors> {
            val parsedCpid = parseCpid(value = cpid).onFailure { return it }
            val parsedOcid = parseOcid(value = ocid).onFailure { return it }
            val parsedDate = parseDate(value = date).onFailure { return it }

            if (contracts.isEmpty())
                return DataErrors.Validation.EmptyArray(name = "contracts").asFailure()

            return CreateConfirmationResponseParams(
                cpid = parsedCpid,
                ocid = parsedOcid,
                date = parsedDate,
                contracts = contracts
            ).asSuccess()
        }
    }

    class Contract private constructor(
        val id: String,
        val confirmationResponses: List<ConfirmationResponse>
    ) {
        companion object {
            fun tryCreate(id: String, confirmationResponses: List<ConfirmationResponse>): Result<Contract, DataErrors> {
                if (confirmationResponses.isEmpty())
                    return DataErrors.Validation.EmptyArray(name = "confirmationResponses").asFailure()

                return Contract(id = id, confirmationResponses = confirmationResponses).asSuccess()
            }
        }

        class ConfirmationResponse private constructor(
            val id: ConfirmationResponseId,
            val requestId: ConfirmationRequestId,
            val type: ConfirmationResponseType,
            val value: String,
            val relatedPerson: Person,
        ) {
            companion object {
                private val allowedConfirmationResponseTypes = ConfirmationResponseType.allowedElements
                    .filter { value ->
                        when (value) {
                            ConfirmationResponseType.DOCUMENT,
                            ConfirmationResponseType.HASH -> true

                            ConfirmationResponseType.CODE -> false
                        }
                    }.toSet()

                fun tryCreate(
                    id: String,
                    requestId: String,
                    type: String,
                    value: String,
                    relatedPerson: Person,
                ): Result<ConfirmationResponse, DataErrors.Validation> {
                    val parsedRequestId = ConfirmationRequestId.orNull(requestId)
                        ?: return DataErrors.Validation.DataMismatchToPattern(
                            name = "requestId", pattern = ConfirmationRequestId.pattern, actualValue = requestId
                        ).asFailure()

                    val parsedType = parseEnum(type, allowedConfirmationResponseTypes, "type", ConfirmationResponseType)
                        .onFailure { return it.reason.asFailure() }

                    return ConfirmationResponse(
                        id = id,
                        requestId = parsedRequestId,
                        type = parsedType,
                        value = value,
                        relatedPerson = relatedPerson
                    ).asSuccess()
                }
            }

            class Person private constructor(
                val id: String,
                val title: String,
                val name: String,
                val identifier: Identifier,
                val businessFunctions: List<BusinessFunction>,
            ) {
                companion object {
                    fun tryCreate(
                        id: String,
                        title: String,
                        name: String,
                        identifier: Identifier,
                        businessFunctions: List<BusinessFunction>,
                    ): Result<Person, DataErrors> {
                        if (businessFunctions.isEmpty())
                            return DataErrors.Validation.EmptyArray(name = "businessFunctions").asFailure()

                        return Person(
                            id = id,
                            title = title,
                            name = name,
                            identifier = identifier,
                            businessFunctions = businessFunctions
                        ).asSuccess()
                    }
                }

                data class Identifier(
                    val id: String,
                    val scheme: String,
                    val uri: String?,
                )

                class BusinessFunction private constructor(
                    val id: String,
                    val type: BusinessFunctionType,
                    val jobTitle: String,
                    val period: Period,
                    val documents: List<Document>,
                ) {
                    companion object {
                        private val allowedBusinessFunctionTypes = BusinessFunctionType.allowedElements
                            .filter { value ->
                                when (value) {
                                    BusinessFunctionType.AUTHORITY,
                                    BusinessFunctionType.CHAIRMAN,
                                    BusinessFunctionType.CONTACT_POINT,
                                    BusinessFunctionType.PRICE_EVALUATOR,
                                    BusinessFunctionType.PRICE_OPENER,
                                    BusinessFunctionType.PROCUREMENT_OFFICER,
                                    BusinessFunctionType.TECHNICAL_EVALUATOR,
                                    BusinessFunctionType.TECHNICAL_OPENER -> true
                                }
                            }.toSet()

                        fun tryCreate(
                            id: String,
                            type: String,
                            jobTitle: String,
                            period: Period,
                            documents: List<Document>?,
                        ): Result<BusinessFunction, DataErrors> {
                            if (documents != null && documents.isEmpty())
                                return DataErrors.Validation.EmptyArray(name = "documents").asFailure()

                            val parsedType = parseEnum(type, allowedBusinessFunctionTypes, "type", BusinessFunctionType)
                                .onFailure { return it }

                            return BusinessFunction(
                                id = id,
                                type = parsedType,
                                jobTitle = jobTitle,
                                period = period,
                                documents = documents.orEmpty(),
                            ).asSuccess()
                        }
                    }

                    class Period private constructor(
                        val startDate: LocalDateTime
                    ) {
                        companion object {
                            fun tryCreate(startDate: String): Result<Period, DataErrors.Validation> {
                                val parsedDate = parseDate(value = startDate, "startDate").onFailure { return it }

                                return Period(startDate = parsedDate).asSuccess()
                            }
                        }
                    }

                    class Document private constructor(
                        val id: String,
                        val documentType: DocumentTypeBF,
                        val title: String,
                        val description: String?,
                    ) {
                        companion object {
                            private val allowedDocumentTypes = DocumentTypeBF.allowedElements
                                .filter { value ->
                                    when (value) {
                                        DocumentTypeBF.REGULATORY_DOCUMENT -> true
                                    }
                                }.toSet()

                            fun tryCreate(
                                id: String,
                                documentType: String,
                                title: String,
                                description: String?,
                            ): Result<Document, DataErrors.Validation> {
                                val parsedType = parseEnum(documentType, allowedDocumentTypes, "documentType", DocumentTypeBF)
                                        .onFailure { return it }

                                return Document(
                                    id = id,
                                    documentType = parsedType,
                                    title = title,
                                    description = description
                                ).asSuccess()
                            }
                        }
                    }

                }

            }

        }

    }
}