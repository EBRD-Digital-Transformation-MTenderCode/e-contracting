package com.procurement.contracting.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

public enum ContractStatus {
    PENDING("pending"),
    ACTIVE("active"),
    CANCELLED("cancelled"),
    COMPLETE("complete"),
    TERMINATED("terminated");

    private static final Map<String, ContractStatus> CONSTANTS = new HashMap<>();

    static {
        for (final ContractStatus c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private final String value;

    ContractStatus(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static ContractStatus fromValue(final String value) {
        final ContractStatus constant = CONSTANTS.get(value);
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
