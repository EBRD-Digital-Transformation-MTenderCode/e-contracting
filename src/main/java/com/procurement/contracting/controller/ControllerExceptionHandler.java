package com.procurement.contracting.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.procurement.contracting.exception.ErrorInsertException;
import com.procurement.contracting.exception.ValidationException;
import com.procurement.contracting.model.dto.response.ErrorInsertResponse;
import com.procurement.contracting.model.dto.response.MappingErrorResponse;
import com.procurement.contracting.model.dto.response.ValidationErrorResponse;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerExceptionHandler {

    public static final String ERROR_MESSAGE = "Houston we have a problem";

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ValidationErrorResponse handleValidationContractProcessPeriod(
        final ValidationException e) {

        return new ValidationErrorResponse(
            ERROR_MESSAGE,
            e.getErrors()
             .getFieldErrors()
             .stream()
             .map(f -> new ValidationErrorResponse.ErrorPoint(
                 f.getField(),
                 f.getDefaultMessage(),
                 f.getCode()))
             .collect(Collectors.toList()));
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JsonMappingException.class)
    public MappingErrorResponse handleJsonMappingException(final JsonMappingException e) {

        return new MappingErrorResponse(ERROR_MESSAGE, e);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ErrorInsertException.class)
    public ErrorInsertResponse handleErrorInsertException(final ErrorInsertException e) {
        return new ErrorInsertResponse(e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NullPointerException.class)
    public ErrorInsertResponse handleNullPointerException(final NullPointerException e) {
        return new ErrorInsertResponse(e.getMessage());
    }
}
