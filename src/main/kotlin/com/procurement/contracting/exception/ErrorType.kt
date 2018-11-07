package com.procurement.contracting.exception

enum class ErrorType constructor(val code: String, val message: String) {
    JSON_TYPE("00.00", "Invalid type: "),
    CANS_NOT_FOUND("00.01", "CANs not found."),
    CONTRACT_NOT_FOUND("00.02", "Contract not found."),
    OWNER("00.03", "Invalid owner."),
    CAN_ID("00.04", "Invalid CAN id."),
    CONTRACT_ALREADY_CREATED("00.05", "Contract already created."),
    DOCUMENTS("00.06", "Documents is not valid"),
    NO_AMENDMENTS("00.07", "No amendments."),
    BUDGET_SUM("00.09", "Budget sum is invalid"),
    PLATFORM("00.10", "Invalid platform"),
    DATE("00.11", "Date Signed is empty"),
    NO_ACTIVE_AWARDS("00.12", "No active awards."),
    NO_COMPLETED_LOT("00.13", "No completed lot found."),
    NO_ITEMS("00.14", "No items for related lot found."),
    AWARDS_NOT_FOUND("00.15", "Awards not found."),
    CONTRACT_ID("00.16", "Invalid contract id."),
    AWARD_ID("00.17", "Invalid award id."),
    AWARD_VALUE("00.18", "Invalid award value."),
    ITEM_VALUE("00.19", "Invalid item value."),
    CONTEXT("20.01", "Context parameter not found.");
}
