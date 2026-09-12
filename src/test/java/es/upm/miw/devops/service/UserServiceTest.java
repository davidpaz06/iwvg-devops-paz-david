package es.upm.miw.devops.service;

import es.upm.miw.devops.persistence.User;
import es.upm.miw.devops.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testReadByIdFound() {
        User user = new User();
        user.setName("Oscar");
        user.setFamilyName("Fernandez");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThat(userService.readById(1L))
                .satisfies(found -> {
                    assertThat(found.getName()).isEqualTo("Oscar");
                    assertThat(found.getFamilyName()).isEqualTo("Fernandez");
                });
    }

    @Test
    void testReadByIdNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.readById(999L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(exception -> ((ResponseStatusException) exception).getStatusCode())
                .isEqualTo(NOT_FOUND);
    }

    @Test
    void testReadAllWithoutFilter() {
        User billable = billableUser();
        User notBillable = new User();
        when(userRepository.findAll()).thenReturn(List.of(billable, notBillable));

        assertThat(userService.readAll(null)).hasSize(2);
    }

    @Test
    void testReadAllBillableTrue() {
        User billable = billableUser();
        User notBillable = new User();
        when(userRepository.findAll()).thenReturn(List.of(billable, notBillable));

        assertThat(userService.readAll(true)).containsExactly(billable);
    }

    @Test
    void testReadAllBillableFalse() {
        User billable = billableUser();
        User notBillable = new User();
        when(userRepository.findAll()).thenReturn(List.of(billable, notBillable));

        assertThat(userService.readAll(false)).containsExactly(notBillable);
    }

    private User billableUser() {
        User user = new User();
        user.setName("Oscar");
        user.setFamilyName("Fernandez");
        user.setEmail("oscar@upm.es");
        user.setIdentity("12345678A");
        user.setAddress("Calle Falsa 123");
        user.setCity("Madrid");
        user.setProvince("Madrid");
        user.setPostalCode("28000");
        return user;
    }
}
