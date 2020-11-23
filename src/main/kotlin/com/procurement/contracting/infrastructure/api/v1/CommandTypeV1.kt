package com.procurement.contracting.infrastructure.api.v1

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider
import com.procurement.contracting.infrastructure.api.Action

enum class CommandTypeV1(@JsonValue override val key: String): EnumElementProvider.Key, Action {

    CHECK_CAN(key = "checkCan"),
    CHECK_CAN_BY_AWARD(key = "checkCanBiAwardId"),
    CREATE_CAN(key = "createCan"),
    GET_CANS(key = "getCans"),
    UPDATE_CAN_DOCS(key = "updateCanDocs"),
    CANCEL_CAN(key = "cancelCan"),
    CONFIRMATION_CAN(key = "confirmationCan"),
    CREATE_AC(key = "createAC"),
    UPDATE_AC(key = "updateAC"),
    GET_BUDGET_SOURCES(key = "getActualBudgetSources"),
    CHECK_STATUS_DETAILS(key = "contractingCheckStatusDetails"),
    GET_RELATED_BID_ID(key = "getRelatedBidId"),
    ISSUING_AC(key = "issuingAC"),
    FINAL_UPDATE(key = "finalUpdateAC"),
    BUYER_SIGNING_AC(key = "buyerSigningAC"),
    SUPPLIER_SIGNING_AC(key = "supplierSigningAC"),
    VERIFICATION_AC(key = "verificationAC"),
    TREASURY_RESPONSE_PROCESSING(key = "treasuryResponseProcessing"),
    ACTIVATION_AC(key = "activationAC");

    override fun toString(): String = key
}
