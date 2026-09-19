package es.upm.miw.devops.persistence;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private User fullyPopulatedUser() {
        User user = new User();
        user.setName("Oscar");
        user.setFamilyName("Fernandez");
        user.setEmail("oscar@upm.es");
        user.setIdentity("00000000A");
        user.setAddress("Calle 1");
        user.setCity("Madrid");
        user.setProvince("Madrid");
        user.setPostalCode("28001");
        return user;
    }

    @Test
    void testIsBillableTrueWhenAllFieldsPresent() {
        assertThat(fullyPopulatedUser().isBillable()).isTrue();
    }

    @Test
    void testIsBillableFalseWhenFieldIsNull() {
        User user = fullyPopulatedUser();
        user.setEmail(null);

        assertThat(user.isBillable()).isFalse();
    }

    @Test
    void testIsBillableFalseWhenFieldIsBlank() {
        User user = fullyPopulatedUser();
        user.setEmail("   ");

        assertThat(user.isBillable()).isFalse();
    }
}
