package com.starto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BillingType {
    ONE_TIME,
    RECURRING;

    public static BillingType fromString(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return BillingType.valueOf(value.toUpperCase()
                .trim()
                .replace(" ", "_")
                .replace("-", "_"));
        } catch (IllegalArgumentException e) {
            return ONE_TIME; // Default fallback for safety
        }
    }

    @JsonCreator
    public static BillingType fromJson(String value) {
        return fromString(value);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
