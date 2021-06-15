package com.springlearn.Springboot.tutorial.error;

import com.springlearn.Springboot.tutorial.entity.ApiError;
import com.springlearn.Springboot.tutorial.entity.FieldValidationError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

//the class that will handle all the exceptions that will be send back as a response
//extends ResponseEntityExceptionHandler
//ControllerAdvice - need the annotation - this is the class that will handle all the exceptions
//this will create a response for the exception and send it back as a response object
@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {
    //400
    //MethodArgumentNotValidException: this exception is thrown when argument annotated with @Valid failed validation
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        List<FieldValidationError> errors = new ArrayList<>();

        for(FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(new FieldValidationError(error.getField(), error.getDefaultMessage()));
        }
        for(ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(new FieldValidationError(error.getObjectName(), error.getDefaultMessage()));
        }

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                errors
        );
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(
            MissingServletRequestPartException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        String error = ex.getRequestPartName() + " parameter is missing";
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getRequestPartName(), error);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    //MissingServletRequestParameterException: this exception is thrown when request missing parameter
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getParameterName(), error);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    //MethodArgumentTypeMismatchException: this exception is thrown when method argument is not the expected type
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex,
            WebRequest request) {
        String error = ex.getName() + " should be of type " + ex.getRequiredType().getName();
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getName(), error);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    //ConstraintViolationException: this exception reports the result of constraint violations
    //@PathVariable & @RequestParam
    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        List<FieldValidationError> errors = new ArrayList<>();
        String fieldName = null;

        for(ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(new FieldValidationError(violation.getPropertyPath().toString(), violation.getMessage()));
        }

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, errors);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(DepartmentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleDepartmentNotFoundException(
            DepartmentNotFoundException ex,
            WebRequest request) {
        System.out.println(request.getDescription(false));
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, request.getDescription(false), ex.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

}
