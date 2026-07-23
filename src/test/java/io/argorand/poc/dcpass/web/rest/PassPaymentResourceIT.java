package io.argorand.poc.dcpass.web.rest;

import static io.argorand.poc.dcpass.domain.PassPaymentAsserts.*;
import static io.argorand.poc.dcpass.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.argorand.poc.dcpass.IntegrationTest;
import io.argorand.poc.dcpass.domain.PassPayment;
import io.argorand.poc.dcpass.repository.PassPaymentRepository;
import io.argorand.poc.dcpass.service.dto.PassPaymentDTO;
import io.argorand.poc.dcpass.service.mapper.PassPaymentMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PassPaymentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PassPaymentResourceIT {

    private static final String DEFAULT_AGENCY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_AGENCY_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_AGENCY_ACRONYM = "AAAAAAAAAA";
    private static final String UPDATED_AGENCY_ACRONYM = "BBBBBBBBBB";

    private static final String DEFAULT_AGENCY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_AGENCY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CONTRACT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_CONTRACT_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_SUPPLIER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SUPPLIER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_INVOICE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_INVOICE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_PO_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PO_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_VOUCHER_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_VOUCHER_NUMBER = "BBBBBBBBBB";

    private static final Instant DEFAULT_PAYMENT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PAYMENT_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    private static final Instant SMALLER_PAYMENT_DATE = Instant.ofEpochMilli(-1L);

    private static final BigDecimal DEFAULT_PAYMENT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_PAYMENT_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_PAYMENT_AMOUNT = new BigDecimal(1 - 1);

    private static final Integer DEFAULT_FISCAL_YEAR = 1;
    private static final Integer UPDATED_FISCAL_YEAR = 2;
    private static final Integer SMALLER_FISCAL_YEAR = 1 - 1;

    private static final String DEFAULT_TRANSACTION_CODE = "AAAAAAAAAA";
    private static final String UPDATED_TRANSACTION_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_PAYMENT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_TYPE = "BBBBBBBBBB";

    private static final Instant DEFAULT_INVOICE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_INVOICE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    private static final Instant SMALLER_INVOICE_DATE = Instant.ofEpochMilli(-1L);

    private static final Instant DEFAULT_EST_PAYMENT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EST_PAYMENT_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    private static final Instant SMALLER_EST_PAYMENT_DATE = Instant.ofEpochMilli(-1L);

    private static final String DEFAULT_PAYMENT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_NUMBER = "BBBBBBBBBB";

    private static final Instant DEFAULT_RECORD_UPDATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RECORD_UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    private static final Instant SMALLER_RECORD_UPDATED_DATE = Instant.ofEpochMilli(-1L);

    private static final Instant DEFAULT_RECORD_CREATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RECORD_CREATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    private static final Instant SMALLER_RECORD_CREATED = Instant.ofEpochMilli(-1L);

    private static final Instant DEFAULT_DCS_REC_CRT_DTTM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DCS_REC_CRT_DTTM = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    private static final Instant SMALLER_DCS_REC_CRT_DTTM = Instant.ofEpochMilli(-1L);

    private static final Instant DEFAULT_DCS_LAST_MOD_DTTM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DCS_LAST_MOD_DTTM = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    private static final Instant SMALLER_DCS_LAST_MOD_DTTM = Instant.ofEpochMilli(-1L);

    private static final Long DEFAULT_OBJECT_ID = 1L;
    private static final Long UPDATED_OBJECT_ID = 2L;
    private static final Long SMALLER_OBJECT_ID = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/pass-payments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PassPaymentRepository passPaymentRepository;

    @Autowired
    private PassPaymentMapper passPaymentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPassPaymentMockMvc;

    private PassPayment passPayment;

    private PassPayment insertedPassPayment;

    public static PassPayment createEntity() {
        return new PassPayment()
            .agencyCode(DEFAULT_AGENCY_CODE)
            .agencyAcronym(DEFAULT_AGENCY_ACRONYM)
            .agencyName(DEFAULT_AGENCY_NAME)
            .contractNumber(DEFAULT_CONTRACT_NUMBER)
            .supplierName(DEFAULT_SUPPLIER_NAME)
            .invoiceNumber(DEFAULT_INVOICE_NUMBER)
            .poNumber(DEFAULT_PO_NUMBER)
            .voucherNumber(DEFAULT_VOUCHER_NUMBER)
            .paymentDate(DEFAULT_PAYMENT_DATE)
            .paymentAmount(DEFAULT_PAYMENT_AMOUNT)
            .fiscalYear(DEFAULT_FISCAL_YEAR)
            .transactionCode(DEFAULT_TRANSACTION_CODE)
            .paymentType(DEFAULT_PAYMENT_TYPE)
            .invoiceDate(DEFAULT_INVOICE_DATE)
            .estPaymentDate(DEFAULT_EST_PAYMENT_DATE)
            .paymentNumber(DEFAULT_PAYMENT_NUMBER)
            .recordUpdatedDate(DEFAULT_RECORD_UPDATED_DATE)
            .recordCreated(DEFAULT_RECORD_CREATED)
            .dcsRecCrtDttm(DEFAULT_DCS_REC_CRT_DTTM)
            .dcsLastModDttm(DEFAULT_DCS_LAST_MOD_DTTM)
            .objectId(DEFAULT_OBJECT_ID);
    }

    public static PassPayment createUpdatedEntity() {
        return new PassPayment()
            .agencyCode(UPDATED_AGENCY_CODE)
            .agencyAcronym(UPDATED_AGENCY_ACRONYM)
            .agencyName(UPDATED_AGENCY_NAME)
            .contractNumber(UPDATED_CONTRACT_NUMBER)
            .supplierName(UPDATED_SUPPLIER_NAME)
            .invoiceNumber(UPDATED_INVOICE_NUMBER)
            .poNumber(UPDATED_PO_NUMBER)
            .voucherNumber(UPDATED_VOUCHER_NUMBER)
            .paymentDate(UPDATED_PAYMENT_DATE)
            .paymentAmount(UPDATED_PAYMENT_AMOUNT)
            .fiscalYear(UPDATED_FISCAL_YEAR)
            .transactionCode(UPDATED_TRANSACTION_CODE)
            .paymentType(UPDATED_PAYMENT_TYPE)
            .invoiceDate(UPDATED_INVOICE_DATE)
            .estPaymentDate(UPDATED_EST_PAYMENT_DATE)
            .paymentNumber(UPDATED_PAYMENT_NUMBER)
            .recordUpdatedDate(UPDATED_RECORD_UPDATED_DATE)
            .recordCreated(UPDATED_RECORD_CREATED)
            .dcsRecCrtDttm(UPDATED_DCS_REC_CRT_DTTM)
            .dcsLastModDttm(UPDATED_DCS_LAST_MOD_DTTM)
            .objectId(UPDATED_OBJECT_ID);
    }

    @BeforeEach
    void initTest() {
        passPayment = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPassPayment != null) {
            passPaymentRepository.delete(insertedPassPayment);
            insertedPassPayment = null;
        }
    }

    @Test
    @Transactional
    void createPassPayment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        PassPaymentDTO passPaymentDTO = passPaymentMapper.toDto(passPayment);
        var returnedPassPaymentDTO = om.readValue(
            restPassPaymentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(passPaymentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PassPaymentDTO.class
        );

        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPassPayment = passPaymentMapper.toEntity(returnedPassPaymentDTO);
        assertPassPaymentUpdatableFieldsEquals(returnedPassPayment, getPersistedPassPayment(returnedPassPayment));

        insertedPassPayment = returnedPassPayment;
    }

    @Test
    @Transactional
    void createPassPaymentWithExistingId() throws Exception {
        passPayment.setId(1L);
        PassPaymentDTO passPaymentDTO = passPaymentMapper.toDto(passPayment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        restPassPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(passPaymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPassPayments() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);

        restPassPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(passPayment.getId().intValue())))
            .andExpect(jsonPath("$.[*].agencyCode").value(hasItem(DEFAULT_AGENCY_CODE)))
            .andExpect(jsonPath("$.[*].agencyAcronym").value(hasItem(DEFAULT_AGENCY_ACRONYM)))
            .andExpect(jsonPath("$.[*].agencyName").value(hasItem(DEFAULT_AGENCY_NAME)))
            .andExpect(jsonPath("$.[*].contractNumber").value(hasItem(DEFAULT_CONTRACT_NUMBER)))
            .andExpect(jsonPath("$.[*].supplierName").value(hasItem(DEFAULT_SUPPLIER_NAME)))
            .andExpect(jsonPath("$.[*].invoiceNumber").value(hasItem(DEFAULT_INVOICE_NUMBER)))
            .andExpect(jsonPath("$.[*].poNumber").value(hasItem(DEFAULT_PO_NUMBER)))
            .andExpect(jsonPath("$.[*].voucherNumber").value(hasItem(DEFAULT_VOUCHER_NUMBER)))
            .andExpect(jsonPath("$.[*].paymentDate").value(hasItem(DEFAULT_PAYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].paymentAmount").value(hasItem(sameNumber(DEFAULT_PAYMENT_AMOUNT))))
            .andExpect(jsonPath("$.[*].fiscalYear").value(hasItem(DEFAULT_FISCAL_YEAR)))
            .andExpect(jsonPath("$.[*].transactionCode").value(hasItem(DEFAULT_TRANSACTION_CODE)))
            .andExpect(jsonPath("$.[*].paymentType").value(hasItem(DEFAULT_PAYMENT_TYPE)))
            .andExpect(jsonPath("$.[*].invoiceDate").value(hasItem(DEFAULT_INVOICE_DATE.toString())))
            .andExpect(jsonPath("$.[*].estPaymentDate").value(hasItem(DEFAULT_EST_PAYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].paymentNumber").value(hasItem(DEFAULT_PAYMENT_NUMBER)))
            .andExpect(jsonPath("$.[*].recordUpdatedDate").value(hasItem(DEFAULT_RECORD_UPDATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].recordCreated").value(hasItem(DEFAULT_RECORD_CREATED.toString())))
            .andExpect(jsonPath("$.[*].dcsRecCrtDttm").value(hasItem(DEFAULT_DCS_REC_CRT_DTTM.toString())))
            .andExpect(jsonPath("$.[*].dcsLastModDttm").value(hasItem(DEFAULT_DCS_LAST_MOD_DTTM.toString())))
            .andExpect(jsonPath("$.[*].objectId").value(hasItem(DEFAULT_OBJECT_ID.intValue())));
    }

    @Test
    @Transactional
    void getPassPayment() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);

        restPassPaymentMockMvc
            .perform(get(ENTITY_API_URL_ID, passPayment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(passPayment.getId().intValue()))
            .andExpect(jsonPath("$.agencyCode").value(DEFAULT_AGENCY_CODE))
            .andExpect(jsonPath("$.agencyAcronym").value(DEFAULT_AGENCY_ACRONYM))
            .andExpect(jsonPath("$.agencyName").value(DEFAULT_AGENCY_NAME))
            .andExpect(jsonPath("$.contractNumber").value(DEFAULT_CONTRACT_NUMBER))
            .andExpect(jsonPath("$.supplierName").value(DEFAULT_SUPPLIER_NAME))
            .andExpect(jsonPath("$.invoiceNumber").value(DEFAULT_INVOICE_NUMBER))
            .andExpect(jsonPath("$.poNumber").value(DEFAULT_PO_NUMBER))
            .andExpect(jsonPath("$.voucherNumber").value(DEFAULT_VOUCHER_NUMBER))
            .andExpect(jsonPath("$.paymentDate").value(DEFAULT_PAYMENT_DATE.toString()))
            .andExpect(jsonPath("$.paymentAmount").value(sameNumber(DEFAULT_PAYMENT_AMOUNT)))
            .andExpect(jsonPath("$.fiscalYear").value(DEFAULT_FISCAL_YEAR))
            .andExpect(jsonPath("$.transactionCode").value(DEFAULT_TRANSACTION_CODE))
            .andExpect(jsonPath("$.paymentType").value(DEFAULT_PAYMENT_TYPE))
            .andExpect(jsonPath("$.invoiceDate").value(DEFAULT_INVOICE_DATE.toString()))
            .andExpect(jsonPath("$.estPaymentDate").value(DEFAULT_EST_PAYMENT_DATE.toString()))
            .andExpect(jsonPath("$.paymentNumber").value(DEFAULT_PAYMENT_NUMBER))
            .andExpect(jsonPath("$.recordUpdatedDate").value(DEFAULT_RECORD_UPDATED_DATE.toString()))
            .andExpect(jsonPath("$.recordCreated").value(DEFAULT_RECORD_CREATED.toString()))
            .andExpect(jsonPath("$.dcsRecCrtDttm").value(DEFAULT_DCS_REC_CRT_DTTM.toString()))
            .andExpect(jsonPath("$.dcsLastModDttm").value(DEFAULT_DCS_LAST_MOD_DTTM.toString()))
            .andExpect(jsonPath("$.objectId").value(DEFAULT_OBJECT_ID.intValue()));
    }

    @Test
    @Transactional
    void getPassPaymentsByIdFiltering() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);

        Long id = passPayment.getId();

        defaultPassPaymentFiltering("id.equals=" + id, "id.notEquals=" + id);
        defaultPassPaymentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);
        defaultPassPaymentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPoNumberIsSorted() throws Exception {
        PassPayment first = createEntity().poNumber("AAA-SORT-TEST");
        PassPayment second = createEntity().poNumber("ZZZ-SORT-TEST");
        passPaymentRepository.saveAndFlush(first);
        passPaymentRepository.saveAndFlush(second);

        restPassPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=poNumber,asc&poNumber.contains=SORT-TEST"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].poNumber").value("AAA-SORT-TEST"))
            .andExpect(jsonPath("$[1].poNumber").value("ZZZ-SORT-TEST"));

        restPassPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=poNumber,desc&poNumber.contains=SORT-TEST"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].poNumber").value("ZZZ-SORT-TEST"))
            .andExpect(jsonPath("$[1].poNumber").value("AAA-SORT-TEST"));
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPoNumberSortsNullsAndEmptyLast() throws Exception {
        PassPayment withValue = createEntity().poNumber("MMM-NULL-SORT").paymentNumber("NULL-SORT-MARKER");
        PassPayment withEmpty = createEntity().poNumber("").paymentNumber("NULL-SORT-MARKER");
        PassPayment withNull = createEntity().poNumber(null).paymentNumber("NULL-SORT-MARKER");
        passPaymentRepository.saveAndFlush(withValue);
        passPaymentRepository.saveAndFlush(withEmpty);
        passPaymentRepository.saveAndFlush(withNull);

        restPassPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=poNumber,asc&paymentNumber.equals=NULL-SORT-MARKER"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].poNumber").value("MMM-NULL-SORT"));

        restPassPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=poNumber,desc&paymentNumber.equals=NULL-SORT-MARKER"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].poNumber").value("MMM-NULL-SORT"));
    }

    @Test
    @Transactional
    void getAllPassPaymentsByAgencyCodeIsEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("agencyCode.equals=" + DEFAULT_AGENCY_CODE, "agencyCode.equals=" + UPDATED_AGENCY_CODE);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByAgencyCodeIsInShouldWork() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "agencyCode.in=" + DEFAULT_AGENCY_CODE + "," + UPDATED_AGENCY_CODE,
            "agencyCode.in=" + UPDATED_AGENCY_CODE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByAgencyCodeIsNullOrNotNull() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("agencyCode.specified=true", "agencyCode.specified=false");
    }

    @Test
    @Transactional
    void getAllPassPaymentsByAgencyCodeContainsSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("agencyCode.contains=" + DEFAULT_AGENCY_CODE, "agencyCode.contains=" + UPDATED_AGENCY_CODE);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByAgencyCodeNotContainsSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("agencyCode.doesNotContain=" + UPDATED_AGENCY_CODE, "agencyCode.doesNotContain=" + DEFAULT_AGENCY_CODE);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByAgencyAcronymIsEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("agencyAcronym.equals=" + DEFAULT_AGENCY_ACRONYM, "agencyAcronym.equals=" + UPDATED_AGENCY_ACRONYM);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByAgencyAcronymIsInShouldWork() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "agencyAcronym.in=" + DEFAULT_AGENCY_ACRONYM + "," + UPDATED_AGENCY_ACRONYM,
            "agencyAcronym.in=" + UPDATED_AGENCY_ACRONYM
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByAgencyAcronymIsNullOrNotNull() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("agencyAcronym.specified=true", "agencyAcronym.specified=false");
    }

    @Test
    @Transactional
    void getAllPassPaymentsByAgencyAcronymContainsSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("agencyAcronym.contains=" + DEFAULT_AGENCY_ACRONYM, "agencyAcronym.contains=" + UPDATED_AGENCY_ACRONYM);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByAgencyAcronymNotContainsSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "agencyAcronym.doesNotContain=" + UPDATED_AGENCY_ACRONYM,
            "agencyAcronym.doesNotContain=" + DEFAULT_AGENCY_ACRONYM
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByAgencyNameIsEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("agencyName.equals=" + DEFAULT_AGENCY_NAME, "agencyName.equals=" + UPDATED_AGENCY_NAME);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByAgencyNameIsInShouldWork() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "agencyName.in=" + DEFAULT_AGENCY_NAME + "," + UPDATED_AGENCY_NAME,
            "agencyName.in=" + UPDATED_AGENCY_NAME
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByAgencyNameIsNullOrNotNull() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("agencyName.specified=true", "agencyName.specified=false");
    }

    @Test
    @Transactional
    void getAllPassPaymentsByAgencyNameContainsSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("agencyName.contains=" + DEFAULT_AGENCY_NAME, "agencyName.contains=" + UPDATED_AGENCY_NAME);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByAgencyNameNotContainsSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("agencyName.doesNotContain=" + UPDATED_AGENCY_NAME, "agencyName.doesNotContain=" + DEFAULT_AGENCY_NAME);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByContractNumberIsEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("contractNumber.equals=" + DEFAULT_CONTRACT_NUMBER, "contractNumber.equals=" + UPDATED_CONTRACT_NUMBER);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByContractNumberIsInShouldWork() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "contractNumber.in=" + DEFAULT_CONTRACT_NUMBER + "," + UPDATED_CONTRACT_NUMBER,
            "contractNumber.in=" + UPDATED_CONTRACT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByContractNumberIsNullOrNotNull() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("contractNumber.specified=true", "contractNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllPassPaymentsByContractNumberContainsSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "contractNumber.contains=" + DEFAULT_CONTRACT_NUMBER,
            "contractNumber.contains=" + UPDATED_CONTRACT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByContractNumberNotContainsSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "contractNumber.doesNotContain=" + UPDATED_CONTRACT_NUMBER,
            "contractNumber.doesNotContain=" + DEFAULT_CONTRACT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsBySupplierNameIsEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("supplierName.equals=" + DEFAULT_SUPPLIER_NAME, "supplierName.equals=" + UPDATED_SUPPLIER_NAME);
    }

    @Test
    @Transactional
    void getAllPassPaymentsBySupplierNameIsInShouldWork() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "supplierName.in=" + DEFAULT_SUPPLIER_NAME + "," + UPDATED_SUPPLIER_NAME,
            "supplierName.in=" + UPDATED_SUPPLIER_NAME
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsBySupplierNameIsNullOrNotNull() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("supplierName.specified=true", "supplierName.specified=false");
    }

    @Test
    @Transactional
    void getAllPassPaymentsBySupplierNameContainsSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("supplierName.contains=" + DEFAULT_SUPPLIER_NAME, "supplierName.contains=" + UPDATED_SUPPLIER_NAME);
    }

    @Test
    @Transactional
    void getAllPassPaymentsBySupplierNameNotContainsSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "supplierName.doesNotContain=" + UPDATED_SUPPLIER_NAME,
            "supplierName.doesNotContain=" + DEFAULT_SUPPLIER_NAME
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByInvoiceNumberIsEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("invoiceNumber.equals=" + DEFAULT_INVOICE_NUMBER, "invoiceNumber.equals=" + UPDATED_INVOICE_NUMBER);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByInvoiceNumberIsInShouldWork() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "invoiceNumber.in=" + DEFAULT_INVOICE_NUMBER + "," + UPDATED_INVOICE_NUMBER,
            "invoiceNumber.in=" + UPDATED_INVOICE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByInvoiceNumberIsNullOrNotNull() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("invoiceNumber.specified=true", "invoiceNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllPassPaymentsByInvoiceNumberContainsSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("invoiceNumber.contains=" + DEFAULT_INVOICE_NUMBER, "invoiceNumber.contains=" + UPDATED_INVOICE_NUMBER);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByInvoiceNumberNotContainsSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "invoiceNumber.doesNotContain=" + UPDATED_INVOICE_NUMBER,
            "invoiceNumber.doesNotContain=" + DEFAULT_INVOICE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPoNumberIsEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("poNumber.equals=" + DEFAULT_PO_NUMBER, "poNumber.equals=" + UPDATED_PO_NUMBER);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPoNumberIsInShouldWork() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("poNumber.in=" + DEFAULT_PO_NUMBER + "," + UPDATED_PO_NUMBER, "poNumber.in=" + UPDATED_PO_NUMBER);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPoNumberIsNullOrNotNull() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("poNumber.specified=true", "poNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPoNumberContainsSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("poNumber.contains=" + DEFAULT_PO_NUMBER, "poNumber.contains=" + UPDATED_PO_NUMBER);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPoNumberNotContainsSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("poNumber.doesNotContain=" + UPDATED_PO_NUMBER, "poNumber.doesNotContain=" + DEFAULT_PO_NUMBER);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByVoucherNumberIsEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("voucherNumber.equals=" + DEFAULT_VOUCHER_NUMBER, "voucherNumber.equals=" + UPDATED_VOUCHER_NUMBER);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByVoucherNumberIsInShouldWork() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "voucherNumber.in=" + DEFAULT_VOUCHER_NUMBER + "," + UPDATED_VOUCHER_NUMBER,
            "voucherNumber.in=" + UPDATED_VOUCHER_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByVoucherNumberIsNullOrNotNull() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("voucherNumber.specified=true", "voucherNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllPassPaymentsByVoucherNumberContainsSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("voucherNumber.contains=" + DEFAULT_VOUCHER_NUMBER, "voucherNumber.contains=" + UPDATED_VOUCHER_NUMBER);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByVoucherNumberNotContainsSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "voucherNumber.doesNotContain=" + UPDATED_VOUCHER_NUMBER,
            "voucherNumber.doesNotContain=" + DEFAULT_VOUCHER_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentDateIsEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("paymentDate.equals=" + DEFAULT_PAYMENT_DATE, "paymentDate.equals=" + UPDATED_PAYMENT_DATE);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentDateIsInShouldWork() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "paymentDate.in=" + DEFAULT_PAYMENT_DATE + "," + UPDATED_PAYMENT_DATE,
            "paymentDate.in=" + UPDATED_PAYMENT_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentDateIsNullOrNotNull() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("paymentDate.specified=true", "paymentDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentDateIsGreaterThanOrEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "paymentDate.greaterThanOrEqual=" + DEFAULT_PAYMENT_DATE,
            "paymentDate.greaterThanOrEqual=" + UPDATED_PAYMENT_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentDateIsLessThanOrEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "paymentDate.lessThanOrEqual=" + DEFAULT_PAYMENT_DATE,
            "paymentDate.lessThanOrEqual=" + SMALLER_PAYMENT_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentDateIsLessThanSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("paymentDate.lessThan=" + UPDATED_PAYMENT_DATE, "paymentDate.lessThan=" + DEFAULT_PAYMENT_DATE);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentDateIsGreaterThanSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("paymentDate.greaterThan=" + SMALLER_PAYMENT_DATE, "paymentDate.greaterThan=" + DEFAULT_PAYMENT_DATE);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentAmountIsEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("paymentAmount.equals=" + DEFAULT_PAYMENT_AMOUNT, "paymentAmount.equals=" + UPDATED_PAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentAmountIsInShouldWork() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "paymentAmount.in=" + DEFAULT_PAYMENT_AMOUNT + "," + UPDATED_PAYMENT_AMOUNT,
            "paymentAmount.in=" + UPDATED_PAYMENT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentAmountIsNullOrNotNull() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("paymentAmount.specified=true", "paymentAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentAmountIsGreaterThanOrEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "paymentAmount.greaterThanOrEqual=" + DEFAULT_PAYMENT_AMOUNT,
            "paymentAmount.greaterThanOrEqual=" + UPDATED_PAYMENT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentAmountIsLessThanOrEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "paymentAmount.lessThanOrEqual=" + DEFAULT_PAYMENT_AMOUNT,
            "paymentAmount.lessThanOrEqual=" + SMALLER_PAYMENT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentAmountIsLessThanSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("paymentAmount.lessThan=" + UPDATED_PAYMENT_AMOUNT, "paymentAmount.lessThan=" + DEFAULT_PAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentAmountIsGreaterThanSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "paymentAmount.greaterThan=" + SMALLER_PAYMENT_AMOUNT,
            "paymentAmount.greaterThan=" + DEFAULT_PAYMENT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByFiscalYearIsEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("fiscalYear.equals=" + DEFAULT_FISCAL_YEAR, "fiscalYear.equals=" + UPDATED_FISCAL_YEAR);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByFiscalYearIsInShouldWork() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "fiscalYear.in=" + DEFAULT_FISCAL_YEAR + "," + UPDATED_FISCAL_YEAR,
            "fiscalYear.in=" + UPDATED_FISCAL_YEAR
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByFiscalYearIsNullOrNotNull() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("fiscalYear.specified=true", "fiscalYear.specified=false");
    }

    @Test
    @Transactional
    void getAllPassPaymentsByFiscalYearIsGreaterThanOrEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "fiscalYear.greaterThanOrEqual=" + DEFAULT_FISCAL_YEAR,
            "fiscalYear.greaterThanOrEqual=" + UPDATED_FISCAL_YEAR
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByFiscalYearIsLessThanOrEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "fiscalYear.lessThanOrEqual=" + DEFAULT_FISCAL_YEAR,
            "fiscalYear.lessThanOrEqual=" + SMALLER_FISCAL_YEAR
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByFiscalYearIsLessThanSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("fiscalYear.lessThan=" + UPDATED_FISCAL_YEAR, "fiscalYear.lessThan=" + DEFAULT_FISCAL_YEAR);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByFiscalYearIsGreaterThanSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("fiscalYear.greaterThan=" + SMALLER_FISCAL_YEAR, "fiscalYear.greaterThan=" + DEFAULT_FISCAL_YEAR);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByTransactionCodeIsEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "transactionCode.equals=" + DEFAULT_TRANSACTION_CODE,
            "transactionCode.equals=" + UPDATED_TRANSACTION_CODE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByTransactionCodeIsInShouldWork() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "transactionCode.in=" + DEFAULT_TRANSACTION_CODE + "," + UPDATED_TRANSACTION_CODE,
            "transactionCode.in=" + UPDATED_TRANSACTION_CODE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByTransactionCodeIsNullOrNotNull() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("transactionCode.specified=true", "transactionCode.specified=false");
    }

    @Test
    @Transactional
    void getAllPassPaymentsByTransactionCodeContainsSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "transactionCode.contains=" + DEFAULT_TRANSACTION_CODE,
            "transactionCode.contains=" + UPDATED_TRANSACTION_CODE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByTransactionCodeNotContainsSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "transactionCode.doesNotContain=" + UPDATED_TRANSACTION_CODE,
            "transactionCode.doesNotContain=" + DEFAULT_TRANSACTION_CODE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentTypeIsEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("paymentType.equals=" + DEFAULT_PAYMENT_TYPE, "paymentType.equals=" + UPDATED_PAYMENT_TYPE);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentTypeIsInShouldWork() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "paymentType.in=" + DEFAULT_PAYMENT_TYPE + "," + UPDATED_PAYMENT_TYPE,
            "paymentType.in=" + UPDATED_PAYMENT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentTypeIsNullOrNotNull() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("paymentType.specified=true", "paymentType.specified=false");
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentTypeContainsSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("paymentType.contains=" + DEFAULT_PAYMENT_TYPE, "paymentType.contains=" + UPDATED_PAYMENT_TYPE);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentTypeNotContainsSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "paymentType.doesNotContain=" + UPDATED_PAYMENT_TYPE,
            "paymentType.doesNotContain=" + DEFAULT_PAYMENT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByInvoiceDateIsEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("invoiceDate.equals=" + DEFAULT_INVOICE_DATE, "invoiceDate.equals=" + UPDATED_INVOICE_DATE);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByInvoiceDateIsInShouldWork() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "invoiceDate.in=" + DEFAULT_INVOICE_DATE + "," + UPDATED_INVOICE_DATE,
            "invoiceDate.in=" + UPDATED_INVOICE_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByInvoiceDateIsNullOrNotNull() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("invoiceDate.specified=true", "invoiceDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPassPaymentsByInvoiceDateIsGreaterThanOrEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "invoiceDate.greaterThanOrEqual=" + DEFAULT_INVOICE_DATE,
            "invoiceDate.greaterThanOrEqual=" + UPDATED_INVOICE_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByInvoiceDateIsLessThanOrEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "invoiceDate.lessThanOrEqual=" + DEFAULT_INVOICE_DATE,
            "invoiceDate.lessThanOrEqual=" + SMALLER_INVOICE_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByInvoiceDateIsLessThanSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("invoiceDate.lessThan=" + UPDATED_INVOICE_DATE, "invoiceDate.lessThan=" + DEFAULT_INVOICE_DATE);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByInvoiceDateIsGreaterThanSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("invoiceDate.greaterThan=" + SMALLER_INVOICE_DATE, "invoiceDate.greaterThan=" + DEFAULT_INVOICE_DATE);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByEstPaymentDateIsEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "estPaymentDate.equals=" + DEFAULT_EST_PAYMENT_DATE,
            "estPaymentDate.equals=" + UPDATED_EST_PAYMENT_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByEstPaymentDateIsInShouldWork() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "estPaymentDate.in=" + DEFAULT_EST_PAYMENT_DATE + "," + UPDATED_EST_PAYMENT_DATE,
            "estPaymentDate.in=" + UPDATED_EST_PAYMENT_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByEstPaymentDateIsNullOrNotNull() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("estPaymentDate.specified=true", "estPaymentDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPassPaymentsByEstPaymentDateIsGreaterThanOrEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "estPaymentDate.greaterThanOrEqual=" + DEFAULT_EST_PAYMENT_DATE,
            "estPaymentDate.greaterThanOrEqual=" + UPDATED_EST_PAYMENT_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByEstPaymentDateIsLessThanOrEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "estPaymentDate.lessThanOrEqual=" + DEFAULT_EST_PAYMENT_DATE,
            "estPaymentDate.lessThanOrEqual=" + SMALLER_EST_PAYMENT_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByEstPaymentDateIsLessThanSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "estPaymentDate.lessThan=" + UPDATED_EST_PAYMENT_DATE,
            "estPaymentDate.lessThan=" + DEFAULT_EST_PAYMENT_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByEstPaymentDateIsGreaterThanSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "estPaymentDate.greaterThan=" + SMALLER_EST_PAYMENT_DATE,
            "estPaymentDate.greaterThan=" + DEFAULT_EST_PAYMENT_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentNumberIsEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("paymentNumber.equals=" + DEFAULT_PAYMENT_NUMBER, "paymentNumber.equals=" + UPDATED_PAYMENT_NUMBER);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentNumberIsInShouldWork() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "paymentNumber.in=" + DEFAULT_PAYMENT_NUMBER + "," + UPDATED_PAYMENT_NUMBER,
            "paymentNumber.in=" + UPDATED_PAYMENT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentNumberIsNullOrNotNull() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("paymentNumber.specified=true", "paymentNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentNumberContainsSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("paymentNumber.contains=" + DEFAULT_PAYMENT_NUMBER, "paymentNumber.contains=" + UPDATED_PAYMENT_NUMBER);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByPaymentNumberNotContainsSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "paymentNumber.doesNotContain=" + UPDATED_PAYMENT_NUMBER,
            "paymentNumber.doesNotContain=" + DEFAULT_PAYMENT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByRecordUpdatedDateIsEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "recordUpdatedDate.equals=" + DEFAULT_RECORD_UPDATED_DATE,
            "recordUpdatedDate.equals=" + UPDATED_RECORD_UPDATED_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByRecordUpdatedDateIsInShouldWork() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "recordUpdatedDate.in=" + DEFAULT_RECORD_UPDATED_DATE + "," + UPDATED_RECORD_UPDATED_DATE,
            "recordUpdatedDate.in=" + UPDATED_RECORD_UPDATED_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByRecordUpdatedDateIsNullOrNotNull() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("recordUpdatedDate.specified=true", "recordUpdatedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPassPaymentsByRecordUpdatedDateIsGreaterThanOrEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "recordUpdatedDate.greaterThanOrEqual=" + DEFAULT_RECORD_UPDATED_DATE,
            "recordUpdatedDate.greaterThanOrEqual=" + UPDATED_RECORD_UPDATED_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByRecordUpdatedDateIsLessThanOrEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "recordUpdatedDate.lessThanOrEqual=" + DEFAULT_RECORD_UPDATED_DATE,
            "recordUpdatedDate.lessThanOrEqual=" + SMALLER_RECORD_UPDATED_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByRecordUpdatedDateIsLessThanSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "recordUpdatedDate.lessThan=" + UPDATED_RECORD_UPDATED_DATE,
            "recordUpdatedDate.lessThan=" + DEFAULT_RECORD_UPDATED_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByRecordUpdatedDateIsGreaterThanSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "recordUpdatedDate.greaterThan=" + SMALLER_RECORD_UPDATED_DATE,
            "recordUpdatedDate.greaterThan=" + DEFAULT_RECORD_UPDATED_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByRecordCreatedIsEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("recordCreated.equals=" + DEFAULT_RECORD_CREATED, "recordCreated.equals=" + UPDATED_RECORD_CREATED);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByRecordCreatedIsInShouldWork() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "recordCreated.in=" + DEFAULT_RECORD_CREATED + "," + UPDATED_RECORD_CREATED,
            "recordCreated.in=" + UPDATED_RECORD_CREATED
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByRecordCreatedIsNullOrNotNull() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("recordCreated.specified=true", "recordCreated.specified=false");
    }

    @Test
    @Transactional
    void getAllPassPaymentsByRecordCreatedIsGreaterThanOrEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "recordCreated.greaterThanOrEqual=" + DEFAULT_RECORD_CREATED,
            "recordCreated.greaterThanOrEqual=" + UPDATED_RECORD_CREATED
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByRecordCreatedIsLessThanOrEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "recordCreated.lessThanOrEqual=" + DEFAULT_RECORD_CREATED,
            "recordCreated.lessThanOrEqual=" + SMALLER_RECORD_CREATED
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByRecordCreatedIsLessThanSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("recordCreated.lessThan=" + UPDATED_RECORD_CREATED, "recordCreated.lessThan=" + DEFAULT_RECORD_CREATED);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByRecordCreatedIsGreaterThanSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "recordCreated.greaterThan=" + SMALLER_RECORD_CREATED,
            "recordCreated.greaterThan=" + DEFAULT_RECORD_CREATED
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByDcsRecCrtDttmIsEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("dcsRecCrtDttm.equals=" + DEFAULT_DCS_REC_CRT_DTTM, "dcsRecCrtDttm.equals=" + UPDATED_DCS_REC_CRT_DTTM);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByDcsRecCrtDttmIsInShouldWork() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "dcsRecCrtDttm.in=" + DEFAULT_DCS_REC_CRT_DTTM + "," + UPDATED_DCS_REC_CRT_DTTM,
            "dcsRecCrtDttm.in=" + UPDATED_DCS_REC_CRT_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByDcsRecCrtDttmIsNullOrNotNull() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("dcsRecCrtDttm.specified=true", "dcsRecCrtDttm.specified=false");
    }

    @Test
    @Transactional
    void getAllPassPaymentsByDcsRecCrtDttmIsGreaterThanOrEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "dcsRecCrtDttm.greaterThanOrEqual=" + DEFAULT_DCS_REC_CRT_DTTM,
            "dcsRecCrtDttm.greaterThanOrEqual=" + UPDATED_DCS_REC_CRT_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByDcsRecCrtDttmIsLessThanOrEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "dcsRecCrtDttm.lessThanOrEqual=" + DEFAULT_DCS_REC_CRT_DTTM,
            "dcsRecCrtDttm.lessThanOrEqual=" + SMALLER_DCS_REC_CRT_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByDcsRecCrtDttmIsLessThanSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "dcsRecCrtDttm.lessThan=" + UPDATED_DCS_REC_CRT_DTTM,
            "dcsRecCrtDttm.lessThan=" + DEFAULT_DCS_REC_CRT_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByDcsRecCrtDttmIsGreaterThanSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "dcsRecCrtDttm.greaterThan=" + SMALLER_DCS_REC_CRT_DTTM,
            "dcsRecCrtDttm.greaterThan=" + DEFAULT_DCS_REC_CRT_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByDcsLastModDttmIsEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "dcsLastModDttm.equals=" + DEFAULT_DCS_LAST_MOD_DTTM,
            "dcsLastModDttm.equals=" + UPDATED_DCS_LAST_MOD_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByDcsLastModDttmIsInShouldWork() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "dcsLastModDttm.in=" + DEFAULT_DCS_LAST_MOD_DTTM + "," + UPDATED_DCS_LAST_MOD_DTTM,
            "dcsLastModDttm.in=" + UPDATED_DCS_LAST_MOD_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByDcsLastModDttmIsNullOrNotNull() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("dcsLastModDttm.specified=true", "dcsLastModDttm.specified=false");
    }

    @Test
    @Transactional
    void getAllPassPaymentsByDcsLastModDttmIsGreaterThanOrEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "dcsLastModDttm.greaterThanOrEqual=" + DEFAULT_DCS_LAST_MOD_DTTM,
            "dcsLastModDttm.greaterThanOrEqual=" + UPDATED_DCS_LAST_MOD_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByDcsLastModDttmIsLessThanOrEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "dcsLastModDttm.lessThanOrEqual=" + DEFAULT_DCS_LAST_MOD_DTTM,
            "dcsLastModDttm.lessThanOrEqual=" + SMALLER_DCS_LAST_MOD_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByDcsLastModDttmIsLessThanSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "dcsLastModDttm.lessThan=" + UPDATED_DCS_LAST_MOD_DTTM,
            "dcsLastModDttm.lessThan=" + DEFAULT_DCS_LAST_MOD_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByDcsLastModDttmIsGreaterThanSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering(
            "dcsLastModDttm.greaterThan=" + SMALLER_DCS_LAST_MOD_DTTM,
            "dcsLastModDttm.greaterThan=" + DEFAULT_DCS_LAST_MOD_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPassPaymentsByObjectIdIsEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("objectId.equals=" + DEFAULT_OBJECT_ID, "objectId.equals=" + UPDATED_OBJECT_ID);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByObjectIdIsInShouldWork() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("objectId.in=" + DEFAULT_OBJECT_ID + "," + UPDATED_OBJECT_ID, "objectId.in=" + UPDATED_OBJECT_ID);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByObjectIdIsNullOrNotNull() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("objectId.specified=true", "objectId.specified=false");
    }

    @Test
    @Transactional
    void getAllPassPaymentsByObjectIdIsGreaterThanOrEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("objectId.greaterThanOrEqual=" + DEFAULT_OBJECT_ID, "objectId.greaterThanOrEqual=" + UPDATED_OBJECT_ID);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByObjectIdIsLessThanOrEqualToSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("objectId.lessThanOrEqual=" + DEFAULT_OBJECT_ID, "objectId.lessThanOrEqual=" + SMALLER_OBJECT_ID);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByObjectIdIsLessThanSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("objectId.lessThan=" + UPDATED_OBJECT_ID, "objectId.lessThan=" + DEFAULT_OBJECT_ID);
    }

    @Test
    @Transactional
    void getAllPassPaymentsByObjectIdIsGreaterThanSomething() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);
        defaultPassPaymentFiltering("objectId.greaterThan=" + SMALLER_OBJECT_ID, "objectId.greaterThan=" + DEFAULT_OBJECT_ID);
    }

    @Test
    @Transactional
    void getNonExistingPassPayment() throws Exception {
        restPassPaymentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPassPayment() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        PassPayment updatedPassPayment = passPaymentRepository.findById(passPayment.getId()).orElseThrow();

        updatedPassPayment.agencyCode(UPDATED_AGENCY_CODE);
        updatedPassPayment.agencyAcronym(UPDATED_AGENCY_ACRONYM);
        updatedPassPayment.agencyName(UPDATED_AGENCY_NAME);
        updatedPassPayment.contractNumber(UPDATED_CONTRACT_NUMBER);
        updatedPassPayment.supplierName(UPDATED_SUPPLIER_NAME);
        updatedPassPayment.invoiceNumber(UPDATED_INVOICE_NUMBER);
        updatedPassPayment.poNumber(UPDATED_PO_NUMBER);
        updatedPassPayment.voucherNumber(UPDATED_VOUCHER_NUMBER);
        updatedPassPayment.paymentDate(UPDATED_PAYMENT_DATE);
        updatedPassPayment.paymentAmount(UPDATED_PAYMENT_AMOUNT);
        updatedPassPayment.fiscalYear(UPDATED_FISCAL_YEAR);
        updatedPassPayment.transactionCode(UPDATED_TRANSACTION_CODE);
        updatedPassPayment.paymentType(UPDATED_PAYMENT_TYPE);
        updatedPassPayment.invoiceDate(UPDATED_INVOICE_DATE);
        updatedPassPayment.estPaymentDate(UPDATED_EST_PAYMENT_DATE);
        updatedPassPayment.paymentNumber(UPDATED_PAYMENT_NUMBER);
        updatedPassPayment.recordUpdatedDate(UPDATED_RECORD_UPDATED_DATE);
        updatedPassPayment.recordCreated(UPDATED_RECORD_CREATED);
        updatedPassPayment.dcsRecCrtDttm(UPDATED_DCS_REC_CRT_DTTM);
        updatedPassPayment.dcsLastModDttm(UPDATED_DCS_LAST_MOD_DTTM);
        updatedPassPayment.objectId(UPDATED_OBJECT_ID);

        PassPaymentDTO passPaymentDTO = passPaymentMapper.toDto(updatedPassPayment);

        restPassPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, passPaymentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(passPaymentDTO))
            )
            .andExpect(status().isOk());

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPassPaymentToMatchAllProperties(updatedPassPayment);
    }

    @Test
    @Transactional
    void putNonExistingPassPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        passPayment.setId(longCount.incrementAndGet());

        PassPaymentDTO passPaymentDTO = passPaymentMapper.toDto(passPayment);

        restPassPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, passPaymentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(passPaymentDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPassPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        passPayment.setId(longCount.incrementAndGet());

        PassPaymentDTO passPaymentDTO = passPaymentMapper.toDto(passPayment);

        restPassPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(passPaymentDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPassPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        passPayment.setId(longCount.incrementAndGet());

        PassPaymentDTO passPaymentDTO = passPaymentMapper.toDto(passPayment);

        restPassPaymentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(passPaymentDTO)))
            .andExpect(status().isMethodNotAllowed());

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePassPayment() throws Exception {
        insertedPassPayment = passPaymentRepository.saveAndFlush(passPayment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        restPassPaymentMockMvc
            .perform(delete(ENTITY_API_URL_ID, passPayment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected void defaultPassPaymentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPassPaymentShouldBeFound(shouldBeFound);
        defaultPassPaymentShouldNotBeFound(shouldNotBeFound);
    }

    protected void defaultPassPaymentShouldBeFound(String filter) throws Exception {
        restPassPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(passPayment.getId().intValue())))
            .andExpect(jsonPath("$.[*].agencyCode").value(hasItem(DEFAULT_AGENCY_CODE)))
            .andExpect(jsonPath("$.[*].agencyAcronym").value(hasItem(DEFAULT_AGENCY_ACRONYM)))
            .andExpect(jsonPath("$.[*].agencyName").value(hasItem(DEFAULT_AGENCY_NAME)))
            .andExpect(jsonPath("$.[*].contractNumber").value(hasItem(DEFAULT_CONTRACT_NUMBER)))
            .andExpect(jsonPath("$.[*].supplierName").value(hasItem(DEFAULT_SUPPLIER_NAME)))
            .andExpect(jsonPath("$.[*].invoiceNumber").value(hasItem(DEFAULT_INVOICE_NUMBER)))
            .andExpect(jsonPath("$.[*].poNumber").value(hasItem(DEFAULT_PO_NUMBER)))
            .andExpect(jsonPath("$.[*].voucherNumber").value(hasItem(DEFAULT_VOUCHER_NUMBER)))
            .andExpect(jsonPath("$.[*].paymentDate").value(hasItem(DEFAULT_PAYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].paymentAmount").value(hasItem(sameNumber(DEFAULT_PAYMENT_AMOUNT))))
            .andExpect(jsonPath("$.[*].fiscalYear").value(hasItem(DEFAULT_FISCAL_YEAR)))
            .andExpect(jsonPath("$.[*].transactionCode").value(hasItem(DEFAULT_TRANSACTION_CODE)))
            .andExpect(jsonPath("$.[*].paymentType").value(hasItem(DEFAULT_PAYMENT_TYPE)))
            .andExpect(jsonPath("$.[*].invoiceDate").value(hasItem(DEFAULT_INVOICE_DATE.toString())))
            .andExpect(jsonPath("$.[*].estPaymentDate").value(hasItem(DEFAULT_EST_PAYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].paymentNumber").value(hasItem(DEFAULT_PAYMENT_NUMBER)))
            .andExpect(jsonPath("$.[*].recordUpdatedDate").value(hasItem(DEFAULT_RECORD_UPDATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].recordCreated").value(hasItem(DEFAULT_RECORD_CREATED.toString())))
            .andExpect(jsonPath("$.[*].dcsRecCrtDttm").value(hasItem(DEFAULT_DCS_REC_CRT_DTTM.toString())))
            .andExpect(jsonPath("$.[*].dcsLastModDttm").value(hasItem(DEFAULT_DCS_LAST_MOD_DTTM.toString())))
            .andExpect(jsonPath("$.[*].objectId").value(hasItem(DEFAULT_OBJECT_ID.intValue())));

        restPassPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter.replace(passPayment.getId().toString(), Long.toString(Long.MAX_VALUE))))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    protected void defaultPassPaymentShouldNotBeFound(String filter) throws Exception {
        restPassPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    protected long getRepositoryCount() {
        return passPaymentRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected PassPayment getPersistedPassPayment(PassPayment passPayment) {
        return passPaymentRepository.findById(passPayment.getId()).orElseThrow();
    }

    protected void assertPersistedPassPaymentToMatchAllProperties(PassPayment expectedPassPayment) {
        assertPassPaymentAllPropertiesEquals(expectedPassPayment, getPersistedPassPayment(expectedPassPayment));
    }

    protected void assertPersistedPassPaymentToMatchUpdatableProperties(PassPayment expectedPassPayment) {
        assertPassPaymentAllUpdatablePropertiesEquals(expectedPassPayment, getPersistedPassPayment(expectedPassPayment));
    }
}
