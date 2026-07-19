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

    private static final Instant DEFAULT_CREATE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

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

    private static final Instant DEFAULT_DCS_REC_CRT_DTTM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DCS_REC_CRT_DTTM = Instant.now().truncatedTo(ChronoUnit.MILLIS);

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
    void getAllPurchaseOrdersByPoNumberContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("poNumber.contains=" + DEFAULT_PO_NUMBER, "poNumber.contains=" + UPDATED_PO_NUMBER);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByStatusIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersBySupplierContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("supplier.contains=" + DEFAULT_SUPPLIER, "supplier.contains=" + UPDATED_SUPPLIER);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByCommodityCodeIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("commodityCode.equals=" + DEFAULT_COMMODITY_CODE, "commodityCode.equals=" + UPDATED_COMMODITY_CODE);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByPoTotalIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("poTotal.equals=" + DEFAULT_PO_TOTAL, "poTotal.equals=" + UPDATED_PO_TOTAL);
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
    void getAllPurchaseOrdersByFiscalYearIsLessThanSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("fiscalYear.lessThan=" + UPDATED_FISCAL_YEAR, "fiscalYear.lessThan=" + DEFAULT_FISCAL_YEAR);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByOrderedDateIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("orderedDate.equals=" + DEFAULT_ORDERED_DATE, "orderedDate.equals=" + UPDATED_ORDERED_DATE);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByAgencyNameContainsSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("agencyName.contains=" + DEFAULT_AGENCY_NAME, "agencyName.contains=" + UPDATED_AGENCY_NAME);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByObjectIdIsEqualToSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("objectId.equals=" + DEFAULT_OBJECT_ID, "objectId.equals=" + UPDATED_OBJECT_ID);
    }

    @Test
    @Transactional
    void getAllPurchaseOrdersByObjectIdIsGreaterThanSomething() throws Exception {
        insertedPurchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);
        defaultPurchaseOrderFiltering("objectId.greaterThan=" + SMALLER_OBJECT_ID, "objectId.greaterThan=" + DEFAULT_OBJECT_ID);
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
