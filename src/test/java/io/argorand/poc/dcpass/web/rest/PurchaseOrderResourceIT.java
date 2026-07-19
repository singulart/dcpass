package io.argorand.poc.dcpass.web.rest;

import static io.argorand.poc.dcpass.domain.PurchaseOrderAsserts.*;
import static io.argorand.poc.dcpass.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.argorand.poc.dcpass.IntegrationTest;
import io.argorand.poc.dcpass.domain.PurchaseOrder;
import io.argorand.poc.dcpass.repository.PurchaseOrderRepository;
import io.argorand.poc.dcpass.service.dto.PurchaseOrderDTO;
import io.argorand.poc.dcpass.service.mapper.PurchaseOrderMapper;
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
 * Integration tests for the {@link PurchaseOrderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PurchaseOrderResourceIT {

    private static final String DEFAULT_PO_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PO_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_AGENCY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_AGENCY_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_REQUESTER = "AAAAAAAAAA";
    private static final String UPDATED_REQUESTER = "BBBBBBBBBB";

    private static final String DEFAULT_REQUISITION_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_REQUISITION_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_COMMODITY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_COMMODITY_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_COMMODITY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COMMODITY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CONTRACT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_CONTRACT_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_SUPPLIER = "AAAAAAAAAA";
    private static final String UPDATED_SUPPLIER = "BBBBBBBBBB";

    private static final Instant DEFAULT_ORDERED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ORDERED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    private static final Instant SMALLER_ORDERED_DATE = Instant.ofEpochMilli(-1L);

    private static final Instant DEFAULT_CREATE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    private static final Instant SMALLER_CREATE_DATE = Instant.ofEpochMilli(-1L);

    private static final BigDecimal DEFAULT_PO_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_PO_TOTAL = new BigDecimal(2);
    private static final BigDecimal SMALLER_PO_TOTAL = new BigDecimal(1 - 1);

    private static final Integer DEFAULT_FISCAL_YEAR = 1;
    private static final Integer UPDATED_FISCAL_YEAR = 2;
    private static final Integer SMALLER_FISCAL_YEAR = 1 - 1;

    private static final String DEFAULT_PO_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_PO_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_AGENCY_ACRONYM = "AAAAAAAAAA";
    private static final String UPDATED_AGENCY_ACRONYM = "BBBBBBBBBB";

    private static final String DEFAULT_AGENCY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_AGENCY_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_DCS_LAST_MOD_DTTM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DCS_LAST_MOD_DTTM = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    private static final Instant SMALLER_DCS_LAST_MOD_DTTM = Instant.ofEpochMilli(-1L);

    private static final Instant DEFAULT_DCS_REC_CRT_DTTM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DCS_REC_CRT_DTTM = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    private static final Instant SMALLER_DCS_REC_CRT_DTTM = Instant.ofEpochMilli(-1L);

    private static final Long DEFAULT_OBJECT_ID = 1L;
    private static final Long UPDATED_OBJECT_ID = 2L;
    private static final Long SMALLER_OBJECT_ID = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/purchase-orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPurchaseOrderMockMvc;

    private PurchaseOrder purchaseOrder;

    private PurchaseOrder insertedPurchaseOrder;

    public static PurchaseOrder createEntity() {
        return new PurchaseOrder()
            .poNumber(DEFAULT_PO_NUMBER)
            .agencyCode(DEFAULT_AGENCY_CODE)
            .status(DEFAULT_STATUS)
            .requester(DEFAULT_REQUESTER)
            .requisitionNumber(DEFAULT_REQUISITION_NUMBER)
            .commodityCode(DEFAULT_COMMODITY_CODE)
            .commodityName(DEFAULT_COMMODITY_NAME)
            .contractNumber(DEFAULT_CONTRACT_NUMBER)
            .supplier(DEFAULT_SUPPLIER)
            .orderedDate(DEFAULT_ORDERED_DATE)
            .createDate(DEFAULT_CREATE_DATE)
            .poTotal(DEFAULT_PO_TOTAL)
            .fiscalYear(DEFAULT_FISCAL_YEAR)
            .poTitle(DEFAULT_PO_TITLE)
            .agencyAcronym(DEFAULT_AGENCY_ACRONYM)
            .agencyName(DEFAULT_AGENCY_NAME)
            .dcsLastModDttm(DEFAULT_DCS_LAST_MOD_DTTM)
            .dcsRecCrtDttm(DEFAULT_DCS_REC_CRT_DTTM)
            .objectId(DEFAULT_OBJECT_ID);
    }

    public static PurchaseOrder createUpdatedEntity() {
        return new PurchaseOrder()
            .poNumber(UPDATED_PO_NUMBER)
            .agencyCode(UPDATED_AGENCY_CODE)
            .status(UPDATED_STATUS)
            .requester(UPDATED_REQUESTER)
            .requisitionNumber(UPDATED_REQUISITION_NUMBER)
            .commodityCode(UPDATED_COMMODITY_CODE)
            .commodityName(UPDATED_COMMODITY_NAME)
            .contractNumber(UPDATED_CONTRACT_NUMBER)
            .supplier(UPDATED_SUPPLIER)
            .orderedDate(UPDATED_ORDERED_DATE)
            .createDate(UPDATED_CREATE_DATE)
            .poTotal(UPDATED_PO_TOTAL)
            .fiscalYear(UPDATED_FISCAL_YEAR)
            .poTitle(UPDATED_PO_TITLE)
            .agencyAcronym(UPDATED_AGENCY_ACRONYM)
            .agencyName(UPDATED_AGENCY_NAME)
            .dcsLastModDttm(UPDATED_DCS_LAST_MOD_DTTM)
            .dcsRecCrtDttm(UPDATED_DCS_REC_CRT_DTTM)
            .objectId(UPDATED_OBJECT_ID);
    }

    @BeforeEach
    void initTest() {
        purchaseOrder = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPurchaseOrder != null) {
            purchaseOrderRepository.delete(insertedPurchaseOrder);
            insertedPurchaseOrder = null;
        }
    }

    @Test
    @Transactional
    void createPurchaseOrder() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);
        var returnedPurchaseOrderDTO = om.readValue(
            restPurchaseOrderMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseOrderDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PurchaseOrderDTO.class
        );

        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPurchaseOrder = purchaseOrderMapper.toEntity(returnedPurchaseOrderDTO);
        assertPurchaseOrderUpdatableFieldsEquals(returnedPurchaseOrder, getPersistedPurchaseOrder(returnedPurchaseOrder));

        insertedPurchaseOrder = returnedPurchaseOrder;
    }

    @Test
    @Transactional
    void createPurchaseOrderWithExistingId() throws Exception {
        purchaseOrder.setId(1L);
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);

        long databaseSizeBeforeCreate = getRepositoryCount();

        restPurchaseOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseOrderDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPurchaseOrders() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);

        restPurchaseOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(purchaseOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].poNumber").value(hasItem(DEFAULT_PO_NUMBER)))
            .andExpect(jsonPath("$.[*].agencyCode").value(hasItem(DEFAULT_AGENCY_CODE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].requester").value(hasItem(DEFAULT_REQUESTER)))
            .andExpect(jsonPath("$.[*].requisitionNumber").value(hasItem(DEFAULT_REQUISITION_NUMBER)))
            .andExpect(jsonPath("$.[*].commodityCode").value(hasItem(DEFAULT_COMMODITY_CODE)))
            .andExpect(jsonPath("$.[*].commodityName").value(hasItem(DEFAULT_COMMODITY_NAME)))
            .andExpect(jsonPath("$.[*].contractNumber").value(hasItem(DEFAULT_CONTRACT_NUMBER)))
            .andExpect(jsonPath("$.[*].supplier").value(hasItem(DEFAULT_SUPPLIER)))
            .andExpect(jsonPath("$.[*].orderedDate").value(hasItem(DEFAULT_ORDERED_DATE.toString())))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].poTotal").value(hasItem(sameNumber(DEFAULT_PO_TOTAL))))
            .andExpect(jsonPath("$.[*].fiscalYear").value(hasItem(DEFAULT_FISCAL_YEAR)))
            .andExpect(jsonPath("$.[*].poTitle").value(hasItem(DEFAULT_PO_TITLE)))
            .andExpect(jsonPath("$.[*].agencyAcronym").value(hasItem(DEFAULT_AGENCY_ACRONYM)))
            .andExpect(jsonPath("$.[*].agencyName").value(hasItem(DEFAULT_AGENCY_NAME)))
            .andExpect(jsonPath("$.[*].dcsLastModDttm").value(hasItem(DEFAULT_DCS_LAST_MOD_DTTM.toString())))
            .andExpect(jsonPath("$.[*].dcsRecCrtDttm").value(hasItem(DEFAULT_DCS_REC_CRT_DTTM.toString())))
            .andExpect(jsonPath("$.[*].objectId").value(hasItem(DEFAULT_OBJECT_ID.intValue())));
    }

    @Test
    @Transactional
    void getPurchaseOrder() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);

        restPurchaseOrderMockMvc
            .perform(get(ENTITY_API_URL_ID, purchaseOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(purchaseOrder.getId().intValue()))
            .andExpect(jsonPath("$.poNumber").value(DEFAULT_PO_NUMBER))
            .andExpect(jsonPath("$.agencyCode").value(DEFAULT_AGENCY_CODE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.requester").value(DEFAULT_REQUESTER))
            .andExpect(jsonPath("$.requisitionNumber").value(DEFAULT_REQUISITION_NUMBER))
            .andExpect(jsonPath("$.commodityCode").value(DEFAULT_COMMODITY_CODE))
            .andExpect(jsonPath("$.commodityName").value(DEFAULT_COMMODITY_NAME))
            .andExpect(jsonPath("$.contractNumber").value(DEFAULT_CONTRACT_NUMBER))
            .andExpect(jsonPath("$.supplier").value(DEFAULT_SUPPLIER))
            .andExpect(jsonPath("$.orderedDate").value(DEFAULT_ORDERED_DATE.toString()))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.poTotal").value(sameNumber(DEFAULT_PO_TOTAL)))
            .andExpect(jsonPath("$.fiscalYear").value(DEFAULT_FISCAL_YEAR))
            .andExpect(jsonPath("$.poTitle").value(DEFAULT_PO_TITLE))
            .andExpect(jsonPath("$.agencyAcronym").value(DEFAULT_AGENCY_ACRONYM))
            .andExpect(jsonPath("$.agencyName").value(DEFAULT_AGENCY_NAME))
            .andExpect(jsonPath("$.dcsLastModDttm").value(DEFAULT_DCS_LAST_MOD_DTTM.toString()))
            .andExpect(jsonPath("$.dcsRecCrtDttm").value(DEFAULT_DCS_REC_CRT_DTTM.toString()))
            .andExpect(jsonPath("$.objectId").value(DEFAULT_OBJECT_ID.intValue()));
    }

    @Test
    @Transactional
    void getPurchaseOrdersByIdFiltering() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);

        Long id = purchaseOrder.getId();

        defaultPurchaseOrderFiltering("id.equals=" + id, "id.notEquals=" + id);
        defaultPurchaseOrderFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);
        defaultPurchaseOrderFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByPoNumberIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("poNumber.equals=" + DEFAULT_PO_NUMBER, "poNumber.equals=" + UPDATED_PO_NUMBER);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByPoNumberIsInShouldWork() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("poNumber.in=" + DEFAULT_PO_NUMBER + "," + UPDATED_PO_NUMBER, "poNumber.in=" + UPDATED_PO_NUMBER);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByPoNumberIsNullOrNotNull() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("poNumber.specified=true", "poNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByPoNumberContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("poNumber.contains=" + DEFAULT_PO_NUMBER, "poNumber.contains=" + UPDATED_PO_NUMBER);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByPoNumberNotContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("poNumber.doesNotContain=" + UPDATED_PO_NUMBER, "poNumber.doesNotContain=" + DEFAULT_PO_NUMBER);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByAgencyCodeIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("agencyCode.equals=" + DEFAULT_AGENCY_CODE, "agencyCode.equals=" + UPDATED_AGENCY_CODE);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByAgencyCodeIsInShouldWork() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "agencyCode.in=" + DEFAULT_AGENCY_CODE + "," + UPDATED_AGENCY_CODE,
            "agencyCode.in=" + UPDATED_AGENCY_CODE
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByAgencyCodeIsNullOrNotNull() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("agencyCode.specified=true", "agencyCode.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByAgencyCodeContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("agencyCode.contains=" + DEFAULT_AGENCY_CODE, "agencyCode.contains=" + UPDATED_AGENCY_CODE);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByAgencyCodeNotContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "agencyCode.doesNotContain=" + UPDATED_AGENCY_CODE,
            "agencyCode.doesNotContain=" + DEFAULT_AGENCY_CODE
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByStatusIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByStatusIsInShouldWork() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByStatusIsNullOrNotNull() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByStatusContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("status.contains=" + DEFAULT_STATUS, "status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByStatusNotContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("status.doesNotContain=" + UPDATED_STATUS, "status.doesNotContain=" + DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByRequesterIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("requester.equals=" + DEFAULT_REQUESTER, "requester.equals=" + UPDATED_REQUESTER);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByRequesterIsInShouldWork() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("requester.in=" + DEFAULT_REQUESTER + "," + UPDATED_REQUESTER, "requester.in=" + UPDATED_REQUESTER);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByRequesterIsNullOrNotNull() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("requester.specified=true", "requester.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByRequesterContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("requester.contains=" + DEFAULT_REQUESTER, "requester.contains=" + UPDATED_REQUESTER);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByRequesterNotContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("requester.doesNotContain=" + UPDATED_REQUESTER, "requester.doesNotContain=" + DEFAULT_REQUESTER);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByRequisitionNumberIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "requisitionNumber.equals=" + DEFAULT_REQUISITION_NUMBER,
            "requisitionNumber.equals=" + UPDATED_REQUISITION_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByRequisitionNumberIsInShouldWork() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "requisitionNumber.in=" + DEFAULT_REQUISITION_NUMBER + "," + UPDATED_REQUISITION_NUMBER,
            "requisitionNumber.in=" + UPDATED_REQUISITION_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByRequisitionNumberIsNullOrNotNull() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("requisitionNumber.specified=true", "requisitionNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByRequisitionNumberContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "requisitionNumber.contains=" + DEFAULT_REQUISITION_NUMBER,
            "requisitionNumber.contains=" + UPDATED_REQUISITION_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByRequisitionNumberNotContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "requisitionNumber.doesNotContain=" + UPDATED_REQUISITION_NUMBER,
            "requisitionNumber.doesNotContain=" + DEFAULT_REQUISITION_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCommodityCodeIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("commodityCode.equals=" + DEFAULT_COMMODITY_CODE, "commodityCode.equals=" + UPDATED_COMMODITY_CODE);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCommodityCodeIsInShouldWork() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "commodityCode.in=" + DEFAULT_COMMODITY_CODE + "," + UPDATED_COMMODITY_CODE,
            "commodityCode.in=" + UPDATED_COMMODITY_CODE
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCommodityCodeIsNullOrNotNull() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("commodityCode.specified=true", "commodityCode.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCommodityCodeContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "commodityCode.contains=" + DEFAULT_COMMODITY_CODE,
            "commodityCode.contains=" + UPDATED_COMMODITY_CODE
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCommodityCodeNotContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "commodityCode.doesNotContain=" + UPDATED_COMMODITY_CODE,
            "commodityCode.doesNotContain=" + DEFAULT_COMMODITY_CODE
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCommodityNameIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("commodityName.equals=" + DEFAULT_COMMODITY_NAME, "commodityName.equals=" + UPDATED_COMMODITY_NAME);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCommodityNameIsInShouldWork() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "commodityName.in=" + DEFAULT_COMMODITY_NAME + "," + UPDATED_COMMODITY_NAME,
            "commodityName.in=" + UPDATED_COMMODITY_NAME
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCommodityNameIsNullOrNotNull() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("commodityName.specified=true", "commodityName.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCommodityNameContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "commodityName.contains=" + DEFAULT_COMMODITY_NAME,
            "commodityName.contains=" + UPDATED_COMMODITY_NAME
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCommodityNameNotContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "commodityName.doesNotContain=" + UPDATED_COMMODITY_NAME,
            "commodityName.doesNotContain=" + DEFAULT_COMMODITY_NAME
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByContractNumberIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "contractNumber.equals=" + DEFAULT_CONTRACT_NUMBER,
            "contractNumber.equals=" + UPDATED_CONTRACT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByContractNumberIsInShouldWork() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "contractNumber.in=" + DEFAULT_CONTRACT_NUMBER + "," + UPDATED_CONTRACT_NUMBER,
            "contractNumber.in=" + UPDATED_CONTRACT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByContractNumberIsNullOrNotNull() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("contractNumber.specified=true", "contractNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByContractNumberContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "contractNumber.contains=" + DEFAULT_CONTRACT_NUMBER,
            "contractNumber.contains=" + UPDATED_CONTRACT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByContractNumberNotContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "contractNumber.doesNotContain=" + UPDATED_CONTRACT_NUMBER,
            "contractNumber.doesNotContain=" + DEFAULT_CONTRACT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersBySupplierIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("supplier.equals=" + DEFAULT_SUPPLIER, "supplier.equals=" + UPDATED_SUPPLIER);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersBySupplierIsInShouldWork() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("supplier.in=" + DEFAULT_SUPPLIER + "," + UPDATED_SUPPLIER, "supplier.in=" + UPDATED_SUPPLIER);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersBySupplierIsNullOrNotNull() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("supplier.specified=true", "supplier.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersBySupplierContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("supplier.contains=" + DEFAULT_SUPPLIER, "supplier.contains=" + UPDATED_SUPPLIER);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersBySupplierNotContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("supplier.doesNotContain=" + UPDATED_SUPPLIER, "supplier.doesNotContain=" + DEFAULT_SUPPLIER);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByPoTitleIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("poTitle.equals=" + DEFAULT_PO_TITLE, "poTitle.equals=" + UPDATED_PO_TITLE);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByPoTitleIsInShouldWork() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("poTitle.in=" + DEFAULT_PO_TITLE + "," + UPDATED_PO_TITLE, "poTitle.in=" + UPDATED_PO_TITLE);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByPoTitleIsNullOrNotNull() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("poTitle.specified=true", "poTitle.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByPoTitleContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("poTitle.contains=" + DEFAULT_PO_TITLE, "poTitle.contains=" + UPDATED_PO_TITLE);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByPoTitleNotContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("poTitle.doesNotContain=" + UPDATED_PO_TITLE, "poTitle.doesNotContain=" + DEFAULT_PO_TITLE);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByAgencyAcronymIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("agencyAcronym.equals=" + DEFAULT_AGENCY_ACRONYM, "agencyAcronym.equals=" + UPDATED_AGENCY_ACRONYM);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByAgencyAcronymIsInShouldWork() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "agencyAcronym.in=" + DEFAULT_AGENCY_ACRONYM + "," + UPDATED_AGENCY_ACRONYM,
            "agencyAcronym.in=" + UPDATED_AGENCY_ACRONYM
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByAgencyAcronymIsNullOrNotNull() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("agencyAcronym.specified=true", "agencyAcronym.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByAgencyAcronymContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "agencyAcronym.contains=" + DEFAULT_AGENCY_ACRONYM,
            "agencyAcronym.contains=" + UPDATED_AGENCY_ACRONYM
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByAgencyAcronymNotContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "agencyAcronym.doesNotContain=" + UPDATED_AGENCY_ACRONYM,
            "agencyAcronym.doesNotContain=" + DEFAULT_AGENCY_ACRONYM
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByAgencyNameIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("agencyName.equals=" + DEFAULT_AGENCY_NAME, "agencyName.equals=" + UPDATED_AGENCY_NAME);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByAgencyNameIsInShouldWork() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "agencyName.in=" + DEFAULT_AGENCY_NAME + "," + UPDATED_AGENCY_NAME,
            "agencyName.in=" + UPDATED_AGENCY_NAME
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByAgencyNameIsNullOrNotNull() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("agencyName.specified=true", "agencyName.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByAgencyNameContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("agencyName.contains=" + DEFAULT_AGENCY_NAME, "agencyName.contains=" + UPDATED_AGENCY_NAME);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByAgencyNameNotContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "agencyName.doesNotContain=" + UPDATED_AGENCY_NAME,
            "agencyName.doesNotContain=" + DEFAULT_AGENCY_NAME
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByOrderedDateIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("orderedDate.equals=" + DEFAULT_ORDERED_DATE, "orderedDate.equals=" + UPDATED_ORDERED_DATE);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByOrderedDateIsInShouldWork() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "orderedDate.in=" + DEFAULT_ORDERED_DATE + "," + UPDATED_ORDERED_DATE,
            "orderedDate.in=" + UPDATED_ORDERED_DATE
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByOrderedDateIsNullOrNotNull() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("orderedDate.specified=true", "orderedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByOrderedDateIsGreaterThanOrEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "orderedDate.greaterThanOrEqual=" + DEFAULT_ORDERED_DATE,
            "orderedDate.greaterThanOrEqual=" + UPDATED_ORDERED_DATE
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByOrderedDateIsLessThanOrEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "orderedDate.lessThanOrEqual=" + DEFAULT_ORDERED_DATE,
            "orderedDate.lessThanOrEqual=" + SMALLER_ORDERED_DATE
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByOrderedDateIsLessThanSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("orderedDate.lessThan=" + UPDATED_ORDERED_DATE, "orderedDate.lessThan=" + DEFAULT_ORDERED_DATE);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByOrderedDateIsGreaterThanSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("orderedDate.greaterThan=" + SMALLER_ORDERED_DATE, "orderedDate.greaterThan=" + DEFAULT_ORDERED_DATE);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCreateDateIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("createDate.equals=" + DEFAULT_CREATE_DATE, "createDate.equals=" + UPDATED_CREATE_DATE);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCreateDateIsInShouldWork() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "createDate.in=" + DEFAULT_CREATE_DATE + "," + UPDATED_CREATE_DATE,
            "createDate.in=" + UPDATED_CREATE_DATE
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCreateDateIsNullOrNotNull() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("createDate.specified=true", "createDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCreateDateIsGreaterThanOrEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "createDate.greaterThanOrEqual=" + DEFAULT_CREATE_DATE,
            "createDate.greaterThanOrEqual=" + UPDATED_CREATE_DATE
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCreateDateIsLessThanOrEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "createDate.lessThanOrEqual=" + DEFAULT_CREATE_DATE,
            "createDate.lessThanOrEqual=" + SMALLER_CREATE_DATE
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCreateDateIsLessThanSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("createDate.lessThan=" + UPDATED_CREATE_DATE, "createDate.lessThan=" + DEFAULT_CREATE_DATE);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCreateDateIsGreaterThanSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("createDate.greaterThan=" + SMALLER_CREATE_DATE, "createDate.greaterThan=" + DEFAULT_CREATE_DATE);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByPoTotalIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("poTotal.equals=" + DEFAULT_PO_TOTAL, "poTotal.equals=" + UPDATED_PO_TOTAL);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByPoTotalIsInShouldWork() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("poTotal.in=" + DEFAULT_PO_TOTAL + "," + UPDATED_PO_TOTAL, "poTotal.in=" + UPDATED_PO_TOTAL);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByPoTotalIsNullOrNotNull() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("poTotal.specified=true", "poTotal.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByPoTotalIsGreaterThanOrEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("poTotal.greaterThanOrEqual=" + DEFAULT_PO_TOTAL, "poTotal.greaterThanOrEqual=" + UPDATED_PO_TOTAL);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByPoTotalIsLessThanOrEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("poTotal.lessThanOrEqual=" + DEFAULT_PO_TOTAL, "poTotal.lessThanOrEqual=" + SMALLER_PO_TOTAL);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByPoTotalIsLessThanSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("poTotal.lessThan=" + UPDATED_PO_TOTAL, "poTotal.lessThan=" + DEFAULT_PO_TOTAL);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByPoTotalIsGreaterThanSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("poTotal.greaterThan=" + SMALLER_PO_TOTAL, "poTotal.greaterThan=" + DEFAULT_PO_TOTAL);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByFiscalYearIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("fiscalYear.equals=" + DEFAULT_FISCAL_YEAR, "fiscalYear.equals=" + UPDATED_FISCAL_YEAR);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByFiscalYearIsInShouldWork() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "fiscalYear.in=" + DEFAULT_FISCAL_YEAR + "," + UPDATED_FISCAL_YEAR,
            "fiscalYear.in=" + UPDATED_FISCAL_YEAR
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByFiscalYearIsNullOrNotNull() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("fiscalYear.specified=true", "fiscalYear.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByFiscalYearIsGreaterThanOrEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "fiscalYear.greaterThanOrEqual=" + DEFAULT_FISCAL_YEAR,
            "fiscalYear.greaterThanOrEqual=" + UPDATED_FISCAL_YEAR
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByFiscalYearIsLessThanOrEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "fiscalYear.lessThanOrEqual=" + DEFAULT_FISCAL_YEAR,
            "fiscalYear.lessThanOrEqual=" + SMALLER_FISCAL_YEAR
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByFiscalYearIsLessThanSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("fiscalYear.lessThan=" + UPDATED_FISCAL_YEAR, "fiscalYear.lessThan=" + DEFAULT_FISCAL_YEAR);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByFiscalYearIsGreaterThanSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("fiscalYear.greaterThan=" + SMALLER_FISCAL_YEAR, "fiscalYear.greaterThan=" + DEFAULT_FISCAL_YEAR);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByObjectIdIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("objectId.equals=" + DEFAULT_OBJECT_ID, "objectId.equals=" + UPDATED_OBJECT_ID);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByObjectIdIsInShouldWork() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("objectId.in=" + DEFAULT_OBJECT_ID + "," + UPDATED_OBJECT_ID, "objectId.in=" + UPDATED_OBJECT_ID);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByObjectIdIsNullOrNotNull() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("objectId.specified=true", "objectId.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByObjectIdIsGreaterThanOrEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "objectId.greaterThanOrEqual=" + DEFAULT_OBJECT_ID,
            "objectId.greaterThanOrEqual=" + UPDATED_OBJECT_ID
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByObjectIdIsLessThanOrEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("objectId.lessThanOrEqual=" + DEFAULT_OBJECT_ID, "objectId.lessThanOrEqual=" + SMALLER_OBJECT_ID);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByObjectIdIsLessThanSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("objectId.lessThan=" + UPDATED_OBJECT_ID, "objectId.lessThan=" + DEFAULT_OBJECT_ID);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByObjectIdIsGreaterThanSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("objectId.greaterThan=" + SMALLER_OBJECT_ID, "objectId.greaterThan=" + DEFAULT_OBJECT_ID);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByDcsLastModDttmIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "dcsLastModDttm.equals=" + DEFAULT_DCS_LAST_MOD_DTTM,
            "dcsLastModDttm.equals=" + UPDATED_DCS_LAST_MOD_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByDcsLastModDttmIsInShouldWork() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "dcsLastModDttm.in=" + DEFAULT_DCS_LAST_MOD_DTTM + "," + UPDATED_DCS_LAST_MOD_DTTM,
            "dcsLastModDttm.in=" + UPDATED_DCS_LAST_MOD_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByDcsLastModDttmIsNullOrNotNull() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("dcsLastModDttm.specified=true", "dcsLastModDttm.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByDcsLastModDttmIsGreaterThanOrEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "dcsLastModDttm.greaterThanOrEqual=" + DEFAULT_DCS_LAST_MOD_DTTM,
            "dcsLastModDttm.greaterThanOrEqual=" + UPDATED_DCS_LAST_MOD_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByDcsLastModDttmIsLessThanOrEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "dcsLastModDttm.lessThanOrEqual=" + DEFAULT_DCS_LAST_MOD_DTTM,
            "dcsLastModDttm.lessThanOrEqual=" + SMALLER_DCS_LAST_MOD_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByDcsLastModDttmIsLessThanSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "dcsLastModDttm.lessThan=" + UPDATED_DCS_LAST_MOD_DTTM,
            "dcsLastModDttm.lessThan=" + DEFAULT_DCS_LAST_MOD_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByDcsLastModDttmIsGreaterThanSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "dcsLastModDttm.greaterThan=" + SMALLER_DCS_LAST_MOD_DTTM,
            "dcsLastModDttm.greaterThan=" + DEFAULT_DCS_LAST_MOD_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByDcsRecCrtDttmIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "dcsRecCrtDttm.equals=" + DEFAULT_DCS_REC_CRT_DTTM,
            "dcsRecCrtDttm.equals=" + UPDATED_DCS_REC_CRT_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByDcsRecCrtDttmIsInShouldWork() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "dcsRecCrtDttm.in=" + DEFAULT_DCS_REC_CRT_DTTM + "," + UPDATED_DCS_REC_CRT_DTTM,
            "dcsRecCrtDttm.in=" + UPDATED_DCS_REC_CRT_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByDcsRecCrtDttmIsNullOrNotNull() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("dcsRecCrtDttm.specified=true", "dcsRecCrtDttm.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByDcsRecCrtDttmIsGreaterThanOrEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "dcsRecCrtDttm.greaterThanOrEqual=" + DEFAULT_DCS_REC_CRT_DTTM,
            "dcsRecCrtDttm.greaterThanOrEqual=" + UPDATED_DCS_REC_CRT_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByDcsRecCrtDttmIsLessThanOrEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "dcsRecCrtDttm.lessThanOrEqual=" + DEFAULT_DCS_REC_CRT_DTTM,
            "dcsRecCrtDttm.lessThanOrEqual=" + SMALLER_DCS_REC_CRT_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByDcsRecCrtDttmIsLessThanSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "dcsRecCrtDttm.lessThan=" + UPDATED_DCS_REC_CRT_DTTM,
            "dcsRecCrtDttm.lessThan=" + DEFAULT_DCS_REC_CRT_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByDcsRecCrtDttmIsGreaterThanSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering(
            "dcsRecCrtDttm.greaterThan=" + SMALLER_DCS_REC_CRT_DTTM,
            "dcsRecCrtDttm.greaterThan=" + DEFAULT_DCS_REC_CRT_DTTM
        );
    }

    private void defaultPurchaseOrderFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPurchaseOrderShouldBeFound(shouldBeFound);
        defaultPurchaseOrderShouldNotBeFound(shouldNotBeFound);
    }

    private void defaultPurchaseOrderShouldBeFound(String filter) throws Exception {
        restPurchaseOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(purchaseOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].poNumber").value(hasItem(DEFAULT_PO_NUMBER)))
            .andExpect(jsonPath("$.[*].agencyCode").value(hasItem(DEFAULT_AGENCY_CODE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].requester").value(hasItem(DEFAULT_REQUESTER)))
            .andExpect(jsonPath("$.[*].requisitionNumber").value(hasItem(DEFAULT_REQUISITION_NUMBER)))
            .andExpect(jsonPath("$.[*].commodityCode").value(hasItem(DEFAULT_COMMODITY_CODE)))
            .andExpect(jsonPath("$.[*].commodityName").value(hasItem(DEFAULT_COMMODITY_NAME)))
            .andExpect(jsonPath("$.[*].contractNumber").value(hasItem(DEFAULT_CONTRACT_NUMBER)))
            .andExpect(jsonPath("$.[*].supplier").value(hasItem(DEFAULT_SUPPLIER)))
            .andExpect(jsonPath("$.[*].orderedDate").value(hasItem(DEFAULT_ORDERED_DATE.toString())))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].poTotal").value(hasItem(sameNumber(DEFAULT_PO_TOTAL))))
            .andExpect(jsonPath("$.[*].fiscalYear").value(hasItem(DEFAULT_FISCAL_YEAR)))
            .andExpect(jsonPath("$.[*].poTitle").value(hasItem(DEFAULT_PO_TITLE)))
            .andExpect(jsonPath("$.[*].agencyAcronym").value(hasItem(DEFAULT_AGENCY_ACRONYM)))
            .andExpect(jsonPath("$.[*].agencyName").value(hasItem(DEFAULT_AGENCY_NAME)))
            .andExpect(jsonPath("$.[*].dcsLastModDttm").value(hasItem(DEFAULT_DCS_LAST_MOD_DTTM.toString())))
            .andExpect(jsonPath("$.[*].dcsRecCrtDttm").value(hasItem(DEFAULT_DCS_REC_CRT_DTTM.toString())))
            .andExpect(jsonPath("$.[*].objectId").value(hasItem(DEFAULT_OBJECT_ID.intValue())));
    }

    private void defaultPurchaseOrderShouldNotBeFound(String filter) throws Exception {
        restPurchaseOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    void getNonExistingPurchaseOrder() throws Exception {
        restPurchaseOrderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPurchaseOrder() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        PurchaseOrder updatedPurchaseOrder = purchaseOrderRepository.findById(purchaseOrder.getId()).orElseThrow();
        em.detach(updatedPurchaseOrder);
        updatedPurchaseOrder
            .poNumber(UPDATED_PO_NUMBER)
            .agencyCode(UPDATED_AGENCY_CODE)
            .status(UPDATED_STATUS)
            .requester(UPDATED_REQUESTER)
            .requisitionNumber(UPDATED_REQUISITION_NUMBER)
            .commodityCode(UPDATED_COMMODITY_CODE)
            .commodityName(UPDATED_COMMODITY_NAME)
            .contractNumber(UPDATED_CONTRACT_NUMBER)
            .supplier(UPDATED_SUPPLIER)
            .orderedDate(UPDATED_ORDERED_DATE)
            .createDate(UPDATED_CREATE_DATE)
            .poTotal(UPDATED_PO_TOTAL)
            .fiscalYear(UPDATED_FISCAL_YEAR)
            .poTitle(UPDATED_PO_TITLE)
            .agencyAcronym(UPDATED_AGENCY_ACRONYM)
            .agencyName(UPDATED_AGENCY_NAME)
            .dcsLastModDttm(UPDATED_DCS_LAST_MOD_DTTM)
            .dcsRecCrtDttm(UPDATED_DCS_REC_CRT_DTTM)
            .objectId(UPDATED_OBJECT_ID);
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(updatedPurchaseOrder);

        restPurchaseOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, purchaseOrderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(purchaseOrderDTO))
            )
            .andExpect(status().isOk());

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPurchaseOrderToMatchAllProperties(updatedPurchaseOrder);
    }

    @Test
    @Transactional
    void putNonExistingPurchaseOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchaseOrder.setId(longCount.incrementAndGet());

        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);

        restPurchaseOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, purchaseOrderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(purchaseOrderDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPurchaseOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchaseOrder.setId(longCount.incrementAndGet());

        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);

        restPurchaseOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(purchaseOrderDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPurchaseOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchaseOrder.setId(longCount.incrementAndGet());

        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderMapper.toDto(purchaseOrder);

        restPurchaseOrderMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchaseOrderDTO)))
            .andExpect(status().isMethodNotAllowed());

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePurchaseOrder() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);

        long databaseSizeBeforeDelete = getRepositoryCount();

        restPurchaseOrderMockMvc
            .perform(delete(ENTITY_API_URL_ID, purchaseOrder.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return purchaseOrderRepository.count();
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

    protected PurchaseOrder getPersistedPurchaseOrder(PurchaseOrder purchaseOrder) {
        return purchaseOrderRepository.findById(purchaseOrder.getId()).orElseThrow();
    }

    protected void assertPersistedPurchaseOrderToMatchAllProperties(PurchaseOrder expectedPurchaseOrder) {
        assertPurchaseOrderAllPropertiesEquals(expectedPurchaseOrder, getPersistedPurchaseOrder(expectedPurchaseOrder));
    }
}
