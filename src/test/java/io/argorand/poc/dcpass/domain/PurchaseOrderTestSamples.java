package io.argorand.poc.dcpass.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PurchaseOrderTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static PurchaseOrder getPurchaseOrderSample1() {
        return new PurchaseOrder()
            .id(1L)
            .poNumber("poNumber1")
            .agencyCode("agencyCode1")
            .status("status1")
            .requester("requester1")
            .requisitionNumber("requisitionNumber1")
            .commodityCode("commodityCode1")
            .commodityName("commodityName1")
            .contractNumber("contractNumber1")
            .supplier("supplier1")
            .fiscalYear(1)
            .poTitle("poTitle1")
            .agencyAcronym("agencyAcronym1")
            .agencyName("agencyName1")
            .objectId(1L);
    }

    public static PurchaseOrder getPurchaseOrderSample2() {
        return new PurchaseOrder()
            .id(2L)
            .poNumber("poNumber2")
            .agencyCode("agencyCode2")
            .status("status2")
            .requester("requester2")
            .requisitionNumber("requisitionNumber2")
            .commodityCode("commodityCode2")
            .commodityName("commodityName2")
            .contractNumber("contractNumber2")
            .supplier("supplier2")
            .fiscalYear(2)
            .poTitle("poTitle2")
            .agencyAcronym("agencyAcronym2")
            .agencyName("agencyName2")
            .objectId(2L);
    }

    public static PurchaseOrder getPurchaseOrderRandomSampleGenerator() {
        return new PurchaseOrder()
            .id(longCount.incrementAndGet())
            .poNumber(UUID.randomUUID().toString())
            .agencyCode(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString())
            .requester(UUID.randomUUID().toString())
            .requisitionNumber(UUID.randomUUID().toString())
            .commodityCode(UUID.randomUUID().toString())
            .commodityName(UUID.randomUUID().toString())
            .contractNumber(UUID.randomUUID().toString())
            .supplier(UUID.randomUUID().toString())
            .fiscalYear(intCount.incrementAndGet())
            .poTitle(UUID.randomUUID().toString())
            .agencyAcronym(UUID.randomUUID().toString())
            .agencyName(UUID.randomUUID().toString())
            .objectId(longCount.incrementAndGet());
    }
}
