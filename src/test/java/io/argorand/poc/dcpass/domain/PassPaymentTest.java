package io.argorand.poc.dcpass.domain;

import static io.argorand.poc.dcpass.domain.PassPaymentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.argorand.poc.dcpass.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PassPaymentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PassPayment.class);
        PassPayment passPayment1 = getPassPaymentSample1();
        PassPayment passPayment2 = new PassPayment();
        assertThat(passPayment1).isNotEqualTo(passPayment2);

        passPayment2.setId(passPayment1.getId());
        assertThat(passPayment1).isEqualTo(passPayment2);

        passPayment2 = getPassPaymentSample2();
        assertThat(passPayment1).isNotEqualTo(passPayment2);
    }
}
