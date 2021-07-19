package nl.rabobank.api;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.generated.model.ErrorResponse;
import nl.rabobank.service.InvalidAccountException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception exception) {
        log.error("General Exception", exception);
        return ResponseEntity.internalServerError().body(errorResponse(INTERNAL_SERVER_ERROR, exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMissingUserId(MissingRequestHeaderException exception) {
        log.error("Unauthorized", exception);
        return ResponseEntity.status(UNAUTHORIZED).body(errorResponse(UNAUTHORIZED, exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleInvalidAccount(InvalidAccountException exception) {
        log.error("Invalid/Unknown account", exception);
        return badRequestResponse(exception.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        log.error("Http message not readable", exception);
        return badRequestResponse(ofNullable(exception.getCause())
                                    .filter(UnrecognizedPropertyException.class::isInstance)
                                    .map(UnrecognizedPropertyException.class::cast)
                                    .map(this::getMessage)
                                    .orElseGet(exception::getMessage));
    }

    private String getMessage(UnrecognizedPropertyException exception) {
        return format("Unrecognized property: %s", exception.getPropertyName());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMissingProperty(MethodArgumentNotValidException exception) {
        log.error("Missing property", exception);
        return badRequestResponse(getMessage(exception));
    }

    private String getMessage(MethodArgumentNotValidException exception) {
        return format("Invalid/missing value for property(s): %s",
            exception.getBindingResult().getFieldErrors().stream().map(FieldError::getField).collect(joining(",")));
    }

    private ResponseEntity<ErrorResponse> badRequestResponse(String message) {
        return ResponseEntity.badRequest().body(errorResponse(BAD_REQUEST, message));
    }

    private ErrorResponse errorResponse(HttpStatus status, String message) {
        return new ErrorResponse()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message);
    }
}
