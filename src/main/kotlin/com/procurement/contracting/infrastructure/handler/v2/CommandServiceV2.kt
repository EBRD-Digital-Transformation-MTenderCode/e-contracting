package com.procurement.contracting.infrastructure.handler.v2

import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.infrastructure.api.v2.ApiResponseV2
import com.procurement.contracting.infrastructure.api.v2.CommandTypeV2
import com.procurement.contracting.infrastructure.fail.error.BadRequest
import com.procurement.contracting.infrastructure.handler.v2.model.ApiResponseV2Generator.generateResponseOnFailure
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor
import org.springframework.stereotype.Service

@Service
class CommandServiceV2(
    private val logger: Logger,
    private val addGeneratedDocumentToContractHandler: AddGeneratedDocumentToContractHandler,
    private val addSupplierReferencesInFCHandler: AddSupplierReferencesInFCHandler,
    private val cancelFrameworkContractHandler: CancelFrameworkContractHandler,
    private val checkAccessToContractHandler: CheckAccessToContractHandler,
    private val checkAccessToRequestOfConfirmationHandler: CheckAccessToRequestOfConfirmationHandler,
    private val checkContractStateHandler: CheckContractStateHandler,
    private val checkExistenceOfConfirmationResponsesHandler: CheckExistenceOfConfirmationResponsesHandler,
    private val checkExistenceSupplierReferencesInFCHandler: CheckExistenceSupplierReferencesInFCHandler,
    private val createConfirmationRequestsHandler: CreateConfirmationRequestsHandler,
    private val createConfirmationResponseHandler: CreateConfirmationResponseHandler,
    private val createFrameworkContractHandler: CreateFrameworkContractHandler,
    private val doPacsHandler: DoPacsHandler,
    private val findCANIdsHandler: FindCANIdsHandler,
    private val findContractDocumentIdHandler: FindContractDocumentIdHandler,
    private val findPacsByLotIdsHandler: FindPacsByLotIdsHandler,
    private val getContractStateHandler: GetContractStateHandler,
    private val getRequestByConfirmationResponseHandler: GetRequestByConfirmationResponseHandler,
    private val getSupplierIdsByContractHandler: GetSupplierIdsByContractHandler,
    private val setStateForContractsHandler: SetStateForContractsHandler,
    private val validateConfirmationResponseDataHandler: ValidateConfirmationResponseDataHandler,
) {

    fun execute(descriptor: CommandDescriptor): ApiResponseV2 {
        return when (descriptor.action) {
            CommandTypeV2.ADD_GENERATED_DOCUMENT_TO_CONTRACT -> addGeneratedDocumentToContractHandler.handle(descriptor)
            CommandTypeV2.ADD_SUPPLIER_REFERENCES_IN_FC -> addSupplierReferencesInFCHandler.handle(descriptor)
            CommandTypeV2.CANCEL_FRAMEWORK_CONTRACT -> cancelFrameworkContractHandler.handle(descriptor)
            CommandTypeV2.CHECK_ACCESS_TO_CONTRACT -> checkAccessToContractHandler.handle(descriptor)
            CommandTypeV2.CHECK_ACCESS_TO_REQUEST_OF_CONFIRMATION -> checkAccessToRequestOfConfirmationHandler.handle(descriptor)
            CommandTypeV2.CHECK_CONTRACT_STATE -> checkContractStateHandler.handle(descriptor)
            CommandTypeV2.CHECK_EXISTENCE_OF_CONFIRMATION_RESPONSES -> checkExistenceOfConfirmationResponsesHandler.handle(descriptor)
            CommandTypeV2.CHECK_EXISTENCE_SUPPLIER_REFERENCES_IN_FC -> checkExistenceSupplierReferencesInFCHandler.handle(descriptor)
            CommandTypeV2.CREATE_CONFIRMATION_REQUESTS -> createConfirmationRequestsHandler.handle(descriptor)
            CommandTypeV2.CREATE_CONFIRMATION_RESPONSE -> createConfirmationResponseHandler.handle(descriptor)
            CommandTypeV2.CREATE_FRAMEWORK_CONTRACT -> createFrameworkContractHandler.handle(descriptor)
            CommandTypeV2.DO_PACS -> doPacsHandler.handle(descriptor)
            CommandTypeV2.FIND_CAN_IDS -> findCANIdsHandler.handle(descriptor)
            CommandTypeV2.FIND_CONTRACT_DOCUMENT_ID -> findContractDocumentIdHandler.handle(descriptor)
            CommandTypeV2.FIND_PACS_BY_LOT_IDS -> findPacsByLotIdsHandler.handle(descriptor)
            CommandTypeV2.GET_CONTRACT_STATE -> getContractStateHandler.handle(descriptor)
            CommandTypeV2.GET_REQUEST_BY_CONFIRMATION_RESPONSE -> getRequestByConfirmationResponseHandler.handle(descriptor)
            CommandTypeV2.GET_SUPPLIER_IDS_BY_CONTRACT -> getSupplierIdsByContractHandler.handle(descriptor)
            CommandTypeV2.SET_STATE_FOR_CONTRACTS -> setStateForContractsHandler.handle(descriptor)
            CommandTypeV2.VALIDATE_CONFIRMATION_RESPONSE_DATA -> validateConfirmationResponseDataHandler.handle(descriptor)

            else -> {
                val errorDescription = "Unknown action '${descriptor.action.key}'."
                generateResponseOnFailure(
                    fail = BadRequest(description = errorDescription, exception = RuntimeException(errorDescription)),
                    id = descriptor.id,
                    version = descriptor.version,
                    logger = logger
                )
            }
        }
    }
}
