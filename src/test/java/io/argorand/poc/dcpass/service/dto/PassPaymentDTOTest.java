package io.argorand.poc.dcpass.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.argorand.poc.dcpass.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PassPaymentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PassPaymentDTO.class);
        PassPaymentDTO passPaymentDTO1 = new PassPaymentDTO();
        passPaymentDTO1.setId(1L);
        PassPaymentDTO passPaymentDTO2 = new PassPaymentDTO();
        assertThat(passPaymentDTO1).isNotEqualTo(passPaymentDTO2);
        passPaymentDTO2.setId(passPaymentDTO1.getId());
        assertThat(passPaymentDTO1).isEqualTo(passPaymentDTO2);
        passPaymentDTO2.setId(2L);
        assertThat(passPaymentDTO1).isNotEqualTo(passPaymentDTO2);
        passPaymentDTO1.setId(null);
        assertThat(passPaymentDTO1).isNotEqualTo(passPaymentDTO2);
    }
}
