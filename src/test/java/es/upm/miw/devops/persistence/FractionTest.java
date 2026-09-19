package es.upm.miw.devops.persistence;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FractionTest {

    @Test
    void testGettersAndSetters() {
        Fraction fraction = new Fraction();
        User user = new User();

        fraction.setNumerator(1);
        fraction.setDenominator(2);
        fraction.setUser(user);

        assertThat(fraction.getId()).isNull();
        assertThat(fraction.getNumerator()).isEqualTo(1);
        assertThat(fraction.getDenominator()).isEqualTo(2);
        assertThat(fraction.getUser()).isSameAs(user);
    }
}
