package io.argorand.poc.dcpass.service.mapper;

import static io.argorand.poc.dcpass.domain.PurchaseOrderAsserts.*;
import static io.argorand.poc.dcpass.domain.PurchaseOrderTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PurchaseOrderMapperTest {

    private PurchaseOrderMapperImpl purchaseOrderMapper;

    @BeforeEach
    void setUp() {
        purchaseOrderMapper = new PurchaseOrderMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPurchaseOrderSample1();
        var actual = purchaseOrderMapper.toEntity(purchaseOrderMapper.toDto(expected));
        assertPurchaseOrderAllPropertiesEquals(expected, actual);
    }
}
