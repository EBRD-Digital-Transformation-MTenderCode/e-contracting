package com.procurement.contracting.model.dto.response;

import java.util.List;
import lombok.Getter;

@Getter
public class ValidationErrorResponse {
    private final String message;
    private final List<ErrorPoint> errors;

    public ValidationErrorResponse(final String message, final List<ErrorPoint> errors) {
        this.message = message;
        this.errors = errors;
    }

    @Getter
    public static class ErrorPoint {
        private final String field;
        private final String message;
        private final String code;

        public ErrorPoint(final String field, final String message, final String code) {
            this.field = field;
            this.message = message;
            this.code = code;
        }
    }
}
