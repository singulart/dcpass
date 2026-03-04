package io.argorand.poc.dcpass.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.argorand.poc.dcpass.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PassContractDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PassContractDTO.class);
        PassContractDTO passContractDTO1 = new PassContractDTO();
        passContractDTO1.setId(1L);
        PassContractDTO passContractDTO2 = new PassContractDTO();
        assertThat(passContractDTO1).isNotEqualTo(passContractDTO2);
        passContractDTO2.setId(passContractDTO1.getId());
        assertThat(passContractDTO1).isEqualTo(passContractDTO2);
        passContractDTO2.setId(2L);
        assertThat(passContractDTO1).isNotEqualTo(passContractDTO2);
        passContractDTO1.setId(null);
        assertThat(passContractDTO1).isNotEqualTo(passContractDTO2);
    }
}
