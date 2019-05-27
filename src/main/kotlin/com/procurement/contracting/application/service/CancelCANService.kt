package com.procurement.contracting.application.service

import com.procurement.contracting.application.repository.ACRepository
import com.procurement.contracting.application.repository.CANRepository
import com.procurement.contracting.application.repository.DataCancelAC
import com.procurement.contracting.application.repository.DataCancelCAN
import com.procurement.contracting.domain.entity.ACEntity
import com.procurement.contracting.domain.entity.CANEntity
import com.procurement.contracting.domain.model.CAN
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.model.dto.ContractProcess
import com.procurement.contracting.model.dto.ocds.Contract
import com.procurement.contracting.model.dto.ocds.ContractStatus
import com.procurement.contracting.model.dto.ocds.ContractStatusDetails
import com.procurement.contracting.model.dto.ocds.DocumentTypeAmendment
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service
import java.util.*

data class CancelCANContext(
    val cpid: String,
    val token: UUID,
    val owner: String,
    val canId: UUID
)

data class CancelCANData(
    val amendment: Amendment
) {

    data class Amendment(
        val rationale: String,
        val description: String?,
        val documents: List<Document>?
    ) {

        data class Document(
            val id: String,
            val documentType: DocumentTypeAmendment,
            val title: String,
            val description: String?
        )
    }
}

data class CancelledCANData(
    val cans: List<CAN>,
    val isCancelledAC: Boolean,
    val lotId: String,
    val contract: Contract?
) {

    data class CAN(
        val id: UUID,
        val status: ContractStatus,
        val statusDetails: ContractStatusDetails,
        val amendment: Amendment?
    ) {

        data class Amendment(
            val rationale: String,
            val description: String?,
            val documents: List<Document>?
        ) {

            data class Document(
                val id: String,
                val documentType: DocumentTypeAmendment,
                val title: String,
                val description: String?
            )
        }
    }

    data class Contract(
        val id: String,
        val status: ContractStatus,
        val statusDetails: ContractStatusDetails
    )
}

interface CancelCANService {
    fun cancel(context: CancelCANContext, data: CancelCANData): CancelledCANData
}

