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
    private val checkContractStateHandler: CheckContractStateHandler,
    private val checkExistenceSupplierReferencesInFCHandler: CheckExistenceSupplierReferencesInFCHandler,
    private val createConfirmationRequestsHandler: CreateConfirmationRequestsHandler,
    private val createFrameworkContractHandler: CreateFrameworkContractHandler,
    private val doPacsHandler: DoPacsHandler,
    private val findCANIdsHandler: FindCANIdsHandler,
    private val findPacsByLotIdsHandler: FindPacsByLotIdsHandler,
    private val setStateForContractsHandler: SetStateForContractsHandler,
) {

    fun execute(descriptor: CommandDescriptor): ApiResponseV2 {
        return when (descriptor.action) {
            CommandTypeV2.ADD_GENERATED_DOCUMENT_TO_CONTRACT -> addGeneratedDocumentToContractHandler.handle(descriptor)
            CommandTypeV2.ADD_SUPPLIER_REFERENCES_IN_FC -> addSupplierReferencesInFCHandler.handle(descriptor)
            CommandTypeV2.CANCEL_FRAMEWORK_CONTRACT -> cancelFrameworkContractHandler.handle(descriptor)
            CommandTypeV2.CHECK_CONTRACT_STATE -> checkContractStateHandler.handle(descriptor)
            CommandTypeV2.CHECK_EXISTENCE_SUPPLIER_REFERENCES_IN_FC -> checkExistenceSupplierReferencesInFCHandler.handle(descriptor)
            CommandTypeV2.CREATE_CONFIRMATION_REQUESTS -> createConfirmationRequestsHandler.handle(descriptor)
            CommandTypeV2.CREATE_FRAMEWORK_CONTRACT -> createFrameworkContractHandler.handle(descriptor)
            CommandTypeV2.FIND_CAN_IDS -> findCANIdsHandler.handle(descriptor)
            CommandTypeV2.FIND_PACS_BY_LOT_IDS -> findPacsByLotIdsHandler.handle(descriptor)
            CommandTypeV2.DO_PACS -> doPacsHandler.handle(descriptor)
            CommandTypeV2.SET_STATE_FOR_CONTRACTS -> setStateForContractsHandler.handle(descriptor)

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
