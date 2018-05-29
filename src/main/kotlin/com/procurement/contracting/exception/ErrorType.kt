package com.procurement.contracting.exception

enum class ErrorType constructor(val code: String, val message: String) {
    CANS_NOT_FOUND("00.01", "CANs not found."),
    CONTRACT_NOT_FOUND("00.02", "Contract not found."),
    INVALID_OWNER("00.03", "Invalid owner."),
    INVALID_ID("00.04", "Invalid CAN id."),
    CONTRACT_ALREADY_CREATED("00.05", "Contract already created."),
    DOCUMENTS_IS_INVALID("00.06", "Documents is not valid"),
    NO_AMENDMENTS("00.07", "No amendments."),
    BUDGET_SUM_IS_NOT_VALID("00.09", "Budget sum is invalid"),
    INVALID_PLATFORM("00.10", "Invalid platform"),
    INVALID_DATE("00.11", "Date Signed is empty"),
    NO_ACTIVE_AWARDS("00.12", "No active awards."),
    NO_COMPLETED_LOT("00.13", "No completed lot found."),
    NO_ITEMS("00.14", "No items for related lot found.");
}
