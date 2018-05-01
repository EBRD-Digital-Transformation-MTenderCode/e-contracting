package com.procurement.contracting.exception;

public enum ErrorType {

    CANS_NOT_FOUND("00.01", "CANs not found."),
    INVALID_OWNER("00.02", "Invalid owner."),
    INVALID_ID("00.03", "Invalid CAN id."),
    CONTRACT_ALREADY_CREATED("00.04", "Contract already created."),
    DOCUMENTS_IS_INVALID("00.06", "Documents is not valid"),
    NO_AMENDMENTS("00.07", "No amendments."),
    AC_NOT_FOUND("00.08", "AC not found"),
    BUDGET_SUM_IS_NOT_VALID("00.09", "Budget sum is invalid"),
    INVALID_PLATFORM("00.10", "Invalid platform"),
    INVALID_DATE("00.11", "Date Signed is empty"),
    NO_ACTIVE_AWARDS("00.12", "No active awards."),
    NO_COMPLETED_LOT("00.13", "No completed lot found."),
    NO_ITEMS("00.14", "No items for related lot found.");

    private final String code;
    private final String message;

    ErrorType(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
