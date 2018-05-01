package com.procurement.contracting.model.dto.ocds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

public enum ContractStatusDetails {
    CONTRACT_PROJECT("contractProject"),
    ACTIVE("active"),
    VERIFIED("verified"),
    CANCELLED("cancelled"),
    COMPLETE("complete"),
    UNSUCCESSFUL("unsuccessful"),
    EMPTY("empty");

    private static final Map<String, ContractStatusDetails> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final ContractStatusDetails c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    ContractStatusDetails(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static ContractStatusDetails fromValue(final String value) {
        final ContractStatusDetails constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        }
        return constant;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }
}
