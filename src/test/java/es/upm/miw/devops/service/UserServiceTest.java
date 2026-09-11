package es.upm.miw.devops.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class UserServiceTest {

    private final UserService userService = new UserService();

    @Test
    void testReadByIdFound() {
        assertThat(userService.readById("1"))
                .satisfies(user -> {
                    assertThat(user.getName()).isEqualTo("Oscar");
                    assertThat(user.getFamilyName()).isEqualTo("Fernandez");
                });
    }

    @Test
    void testReadByIdNotFound() {
        assertThatThrownBy(() -> userService.readById("999"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(exception -> ((ResponseStatusException) exception).getStatusCode())
                .isEqualTo(NOT_FOUND);
    }
}
