package com.springlearn.Springboot.tutorial.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ApiError {

    //http status code
    private HttpStatus status;

    //local date/time
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime timestamp;

    //list of constructed error message
    private List<FieldValidationError> errors;

    public ApiError() {
        super();
    }

    //multiple error messages
    public ApiError(HttpStatus status, List<FieldValidationError> errors) {
        super();
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.errors = errors;
    }

    //single error message
    public ApiError(HttpStatus status, String field, String message) {
        super();
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.errors = Arrays.asList(new FieldValidationError(field, message));
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public List<FieldValidationError> getErrors() {
        return errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setErrors(List<FieldValidationError> errors) {
        this.errors = errors;
    }

    public void setError(String field, String error) {
        this.errors = Arrays.asList(new FieldValidationError(field, error));
    }
}