@Service
class CancelCANServiceImpl(
    private val canRepository: CANRepository,
    private val acRepository: ACRepository
) : CancelCANService {

    /**
     * 1. Validates value of token parameter from context of Request by rule VR-9.13.1;
     *
     * 2. Validates value of owner parameter from context of Request by rule VR-9.13.2;
     *
     * 3. Analyzes the availability of Amendment.Documents object in Request:
     *      IF Amendment.Documents object was transferred, eContracting validates Amendments.Documents.documentType
     *         values from Request by rule VR-9.13.5;
     *      ELSE (there is no Documents in Request), eContracting does not perform any operation;
     *
     * 4. Finds saved version of CAN object by ID && CPID values from the context of Request:
     *  a. Validates CAN.Status && CAN.statusDetails in saved version of CAN by rule VR-9.13.3;
     *  b. Checks the availability of ACocid value in saved version of CAN:
     *      i. IF ACocid is filled in saved CAN in DB, eContracting performs next operations:
     *          1. Finds saved version of Contract object by CPID value from the context of Request && ACocid from saved CAN;
     *          2. Validates Contract.Status in saved version of Contract (found before) by rule VR-9.13.4;
     *          3. Sets Contract.status && Contract.statusDetails in saved Contract by rule BR-9.13.2;
     *          4. Saves updated Contract to DB;
     *          5. Returns contract.status && contract.statusDetails && contract.ID for Response;
     *          6. Returns acCancel parameter = TRUE for Response;
     *          7. Checks the availability of other Can objects beside proceeded can related to Contract (found on step 4.b.1) by can.ACocid value:
     *              IF can objects were found, eContracting FOR every can performs next steps:
     *                  i.   Sets CAN.statusDetails by rule BR-9.13.4;
     *                  ii.  Sets CAN.ACocid = NULL;
     *                  iii. Returns CAN from DB for Response as can.ID && can.status && can.statusDetails;
     *              ELSE there is no can related to Contract, eContracting does not perform any actions;
     *          ELSE (ACocid is NOT filled), eContracting returns acCancel parameter = FALSE for Response;
     *  c. Sets CAN.status && CAN.statusDetails by rule BR-9.13.3;
     *  d. Saves Contract.Amendments object as CAN.Amendments objects in DB;
     *  e. Saves updated CAN to DB;
     *  f. Get.can.lotId from saved can (found on step 4) and returns it as lotId value for Response;
     *  g. Returns CAN from DB for Response as can.ID && can.status && can.statusDetails && can.amendments;
     */
    override fun cancel(context: CancelCANContext, data: CancelCANData): CancelledCANData {

        /**
         * Begin processing the cancellation CAN
         */
        val canEntity: CANEntity = canRepository.findBy(cpid = context.cpid, canId = context.canId)
            ?: throw ErrorException(ErrorType.CAN_NOT_FOUND)

        //VR-9.13.1
        checkToken(tokenFromRequest = context.token, canEntity = canEntity)

        //VR-9.13.2
        checkOwner(ownerFromRequest = context.owner, canEntity = canEntity)

        val can: CAN = toObject(CAN::class.java, canEntity.jsonData)

        //VR-9.13.3
        checkCANStatuses(can)

        val cansForResponse = mutableListOf<CancelledCANData.CAN>()

        val cancelledCAN: CAN = cancellingCAN(can = can, amendment = data.amendment)
        val dataCancelCAN = DataCancelCAN(
            id = can.id,
            status = cancelledCAN.status,
            statusDetails = cancelledCAN.statusDetails,
            jsonData = toJson(cancelledCAN)
        )
        cansForResponse.add(element = generateCANResponse(can = cancelledCAN))
        /**
         * End processing the cancellation CAN
         */

        /**
         * Begin processing a contract of the cancellation CAN & related CANs
         */
        val dataCancelAC: DataCancelAC?
        val contractForResponse: CancelledCANData.Contract?
        val isCancelledAC: Boolean
        val dataRelatedCANs = mutableListOf<DataCancelCAN>()

        if (canEntity.contractId != null) {
            val contractId: String = canEntity.contractId

            /**
             * Begin update contract
             */
            val acEntity: ACEntity = acRepository.findBy(cpid = context.cpid, contractId = contractId)
                ?: throw ErrorException(ErrorType.CONTRACT_NOT_FOUND)

            val contractProcess: ContractProcess = toObject(ContractProcess::class.java, acEntity.jsonData)

            //VR-9.13.4
            checkContractStatuses(contract = contractProcess.contract)

            val cancelledContract = cancellingContract(contractProcess.contract)
            val updatedContractProcess = contractProcess.copy(contract = cancelledContract)
            dataCancelAC = DataCancelAC(
                cpid = context.cpid,
                id = cancelledContract.id,
                status = cancelledContract.status,
                statusDetails = cancelledContract.statusDetails,
                jsonData = toJson(updatedContractProcess)
            )
            contractForResponse = generateContractResponse(cancelledContract)
            isCancelledAC = true
            /**
             * End update contract
             */

            /**
             * Begin update related CANs
             */
            getRelatedCans(cpid = context.cpid, canId = can.id, contractId = contractId)
                .forEach { relatedCANEntity ->
                    val relatedCAN: CAN = toObject(CAN::class.java, relatedCANEntity.jsonData)

                    //BR-9.13.4
                    val updatedRelatedCAN = settingStatusesRelatedCAN(relatedCAN)
                    val dataRelatedCAN = DataCancelCAN(
                        id = relatedCAN.id,
                        status = updatedRelatedCAN.status,
                        statusDetails = updatedRelatedCAN.statusDetails,
                        jsonData = toJson(updatedRelatedCAN)
                    )
                    dataRelatedCANs.add(dataRelatedCAN)
                    cansForResponse.add(element = generateCANResponse(can = updatedRelatedCAN))
                }

            /**
             * End update related CANs
             */
        } else {
            contractForResponse = null
            dataCancelAC = null
            isCancelledAC = false
        }
        /**
         * End processing a contract of the cancellation CAN & related CANs
         */

        if (dataCancelAC != null)
            acRepository.cancellationAC(dataCancelAC = dataCancelAC)
        canRepository.saveCancelledCANs(
            cpid = context.cpid,
            dataCancelledCAN = dataCancelCAN,
            dataRelatedCANs = dataRelatedCANs
        )

        return CancelledCANData(
            cans = cansForResponse,
            isCancelledAC = isCancelledAC,
            lotId = can.lotId,
            contract = contractForResponse
        )
    }

    private fun getRelatedCans(
        cpid: String,
        canId: UUID,
        contractId: String
    ): Sequence<CANEntity> = canRepository.findBy(cpid = cpid)
        .asSequence()
        .filter {
            it.contractId == contractId && it.id != canId
        }

    private fun generateCANResponse(can: CAN) = CancelledCANData.CAN(
        id = can.id,
        status = can.status,
        statusDetails = can.statusDetails,
        amendment = can.amendment?.let {
            CancelledCANData.CAN.Amendment(
                rationale = it.rationale,
                description = it.description,
                documents = it.documents?.map { document ->
                    CancelledCANData.CAN.Amendment.Document(
                        id = document.id,
                        documentType = document.documentType,
                        title = document.title,
                        description = document.description
                    )
                }
            )
        }
    )

    private fun generateContractResponse(contract: Contract) = CancelledCANData.Contract(
        id = contract.id,
        status = contract.status,
        statusDetails = contract.statusDetails
    )

    private fun cancellingContract(contract: Contract): Contract {
        return contract.copy(
            /**
             * BR-9.13.2 Contract.statusDetails Contract.status (contract)
             *
             * eContracting sets Contract.status == "cancelled" && Contract.statusDetails value == "empty" and saves them to DB;
             */
            status = ContractStatus.CANCELLED,
            statusDetails = ContractStatusDetails.EMPTY
        )
    }

    private fun cancellingCAN(can: CAN, amendment: CancelCANData.Amendment): CAN {
        return can.copy(
            /**
             * BR-9.13.3 Status statusDetails (CAN)
             *
             * eContracting sets CAN.status == "cancelled" && CAN.statusDetails value == "empty" and saves them to DB;
             */
            status = ContractStatus.CANCELLED,
            statusDetails = ContractStatusDetails.EMPTY,

            /**
             * BR-9.13.1(4.d)
             */
            amendment = CAN.Amendment(
                rationale = amendment.rationale,
                description = amendment.description,
                documents = amendment.documents?.map { document ->
                    CAN.Amendment.Document(
                        id = document.id,
                        documentType = document.documentType,
                        title = document.title,
                        description = document.description
                    )
                }
            )
        )
    }

    /**
     * BR-9.13.4 statusDetails (CAN)
     *
     * eContracting sets CAN.statusDetails value == "contractProject" and saves it to DB;
     */
    private fun settingStatusesRelatedCAN(can: CAN): CAN {
        return can.copy(
            statusDetails = ContractStatusDetails.CONTRACT_PROJECT
        )
    }

    /**
     * VR-9.13.1 ID (can) token
     *
     * eContracting проверяет что найденный по ID && CPID из запароса CAN содержит token,
     * значение которого равно значению параметра token из запроса.
     */
    private fun checkToken(tokenFromRequest: UUID, canEntity: CANEntity) {
        if (canEntity.token != tokenFromRequest)
            throw ErrorException(error = ErrorType.INVALID_TOKEN)
    }

    /**
     * VR-9.13.2 Owner
     *
     * eContracting проверяет соответствие owner связанного CAN (выбранного из БД) и owner,
     * полученного в контексте запроса.
     */
    private fun checkOwner(ownerFromRequest: String, canEntity: CANEntity) {
        if (canEntity.owner != ownerFromRequest)
            throw ErrorException(error = ErrorType.INVALID_OWNER)
    }

    /**
     * VR-9.13.3 Status statusDetails (CAN)
     *
     * eContracting checks can.status && can.statusDetails in saved version of CAN:
     * IF can.status value == "pending" && can.statusDetails value == "contractProject" || "active" || "unsuccessful",
     *      validation is successful;
     * ELSE eContracting throws Exception;
     */
    private fun checkCANStatuses(can: CAN) {
        when (can.status) {
            ContractStatus.PENDING -> {
                return when (can.statusDetails) {
                    ContractStatusDetails.CONTRACT_PROJECT,
                    ContractStatusDetails.ACTIVE,
                    ContractStatusDetails.UNSUCCESSFUL -> Unit

                    ContractStatusDetails.CONTRACT_PREPARATION,
                    ContractStatusDetails.APPROVED,
                    ContractStatusDetails.SIGNED,
                    ContractStatusDetails.VERIFICATION,
                    ContractStatusDetails.VERIFIED,
                    ContractStatusDetails.CANCELLED,
                    ContractStatusDetails.COMPLETE,
                    ContractStatusDetails.ISSUED,
                    ContractStatusDetails.APPROVEMENT,
                    ContractStatusDetails.EXECUTION,
                    ContractStatusDetails.EMPTY -> throw ErrorException(error = ErrorType.INVALID_CAN_STATUS_DETAILS)
                }
            }

            ContractStatus.ACTIVE,
            ContractStatus.CANCELLED,
            ContractStatus.COMPLETE,
            ContractStatus.TERMINATED,
            ContractStatus.UNSUCCESSFUL -> throw ErrorException(error = ErrorType.INVALID_CAN_STATUS)
        }
    }

    /**
     * VR-9.13.4 Contract.status (contract)
     *
     * eContracting checks Contract.Status in saved version of Contract:
     * IF (contract.status value == "pending") && (contract.statusDetails value != "verification" || "verified"),
     *    validation is successful;
     * ELSE eContracting throws Exception;
     */
    private fun checkContractStatuses(contract: Contract) {
        when (contract.status) {
            ContractStatus.PENDING -> {
                return when (contract.statusDetails) {
                    ContractStatusDetails.CONTRACT_PROJECT,
                    ContractStatusDetails.CONTRACT_PREPARATION,
                    ContractStatusDetails.ACTIVE,
                    ContractStatusDetails.APPROVED,
                    ContractStatusDetails.SIGNED,
                    ContractStatusDetails.CANCELLED,
                    ContractStatusDetails.COMPLETE,
                    ContractStatusDetails.UNSUCCESSFUL,
                    ContractStatusDetails.ISSUED,
                    ContractStatusDetails.APPROVEMENT,
                    ContractStatusDetails.EXECUTION,
                    ContractStatusDetails.EMPTY -> Unit

                    ContractStatusDetails.VERIFICATION,
                    ContractStatusDetails.VERIFIED -> throw ErrorException(error = ErrorType.CONTRACT_STATUS_DETAILS)
                }
            }

            ContractStatus.CANCELLED -> {
                return when (contract.statusDetails) {
                    ContractStatusDetails.EMPTY -> Unit

                    ContractStatusDetails.CONTRACT_PROJECT,
                    ContractStatusDetails.CONTRACT_PREPARATION,
                    ContractStatusDetails.ACTIVE,
                    ContractStatusDetails.APPROVED,
                    ContractStatusDetails.SIGNED,
                    ContractStatusDetails.VERIFICATION,
                    ContractStatusDetails.VERIFIED,
                    ContractStatusDetails.CANCELLED,
                    ContractStatusDetails.COMPLETE,
                    ContractStatusDetails.UNSUCCESSFUL,
                    ContractStatusDetails.ISSUED,
                    ContractStatusDetails.APPROVEMENT,
                    ContractStatusDetails.EXECUTION -> throw ErrorException(error = ErrorType.CONTRACT_STATUS_DETAILS)
                }
            }

            ContractStatus.ACTIVE,
            ContractStatus.COMPLETE,
            ContractStatus.TERMINATED,
            ContractStatus.UNSUCCESSFUL -> throw ErrorException(error = ErrorType.CONTRACT_STATUS)
        }
    }
}
