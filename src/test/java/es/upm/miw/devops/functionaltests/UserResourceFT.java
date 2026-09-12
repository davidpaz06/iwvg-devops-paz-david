package es.upm.miw.devops.functionaltests;

import es.upm.miw.devops.persistence.User;
import es.upm.miw.devops.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

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
}
