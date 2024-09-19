package at.saekenz.cinerator.util;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.PropertyValueException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(PropertyValueException.class)
    public ResponseEntity<Object> handlePropertyValueException(PropertyValueException ex, WebRequest request) {
        log.error("PropertyValueException occurred: {}", ex.getMessage(), ex);
        String errorMessage = String.format("The property '%s' in entity '%s' must not be null or invalid!",
                ex.getPropertyName(), ex.getEntityName());

        return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(JpaObjectRetrievalFailureException.class)
    public ResponseEntity<Object> handleJpaObjectRetrievalFailureException(JpaObjectRetrievalFailureException ex,
                                                                           WebRequest request) {
        log.error("JpaObjectRetrievalFailureException occurred: {}", ex.getMessage(), ex);
        String errorMessage = String.format("%s with id %s could not be found!",
                ex.getPersistentClassName(), ex.getIdentifier());

        return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<Object> handleObjectNotFoundException(ObjectNotFoundException ex, WebRequest request) {
        log.error("ObjectNotFoundException occurred: {}", ex.getMessage(), ex);
        String errorMessage = String.format("%s with id %s could not be found!",
                ex.getEntityName(), ex.getIdentifier());

        return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("ConstraintViolationException occurred: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Create/Update failed!",
                String.format("Error caused by: %s", ex.getConstraintName()));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
