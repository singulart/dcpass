package io.argorand.poc.dcpass.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PassPaymentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static PassPayment getPassPaymentSample1() {
        return new PassPayment()
            .id(1L)
            .agencyCode("agencyCode1")
            .agencyAcronym("agencyAcronym1")
            .agencyName("agencyName1")
            .contractNumber("contractNumber1")
            .supplierName("supplierName1")
            .invoiceNumber("invoiceNumber1")
            .poNumber("poNumber1")
            .voucherNumber("voucherNumber1")
            .fiscalYear(1)
            .transactionCode("transactionCode1")
            .paymentType("paymentType1")
            .paymentNumber("paymentNumber1")
            .objectId(1L);
    }

    public static PassPayment getPassPaymentSample2() {
        return new PassPayment()
            .id(2L)
            .agencyCode("agencyCode2")
            .agencyAcronym("agencyAcronym2")
            .agencyName("agencyName2")
            .contractNumber("contractNumber2")
            .supplierName("supplierName2")
            .invoiceNumber("invoiceNumber2")
            .poNumber("poNumber2")
            .voucherNumber("voucherNumber2")
            .fiscalYear(2)
            .transactionCode("transactionCode2")
            .paymentType("paymentType2")
            .paymentNumber("paymentNumber2")
            .objectId(2L);
    }

    public static PassPayment getPassPaymentRandomSampleGenerator() {
        return new PassPayment()
            .id(longCount.incrementAndGet())
            .agencyCode(UUID.randomUUID().toString())
            .agencyAcronym(UUID.randomUUID().toString())
            .agencyName(UUID.randomUUID().toString())
            .contractNumber(UUID.randomUUID().toString())
            .supplierName(UUID.randomUUID().toString())
            .invoiceNumber(UUID.randomUUID().toString())
            .poNumber(UUID.randomUUID().toString())
            .voucherNumber(UUID.randomUUID().toString())
            .fiscalYear(intCount.incrementAndGet())
            .transactionCode(UUID.randomUUID().toString())
            .paymentType(UUID.randomUUID().toString())
            .paymentNumber(UUID.randomUUID().toString())
            .objectId(longCount.incrementAndGet());
    }
}
