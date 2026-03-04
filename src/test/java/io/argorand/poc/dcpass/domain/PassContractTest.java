package io.argorand.poc.dcpass.domain;

import static io.argorand.poc.dcpass.domain.PassContractTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.argorand.poc.dcpass.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PassContractTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PassContract.class);
        PassContract passContract1 = getPassContractSample1();
        PassContract passContract2 = new PassContract();
        assertThat(passContract1).isNotEqualTo(passContract2);

        passContract2.setId(passContract1.getId());
        assertThat(passContract1).isEqualTo(passContract2);

        passContract2 = getPassContractSample2();
        assertThat(passContract1).isNotEqualTo(passContract2);
    }
}
