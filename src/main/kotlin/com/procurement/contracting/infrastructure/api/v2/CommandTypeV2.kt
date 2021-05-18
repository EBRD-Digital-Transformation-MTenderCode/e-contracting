package com.procurement.contracting.infrastructure.api.v2

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider
import com.procurement.contracting.infrastructure.api.Action

enum class CommandTypeV2(@JsonValue override val key: String) : EnumElementProvider.Element, Action {

    ADD_GENERATED_DOCUMENT_TO_CONTRACT("addGeneratedDocumentToContract"),
    ADD_SUPPLIER_REFERENCES_IN_FC("addSupplierReferencesInFC"),
    CANCEL_FRAMEWORK_CONTRACT("cancelFrameworkContract"),
    CHECK_ACCESS_TO_REQUEST_OF_CONFIRMATION("checkAccessToRequestOfConfirmation"),
    CHECK_CONTRACT_STATE("checkContractState"),
    CHECK_EXISTENCE_OF_CONFIRMATION_RESPONSES("checkExistenceOfConfirmationResponses"),
    CHECK_EXISTENCE_SUPPLIER_REFERENCES_IN_FC("checkExistenceSupplierReferencesInFC"),
    CREATE_CONFIRMATION_REQUESTS("createConfirmationRequests"),
    CREATE_CONFIRMATION_RESPONSE("createConfirmationResponse"),
    CREATE_FRAMEWORK_CONTRACT("createFrameworkContract"),
    DO_PACS("doPacs"),
    FIND_CAN_IDS("findCANIds"),
    FIND_PACS_BY_LOT_IDS("findPacsByLotIds"),
    GET_CONTRACT_STATE("getContractState"),
    GET_REQUEST_BY_CONFIRMATION_RESPONSE("getRequestByConfirmationResponse"),
    SET_STATE_FOR_CONTRACTS("setStateForContracts"),
    VALIDATE_CONFIRMATION_RESPONSE_DATA("validateConfirmationResponseData"),
    ;

    override fun toString(): String = key

    companion object : EnumElementProvider<CommandTypeV2>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = CommandTypeV2.orThrow(name)
    }
}
