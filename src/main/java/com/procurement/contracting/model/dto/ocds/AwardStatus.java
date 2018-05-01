package com.procurement.contracting.model.dto.ocds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

public enum AwardStatus {
    PENDING("pending"),
    ACTIVE("active"),
    UNSUCCESSFUL("unsuccessful"),
    CONSIDERATION("consideration"),
    EMPTY("empty");

    private static final Map<String, AwardStatus> CONSTANTS = new HashMap<>();

    static {
        for (final AwardStatus c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private final String value;

    AwardStatus(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static AwardStatus fromValue(final String value) {
        final AwardStatus constant = CONSTANTS.get(value);
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
