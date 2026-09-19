package es.upm.miw.devops.rest.exceptionshandler;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorMessageTest {

    @Test
    void testFieldsFromException() {
        ErrorMessage errorMessage = new ErrorMessage(new IllegalArgumentException("boom"), 400);

        assertThat(errorMessage.getError()).isEqualTo("IllegalArgumentException");
        assertThat(errorMessage.getMessage()).isEqualTo("boom");
        assertThat(errorMessage.getCode()).isEqualTo(400);
    }

    @Test
    void testToString() {
        ErrorMessage errorMessage = new ErrorMessage(new IllegalArgumentException("boom"), 400);

        assertThat(errorMessage.toString())
                .contains("IllegalArgumentException")
                .contains("boom")
                .contains("400");
    }
}
