package io.argorand.poc.dcpass.service.mapper;

import static io.argorand.poc.dcpass.domain.PassPaymentAsserts.*;
import static io.argorand.poc.dcpass.domain.PassPaymentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PassPaymentMapperTest {

    private PassPaymentMapperImpl passPaymentMapper;

    @BeforeEach
    void setUp() {
        passPaymentMapper = new PassPaymentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPassPaymentSample1();
        var actual = passPaymentMapper.toEntity(passPaymentMapper.toDto(expected));
        assertPassPaymentAllPropertiesEquals(expected, actual);
    }
}
