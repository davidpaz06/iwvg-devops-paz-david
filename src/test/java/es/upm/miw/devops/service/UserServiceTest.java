package es.upm.miw.devops.service;

import es.upm.miw.devops.persistence.User;
import es.upm.miw.devops.persistence.UserRepository;
import es.upm.miw.devops.rest.dto.UserActiveRequest;
import es.upm.miw.devops.rest.dto.UserRequest;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CONFLICT;
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

    @Test
    void testDeleteByIdFound() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteById(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteByIdNotFound() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteById(999L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(exception -> ((ResponseStatusException) exception).getStatusCode())
                .isEqualTo(NOT_FOUND);
        verify(userRepository, never()).deleteById(999L);
    }

    @Test
    void testUpdateActiveFound() {
        User user = new User();
        user.setActive(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User updated = userService.updateActive(1L, false);

        assertThat(updated.isActive()).isFalse();
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateActiveNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateActive(999L, false))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(exception -> ((ResponseStatusException) exception).getStatusCode())
                .isEqualTo(NOT_FOUND);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateActiveAdminCannotBeDeactivated() {
        User admin = new User();
        admin.setRole("ADMIN");
        admin.setActive(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> userService.updateActive(1L, false))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(exception -> ((ResponseStatusException) exception).getStatusCode())
                .isEqualTo(CONFLICT);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateActiveAdminCanBeActivated() {
        User admin = new User();
        admin.setRole("ADMIN");
        admin.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.save(admin)).thenReturn(admin);

        User updated = userService.updateActive(1L, true);

        assertThat(updated.isActive()).isTrue();
    }

    @Test
    void testUpdateFound() {
        User existing = new User();
        existing.setName("Oscar");
        existing.setFamilyName("Fernandez");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        UserRequest payload = new UserRequest(
                "Oscar Updated", "Fernandez Updated", "oscar@upm.es", "12345678A",
                "Calle Falsa 123", "Madrid", "Madrid", "28000");

        User updated = userService.update(1L, payload);

        assertThat(updated.getName()).isEqualTo("Oscar Updated");
        assertThat(updated.getFamilyName()).isEqualTo("Fernandez Updated");
        assertThat(updated.getEmail()).isEqualTo("oscar@upm.es");
        assertThat(updated.getIdentity()).isEqualTo("12345678A");
        assertThat(updated.getAddress()).isEqualTo("Calle Falsa 123");
        assertThat(updated.getCity()).isEqualTo("Madrid");
        assertThat(updated.getProvince()).isEqualTo("Madrid");
        assertThat(updated.getPostalCode()).isEqualTo("28000");
        verify(userRepository).save(existing);
    }

    @Test
    void testUpdateNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(999L, new UserRequest(null, null, null, null, null, null, null, null)))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(exception -> ((ResponseStatusException) exception).getStatusCode())
                .isEqualTo(NOT_FOUND);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateActiveBulkFound() {
        User user1 = new User();
        user1.setActive(true);
        User user2 = new User();
        user2.setActive(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(userRepository.save(user1)).thenReturn(user1);
        when(userRepository.save(user2)).thenReturn(user2);

        List<User> updated = userService.updateActive(List.of(
                new UserActiveRequest(1L, false),
                new UserActiveRequest(2L, false)));

        assertThat(updated).extracting(User::isActive).containsExactly(false, false);
    }

    @Test
    void testUpdateActiveBulkNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateActive(List.of(new UserActiveRequest(999L, false))))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(exception -> ((ResponseStatusException) exception).getStatusCode())
                .isEqualTo(NOT_FOUND);
    }

    @Test
    void testUpdateActiveBulkAdminCannotBeDeactivated() {
        User admin = new User();
        admin.setRole("ADMIN");
        admin.setActive(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> userService.updateActive(List.of(new UserActiveRequest(1L, false))))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(exception -> ((ResponseStatusException) exception).getStatusCode())
                .isEqualTo(CONFLICT);
        verify(userRepository, never()).save(any());
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
