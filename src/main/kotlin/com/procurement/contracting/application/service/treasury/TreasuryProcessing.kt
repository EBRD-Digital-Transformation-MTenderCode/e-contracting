package com.procurement.contracting.application.service.treasury

import com.procurement.contracting.application.repository.ACRepository
import com.procurement.contracting.application.repository.CANRepository
import com.procurement.contracting.application.repository.DataStatusesCAN
import com.procurement.contracting.domain.entity.ACEntity
import com.procurement.contracting.domain.model.can.CAN
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestReleaseTo
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestSource
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestType
import com.procurement.contracting.domain.model.confirmation.response.ConfirmationResponseType
import com.procurement.contracting.domain.model.contract.status.ContractStatus
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails
import com.procurement.contracting.domain.model.milestone.status.MilestoneStatus
import com.procurement.contracting.domain.model.milestone.type.MilestoneSubType
import com.procurement.contracting.domain.model.treasury.TreasuryResponseStatus.APPROVED
import com.procurement.contracting.domain.model.treasury.TreasuryResponseStatus.NOT_ACCEPTED
import com.procurement.contracting.domain.model.treasury.TreasuryResponseStatus.REJECTED
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType.CONFIRMATION_REQUEST
import com.procurement.contracting.exception.ErrorType.CONTRACT_NOT_FOUND
import com.procurement.contracting.exception.ErrorType.CONTRACT_STATUS
import com.procurement.contracting.exception.ErrorType.CONTRACT_STATUS_DETAILS
import com.procurement.contracting.exception.ErrorType.MILESTONE
import com.procurement.contracting.model.dto.ContractProcess
import com.procurement.contracting.model.dto.ocds.ConfirmationResponse
import com.procurement.contracting.model.dto.ocds.ConfirmationResponseValue
import com.procurement.contracting.model.dto.ocds.Contract
import com.procurement.contracting.model.dto.ocds.DocumentContract
import com.procurement.contracting.model.dto.ocds.Milestone
import com.procurement.contracting.model.dto.ocds.TreasuryData
import com.procurement.contracting.model.dto.ocds.Verification
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service

interface TreasuryProcessing {
    fun processing(context: TreasuryProcessingContext, data: TreasuryProcessingData): TreasuryProcessedData
}

