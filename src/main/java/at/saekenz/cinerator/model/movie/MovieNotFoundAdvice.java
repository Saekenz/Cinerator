package at.saekenz.cinerator.model.movie;

import org.hibernate.PropertyValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class MovieNotFoundAdvice extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(MovieNotFoundAdvice.class);

    @ExceptionHandler(MovieNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String movieNotFoundHandler(MovieNotFoundException ex) { return ex.getMessage(); }

    @ExceptionHandler(PropertyValueException.class)
    public ResponseEntity<Object> handlePropertyValueException(PropertyValueException ex, WebRequest request) {
        log.error("PropertyValueException occurred: {}", ex.getMessage(), ex);
        String errorMessage = String.format("The property '%s' in entity '%s' must not be null or invalid!",
                ex.getPropertyName(), ex.getEntityName());

        return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(JpaObjectRetrievalFailureException.class)
    public ResponseEntity<Object> handleJpaObjectRetrievalFailureException(JpaObjectRetrievalFailureException ex, WebRequest request) {
        log.error("JpaObjectRetrievalFailureException occurred: {}", ex.getMessage(), ex);
        String errorMessage = String.format("%s with id %s could not be found!",
                ex.getPersistentClassName(), ex.getIdentifier());

        return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}
