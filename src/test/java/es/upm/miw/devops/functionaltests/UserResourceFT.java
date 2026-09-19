package es.upm.miw.devops.functionaltests;

import es.upm.miw.devops.persistence.User;
import es.upm.miw.devops.persistence.UserRepository;
import es.upm.miw.devops.rest.dto.UserActiveRequest;
import es.upm.miw.devops.rest.dto.UserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static es.upm.miw.devops.rest.UserResource.USERS;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class UserResourceFT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testReadByIdFound() {
        webTestClient.get()
                .uri(USERS + "/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(user -> {
                    assertThat(user.getName()).isEqualTo("Oscar");
                    assertThat(user.getFamilyName()).isEqualTo("Fernandez");
                });
    }

    @Test
    void testReadByIdNotFound() {
        webTestClient.get()
                .uri(USERS + "/999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testReadAllWithoutFilter() {
        webTestClient.get()
                .uri(USERS)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .value(users -> assertThat(users).extracting(User::getId).contains(1L));
    }

    @Test
    void testReadAllNotBillable() {
        webTestClient.get()
                .uri(USERS + "?billable=false")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .value(users -> assertThat(users)
                        .extracting(User::getId)
                        .contains(1L));
    }

    @Test
    void testReadAllBillable() {
        webTestClient.get()
                .uri(USERS + "?billable=true")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .value(users -> assertThat(users)
                        .extracting(User::getId)
                        .doesNotContain(1L));
    }

    @Test
    void testDeleteByIdFound() {
        User user = new User();
        user.setName("Temp");
        user.setFamilyName("Temp");
        Long id = userRepository.save(user).getId();

        webTestClient.delete()
                .uri(USERS + "/" + id)
                .exchange()
                .expectStatus().isNoContent();

        assertThat(userRepository.existsById(id)).isFalse();
    }

    @Test
    void testDeleteByIdNotFound() {
        webTestClient.delete()
                .uri(USERS + "/999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateActiveFound() {
        User user = new User();
        user.setName("Temp");
        user.setFamilyName("Temp");
        user.setActive(true);
        Long id = userRepository.save(user).getId();

        webTestClient.put()
                .uri(USERS + "/" + id + "/active")
                .bodyValue(false)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(updated -> assertThat(updated.isActive()).isFalse());
    }

    @Test
    void testUpdateActiveNotFound() {
        webTestClient.put()
                .uri(USERS + "/999/active")
                .bodyValue(false)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateActiveAdminCannotBeDeactivated() {
        User admin = new User();
        admin.setName("Temp");
        admin.setFamilyName("Admin");
        admin.setRole("ADMIN");
        admin.setActive(true);
        Long id = userRepository.save(admin).getId();

        webTestClient.put()
                .uri(USERS + "/" + id + "/active")
                .bodyValue(false)
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    @Test
    void testUpdateFound() {
        User user = new User();
        user.setName("Temp");
        user.setFamilyName("Temp");
        Long id = userRepository.save(user).getId();

        UserRequest payload = new UserRequest(
                "Updated", "Updated", "updated@upm.es", "00000000A",
                "Nueva Calle 1", "Madrid", "Madrid", "28001");

        webTestClient.put()
                .uri(USERS + "/" + id)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(updated -> {
                    assertThat(updated.getName()).isEqualTo("Updated");
                    assertThat(updated.getEmail()).isEqualTo("updated@upm.es");
                });
    }

    @Test
    void testUpdateNotFound() {
        webTestClient.put()
                .uri(USERS + "/999")
                .bodyValue(new UserRequest(null, null, null, null, null, null, null, null))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateActiveBulkFound() {
        User user1 = new User();
        user1.setName("Temp1");
        user1.setFamilyName("Temp1");
        user1.setActive(true);
        Long id1 = userRepository.save(user1).getId();

        User user2 = new User();
        user2.setName("Temp2");
        user2.setFamilyName("Temp2");
        user2.setActive(true);
        Long id2 = userRepository.save(user2).getId();

        webTestClient.patch()
                .uri(USERS)
                .bodyValue(List.of(
                        new UserActiveRequest(id1, false),
                        new UserActiveRequest(id2, false)))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .value(users -> assertThat(users).extracting(User::isActive).containsExactly(false, false));
    }

    @Test
    void testUpdateActiveBulkNotFound() {
        webTestClient.patch()
                .uri(USERS)
                .bodyValue(List.of(new UserActiveRequest(999L, false)))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateActiveBulkAdminCannotBeDeactivated() {
        User admin = new User();
        admin.setName("Temp");
        admin.setFamilyName("Admin");
        admin.setRole("ADMIN");
        admin.setActive(true);
        Long id = userRepository.save(admin).getId();

        webTestClient.patch()
                .uri(USERS)
                .bodyValue(List.of(new UserActiveRequest(id, false)))
                .exchange()
                .expectStatus().isEqualTo(409);
    }
}
