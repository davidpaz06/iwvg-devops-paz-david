package es.upm.miw.devops.rest.exceptionshandler;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler apiExceptionHandler = new ApiExceptionHandler();

    @Test
    void testNoResourceFoundRequest() {
        ErrorMessage errorMessage = apiExceptionHandler.noResourceFoundRequest(
                new NoResourceFoundException(org.springframework.http.HttpMethod.GET, "/unknown"));

        assertThat(errorMessage.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(errorMessage.getMessage()).contains("swagger-ui.html");
    }

    @Test
    void testResponseStatusException() {
        ResponseEntity<ErrorMessage> response = apiExceptionHandler.responseStatusException(
                new ResponseStatusException(HttpStatus.CONFLICT, "conflict"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.getBody().getMessage()).contains("conflict");
    }

    @Test
    void testGenericException() {
        ErrorMessage errorMessage = apiExceptionHandler.exception(new RuntimeException("unexpected"));

        assertThat(errorMessage.getCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(errorMessage.getMessage()).isEqualTo("ERROR");
    }
}
