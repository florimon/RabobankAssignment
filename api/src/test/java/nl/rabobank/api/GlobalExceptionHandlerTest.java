package nl.rabobank.api;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import nl.rabobank.generated.model.ErrorResponse;
import nl.rabobank.service.InvalidAccountException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;

import java.lang.reflect.Executable;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler sut = new GlobalExceptionHandler();

    @Test
    void testHandleGeneralException() {
        ResponseEntity<ErrorResponse> responseEntity = sut.handleGeneralException(new RuntimeException("generic exception"));

        assertResponseIs(responseEntity, HttpStatus.INTERNAL_SERVER_ERROR, "generic exception");
    }

    @Test
    void testHandleMissingUserId() {
        MissingRequestHeaderException missingRequestHeaderException = mock(MissingRequestHeaderException.class);
        String errorMessage = "Required request header 'X-UserId' for method parameter type String is not present";
        when(missingRequestHeaderException.getMessage()).thenReturn(errorMessage);
        ResponseEntity<ErrorResponse> responseEntity = sut.handleMissingUserId(missingRequestHeaderException);

        assertResponseIs(responseEntity, HttpStatus.UNAUTHORIZED, errorMessage);
    }

    @Test
    void testHandleInvalidAccount() {
        ResponseEntity<ErrorResponse> responseEntity = sut.handleInvalidAccount(new InvalidAccountException("RABO001", "testuser", true));

        assertResponseIs(responseEntity, HttpStatus.BAD_REQUEST, "No Payment account with number 'RABO001' for holder 'testuser'");
    }

    @Test
    void testHandleHttpMessageNotReadableCausedByUnrecognizedProperty() {
        HttpMessageNotReadableException httpMessageNotReadableException = mock(HttpMessageNotReadableException.class);
        UnrecognizedPropertyException unrecognizedPropertyException = mock(UnrecognizedPropertyException.class);
        when(httpMessageNotReadableException.getCause()).thenReturn(unrecognizedPropertyException);
        when(unrecognizedPropertyException.getPropertyName()).thenReturn("unknownProperty");

        ResponseEntity<ErrorResponse> responseEntity = sut.handleHttpMessageNotReadable(httpMessageNotReadableException);

        assertResponseIs(responseEntity, HttpStatus.BAD_REQUEST, "Unrecognized property: unknownProperty");
    }

    @Test
    void testHandleHttpMessageNotReadableCausedBySomethingElse() {
        HttpMessageNotReadableException httpMessageNotReadableException = mock(HttpMessageNotReadableException.class);
        when(httpMessageNotReadableException.getCause()).thenReturn(new RuntimeException());
        when(httpMessageNotReadableException.getMessage()).thenReturn("Some other message");

        ResponseEntity<ErrorResponse> responseEntity = sut.handleHttpMessageNotReadable(httpMessageNotReadableException);

        assertResponseIs(responseEntity, HttpStatus.BAD_REQUEST, "Some other message");
    }

    @Test
    void testHandleMissingProperty() {
        MethodArgumentNotValidException methodArgumentNotValidException = new MethodArgumentNotValidException(
                mockMethodParameter(), mockBindingResult("field1", "field2"));

        ResponseEntity<ErrorResponse> responseEntity = sut.handleMissingProperty(methodArgumentNotValidException);

        assertResponseIs(responseEntity, HttpStatus.BAD_REQUEST, "Invalid/missing value for property(s): field1,field2");
    }

    private BindingResult mockBindingResult(String... fieldNames) {
        BindingResult bindingResult = mock(BindingResult.class);
        List<FieldError> fieldErrors = Arrays.stream(fieldNames).map(this::mockFieldError).collect(toList());
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        return bindingResult;
    }

    private FieldError mockFieldError(String fieldName) {
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn(fieldName);
        return fieldError;
    }

    private MethodParameter mockMethodParameter() {
        MethodParameter methodParameter = mock(MethodParameter.class);
        when(methodParameter.getParameterIndex()).thenReturn(0);
        Executable executable = mock(Executable.class);
        when(executable.toGenericString()).thenReturn("genericString");
        when(methodParameter.getExecutable()).thenReturn(executable);
        return methodParameter;
    }


    private void assertResponseIs(ResponseEntity<ErrorResponse> responseEntity, HttpStatus status, String message) {
        assertEquals(status, responseEntity.getStatusCode());
        assertEquals(status.value(), responseEntity.getBody().getStatus());
        assertEquals(status.getReasonPhrase(), responseEntity.getBody().getError());
        assertEquals(message, responseEntity.getBody().getMessage());
    }
}
