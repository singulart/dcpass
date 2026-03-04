package io.argorand.poc.dcpass.service.mapper;

import static io.argorand.poc.dcpass.domain.PassContractAsserts.*;
import static io.argorand.poc.dcpass.domain.PassContractTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PassContractMapperTest {

    private PassContractMapper passContractMapper;

    @BeforeEach
    void setUp() {
        passContractMapper = new PassContractMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPassContractSample1();
        var actual = passContractMapper.toEntity(passContractMapper.toDto(expected));
        assertPassContractAllPropertiesEquals(expected, actual);
    }
}
