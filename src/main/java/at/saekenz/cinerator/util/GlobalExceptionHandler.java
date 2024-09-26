package at.saekenz.cinerator.util;

import jakarta.persistence.EntityNotFoundException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.PropertyValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
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

    // handles Exception thrown by getReferenceById()
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseStatusException handleEntityNotFoundException(EntityNotFoundException ex) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // TODO: remove?
    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<Object> handleObjectNotFoundException(ObjectNotFoundException ex, WebRequest request) {
        log.error("ObjectNotFoundException occurred: {}", ex.getMessage(), ex);
        String errorMessage = String.format("%s with id %s could not be found!",
                ex.getEntityName(), ex.getIdentifier());

        return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}