@Service
class TreasuryProcessingImpl(
    private val acRepository: ACRepository,
    private val canRepository: CANRepository
) :
    TreasuryProcessing {
    /**
     * eContracting executes next operations:
     * 1. Finds saved version of Contract object by OCID && CPID values from parameter of Request;
     *   a. Validates Contract.Status && Contract.statusDetails in saved version of Contract by rule VR-9.9.1;
     *   b. Proceeds confirmationResponses object by rule BR-9.9.2;
     *   c. Proceeds Documents object by rule BR-9.9.8;
     *   d. Analyzes the value of verification.value field from Request:
     *     i.   IF verification.value == "3004":
     *       1. Proceeds Contract.Milestones object by rule BR-9.9.6;
     *       2. Sets Contract.statusDetails by rule BR-9.9.7;
     *     ii.  IF verification.value == "3005":
     *       1. Proceeds Contract.Milestones object by rule BR-9.9.9;
     *       2. Sets Contract.status && Contract.statusDetails by rule BR-9.9.10;
     *       3. Saves regData object & dateMet from Request to separate table in DB with
     *          ac_id && cp_id && can_id array && ac_status && ac_statusDetails;
     *       4. Finds all CAN objects in DB related to proceeded Contract where can.acOcid value == OCID
     *          from the context of Request and saves them as list in memory;
     *       5. Selects only CAN objects from list (got on step 1.e.ii.3) with can.status == "pending"
     *          and saves them as a list to memory;
     *       6. FOR every CAN from list (got before):
     *         a. Sets CAN.statusDetails by rule BR-9.9.11;
     *         b. Returns CAN from DB for Response as can.ID && can.status && can.statusDetails;
     *     iii. IF verification.value == "3006":
     *       1. Proceeds Contract.Milestones object by rule BR-9.9.9;
     *       2. Sets Contract.status && Contract.statusDetails by rule BR-9.9.10;
     *       3. Finds all CAN objects in DB related to proceeded Contract where can.acOcid value == OCID
     *          from the context of Request and saves them as list in memory;
     *       4. Selects only CAN objects from list (got on step 1.e.ii.3) with can.status == "pending"
     *          and saves them as a list to memory;
     *       5. FOR every CAN from list (got before) eContracting performs next steps:
     *         a. Sets CAN.status && CAN.statusDetails by rule BR-9.9.12;
     *         b. Returns CAN from DB for Response as can.ID && can.status && can.statusDetails;
     *   e. Returns updated Contract object for Response;
     */
    override fun processing(context: TreasuryProcessingContext, data: TreasuryProcessingData): TreasuryProcessedData {
        val acEntity: ACEntity = acRepository.findBy(cpid = context.cpid, contractId = context.ocid)
            ?: throw ErrorException(error = CONTRACT_NOT_FOUND)

        // VR-9.9.1
        checkStatusAndStatusDetails(acEntity)

        return when (data.verification.status) {
            APPROVED -> processingStatus3004(context, data, acEntity)
            NOT_ACCEPTED -> processingStatus3005(context, data, acEntity)
            REJECTED -> processingStatus3006(context, data, acEntity)
        }
    }

    /**
     * i. IF verification.value == "3004":
     *   1. Proceeds Contract.Milestones object by rule BR-9.9.6;
     *   2. Sets Contract.statusDetails by rule BR-9.9.7;
     */
    private fun processingStatus3004(
            context: TreasuryProcessingContext,
            data: TreasuryProcessingData,
            acEntity: ACEntity
    ): TreasuryProcessedData {
        val contractProcess: ContractProcess = toObject(ContractProcess::class.java, acEntity.jsonData)

        // BR-9.9.2
        val confirmationResponses = addedNewConfirmationResponse(data, contractProcess.contract)

        // BR-9.9.8
        val documents = documentRelatedConfirmations(contractProcess.contract)

        // BR-9.9.6
        val milestones = milestonesForApprovedContract(context, data, contractProcess.contract).toMutableList()

        /*
         * BR-9.9.7 Contract.statusDetails (contract)
         *
         * eContracting sets Contract.statusDetails value == "verified";
         */
        val statusDetails = ContractStatusDetails.VERIFIED

        val updatedContractProcess = contractProcess.copy(
            contract = contractProcess.contract.copy(
                milestones = milestones,
                statusDetails = statusDetails,
                confirmationResponses = confirmationResponses,
                documents = documents
            )
        )

        acRepository.updateStatusesAC(
            cpid = context.cpid,
            id = updatedContractProcess.contract.id,
            status = updatedContractProcess.contract.status,
            statusDetails = updatedContractProcess.contract.statusDetails,
            jsonData = toJson(updatedContractProcess)
        )

        return genResponse(contract = updatedContractProcess.contract, cans = emptyList())
    }

    /**
     * ii.  IF verification.value == "3005":
     *   1. Proceeds Contract.Milestones object by rule BR-9.9.9;
     *   2. Sets Contract.status && Contract.statusDetails by rule BR-9.9.10;
     *   3. Saves regData object & dateMet from Request to separate table in DB with
     *      ac_id && cp_id && can_id array && ac_status && ac_statusDetails;
     *   4. Finds all CAN objects in DB related to proceeded Contract where can.acOcid value == OCID
     *      from the context of Request and saves them as list in memory;
     *   5. Selects only CAN objects from list (got on step 1.e.ii.3) with can.status == "pending"
     *      and saves them as a list to memory;
     *   6. FOR every CAN from list (got before):
     *     a. Sets CAN.statusDetails by rule BR-9.9.11;
     *     b. Returns CAN from DB for Response as can.ID && can.status && can.statusDetails;
     */
    private fun processingStatus3005(
            context: TreasuryProcessingContext,
            data: TreasuryProcessingData,
            acEntity: ACEntity
    ): TreasuryProcessedData {
        val contractProcess: ContractProcess = toObject(ContractProcess::class.java, acEntity.jsonData)

        // BR-9.9.2
        val confirmationResponses = addedNewConfirmationResponse(data, contractProcess.contract)

        // BR-9.9.8
        val documents = documentRelatedConfirmations(contractProcess.contract)

        // BR-9.9.9
        val milestones = milestonesForApprovedContract(context, data, contractProcess.contract).toMutableList()

        /*
         * BR-9.9.10 Contract.status Contract.statusDetails (contract)
         *
         * eContracting sets:
         * contract.status value == "unsuccessful";
         * contract.statusDetails value == "empty";
         */
        val status = ContractStatus.UNSUCCESSFUL
        val statusDetails = ContractStatusDetails.EMPTY

        val updatedContractProcess = contractProcess.copy(
            contract = contractProcess.contract.copy(
                milestones = milestones,
                status = status,
                statusDetails = statusDetails,
                confirmationResponses = confirmationResponses,
                documents = documents
            ),
            treasuryData = TreasuryData(
                externalRegId = data.regData!!.externalRegId,
                regDate = data.regData.regDate,
                dateMet = data.dateMet
            )
        )

        val updatedCANs = canRepository.findBy(context.cpid).asSequence()
            .filter {
                it.contractId == context.ocid
                    && it.status == CANStatus.PENDING
            }.map {
                val can = toObject(CAN::class.java, it.jsonData)
                can.copy(
                    /*
                     * BR-9.9.12 status statusDetails (CAN)
                     *
                     * eContracting sets CAN.statusDetails value == "contractProject" and saves it to DB;
                     */
                    statusDetails = CANStatusDetails.CONTRACT_PROJECT
                )
            }
            .toList()

        val cansEntities = updatedCANs.map { can ->
            DataStatusesCAN(
                id = can.id,
                status = can.status,
                statusDetails = can.statusDetails,
                jsonData = toJson(can)
            )
        }

        //FIXME Consistency cannot be guaranteed
        acRepository.updateStatusesAC(
            cpid = context.cpid,
            id = updatedContractProcess.contract.id,
            status = updatedContractProcess.contract.status,
            statusDetails = updatedContractProcess.contract.statusDetails,
            jsonData = toJson(updatedContractProcess)
        )
        canRepository.updateStatusesCANs(cpid = context.cpid, cans = cansEntities)

        return genResponse(contract = updatedContractProcess.contract, cans = updatedCANs)
    }

    /**
     * iii. IF verification.value == "3006":
     *   1. Proceeds Contract.Milestones object by rule BR-9.9.9;
     *   2. Sets Contract.status && Contract.statusDetails by rule BR-9.9.10;
     *   3. Finds all CAN objects in DB related to proceeded Contract where can.acOcid value == OCID
     *      from the context of Request and saves them as list in memory;
     *   4. Selects only CAN objects from list (got on step 1.e.ii.3) with can.status == "pending"
     *      and saves them as a list to memory;
     *   5. FOR every CAN from list (got before) eContracting performs next steps:
     *     a. Sets CAN.status && CAN.statusDetails by rule BR-9.9.12;
     *     b. Returns CAN from DB for Response as can.ID && can.status && can.statusDetails;
     */
    private fun processingStatus3006(
            context: TreasuryProcessingContext,
            data: TreasuryProcessingData,
            acEntity: ACEntity
    ): TreasuryProcessedData {
        val contractProcess: ContractProcess = toObject(ContractProcess::class.java, acEntity.jsonData)

        // BR-9.9.2
        val confirmationResponses = addedNewConfirmationResponse(data, contractProcess.contract)

        // BR-9.9.8
        val documents = documentRelatedConfirmations(contractProcess.contract)

        // BR-9.9.9
        val milestones = milestonesForRejectedContract(context, data, contractProcess.contract).toMutableList()

        /*
         * BR-9.9.10 Contract.status Contract.statusDetails (contract)
         *
         * eContracting sets:
         * contract.status value == "unsuccessful";
         * contract.statusDetails value == "empty";
         */
        val status = ContractStatus.UNSUCCESSFUL
        val statusDetails = ContractStatusDetails.EMPTY

        val updatedContractProcess = contractProcess.copy(
            contract = contractProcess.contract.copy(
                milestones = milestones,
                status = status,
                statusDetails = statusDetails,
                confirmationResponses = confirmationResponses,
                documents = documents
            )
        )

        val updatedCANs = canRepository.findBy(context.cpid).asSequence()
            .filter {
                it.contractId == context.ocid
                    && it.status == CANStatus.PENDING
            }.map {
                val can = toObject(CAN::class.java, it.jsonData)
                can.copy(
                    /*
                     * BR-9.9.12 status statusDetails (CAN)
                     *
                     * eContracting sets:
                     * CAN.status value == "unsuccessful" and saves it to DB;
                     * CAN.statusDetails value == "treasuryRejection" and saves it to DB;
                     */
                    status = CANStatus.UNSUCCESSFUL,
                    statusDetails = CANStatusDetails.TREASURY_REJECTION
                )
            }
            .toList()

        val cansEntities = updatedCANs.map { can ->
            DataStatusesCAN(
                id = can.id,
                status = can.status,
                statusDetails = can.statusDetails,
                jsonData = toJson(can)
            )
        }

        //FIXME Consistency cannot be guaranteed
        acRepository.updateStatusesAC(
            cpid = context.cpid,
            id = updatedContractProcess.contract.id,
            status = updatedContractProcess.contract.status,
            statusDetails = updatedContractProcess.contract.statusDetails,
            jsonData = toJson(updatedContractProcess)
        )
        canRepository.updateStatusesCANs(cpid = context.cpid, cans = cansEntities)

        return genResponse(contract = updatedContractProcess.contract, cans = updatedCANs)
    }

    /**
     * BR-9.9.2 confirmationResponses (contract)
     *
     * eContracting executes next operations:
     * 1. Adds new confirmationResponses object to Contract object in DB getting next fields from Request:
     *   a. Adds confirmationResponses.value.verification.Value == value of verification.value field from Request;
     *   b. Adds confirmationResponses.value.verification.Rationale == value of verification.rationale field
     *      from Request (if it was transferred);
     * 2. Calculates next fields for added contract.confirmationResponses object and saves them:
     *   a. Generates confirmationResponses.ID by rule BR-9.9.3;
     *   b. Determines confirmationResponses.value.Name by rule BR-9.9.4;
     *   c. Determines confirmationResponses.value.ID by rule BR-9.9.4;
     *   d. Sets confirmationResponses.value.Date == dateMet value from Request;
     *   e. Sets confirmationResponses.value.verification.type == "code";
     *   f. Determines confirmationResponses.request value by rule BR-9.9.5;
     */
    private fun addedNewConfirmationResponse(
            data: TreasuryProcessingData,
            contract: Contract
    ): MutableList<ConfirmationResponse> =
        contract.confirmationResponses!!
            .toMutableList()
            .apply {
                add(
                    ConfirmationResponse(
                        id = generateConfirmationResponseId(contract),
                        value = generateConfirmationResponseValue(data, contract),
                        request = confirmationResponseRequest(contract)
                    )
                )
            }

    /**
     * BR-9.9.3 ID (confirmationResponses)
     *
     * eContracting executes next operations:
     * 1. Finds confirmationRequest object from proceeded Contract with confirmationRequests.source == "approveBody";
     * 2. Finds milestone object from proceeded Contract with milestone.subType == "approveBodyValidation";
     * 3. Sets confirmationResponses.ID as a concatenation of (
     *      "cs-approveBody-confirmation-on-"
     *      + (get.confirmationRequest.relatedItem from object found on step 1)
     *      + "-"
     *      + (get.milestone.relatedParties.ID from object found on step 2)
     *    ) and saves it;
     */
    private fun generateConfirmationResponseId(contract: Contract): String {
        val confirmationRequest = contract.confirmationRequests!!.firstOrNull {
            it.source == ConfirmationRequestSource.APPROVE_BODY
        } ?: throw ErrorException(
            error = CONFIRMATION_REQUEST,
            message = "A confirmation request with type source '${ConfirmationRequestSource.APPROVE_BODY.value}' not found."
        )

        val milestone = contract.milestones!!.firstOrNull {
            it.subtype == MilestoneSubType.APPROVE_BODY_VALIDATION
        } ?: throw ErrorException(
            error = MILESTONE,
            message = "Milestone by type '${MilestoneSubType.APPROVE_BODY_VALIDATION.value}' not found."
        )

        val relatedPartyId = milestone.relatedParties!![0].id
        return "cs-approveBody-confirmation-on-${confirmationRequest.relatedItem}-$relatedPartyId"
    }

    /**
     * BR-9.9.4 Value.Name Value.ID (confirmationResponses)
     *
     * eContracting executes next operations:
     * 1. Finds milestone object from proceeded Contract with milestone.subType == "approveBodyValidation";
     * 2. Sets confirmationResponses.value.Name == Get.milestone.relatedParties.Name from object found on step 1;
     * 3. Sets confirmationResponses.value.ID == Get.milestone.relatedParties.ID from object found on step 1;
     *
     * BR-9.9.2 confirmationResponses (contract)
     * 2. Calculates next fields for added contract.confirmationResponses object and saves them:
     *   b. Determines confirmationResponses.value.Name by rule BR-9.9.4;
     *   c. Determines confirmationResponses.value.ID by rule BR-9.9.4;
     *   d. Sets confirmationResponses.value.Date == dateMet value from Request;
     *   e. Sets confirmationResponses.value.verification.type == "code";
     */
    private fun generateConfirmationResponseValue(
            data: TreasuryProcessingData,
            contract: Contract
    ): ConfirmationResponseValue {
        val milestone = contract.milestones!!.firstOrNull {
            it.subtype == MilestoneSubType.APPROVE_BODY_VALIDATION
        } ?: throw ErrorException(
            error = MILESTONE,
            message = "Milestone by type '${MilestoneSubType.APPROVE_BODY_VALIDATION.value}' not found."
        )

        val relatedParty = milestone.relatedParties!![0]
        val verification = data.verification
        return ConfirmationResponseValue(
            id = relatedParty.id,
            name = relatedParty.name,
            date = data.dateMet,
            verification = listOf(
                Verification(
                    type = ConfirmationResponseType.CODE,
                    value = verification.status.value,
                    rationale = verification.rationale
                )
            ),
            relatedPerson = null
        )
    }

    /**
     * BR-9.9.5 Request (confirmationResponses)
     * eContracting executes next operations:

     * 1. Finds confirmationRequest object from proceeded Contract with confirmationRequests.source == "approveBody";
     * 2. Finds first requestGroups.requests object in confirmationRequest object found before;
     * 3. Sets confirmationResponses.Request value == Get.requestGroups.requests.ID from object found on step 2;
     */
    private fun confirmationResponseRequest(contract: Contract): String {
        val confirmationRequest = contract.confirmationRequests!!.firstOrNull {
            it.source == ConfirmationRequestSource.APPROVE_BODY
        } ?: throw ErrorException(
            error = CONFIRMATION_REQUEST,
            message = "Confirmation request by type '${ConfirmationRequestSource.APPROVE_BODY.value}' not found."
        )

        val requestGroup = confirmationRequest.requestGroups!![0]
        return requestGroup.requests[0].id
    }

    /**
     * BR-9.9.6 Milestones (contract - approved)
     *
     * eContracting executes next operations:
     * 1. Finds in DB the Milestone object from proceeded Contract with milestone.subType == "approveBodyValidation";
     * 2. Determines for milestone found before next fields:
     *   a. Sets milestones.dateModified == value of startDate parameter from the context of Request;
     *   b. Sets milestones.dateMet == value of dateMet field from Request;
     *   c. Sets milestones.status == "met";
     */
    private fun milestonesForApprovedContract(
            context: TreasuryProcessingContext,
            data: TreasuryProcessingData,
            contract: Contract
    ): List<Milestone> {
        val milestone = contract.milestones!!.firstOrNull {
            it.subtype == MilestoneSubType.APPROVE_BODY_VALIDATION
        } ?: throw ErrorException(
            error = MILESTONE,
            message = "Milestone by type '${MilestoneSubType.APPROVE_BODY_VALIDATION.value}' not found."
        )

        val updatedMilestone = milestone.copy(
            dateModified = context.startDate,
            dateMet = data.dateMet,
            status = MilestoneStatus.MET
        )

        return contract.milestones!!.map {
            if (it.id == updatedMilestone.id) updatedMilestone else it
        }
    }

    /**
     * BR-9.9.8 Document.relatedConfirmations (contract)
     *
     * eContracting executes next operations:
     * 1. Finds confirmationRequest object from proceeded Contract with confirmationRequests.source == "approveBody";
     * 2. Finds Documents object in saved Contract with Document.ID == value Get.confirmationRequest.relatedItem
     *    from confirmationRequest object found before;
     * 3. Adds to list of values in Document.relatedConfirmations of Document object (found on step 2) value of
     *    confirmationResponses.ID from object generated by rule BR-9.9.3;
     */
    private fun documentRelatedConfirmations(contract: Contract): List<DocumentContract> {
        val confirmationRequest = contract.confirmationRequests!!.firstOrNull {
            it.source == ConfirmationRequestSource.APPROVE_BODY
        } ?: throw ErrorException(
            error = CONFIRMATION_REQUEST,
            message = "Confirmation request by type '${ConfirmationRequestSource.APPROVE_BODY.value}' not found."
        )

        return contract.documents!!.map {
            if (it.id == confirmationRequest.relatedItem) {
                it
            } else
                it
        }
    }

    /**
     * BR-9.9.9 Milestones (contract - rejected)
     *
     * eContracting executes next operations:
     * 1. Finds in DB the Milestone object from proceeded Contract with milestone.subType == "approveBodyValidation";
     * 2. Determines for milestone found before next fields:
     *   a. Sets milestones.dateModified == value of startDate parameter from the context of Request;
     *   b. Sets milestones.dateMet == value of dateMet field from Request;
     *   c. Sets milestones.status == "notMet";
     */
    private fun milestonesForRejectedContract(
            context: TreasuryProcessingContext,
            data: TreasuryProcessingData,
            contract: Contract
    ): List<Milestone> {
        val milestone = contract.milestones!!.firstOrNull {
            it.subtype == MilestoneSubType.APPROVE_BODY_VALIDATION
        } ?: throw ErrorException(
            error = MILESTONE,
            message = "Milestone by type '${MilestoneSubType.APPROVE_BODY_VALIDATION.value}' not found."
        )

        val updatedMilestone = milestone.copy(
            dateModified = context.startDate,
            dateMet = data.dateMet,
            status = MilestoneStatus.MET
        )

        return contract.milestones!!.map {
            if (it.id == updatedMilestone.id) updatedMilestone else it
        }
    }

    /**
     * VR-9.9.1 Contract.Status Contract.statusDetails (contracts)
     *
     * eContracting checks Contract.Status && Contract.statusDetails in saved version of Contract:
     * IF Contract.Status value == "pending" && Contract.statusDetails value == "verification",
     *     validation is successful;
     * ELSE
     *     throws Exception;
     */
    fun checkStatusAndStatusDetails(acEntity: ACEntity) {
        if (acEntity.status != ContractStatus.PENDING)
            throw ErrorException(error = CONTRACT_STATUS)
        if (acEntity.statusDetails != ContractStatusDetails.VERIFICATION)
            throw ErrorException(error = CONTRACT_STATUS_DETAILS)
    }

    private fun genResponse(contract: Contract, cans: List<CAN>): TreasuryProcessedData {
        return TreasuryProcessedData(
            contract = TreasuryProcessedData.Contract(
                id = contract.id,
                date = contract.date!!,
                awardId = contract.awardId,
                status = contract.status,
                statusDetails = contract.statusDetails,
                title = contract.title!!,
                description = contract.description!!,
                period = contract.period.let { period ->
                    TreasuryProcessedData.Contract.Period(
                        startDate = period!!.startDate,
                        endDate = period.endDate!!
                    )
                },
                documents = contract.documents!!.map { document ->
                    TreasuryProcessedData.Contract.Document(
                        id = document.id,
                        documentType = document.documentType,
                        title = document.title,
                        description = document.description,
                        relatedLots = document.relatedLots?.toList(),
                        relatedConfirmations = document.relatedConfirmations?.toList()
                    )
                },
                milestones = contract.milestones!!.map { milestone ->
                    TreasuryProcessedData.Contract.Milestone(
                        id = milestone.id,
                        relatedItems = milestone.relatedItems?.toList(),
                        status = milestone.status!!,
                        additionalInformation = milestone.additionalInformation,
                        dueDate = milestone.dueDate,
                        title = milestone.title,
                        type = milestone.type,
                        description = milestone.description,
                        dateModified = milestone.dateModified,
                        dateMet = milestone.dateMet,
                        relatedParties = milestone.relatedParties!!.map { relatedParty ->
                            TreasuryProcessedData.Contract.Milestone.RelatedParty(
                                id = relatedParty.id,
                                name = relatedParty.name
                            )
                        }
                    )
                },
                confirmationRequests = contract.confirmationRequests!!.map { confirmationRequest ->
                    TreasuryProcessedData.Contract.ConfirmationRequest(
                        id = confirmationRequest.id,
                        type = ConfirmationRequestType.fromString(confirmationRequest.type!!),
                        title = confirmationRequest.title!!,
                        description = confirmationRequest.description!!,
                        relatesTo = ConfirmationRequestReleaseTo.fromString(confirmationRequest.relatesTo!!),
                        relatedItem = confirmationRequest.relatedItem,
                        source = ConfirmationRequestSource.fromString(confirmationRequest.source.value),
                        requestGroups = confirmationRequest.requestGroups!!.map { requestGroup ->
                            TreasuryProcessedData.Contract.ConfirmationRequest.RequestGroup(
                                id = requestGroup.id,
                                requests = requestGroup.requests.map { request ->
                                    TreasuryProcessedData.Contract.ConfirmationRequest.RequestGroup.Request(
                                        id = request.id,
                                        title = request.title,
                                        description = request.description,
                                        relatedPerson = request.relatedPerson?.let { relatedPerson ->
                                            TreasuryProcessedData.Contract.ConfirmationRequest.RequestGroup.Request.RelatedPerson(
                                                id = relatedPerson.id,
                                                name = relatedPerson.name
                                            )
                                        }
                                    )
                                }
                            )
                        }
                    )
                },
                confirmationResponses = contract.confirmationResponses!!.map { confirmationResponse ->
                    TreasuryProcessedData.Contract.ConfirmationResponse(
                        id = confirmationResponse.id,
                        value = confirmationResponse.value.let { value ->
                            TreasuryProcessedData.Contract.ConfirmationResponse.Value(
                                id = value.id,
                                name = value.name,
                                date = value.date,
                                relatedPerson = value.relatedPerson?.let { relatedPerson ->
                                    TreasuryProcessedData.Contract.ConfirmationResponse.Value.RelatedPerson(
                                        id = relatedPerson.id,
                                        name = relatedPerson.name
                                    )
                                },
                                verifications = value.verification.map { verification ->
                                    TreasuryProcessedData.Contract.ConfirmationResponse.Value.Verification(
                                        type = ConfirmationResponseType.fromString(verification.type.value),
                                        value = verification.value,
                                        rationale = verification.rationale
                                    )
                                }
                            )
                        },
                        request = confirmationResponse.request
                    )
                },
                value = contract.value!!.let { value ->
                    TreasuryProcessedData.Contract.Value(
                        amount = value.amount!!,
                        currency = value.currency!!,
                        amountNet = value.amountNet!!,
                        valueAddedTaxIncluded = value.valueAddedTaxIncluded!!
                    )
                }
            ),
            cans = cans.map { can ->
                TreasuryProcessedData.Can(
                    id = can.id,
                    status = can.status,
                    statusDetails = can.statusDetails
                )
            }
        )
    }
}


