package io.argorand.poc.dcpass.web.rest;

import static io.argorand.poc.dcpass.domain.PassContractAsserts.*;
import static io.argorand.poc.dcpass.web.rest.TestUtil.createUpdateProxyForBean;
import static io.argorand.poc.dcpass.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.argorand.poc.dcpass.IntegrationTest;
import io.argorand.poc.dcpass.domain.PassContract;
import io.argorand.poc.dcpass.repository.PassContractRepository;
import io.argorand.poc.dcpass.service.dto.PassContractDTO;
import io.argorand.poc.dcpass.service.mapper.PassContractMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link PassContractResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PassContractResourceIT {

    private static final String DEFAULT_PROCUREMENT_METHOD_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_PROCUREMENT_METHOD_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_AGENCY_ACRONYM = "AAAAAAAAAA";
    private static final String UPDATED_AGENCY_ACRONYM = "BBBBBBBBBB";

    private static final String DEFAULT_AGENCY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_AGENCY_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_ROW_ID = 1L;
    private static final Long UPDATED_ROW_ID = 2L;
    private static final Long SMALLER_ROW_ID = 1L - 1L;

    private static final String DEFAULT_AGENCY = "AAAAAAAAAA";
    private static final String UPDATED_AGENCY = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_AWARD_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_AWARD_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_AWARD_DATE = LocalDate.ofEpochDay(-1L);

    private static final BigDecimal DEFAULT_CONTRACT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_CONTRACT_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_CONTRACT_AMOUNT = new BigDecimal(1 - 1);

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_END_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_CONTRACT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_CONTRACT_NUMBER = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_START_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_CONTRACT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_CONTRACT_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTRACTING_OFFICER = "AAAAAAAAAA";
    private static final String UPDATED_CONTRACTING_OFFICER = "BBBBBBBBBB";

    private static final Integer DEFAULT_FISCAL_YEAR = 1;
    private static final Integer UPDATED_FISCAL_YEAR = 2;
    private static final Integer SMALLER_FISCAL_YEAR = 1 - 1;

    private static final String DEFAULT_MARKET_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_MARKET_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_COMMODITY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_COMMODITY_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_COMMODITY_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_COMMODITY_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_CURRENT_OPTION_PERIOD = 1;
    private static final Integer UPDATED_CURRENT_OPTION_PERIOD = 2;
    private static final Integer SMALLER_CURRENT_OPTION_PERIOD = 1 - 1;

    private static final Integer DEFAULT_TOTAL_OPTION_PERIODS = 1;
    private static final Integer UPDATED_TOTAL_OPTION_PERIODS = 2;
    private static final Integer SMALLER_TOTAL_OPTION_PERIODS = 1 - 1;

    private static final String DEFAULT_SUPPLIER = "AAAAAAAAAA";
    private static final String UPDATED_SUPPLIER = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_CONTRACT_TYPE_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_CONTRACT_TYPE_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_CONTRACTING_OFFICER_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_CONTRACTING_OFFICER_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_VENDOR_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_VENDOR_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_VENDOR_CITY = "AAAAAAAAAA";
    private static final String UPDATED_VENDOR_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_VENDOR_STATE = "AAAAAAAAAA";
    private static final String UPDATED_VENDOR_STATE = "BBBBBBBBBB";

    private static final String DEFAULT_VENDOR_ZIP = "AAAAAAAAAA";
    private static final String UPDATED_VENDOR_ZIP = "BBBBBBBBBB";

    private static final String DEFAULT_PUBLISHED_VERSION_ID = "AAAAAAAAAA";
    private static final String UPDATED_PUBLISHED_VERSION_ID = "BBBBBBBBBB";

    private static final String DEFAULT_DOCUMENT_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_DOCUMENT_VERSION = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CONTRACTING_SPLST = "AAAAAAAAAA";
    private static final String UPDATED_CONTRACTING_SPLST = "BBBBBBBBBB";

    private static final String DEFAULT_CONTRACTING_SPLST_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_CONTRACTING_SPLST_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_SOURCE = "AAAAAAAAAA";
    private static final String UPDATED_SOURCE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTRACT_DETAILS_LINK = "AAAAAAAAAA";
    private static final String UPDATED_CONTRACT_DETAILS_LINK = "BBBBBBBBBB";

    private static final String DEFAULT_CONTRACT_ADMINISTRATOR_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CONTRACT_ADMINISTRATOR_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CONTRACT_ADMINISTRATOR_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_CONTRACT_ADMINISTRATOR_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_CONTRACT_ADMINISTRATOR_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_CONTRACT_ADMINISTRATOR_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTRACT_OFFICER_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_CONTRACT_OFFICER_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_CW_INTERNAL_ID = "AAAAAAAAAA";
    private static final String UPDATED_CW_INTERNAL_ID = "BBBBBBBBBB";

    private static final String DEFAULT_CORPORATE_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_CORPORATE_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_CORPORATE_EMAIL_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_CORPORATE_EMAIL_ADDRESS = "BBBBBBBBBB";

    private static final Instant DEFAULT_REC_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REC_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_REC_UPDATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REC_UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DCS_LAST_MOD_DTTM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DCS_LAST_MOD_DTTM = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_OBJECT_ID = 1L;
    private static final Long UPDATED_OBJECT_ID = 2L;
    private static final Long SMALLER_OBJECT_ID = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/pass-contracts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PassContractRepository passContractRepository;

    @Autowired
    private PassContractMapper passContractMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPassContractMockMvc;

    private PassContract passContract;

    private PassContract insertedPassContract;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PassContract createEntity() {
        return new PassContract()
            .procurementMethodDescription(DEFAULT_PROCUREMENT_METHOD_DESCRIPTION)
            .agencyAcronym(DEFAULT_AGENCY_ACRONYM)
            .agencyName(DEFAULT_AGENCY_NAME)
            .rowId(DEFAULT_ROW_ID)
            .agency(DEFAULT_AGENCY)
            .awardDate(DEFAULT_AWARD_DATE)
            .contractAmount(DEFAULT_CONTRACT_AMOUNT)
            .endDate(DEFAULT_END_DATE)
            .contractNumber(DEFAULT_CONTRACT_NUMBER)
            .startDate(DEFAULT_START_DATE)
            .contractStatus(DEFAULT_CONTRACT_STATUS)
            .title(DEFAULT_TITLE)
            .contractingOfficer(DEFAULT_CONTRACTING_OFFICER)
            .fiscalYear(DEFAULT_FISCAL_YEAR)
            .marketType(DEFAULT_MARKET_TYPE)
            .commodityCode(DEFAULT_COMMODITY_CODE)
            .commodityDescription(DEFAULT_COMMODITY_DESCRIPTION)
            .currentOptionPeriod(DEFAULT_CURRENT_OPTION_PERIOD)
            .totalOptionPeriods(DEFAULT_TOTAL_OPTION_PERIODS)
            .supplier(DEFAULT_SUPPLIER)
            .description(DEFAULT_DESCRIPTION)
            .contractTypeDescription(DEFAULT_CONTRACT_TYPE_DESCRIPTION)
            .contractingOfficerEmail(DEFAULT_CONTRACTING_OFFICER_EMAIL)
            .vendorAddress(DEFAULT_VENDOR_ADDRESS)
            .vendorCity(DEFAULT_VENDOR_CITY)
            .vendorState(DEFAULT_VENDOR_STATE)
            .vendorZip(DEFAULT_VENDOR_ZIP)
            .publishedVersionId(DEFAULT_PUBLISHED_VERSION_ID)
            .documentVersion(DEFAULT_DOCUMENT_VERSION)
            .lastModified(DEFAULT_LAST_MODIFIED)
            .contractingSplst(DEFAULT_CONTRACTING_SPLST)
            .contractingSplstEmail(DEFAULT_CONTRACTING_SPLST_EMAIL)
            .source(DEFAULT_SOURCE)
            .contractDetailsLink(DEFAULT_CONTRACT_DETAILS_LINK)
            .contractAdministratorName(DEFAULT_CONTRACT_ADMINISTRATOR_NAME)
            .contractAdministratorEmail(DEFAULT_CONTRACT_ADMINISTRATOR_EMAIL)
            .contractAdministratorPhone(DEFAULT_CONTRACT_ADMINISTRATOR_PHONE)
            .contractOfficerPhone(DEFAULT_CONTRACT_OFFICER_PHONE)
            .cwInternalId(DEFAULT_CW_INTERNAL_ID)
            .corporatePhone(DEFAULT_CORPORATE_PHONE)
            .corporateEmailAddress(DEFAULT_CORPORATE_EMAIL_ADDRESS)
            .recCreatedDate(DEFAULT_REC_CREATED_DATE)
            .recUpdatedDate(DEFAULT_REC_UPDATED_DATE)
            .dcsLastModDttm(DEFAULT_DCS_LAST_MOD_DTTM)
            .objectId(DEFAULT_OBJECT_ID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PassContract createUpdatedEntity() {
        return new PassContract()
            .procurementMethodDescription(UPDATED_PROCUREMENT_METHOD_DESCRIPTION)
            .agencyAcronym(UPDATED_AGENCY_ACRONYM)
            .agencyName(UPDATED_AGENCY_NAME)
            .rowId(UPDATED_ROW_ID)
            .agency(UPDATED_AGENCY)
            .awardDate(UPDATED_AWARD_DATE)
            .contractAmount(UPDATED_CONTRACT_AMOUNT)
            .endDate(UPDATED_END_DATE)
            .contractNumber(UPDATED_CONTRACT_NUMBER)
            .startDate(UPDATED_START_DATE)
            .contractStatus(UPDATED_CONTRACT_STATUS)
            .title(UPDATED_TITLE)
            .contractingOfficer(UPDATED_CONTRACTING_OFFICER)
            .fiscalYear(UPDATED_FISCAL_YEAR)
            .marketType(UPDATED_MARKET_TYPE)
            .commodityCode(UPDATED_COMMODITY_CODE)
            .commodityDescription(UPDATED_COMMODITY_DESCRIPTION)
            .currentOptionPeriod(UPDATED_CURRENT_OPTION_PERIOD)
            .totalOptionPeriods(UPDATED_TOTAL_OPTION_PERIODS)
            .supplier(UPDATED_SUPPLIER)
            .description(UPDATED_DESCRIPTION)
            .contractTypeDescription(UPDATED_CONTRACT_TYPE_DESCRIPTION)
            .contractingOfficerEmail(UPDATED_CONTRACTING_OFFICER_EMAIL)
            .vendorAddress(UPDATED_VENDOR_ADDRESS)
            .vendorCity(UPDATED_VENDOR_CITY)
            .vendorState(UPDATED_VENDOR_STATE)
            .vendorZip(UPDATED_VENDOR_ZIP)
            .publishedVersionId(UPDATED_PUBLISHED_VERSION_ID)
            .documentVersion(UPDATED_DOCUMENT_VERSION)
            .lastModified(UPDATED_LAST_MODIFIED)
            .contractingSplst(UPDATED_CONTRACTING_SPLST)
            .contractingSplstEmail(UPDATED_CONTRACTING_SPLST_EMAIL)
            .source(UPDATED_SOURCE)
            .contractDetailsLink(UPDATED_CONTRACT_DETAILS_LINK)
            .contractAdministratorName(UPDATED_CONTRACT_ADMINISTRATOR_NAME)
            .contractAdministratorEmail(UPDATED_CONTRACT_ADMINISTRATOR_EMAIL)
            .contractAdministratorPhone(UPDATED_CONTRACT_ADMINISTRATOR_PHONE)
            .contractOfficerPhone(UPDATED_CONTRACT_OFFICER_PHONE)
            .cwInternalId(UPDATED_CW_INTERNAL_ID)
            .corporatePhone(UPDATED_CORPORATE_PHONE)
            .corporateEmailAddress(UPDATED_CORPORATE_EMAIL_ADDRESS)
            .recCreatedDate(UPDATED_REC_CREATED_DATE)
            .recUpdatedDate(UPDATED_REC_UPDATED_DATE)
            .dcsLastModDttm(UPDATED_DCS_LAST_MOD_DTTM)
            .objectId(UPDATED_OBJECT_ID);
    }

    @BeforeEach
    void initTest() {
        passContract = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPassContract != null) {
            passContractRepository.delete(insertedPassContract);
            insertedPassContract = null;
        }
    }

    @Test
    @Transactional
    void createPassContract() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PassContract
        PassContractDTO passContractDTO = passContractMapper.toDto(passContract);
        var returnedPassContractDTO = om.readValue(
            restPassContractMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(passContractDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PassContractDTO.class
        );

        // Validate the PassContract in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPassContract = passContractMapper.toEntity(returnedPassContractDTO);
        assertPassContractUpdatableFieldsEquals(returnedPassContract, getPersistedPassContract(returnedPassContract));

        insertedPassContract = returnedPassContract;
    }

    @Test
    @Transactional
    void createPassContractWithExistingId() throws Exception {
        // Create the PassContract with an existing ID
        passContract.setId(1L);
        PassContractDTO passContractDTO = passContractMapper.toDto(passContract);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPassContractMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(passContractDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PassContract in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPassContracts() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList
        restPassContractMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(passContract.getId().intValue())))
            .andExpect(jsonPath("$.[*].procurementMethodDescription").value(hasItem(DEFAULT_PROCUREMENT_METHOD_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].agencyAcronym").value(hasItem(DEFAULT_AGENCY_ACRONYM)))
            .andExpect(jsonPath("$.[*].agencyName").value(hasItem(DEFAULT_AGENCY_NAME)))
            .andExpect(jsonPath("$.[*].rowId").value(hasItem(DEFAULT_ROW_ID.intValue())))
            .andExpect(jsonPath("$.[*].agency").value(hasItem(DEFAULT_AGENCY)))
            .andExpect(jsonPath("$.[*].awardDate").value(hasItem(DEFAULT_AWARD_DATE.toString())))
            .andExpect(jsonPath("$.[*].contractAmount").value(hasItem(sameNumber(DEFAULT_CONTRACT_AMOUNT))))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].contractNumber").value(hasItem(DEFAULT_CONTRACT_NUMBER)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].contractStatus").value(hasItem(DEFAULT_CONTRACT_STATUS)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].contractingOfficer").value(hasItem(DEFAULT_CONTRACTING_OFFICER)))
            .andExpect(jsonPath("$.[*].fiscalYear").value(hasItem(DEFAULT_FISCAL_YEAR)))
            .andExpect(jsonPath("$.[*].marketType").value(hasItem(DEFAULT_MARKET_TYPE)))
            .andExpect(jsonPath("$.[*].commodityCode").value(hasItem(DEFAULT_COMMODITY_CODE)))
            .andExpect(jsonPath("$.[*].commodityDescription").value(hasItem(DEFAULT_COMMODITY_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].currentOptionPeriod").value(hasItem(DEFAULT_CURRENT_OPTION_PERIOD)))
            .andExpect(jsonPath("$.[*].totalOptionPeriods").value(hasItem(DEFAULT_TOTAL_OPTION_PERIODS)))
            .andExpect(jsonPath("$.[*].supplier").value(hasItem(DEFAULT_SUPPLIER)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].contractTypeDescription").value(hasItem(DEFAULT_CONTRACT_TYPE_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].contractingOfficerEmail").value(hasItem(DEFAULT_CONTRACTING_OFFICER_EMAIL)))
            .andExpect(jsonPath("$.[*].vendorAddress").value(hasItem(DEFAULT_VENDOR_ADDRESS)))
            .andExpect(jsonPath("$.[*].vendorCity").value(hasItem(DEFAULT_VENDOR_CITY)))
            .andExpect(jsonPath("$.[*].vendorState").value(hasItem(DEFAULT_VENDOR_STATE)))
            .andExpect(jsonPath("$.[*].vendorZip").value(hasItem(DEFAULT_VENDOR_ZIP)))
            .andExpect(jsonPath("$.[*].publishedVersionId").value(hasItem(DEFAULT_PUBLISHED_VERSION_ID)))
            .andExpect(jsonPath("$.[*].documentVersion").value(hasItem(DEFAULT_DOCUMENT_VERSION)))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(DEFAULT_LAST_MODIFIED.toString())))
            .andExpect(jsonPath("$.[*].contractingSplst").value(hasItem(DEFAULT_CONTRACTING_SPLST)))
            .andExpect(jsonPath("$.[*].contractingSplstEmail").value(hasItem(DEFAULT_CONTRACTING_SPLST_EMAIL)))
            .andExpect(jsonPath("$.[*].source").value(hasItem(DEFAULT_SOURCE)))
            .andExpect(jsonPath("$.[*].contractDetailsLink").value(hasItem(DEFAULT_CONTRACT_DETAILS_LINK)))
            .andExpect(jsonPath("$.[*].contractAdministratorName").value(hasItem(DEFAULT_CONTRACT_ADMINISTRATOR_NAME)))
            .andExpect(jsonPath("$.[*].contractAdministratorEmail").value(hasItem(DEFAULT_CONTRACT_ADMINISTRATOR_EMAIL)))
            .andExpect(jsonPath("$.[*].contractAdministratorPhone").value(hasItem(DEFAULT_CONTRACT_ADMINISTRATOR_PHONE)))
            .andExpect(jsonPath("$.[*].contractOfficerPhone").value(hasItem(DEFAULT_CONTRACT_OFFICER_PHONE)))
            .andExpect(jsonPath("$.[*].cwInternalId").value(hasItem(DEFAULT_CW_INTERNAL_ID)))
            .andExpect(jsonPath("$.[*].corporatePhone").value(hasItem(DEFAULT_CORPORATE_PHONE)))
            .andExpect(jsonPath("$.[*].corporateEmailAddress").value(hasItem(DEFAULT_CORPORATE_EMAIL_ADDRESS)))
            .andExpect(jsonPath("$.[*].recCreatedDate").value(hasItem(DEFAULT_REC_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].recUpdatedDate").value(hasItem(DEFAULT_REC_UPDATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].dcsLastModDttm").value(hasItem(DEFAULT_DCS_LAST_MOD_DTTM.toString())))
            .andExpect(jsonPath("$.[*].objectId").value(hasItem(DEFAULT_OBJECT_ID.intValue())));
    }

    @Test
    @Transactional
    void getPassContract() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get the passContract
        restPassContractMockMvc
            .perform(get(ENTITY_API_URL_ID, passContract.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(passContract.getId().intValue()))
            .andExpect(jsonPath("$.procurementMethodDescription").value(DEFAULT_PROCUREMENT_METHOD_DESCRIPTION))
            .andExpect(jsonPath("$.agencyAcronym").value(DEFAULT_AGENCY_ACRONYM))
            .andExpect(jsonPath("$.agencyName").value(DEFAULT_AGENCY_NAME))
            .andExpect(jsonPath("$.rowId").value(DEFAULT_ROW_ID.intValue()))
            .andExpect(jsonPath("$.agency").value(DEFAULT_AGENCY))
            .andExpect(jsonPath("$.awardDate").value(DEFAULT_AWARD_DATE.toString()))
            .andExpect(jsonPath("$.contractAmount").value(sameNumber(DEFAULT_CONTRACT_AMOUNT)))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.contractNumber").value(DEFAULT_CONTRACT_NUMBER))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.contractStatus").value(DEFAULT_CONTRACT_STATUS))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.contractingOfficer").value(DEFAULT_CONTRACTING_OFFICER))
            .andExpect(jsonPath("$.fiscalYear").value(DEFAULT_FISCAL_YEAR))
            .andExpect(jsonPath("$.marketType").value(DEFAULT_MARKET_TYPE))
            .andExpect(jsonPath("$.commodityCode").value(DEFAULT_COMMODITY_CODE))
            .andExpect(jsonPath("$.commodityDescription").value(DEFAULT_COMMODITY_DESCRIPTION))
            .andExpect(jsonPath("$.currentOptionPeriod").value(DEFAULT_CURRENT_OPTION_PERIOD))
            .andExpect(jsonPath("$.totalOptionPeriods").value(DEFAULT_TOTAL_OPTION_PERIODS))
            .andExpect(jsonPath("$.supplier").value(DEFAULT_SUPPLIER))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.contractTypeDescription").value(DEFAULT_CONTRACT_TYPE_DESCRIPTION))
            .andExpect(jsonPath("$.contractingOfficerEmail").value(DEFAULT_CONTRACTING_OFFICER_EMAIL))
            .andExpect(jsonPath("$.vendorAddress").value(DEFAULT_VENDOR_ADDRESS))
            .andExpect(jsonPath("$.vendorCity").value(DEFAULT_VENDOR_CITY))
            .andExpect(jsonPath("$.vendorState").value(DEFAULT_VENDOR_STATE))
            .andExpect(jsonPath("$.vendorZip").value(DEFAULT_VENDOR_ZIP))
            .andExpect(jsonPath("$.publishedVersionId").value(DEFAULT_PUBLISHED_VERSION_ID))
            .andExpect(jsonPath("$.documentVersion").value(DEFAULT_DOCUMENT_VERSION))
            .andExpect(jsonPath("$.lastModified").value(DEFAULT_LAST_MODIFIED.toString()))
            .andExpect(jsonPath("$.contractingSplst").value(DEFAULT_CONTRACTING_SPLST))
            .andExpect(jsonPath("$.contractingSplstEmail").value(DEFAULT_CONTRACTING_SPLST_EMAIL))
            .andExpect(jsonPath("$.source").value(DEFAULT_SOURCE))
            .andExpect(jsonPath("$.contractDetailsLink").value(DEFAULT_CONTRACT_DETAILS_LINK))
            .andExpect(jsonPath("$.contractAdministratorName").value(DEFAULT_CONTRACT_ADMINISTRATOR_NAME))
            .andExpect(jsonPath("$.contractAdministratorEmail").value(DEFAULT_CONTRACT_ADMINISTRATOR_EMAIL))
            .andExpect(jsonPath("$.contractAdministratorPhone").value(DEFAULT_CONTRACT_ADMINISTRATOR_PHONE))
            .andExpect(jsonPath("$.contractOfficerPhone").value(DEFAULT_CONTRACT_OFFICER_PHONE))
            .andExpect(jsonPath("$.cwInternalId").value(DEFAULT_CW_INTERNAL_ID))
            .andExpect(jsonPath("$.corporatePhone").value(DEFAULT_CORPORATE_PHONE))
            .andExpect(jsonPath("$.corporateEmailAddress").value(DEFAULT_CORPORATE_EMAIL_ADDRESS))
            .andExpect(jsonPath("$.recCreatedDate").value(DEFAULT_REC_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.recUpdatedDate").value(DEFAULT_REC_UPDATED_DATE.toString()))
            .andExpect(jsonPath("$.dcsLastModDttm").value(DEFAULT_DCS_LAST_MOD_DTTM.toString()))
            .andExpect(jsonPath("$.objectId").value(DEFAULT_OBJECT_ID.intValue()));
    }

    @Test
    @Transactional
    void getPassContractsByIdFiltering() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        Long id = passContract.getId();

        defaultPassContractFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPassContractFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPassContractFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPassContractsByProcurementMethodDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where procurementMethodDescription equals to
        defaultPassContractFiltering(
            "procurementMethodDescription.equals=" + DEFAULT_PROCUREMENT_METHOD_DESCRIPTION,
            "procurementMethodDescription.equals=" + UPDATED_PROCUREMENT_METHOD_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByProcurementMethodDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where procurementMethodDescription in
        defaultPassContractFiltering(
            "procurementMethodDescription.in=" + DEFAULT_PROCUREMENT_METHOD_DESCRIPTION + "," + UPDATED_PROCUREMENT_METHOD_DESCRIPTION,
            "procurementMethodDescription.in=" + UPDATED_PROCUREMENT_METHOD_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByProcurementMethodDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where procurementMethodDescription is not null
        defaultPassContractFiltering("procurementMethodDescription.specified=true", "procurementMethodDescription.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByProcurementMethodDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where procurementMethodDescription contains
        defaultPassContractFiltering(
            "procurementMethodDescription.contains=" + DEFAULT_PROCUREMENT_METHOD_DESCRIPTION,
            "procurementMethodDescription.contains=" + UPDATED_PROCUREMENT_METHOD_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByProcurementMethodDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where procurementMethodDescription does not contain
        defaultPassContractFiltering(
            "procurementMethodDescription.doesNotContain=" + UPDATED_PROCUREMENT_METHOD_DESCRIPTION,
            "procurementMethodDescription.doesNotContain=" + DEFAULT_PROCUREMENT_METHOD_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByAgencyAcronymIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where agencyAcronym equals to
        defaultPassContractFiltering("agencyAcronym.equals=" + DEFAULT_AGENCY_ACRONYM, "agencyAcronym.equals=" + UPDATED_AGENCY_ACRONYM);
    }

    @Test
    @Transactional
    void getAllPassContractsByAgencyAcronymIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where agencyAcronym in
        defaultPassContractFiltering(
            "agencyAcronym.in=" + DEFAULT_AGENCY_ACRONYM + "," + UPDATED_AGENCY_ACRONYM,
            "agencyAcronym.in=" + UPDATED_AGENCY_ACRONYM
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByAgencyAcronymIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where agencyAcronym is not null
        defaultPassContractFiltering("agencyAcronym.specified=true", "agencyAcronym.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByAgencyAcronymContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where agencyAcronym contains
        defaultPassContractFiltering(
            "agencyAcronym.contains=" + DEFAULT_AGENCY_ACRONYM,
            "agencyAcronym.contains=" + UPDATED_AGENCY_ACRONYM
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByAgencyAcronymNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where agencyAcronym does not contain
        defaultPassContractFiltering(
            "agencyAcronym.doesNotContain=" + UPDATED_AGENCY_ACRONYM,
            "agencyAcronym.doesNotContain=" + DEFAULT_AGENCY_ACRONYM
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByAgencyNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where agencyName equals to
        defaultPassContractFiltering("agencyName.equals=" + DEFAULT_AGENCY_NAME, "agencyName.equals=" + UPDATED_AGENCY_NAME);
    }

    @Test
    @Transactional
    void getAllPassContractsByAgencyNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where agencyName in
        defaultPassContractFiltering(
            "agencyName.in=" + DEFAULT_AGENCY_NAME + "," + UPDATED_AGENCY_NAME,
            "agencyName.in=" + UPDATED_AGENCY_NAME
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByAgencyNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where agencyName is not null
        defaultPassContractFiltering("agencyName.specified=true", "agencyName.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByAgencyNameContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where agencyName contains
        defaultPassContractFiltering("agencyName.contains=" + DEFAULT_AGENCY_NAME, "agencyName.contains=" + UPDATED_AGENCY_NAME);
    }

    @Test
    @Transactional
    void getAllPassContractsByAgencyNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where agencyName does not contain
        defaultPassContractFiltering(
            "agencyName.doesNotContain=" + UPDATED_AGENCY_NAME,
            "agencyName.doesNotContain=" + DEFAULT_AGENCY_NAME
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByRowIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where rowId equals to
        defaultPassContractFiltering("rowId.equals=" + DEFAULT_ROW_ID, "rowId.equals=" + UPDATED_ROW_ID);
    }

    @Test
    @Transactional
    void getAllPassContractsByRowIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where rowId in
        defaultPassContractFiltering("rowId.in=" + DEFAULT_ROW_ID + "," + UPDATED_ROW_ID, "rowId.in=" + UPDATED_ROW_ID);
    }

    @Test
    @Transactional
    void getAllPassContractsByRowIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where rowId is not null
        defaultPassContractFiltering("rowId.specified=true", "rowId.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByRowIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where rowId is greater than or equal to
        defaultPassContractFiltering("rowId.greaterThanOrEqual=" + DEFAULT_ROW_ID, "rowId.greaterThanOrEqual=" + UPDATED_ROW_ID);
    }

    @Test
    @Transactional
    void getAllPassContractsByRowIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where rowId is less than or equal to
        defaultPassContractFiltering("rowId.lessThanOrEqual=" + DEFAULT_ROW_ID, "rowId.lessThanOrEqual=" + SMALLER_ROW_ID);
    }

    @Test
    @Transactional
    void getAllPassContractsByRowIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where rowId is less than
        defaultPassContractFiltering("rowId.lessThan=" + UPDATED_ROW_ID, "rowId.lessThan=" + DEFAULT_ROW_ID);
    }

    @Test
    @Transactional
    void getAllPassContractsByRowIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where rowId is greater than
        defaultPassContractFiltering("rowId.greaterThan=" + SMALLER_ROW_ID, "rowId.greaterThan=" + DEFAULT_ROW_ID);
    }

    @Test
    @Transactional
    void getAllPassContractsByAgencyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where agency equals to
        defaultPassContractFiltering("agency.equals=" + DEFAULT_AGENCY, "agency.equals=" + UPDATED_AGENCY);
    }

    @Test
    @Transactional
    void getAllPassContractsByAgencyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where agency in
        defaultPassContractFiltering("agency.in=" + DEFAULT_AGENCY + "," + UPDATED_AGENCY, "agency.in=" + UPDATED_AGENCY);
    }

    @Test
    @Transactional
    void getAllPassContractsByAgencyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where agency is not null
        defaultPassContractFiltering("agency.specified=true", "agency.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByAgencyContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where agency contains
        defaultPassContractFiltering("agency.contains=" + DEFAULT_AGENCY, "agency.contains=" + UPDATED_AGENCY);
    }

    @Test
    @Transactional
    void getAllPassContractsByAgencyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where agency does not contain
        defaultPassContractFiltering("agency.doesNotContain=" + UPDATED_AGENCY, "agency.doesNotContain=" + DEFAULT_AGENCY);
    }

    @Test
    @Transactional
    void getAllPassContractsByAwardDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where awardDate equals to
        defaultPassContractFiltering("awardDate.equals=" + DEFAULT_AWARD_DATE, "awardDate.equals=" + UPDATED_AWARD_DATE);
    }

    @Test
    @Transactional
    void getAllPassContractsByAwardDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where awardDate in
        defaultPassContractFiltering("awardDate.in=" + DEFAULT_AWARD_DATE + "," + UPDATED_AWARD_DATE, "awardDate.in=" + UPDATED_AWARD_DATE);
    }

    @Test
    @Transactional
    void getAllPassContractsByAwardDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where awardDate is not null
        defaultPassContractFiltering("awardDate.specified=true", "awardDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByAwardDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where awardDate is greater than or equal to
        defaultPassContractFiltering(
            "awardDate.greaterThanOrEqual=" + DEFAULT_AWARD_DATE,
            "awardDate.greaterThanOrEqual=" + UPDATED_AWARD_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByAwardDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where awardDate is less than or equal to
        defaultPassContractFiltering("awardDate.lessThanOrEqual=" + DEFAULT_AWARD_DATE, "awardDate.lessThanOrEqual=" + SMALLER_AWARD_DATE);
    }

    @Test
    @Transactional
    void getAllPassContractsByAwardDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where awardDate is less than
        defaultPassContractFiltering("awardDate.lessThan=" + UPDATED_AWARD_DATE, "awardDate.lessThan=" + DEFAULT_AWARD_DATE);
    }

    @Test
    @Transactional
    void getAllPassContractsByAwardDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where awardDate is greater than
        defaultPassContractFiltering("awardDate.greaterThan=" + SMALLER_AWARD_DATE, "awardDate.greaterThan=" + DEFAULT_AWARD_DATE);
    }

    @Test
    @Transactional
    void getAllPassContractsByContractAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractAmount equals to
        defaultPassContractFiltering(
            "contractAmount.equals=" + DEFAULT_CONTRACT_AMOUNT,
            "contractAmount.equals=" + UPDATED_CONTRACT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractAmount in
        defaultPassContractFiltering(
            "contractAmount.in=" + DEFAULT_CONTRACT_AMOUNT + "," + UPDATED_CONTRACT_AMOUNT,
            "contractAmount.in=" + UPDATED_CONTRACT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractAmount is not null
        defaultPassContractFiltering("contractAmount.specified=true", "contractAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByContractAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractAmount is greater than or equal to
        defaultPassContractFiltering(
            "contractAmount.greaterThanOrEqual=" + DEFAULT_CONTRACT_AMOUNT,
            "contractAmount.greaterThanOrEqual=" + UPDATED_CONTRACT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractAmount is less than or equal to
        defaultPassContractFiltering(
            "contractAmount.lessThanOrEqual=" + DEFAULT_CONTRACT_AMOUNT,
            "contractAmount.lessThanOrEqual=" + SMALLER_CONTRACT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractAmount is less than
        defaultPassContractFiltering(
            "contractAmount.lessThan=" + UPDATED_CONTRACT_AMOUNT,
            "contractAmount.lessThan=" + DEFAULT_CONTRACT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractAmount is greater than
        defaultPassContractFiltering(
            "contractAmount.greaterThan=" + SMALLER_CONTRACT_AMOUNT,
            "contractAmount.greaterThan=" + DEFAULT_CONTRACT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where endDate equals to
        defaultPassContractFiltering("endDate.equals=" + DEFAULT_END_DATE, "endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllPassContractsByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where endDate in
        defaultPassContractFiltering("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE, "endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllPassContractsByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where endDate is not null
        defaultPassContractFiltering("endDate.specified=true", "endDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByEndDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where endDate is greater than or equal to
        defaultPassContractFiltering("endDate.greaterThanOrEqual=" + DEFAULT_END_DATE, "endDate.greaterThanOrEqual=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllPassContractsByEndDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where endDate is less than or equal to
        defaultPassContractFiltering("endDate.lessThanOrEqual=" + DEFAULT_END_DATE, "endDate.lessThanOrEqual=" + SMALLER_END_DATE);
    }

    @Test
    @Transactional
    void getAllPassContractsByEndDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where endDate is less than
        defaultPassContractFiltering("endDate.lessThan=" + UPDATED_END_DATE, "endDate.lessThan=" + DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    void getAllPassContractsByEndDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where endDate is greater than
        defaultPassContractFiltering("endDate.greaterThan=" + SMALLER_END_DATE, "endDate.greaterThan=" + DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    void getAllPassContractsByContractNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractNumber equals to
        defaultPassContractFiltering(
            "contractNumber.equals=" + DEFAULT_CONTRACT_NUMBER,
            "contractNumber.equals=" + UPDATED_CONTRACT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractNumber in
        defaultPassContractFiltering(
            "contractNumber.in=" + DEFAULT_CONTRACT_NUMBER + "," + UPDATED_CONTRACT_NUMBER,
            "contractNumber.in=" + UPDATED_CONTRACT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractNumber is not null
        defaultPassContractFiltering("contractNumber.specified=true", "contractNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByContractNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractNumber contains
        defaultPassContractFiltering(
            "contractNumber.contains=" + DEFAULT_CONTRACT_NUMBER,
            "contractNumber.contains=" + UPDATED_CONTRACT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractNumber does not contain
        defaultPassContractFiltering(
            "contractNumber.doesNotContain=" + UPDATED_CONTRACT_NUMBER,
            "contractNumber.doesNotContain=" + DEFAULT_CONTRACT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where startDate equals to
        defaultPassContractFiltering("startDate.equals=" + DEFAULT_START_DATE, "startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllPassContractsByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where startDate in
        defaultPassContractFiltering("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE, "startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllPassContractsByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where startDate is not null
        defaultPassContractFiltering("startDate.specified=true", "startDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByStartDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where startDate is greater than or equal to
        defaultPassContractFiltering(
            "startDate.greaterThanOrEqual=" + DEFAULT_START_DATE,
            "startDate.greaterThanOrEqual=" + UPDATED_START_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByStartDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where startDate is less than or equal to
        defaultPassContractFiltering("startDate.lessThanOrEqual=" + DEFAULT_START_DATE, "startDate.lessThanOrEqual=" + SMALLER_START_DATE);
    }

    @Test
    @Transactional
    void getAllPassContractsByStartDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where startDate is less than
        defaultPassContractFiltering("startDate.lessThan=" + UPDATED_START_DATE, "startDate.lessThan=" + DEFAULT_START_DATE);
    }

    @Test
    @Transactional
    void getAllPassContractsByStartDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where startDate is greater than
        defaultPassContractFiltering("startDate.greaterThan=" + SMALLER_START_DATE, "startDate.greaterThan=" + DEFAULT_START_DATE);
    }

    @Test
    @Transactional
    void getAllPassContractsByContractStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractStatus equals to
        defaultPassContractFiltering(
            "contractStatus.equals=" + DEFAULT_CONTRACT_STATUS,
            "contractStatus.equals=" + UPDATED_CONTRACT_STATUS
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractStatus in
        defaultPassContractFiltering(
            "contractStatus.in=" + DEFAULT_CONTRACT_STATUS + "," + UPDATED_CONTRACT_STATUS,
            "contractStatus.in=" + UPDATED_CONTRACT_STATUS
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractStatus is not null
        defaultPassContractFiltering("contractStatus.specified=true", "contractStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByContractStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractStatus contains
        defaultPassContractFiltering(
            "contractStatus.contains=" + DEFAULT_CONTRACT_STATUS,
            "contractStatus.contains=" + UPDATED_CONTRACT_STATUS
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractStatus does not contain
        defaultPassContractFiltering(
            "contractStatus.doesNotContain=" + UPDATED_CONTRACT_STATUS,
            "contractStatus.doesNotContain=" + DEFAULT_CONTRACT_STATUS
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where title equals to
        defaultPassContractFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllPassContractsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where title in
        defaultPassContractFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllPassContractsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where title is not null
        defaultPassContractFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByTitleContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where title contains
        defaultPassContractFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllPassContractsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where title does not contain
        defaultPassContractFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void getAllPassContractsByContractingOfficerIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractingOfficer equals to
        defaultPassContractFiltering(
            "contractingOfficer.equals=" + DEFAULT_CONTRACTING_OFFICER,
            "contractingOfficer.equals=" + UPDATED_CONTRACTING_OFFICER
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractingOfficerIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractingOfficer in
        defaultPassContractFiltering(
            "contractingOfficer.in=" + DEFAULT_CONTRACTING_OFFICER + "," + UPDATED_CONTRACTING_OFFICER,
            "contractingOfficer.in=" + UPDATED_CONTRACTING_OFFICER
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractingOfficerIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractingOfficer is not null
        defaultPassContractFiltering("contractingOfficer.specified=true", "contractingOfficer.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByContractingOfficerContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractingOfficer contains
        defaultPassContractFiltering(
            "contractingOfficer.contains=" + DEFAULT_CONTRACTING_OFFICER,
            "contractingOfficer.contains=" + UPDATED_CONTRACTING_OFFICER
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractingOfficerNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractingOfficer does not contain
        defaultPassContractFiltering(
            "contractingOfficer.doesNotContain=" + UPDATED_CONTRACTING_OFFICER,
            "contractingOfficer.doesNotContain=" + DEFAULT_CONTRACTING_OFFICER
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByFiscalYearIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where fiscalYear equals to
        defaultPassContractFiltering("fiscalYear.equals=" + DEFAULT_FISCAL_YEAR, "fiscalYear.equals=" + UPDATED_FISCAL_YEAR);
    }

    @Test
    @Transactional
    void getAllPassContractsByFiscalYearIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where fiscalYear in
        defaultPassContractFiltering(
            "fiscalYear.in=" + DEFAULT_FISCAL_YEAR + "," + UPDATED_FISCAL_YEAR,
            "fiscalYear.in=" + UPDATED_FISCAL_YEAR
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByFiscalYearIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where fiscalYear is not null
        defaultPassContractFiltering("fiscalYear.specified=true", "fiscalYear.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByFiscalYearIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where fiscalYear is greater than or equal to
        defaultPassContractFiltering(
            "fiscalYear.greaterThanOrEqual=" + DEFAULT_FISCAL_YEAR,
            "fiscalYear.greaterThanOrEqual=" + UPDATED_FISCAL_YEAR
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByFiscalYearIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where fiscalYear is less than or equal to
        defaultPassContractFiltering(
            "fiscalYear.lessThanOrEqual=" + DEFAULT_FISCAL_YEAR,
            "fiscalYear.lessThanOrEqual=" + SMALLER_FISCAL_YEAR
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByFiscalYearIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where fiscalYear is less than
        defaultPassContractFiltering("fiscalYear.lessThan=" + UPDATED_FISCAL_YEAR, "fiscalYear.lessThan=" + DEFAULT_FISCAL_YEAR);
    }

    @Test
    @Transactional
    void getAllPassContractsByFiscalYearIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where fiscalYear is greater than
        defaultPassContractFiltering("fiscalYear.greaterThan=" + SMALLER_FISCAL_YEAR, "fiscalYear.greaterThan=" + DEFAULT_FISCAL_YEAR);
    }

    @Test
    @Transactional
    void getAllPassContractsByMarketTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where marketType equals to
        defaultPassContractFiltering("marketType.equals=" + DEFAULT_MARKET_TYPE, "marketType.equals=" + UPDATED_MARKET_TYPE);
    }

    @Test
    @Transactional
    void getAllPassContractsByMarketTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where marketType in
        defaultPassContractFiltering(
            "marketType.in=" + DEFAULT_MARKET_TYPE + "," + UPDATED_MARKET_TYPE,
            "marketType.in=" + UPDATED_MARKET_TYPE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByMarketTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where marketType is not null
        defaultPassContractFiltering("marketType.specified=true", "marketType.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByMarketTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where marketType contains
        defaultPassContractFiltering("marketType.contains=" + DEFAULT_MARKET_TYPE, "marketType.contains=" + UPDATED_MARKET_TYPE);
    }

    @Test
    @Transactional
    void getAllPassContractsByMarketTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where marketType does not contain
        defaultPassContractFiltering(
            "marketType.doesNotContain=" + UPDATED_MARKET_TYPE,
            "marketType.doesNotContain=" + DEFAULT_MARKET_TYPE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByCommodityCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where commodityCode equals to
        defaultPassContractFiltering("commodityCode.equals=" + DEFAULT_COMMODITY_CODE, "commodityCode.equals=" + UPDATED_COMMODITY_CODE);
    }

    @Test
    @Transactional
    void getAllPassContractsByCommodityCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where commodityCode in
        defaultPassContractFiltering(
            "commodityCode.in=" + DEFAULT_COMMODITY_CODE + "," + UPDATED_COMMODITY_CODE,
            "commodityCode.in=" + UPDATED_COMMODITY_CODE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByCommodityCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where commodityCode is not null
        defaultPassContractFiltering("commodityCode.specified=true", "commodityCode.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByCommodityCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where commodityCode contains
        defaultPassContractFiltering(
            "commodityCode.contains=" + DEFAULT_COMMODITY_CODE,
            "commodityCode.contains=" + UPDATED_COMMODITY_CODE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByCommodityCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where commodityCode does not contain
        defaultPassContractFiltering(
            "commodityCode.doesNotContain=" + UPDATED_COMMODITY_CODE,
            "commodityCode.doesNotContain=" + DEFAULT_COMMODITY_CODE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByCommodityDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where commodityDescription equals to
        defaultPassContractFiltering(
            "commodityDescription.equals=" + DEFAULT_COMMODITY_DESCRIPTION,
            "commodityDescription.equals=" + UPDATED_COMMODITY_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByCommodityDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where commodityDescription in
        defaultPassContractFiltering(
            "commodityDescription.in=" + DEFAULT_COMMODITY_DESCRIPTION + "," + UPDATED_COMMODITY_DESCRIPTION,
            "commodityDescription.in=" + UPDATED_COMMODITY_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByCommodityDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where commodityDescription is not null
        defaultPassContractFiltering("commodityDescription.specified=true", "commodityDescription.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByCommodityDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where commodityDescription contains
        defaultPassContractFiltering(
            "commodityDescription.contains=" + DEFAULT_COMMODITY_DESCRIPTION,
            "commodityDescription.contains=" + UPDATED_COMMODITY_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByCommodityDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where commodityDescription does not contain
        defaultPassContractFiltering(
            "commodityDescription.doesNotContain=" + UPDATED_COMMODITY_DESCRIPTION,
            "commodityDescription.doesNotContain=" + DEFAULT_COMMODITY_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByCurrentOptionPeriodIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where currentOptionPeriod equals to
        defaultPassContractFiltering(
            "currentOptionPeriod.equals=" + DEFAULT_CURRENT_OPTION_PERIOD,
            "currentOptionPeriod.equals=" + UPDATED_CURRENT_OPTION_PERIOD
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByCurrentOptionPeriodIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where currentOptionPeriod in
        defaultPassContractFiltering(
            "currentOptionPeriod.in=" + DEFAULT_CURRENT_OPTION_PERIOD + "," + UPDATED_CURRENT_OPTION_PERIOD,
            "currentOptionPeriod.in=" + UPDATED_CURRENT_OPTION_PERIOD
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByCurrentOptionPeriodIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where currentOptionPeriod is not null
        defaultPassContractFiltering("currentOptionPeriod.specified=true", "currentOptionPeriod.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByCurrentOptionPeriodIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where currentOptionPeriod is greater than or equal to
        defaultPassContractFiltering(
            "currentOptionPeriod.greaterThanOrEqual=" + DEFAULT_CURRENT_OPTION_PERIOD,
            "currentOptionPeriod.greaterThanOrEqual=" + UPDATED_CURRENT_OPTION_PERIOD
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByCurrentOptionPeriodIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where currentOptionPeriod is less than or equal to
        defaultPassContractFiltering(
            "currentOptionPeriod.lessThanOrEqual=" + DEFAULT_CURRENT_OPTION_PERIOD,
            "currentOptionPeriod.lessThanOrEqual=" + SMALLER_CURRENT_OPTION_PERIOD
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByCurrentOptionPeriodIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where currentOptionPeriod is less than
        defaultPassContractFiltering(
            "currentOptionPeriod.lessThan=" + UPDATED_CURRENT_OPTION_PERIOD,
            "currentOptionPeriod.lessThan=" + DEFAULT_CURRENT_OPTION_PERIOD
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByCurrentOptionPeriodIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where currentOptionPeriod is greater than
        defaultPassContractFiltering(
            "currentOptionPeriod.greaterThan=" + SMALLER_CURRENT_OPTION_PERIOD,
            "currentOptionPeriod.greaterThan=" + DEFAULT_CURRENT_OPTION_PERIOD
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByTotalOptionPeriodsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where totalOptionPeriods equals to
        defaultPassContractFiltering(
            "totalOptionPeriods.equals=" + DEFAULT_TOTAL_OPTION_PERIODS,
            "totalOptionPeriods.equals=" + UPDATED_TOTAL_OPTION_PERIODS
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByTotalOptionPeriodsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where totalOptionPeriods in
        defaultPassContractFiltering(
            "totalOptionPeriods.in=" + DEFAULT_TOTAL_OPTION_PERIODS + "," + UPDATED_TOTAL_OPTION_PERIODS,
            "totalOptionPeriods.in=" + UPDATED_TOTAL_OPTION_PERIODS
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByTotalOptionPeriodsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where totalOptionPeriods is not null
        defaultPassContractFiltering("totalOptionPeriods.specified=true", "totalOptionPeriods.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByTotalOptionPeriodsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where totalOptionPeriods is greater than or equal to
        defaultPassContractFiltering(
            "totalOptionPeriods.greaterThanOrEqual=" + DEFAULT_TOTAL_OPTION_PERIODS,
            "totalOptionPeriods.greaterThanOrEqual=" + UPDATED_TOTAL_OPTION_PERIODS
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByTotalOptionPeriodsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where totalOptionPeriods is less than or equal to
        defaultPassContractFiltering(
            "totalOptionPeriods.lessThanOrEqual=" + DEFAULT_TOTAL_OPTION_PERIODS,
            "totalOptionPeriods.lessThanOrEqual=" + SMALLER_TOTAL_OPTION_PERIODS
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByTotalOptionPeriodsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where totalOptionPeriods is less than
        defaultPassContractFiltering(
            "totalOptionPeriods.lessThan=" + UPDATED_TOTAL_OPTION_PERIODS,
            "totalOptionPeriods.lessThan=" + DEFAULT_TOTAL_OPTION_PERIODS
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByTotalOptionPeriodsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where totalOptionPeriods is greater than
        defaultPassContractFiltering(
            "totalOptionPeriods.greaterThan=" + SMALLER_TOTAL_OPTION_PERIODS,
            "totalOptionPeriods.greaterThan=" + DEFAULT_TOTAL_OPTION_PERIODS
        );
    }

    @Test
    @Transactional
    void getAllPassContractsBySupplierIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where supplier equals to
        defaultPassContractFiltering("supplier.equals=" + DEFAULT_SUPPLIER, "supplier.equals=" + UPDATED_SUPPLIER);
    }

    @Test
    @Transactional
    void getAllPassContractsBySupplierIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where supplier in
        defaultPassContractFiltering("supplier.in=" + DEFAULT_SUPPLIER + "," + UPDATED_SUPPLIER, "supplier.in=" + UPDATED_SUPPLIER);
    }

    @Test
    @Transactional
    void getAllPassContractsBySupplierIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where supplier is not null
        defaultPassContractFiltering("supplier.specified=true", "supplier.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsBySupplierContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where supplier contains
        defaultPassContractFiltering("supplier.contains=" + DEFAULT_SUPPLIER, "supplier.contains=" + UPDATED_SUPPLIER);
    }

    @Test
    @Transactional
    void getAllPassContractsBySupplierNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where supplier does not contain
        defaultPassContractFiltering("supplier.doesNotContain=" + UPDATED_SUPPLIER, "supplier.doesNotContain=" + DEFAULT_SUPPLIER);
    }

    @Test
    @Transactional
    void getAllPassContractsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where description equals to
        defaultPassContractFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllPassContractsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where description in
        defaultPassContractFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where description is not null
        defaultPassContractFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where description contains
        defaultPassContractFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllPassContractsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where description does not contain
        defaultPassContractFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractTypeDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractTypeDescription equals to
        defaultPassContractFiltering(
            "contractTypeDescription.equals=" + DEFAULT_CONTRACT_TYPE_DESCRIPTION,
            "contractTypeDescription.equals=" + UPDATED_CONTRACT_TYPE_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractTypeDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractTypeDescription in
        defaultPassContractFiltering(
            "contractTypeDescription.in=" + DEFAULT_CONTRACT_TYPE_DESCRIPTION + "," + UPDATED_CONTRACT_TYPE_DESCRIPTION,
            "contractTypeDescription.in=" + UPDATED_CONTRACT_TYPE_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractTypeDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractTypeDescription is not null
        defaultPassContractFiltering("contractTypeDescription.specified=true", "contractTypeDescription.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByContractTypeDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractTypeDescription contains
        defaultPassContractFiltering(
            "contractTypeDescription.contains=" + DEFAULT_CONTRACT_TYPE_DESCRIPTION,
            "contractTypeDescription.contains=" + UPDATED_CONTRACT_TYPE_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractTypeDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractTypeDescription does not contain
        defaultPassContractFiltering(
            "contractTypeDescription.doesNotContain=" + UPDATED_CONTRACT_TYPE_DESCRIPTION,
            "contractTypeDescription.doesNotContain=" + DEFAULT_CONTRACT_TYPE_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractingOfficerEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractingOfficerEmail equals to
        defaultPassContractFiltering(
            "contractingOfficerEmail.equals=" + DEFAULT_CONTRACTING_OFFICER_EMAIL,
            "contractingOfficerEmail.equals=" + UPDATED_CONTRACTING_OFFICER_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractingOfficerEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractingOfficerEmail in
        defaultPassContractFiltering(
            "contractingOfficerEmail.in=" + DEFAULT_CONTRACTING_OFFICER_EMAIL + "," + UPDATED_CONTRACTING_OFFICER_EMAIL,
            "contractingOfficerEmail.in=" + UPDATED_CONTRACTING_OFFICER_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractingOfficerEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractingOfficerEmail is not null
        defaultPassContractFiltering("contractingOfficerEmail.specified=true", "contractingOfficerEmail.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByContractingOfficerEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractingOfficerEmail contains
        defaultPassContractFiltering(
            "contractingOfficerEmail.contains=" + DEFAULT_CONTRACTING_OFFICER_EMAIL,
            "contractingOfficerEmail.contains=" + UPDATED_CONTRACTING_OFFICER_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractingOfficerEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractingOfficerEmail does not contain
        defaultPassContractFiltering(
            "contractingOfficerEmail.doesNotContain=" + UPDATED_CONTRACTING_OFFICER_EMAIL,
            "contractingOfficerEmail.doesNotContain=" + DEFAULT_CONTRACTING_OFFICER_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByVendorAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where vendorAddress equals to
        defaultPassContractFiltering("vendorAddress.equals=" + DEFAULT_VENDOR_ADDRESS, "vendorAddress.equals=" + UPDATED_VENDOR_ADDRESS);
    }

    @Test
    @Transactional
    void getAllPassContractsByVendorAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where vendorAddress in
        defaultPassContractFiltering(
            "vendorAddress.in=" + DEFAULT_VENDOR_ADDRESS + "," + UPDATED_VENDOR_ADDRESS,
            "vendorAddress.in=" + UPDATED_VENDOR_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByVendorAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where vendorAddress is not null
        defaultPassContractFiltering("vendorAddress.specified=true", "vendorAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByVendorAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where vendorAddress contains
        defaultPassContractFiltering(
            "vendorAddress.contains=" + DEFAULT_VENDOR_ADDRESS,
            "vendorAddress.contains=" + UPDATED_VENDOR_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByVendorAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where vendorAddress does not contain
        defaultPassContractFiltering(
            "vendorAddress.doesNotContain=" + UPDATED_VENDOR_ADDRESS,
            "vendorAddress.doesNotContain=" + DEFAULT_VENDOR_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByVendorCityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where vendorCity equals to
        defaultPassContractFiltering("vendorCity.equals=" + DEFAULT_VENDOR_CITY, "vendorCity.equals=" + UPDATED_VENDOR_CITY);
    }

    @Test
    @Transactional
    void getAllPassContractsByVendorCityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where vendorCity in
        defaultPassContractFiltering(
            "vendorCity.in=" + DEFAULT_VENDOR_CITY + "," + UPDATED_VENDOR_CITY,
            "vendorCity.in=" + UPDATED_VENDOR_CITY
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByVendorCityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where vendorCity is not null
        defaultPassContractFiltering("vendorCity.specified=true", "vendorCity.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByVendorCityContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where vendorCity contains
        defaultPassContractFiltering("vendorCity.contains=" + DEFAULT_VENDOR_CITY, "vendorCity.contains=" + UPDATED_VENDOR_CITY);
    }

    @Test
    @Transactional
    void getAllPassContractsByVendorCityNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where vendorCity does not contain
        defaultPassContractFiltering(
            "vendorCity.doesNotContain=" + UPDATED_VENDOR_CITY,
            "vendorCity.doesNotContain=" + DEFAULT_VENDOR_CITY
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByVendorStateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where vendorState equals to
        defaultPassContractFiltering("vendorState.equals=" + DEFAULT_VENDOR_STATE, "vendorState.equals=" + UPDATED_VENDOR_STATE);
    }

    @Test
    @Transactional
    void getAllPassContractsByVendorStateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where vendorState in
        defaultPassContractFiltering(
            "vendorState.in=" + DEFAULT_VENDOR_STATE + "," + UPDATED_VENDOR_STATE,
            "vendorState.in=" + UPDATED_VENDOR_STATE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByVendorStateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where vendorState is not null
        defaultPassContractFiltering("vendorState.specified=true", "vendorState.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByVendorStateContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where vendorState contains
        defaultPassContractFiltering("vendorState.contains=" + DEFAULT_VENDOR_STATE, "vendorState.contains=" + UPDATED_VENDOR_STATE);
    }

    @Test
    @Transactional
    void getAllPassContractsByVendorStateNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where vendorState does not contain
        defaultPassContractFiltering(
            "vendorState.doesNotContain=" + UPDATED_VENDOR_STATE,
            "vendorState.doesNotContain=" + DEFAULT_VENDOR_STATE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByVendorZipIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where vendorZip equals to
        defaultPassContractFiltering("vendorZip.equals=" + DEFAULT_VENDOR_ZIP, "vendorZip.equals=" + UPDATED_VENDOR_ZIP);
    }

    @Test
    @Transactional
    void getAllPassContractsByVendorZipIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where vendorZip in
        defaultPassContractFiltering("vendorZip.in=" + DEFAULT_VENDOR_ZIP + "," + UPDATED_VENDOR_ZIP, "vendorZip.in=" + UPDATED_VENDOR_ZIP);
    }

    @Test
    @Transactional
    void getAllPassContractsByVendorZipIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where vendorZip is not null
        defaultPassContractFiltering("vendorZip.specified=true", "vendorZip.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByVendorZipContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where vendorZip contains
        defaultPassContractFiltering("vendorZip.contains=" + DEFAULT_VENDOR_ZIP, "vendorZip.contains=" + UPDATED_VENDOR_ZIP);
    }

    @Test
    @Transactional
    void getAllPassContractsByVendorZipNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where vendorZip does not contain
        defaultPassContractFiltering("vendorZip.doesNotContain=" + UPDATED_VENDOR_ZIP, "vendorZip.doesNotContain=" + DEFAULT_VENDOR_ZIP);
    }

    @Test
    @Transactional
    void getAllPassContractsByPublishedVersionIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where publishedVersionId equals to
        defaultPassContractFiltering(
            "publishedVersionId.equals=" + DEFAULT_PUBLISHED_VERSION_ID,
            "publishedVersionId.equals=" + UPDATED_PUBLISHED_VERSION_ID
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByPublishedVersionIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where publishedVersionId in
        defaultPassContractFiltering(
            "publishedVersionId.in=" + DEFAULT_PUBLISHED_VERSION_ID + "," + UPDATED_PUBLISHED_VERSION_ID,
            "publishedVersionId.in=" + UPDATED_PUBLISHED_VERSION_ID
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByPublishedVersionIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where publishedVersionId is not null
        defaultPassContractFiltering("publishedVersionId.specified=true", "publishedVersionId.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByPublishedVersionIdContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where publishedVersionId contains
        defaultPassContractFiltering(
            "publishedVersionId.contains=" + DEFAULT_PUBLISHED_VERSION_ID,
            "publishedVersionId.contains=" + UPDATED_PUBLISHED_VERSION_ID
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByPublishedVersionIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where publishedVersionId does not contain
        defaultPassContractFiltering(
            "publishedVersionId.doesNotContain=" + UPDATED_PUBLISHED_VERSION_ID,
            "publishedVersionId.doesNotContain=" + DEFAULT_PUBLISHED_VERSION_ID
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByDocumentVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where documentVersion equals to
        defaultPassContractFiltering(
            "documentVersion.equals=" + DEFAULT_DOCUMENT_VERSION,
            "documentVersion.equals=" + UPDATED_DOCUMENT_VERSION
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByDocumentVersionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where documentVersion in
        defaultPassContractFiltering(
            "documentVersion.in=" + DEFAULT_DOCUMENT_VERSION + "," + UPDATED_DOCUMENT_VERSION,
            "documentVersion.in=" + UPDATED_DOCUMENT_VERSION
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByDocumentVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where documentVersion is not null
        defaultPassContractFiltering("documentVersion.specified=true", "documentVersion.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByDocumentVersionContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where documentVersion contains
        defaultPassContractFiltering(
            "documentVersion.contains=" + DEFAULT_DOCUMENT_VERSION,
            "documentVersion.contains=" + UPDATED_DOCUMENT_VERSION
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByDocumentVersionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where documentVersion does not contain
        defaultPassContractFiltering(
            "documentVersion.doesNotContain=" + UPDATED_DOCUMENT_VERSION,
            "documentVersion.doesNotContain=" + DEFAULT_DOCUMENT_VERSION
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByLastModifiedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where lastModified equals to
        defaultPassContractFiltering("lastModified.equals=" + DEFAULT_LAST_MODIFIED, "lastModified.equals=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllPassContractsByLastModifiedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where lastModified in
        defaultPassContractFiltering(
            "lastModified.in=" + DEFAULT_LAST_MODIFIED + "," + UPDATED_LAST_MODIFIED,
            "lastModified.in=" + UPDATED_LAST_MODIFIED
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByLastModifiedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where lastModified is not null
        defaultPassContractFiltering("lastModified.specified=true", "lastModified.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByContractingSplstIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractingSplst equals to
        defaultPassContractFiltering(
            "contractingSplst.equals=" + DEFAULT_CONTRACTING_SPLST,
            "contractingSplst.equals=" + UPDATED_CONTRACTING_SPLST
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractingSplstIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractingSplst in
        defaultPassContractFiltering(
            "contractingSplst.in=" + DEFAULT_CONTRACTING_SPLST + "," + UPDATED_CONTRACTING_SPLST,
            "contractingSplst.in=" + UPDATED_CONTRACTING_SPLST
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractingSplstIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractingSplst is not null
        defaultPassContractFiltering("contractingSplst.specified=true", "contractingSplst.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByContractingSplstContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractingSplst contains
        defaultPassContractFiltering(
            "contractingSplst.contains=" + DEFAULT_CONTRACTING_SPLST,
            "contractingSplst.contains=" + UPDATED_CONTRACTING_SPLST
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractingSplstNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractingSplst does not contain
        defaultPassContractFiltering(
            "contractingSplst.doesNotContain=" + UPDATED_CONTRACTING_SPLST,
            "contractingSplst.doesNotContain=" + DEFAULT_CONTRACTING_SPLST
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractingSplstEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractingSplstEmail equals to
        defaultPassContractFiltering(
            "contractingSplstEmail.equals=" + DEFAULT_CONTRACTING_SPLST_EMAIL,
            "contractingSplstEmail.equals=" + UPDATED_CONTRACTING_SPLST_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractingSplstEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractingSplstEmail in
        defaultPassContractFiltering(
            "contractingSplstEmail.in=" + DEFAULT_CONTRACTING_SPLST_EMAIL + "," + UPDATED_CONTRACTING_SPLST_EMAIL,
            "contractingSplstEmail.in=" + UPDATED_CONTRACTING_SPLST_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractingSplstEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractingSplstEmail is not null
        defaultPassContractFiltering("contractingSplstEmail.specified=true", "contractingSplstEmail.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByContractingSplstEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractingSplstEmail contains
        defaultPassContractFiltering(
            "contractingSplstEmail.contains=" + DEFAULT_CONTRACTING_SPLST_EMAIL,
            "contractingSplstEmail.contains=" + UPDATED_CONTRACTING_SPLST_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractingSplstEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractingSplstEmail does not contain
        defaultPassContractFiltering(
            "contractingSplstEmail.doesNotContain=" + UPDATED_CONTRACTING_SPLST_EMAIL,
            "contractingSplstEmail.doesNotContain=" + DEFAULT_CONTRACTING_SPLST_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllPassContractsBySourceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where source equals to
        defaultPassContractFiltering("source.equals=" + DEFAULT_SOURCE, "source.equals=" + UPDATED_SOURCE);
    }

    @Test
    @Transactional
    void getAllPassContractsBySourceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where source in
        defaultPassContractFiltering("source.in=" + DEFAULT_SOURCE + "," + UPDATED_SOURCE, "source.in=" + UPDATED_SOURCE);
    }

    @Test
    @Transactional
    void getAllPassContractsBySourceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where source is not null
        defaultPassContractFiltering("source.specified=true", "source.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsBySourceContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where source contains
        defaultPassContractFiltering("source.contains=" + DEFAULT_SOURCE, "source.contains=" + UPDATED_SOURCE);
    }

    @Test
    @Transactional
    void getAllPassContractsBySourceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where source does not contain
        defaultPassContractFiltering("source.doesNotContain=" + UPDATED_SOURCE, "source.doesNotContain=" + DEFAULT_SOURCE);
    }

    @Test
    @Transactional
    void getAllPassContractsByContractDetailsLinkIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractDetailsLink equals to
        defaultPassContractFiltering(
            "contractDetailsLink.equals=" + DEFAULT_CONTRACT_DETAILS_LINK,
            "contractDetailsLink.equals=" + UPDATED_CONTRACT_DETAILS_LINK
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractDetailsLinkIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractDetailsLink in
        defaultPassContractFiltering(
            "contractDetailsLink.in=" + DEFAULT_CONTRACT_DETAILS_LINK + "," + UPDATED_CONTRACT_DETAILS_LINK,
            "contractDetailsLink.in=" + UPDATED_CONTRACT_DETAILS_LINK
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractDetailsLinkIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractDetailsLink is not null
        defaultPassContractFiltering("contractDetailsLink.specified=true", "contractDetailsLink.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByContractDetailsLinkContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractDetailsLink contains
        defaultPassContractFiltering(
            "contractDetailsLink.contains=" + DEFAULT_CONTRACT_DETAILS_LINK,
            "contractDetailsLink.contains=" + UPDATED_CONTRACT_DETAILS_LINK
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractDetailsLinkNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractDetailsLink does not contain
        defaultPassContractFiltering(
            "contractDetailsLink.doesNotContain=" + UPDATED_CONTRACT_DETAILS_LINK,
            "contractDetailsLink.doesNotContain=" + DEFAULT_CONTRACT_DETAILS_LINK
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractAdministratorNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractAdministratorName equals to
        defaultPassContractFiltering(
            "contractAdministratorName.equals=" + DEFAULT_CONTRACT_ADMINISTRATOR_NAME,
            "contractAdministratorName.equals=" + UPDATED_CONTRACT_ADMINISTRATOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractAdministratorNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractAdministratorName in
        defaultPassContractFiltering(
            "contractAdministratorName.in=" + DEFAULT_CONTRACT_ADMINISTRATOR_NAME + "," + UPDATED_CONTRACT_ADMINISTRATOR_NAME,
            "contractAdministratorName.in=" + UPDATED_CONTRACT_ADMINISTRATOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractAdministratorNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractAdministratorName is not null
        defaultPassContractFiltering("contractAdministratorName.specified=true", "contractAdministratorName.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByContractAdministratorNameContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractAdministratorName contains
        defaultPassContractFiltering(
            "contractAdministratorName.contains=" + DEFAULT_CONTRACT_ADMINISTRATOR_NAME,
            "contractAdministratorName.contains=" + UPDATED_CONTRACT_ADMINISTRATOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractAdministratorNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractAdministratorName does not contain
        defaultPassContractFiltering(
            "contractAdministratorName.doesNotContain=" + UPDATED_CONTRACT_ADMINISTRATOR_NAME,
            "contractAdministratorName.doesNotContain=" + DEFAULT_CONTRACT_ADMINISTRATOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractAdministratorEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractAdministratorEmail equals to
        defaultPassContractFiltering(
            "contractAdministratorEmail.equals=" + DEFAULT_CONTRACT_ADMINISTRATOR_EMAIL,
            "contractAdministratorEmail.equals=" + UPDATED_CONTRACT_ADMINISTRATOR_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractAdministratorEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractAdministratorEmail in
        defaultPassContractFiltering(
            "contractAdministratorEmail.in=" + DEFAULT_CONTRACT_ADMINISTRATOR_EMAIL + "," + UPDATED_CONTRACT_ADMINISTRATOR_EMAIL,
            "contractAdministratorEmail.in=" + UPDATED_CONTRACT_ADMINISTRATOR_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractAdministratorEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractAdministratorEmail is not null
        defaultPassContractFiltering("contractAdministratorEmail.specified=true", "contractAdministratorEmail.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByContractAdministratorEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractAdministratorEmail contains
        defaultPassContractFiltering(
            "contractAdministratorEmail.contains=" + DEFAULT_CONTRACT_ADMINISTRATOR_EMAIL,
            "contractAdministratorEmail.contains=" + UPDATED_CONTRACT_ADMINISTRATOR_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractAdministratorEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractAdministratorEmail does not contain
        defaultPassContractFiltering(
            "contractAdministratorEmail.doesNotContain=" + UPDATED_CONTRACT_ADMINISTRATOR_EMAIL,
            "contractAdministratorEmail.doesNotContain=" + DEFAULT_CONTRACT_ADMINISTRATOR_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractAdministratorPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractAdministratorPhone equals to
        defaultPassContractFiltering(
            "contractAdministratorPhone.equals=" + DEFAULT_CONTRACT_ADMINISTRATOR_PHONE,
            "contractAdministratorPhone.equals=" + UPDATED_CONTRACT_ADMINISTRATOR_PHONE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractAdministratorPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractAdministratorPhone in
        defaultPassContractFiltering(
            "contractAdministratorPhone.in=" + DEFAULT_CONTRACT_ADMINISTRATOR_PHONE + "," + UPDATED_CONTRACT_ADMINISTRATOR_PHONE,
            "contractAdministratorPhone.in=" + UPDATED_CONTRACT_ADMINISTRATOR_PHONE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractAdministratorPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractAdministratorPhone is not null
        defaultPassContractFiltering("contractAdministratorPhone.specified=true", "contractAdministratorPhone.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByContractAdministratorPhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractAdministratorPhone contains
        defaultPassContractFiltering(
            "contractAdministratorPhone.contains=" + DEFAULT_CONTRACT_ADMINISTRATOR_PHONE,
            "contractAdministratorPhone.contains=" + UPDATED_CONTRACT_ADMINISTRATOR_PHONE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractAdministratorPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractAdministratorPhone does not contain
        defaultPassContractFiltering(
            "contractAdministratorPhone.doesNotContain=" + UPDATED_CONTRACT_ADMINISTRATOR_PHONE,
            "contractAdministratorPhone.doesNotContain=" + DEFAULT_CONTRACT_ADMINISTRATOR_PHONE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractOfficerPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractOfficerPhone equals to
        defaultPassContractFiltering(
            "contractOfficerPhone.equals=" + DEFAULT_CONTRACT_OFFICER_PHONE,
            "contractOfficerPhone.equals=" + UPDATED_CONTRACT_OFFICER_PHONE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractOfficerPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractOfficerPhone in
        defaultPassContractFiltering(
            "contractOfficerPhone.in=" + DEFAULT_CONTRACT_OFFICER_PHONE + "," + UPDATED_CONTRACT_OFFICER_PHONE,
            "contractOfficerPhone.in=" + UPDATED_CONTRACT_OFFICER_PHONE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractOfficerPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractOfficerPhone is not null
        defaultPassContractFiltering("contractOfficerPhone.specified=true", "contractOfficerPhone.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByContractOfficerPhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractOfficerPhone contains
        defaultPassContractFiltering(
            "contractOfficerPhone.contains=" + DEFAULT_CONTRACT_OFFICER_PHONE,
            "contractOfficerPhone.contains=" + UPDATED_CONTRACT_OFFICER_PHONE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByContractOfficerPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where contractOfficerPhone does not contain
        defaultPassContractFiltering(
            "contractOfficerPhone.doesNotContain=" + UPDATED_CONTRACT_OFFICER_PHONE,
            "contractOfficerPhone.doesNotContain=" + DEFAULT_CONTRACT_OFFICER_PHONE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByCwInternalIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where cwInternalId equals to
        defaultPassContractFiltering("cwInternalId.equals=" + DEFAULT_CW_INTERNAL_ID, "cwInternalId.equals=" + UPDATED_CW_INTERNAL_ID);
    }

    @Test
    @Transactional
    void getAllPassContractsByCwInternalIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where cwInternalId in
        defaultPassContractFiltering(
            "cwInternalId.in=" + DEFAULT_CW_INTERNAL_ID + "," + UPDATED_CW_INTERNAL_ID,
            "cwInternalId.in=" + UPDATED_CW_INTERNAL_ID
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByCwInternalIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where cwInternalId is not null
        defaultPassContractFiltering("cwInternalId.specified=true", "cwInternalId.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByCwInternalIdContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where cwInternalId contains
        defaultPassContractFiltering("cwInternalId.contains=" + DEFAULT_CW_INTERNAL_ID, "cwInternalId.contains=" + UPDATED_CW_INTERNAL_ID);
    }

    @Test
    @Transactional
    void getAllPassContractsByCwInternalIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where cwInternalId does not contain
        defaultPassContractFiltering(
            "cwInternalId.doesNotContain=" + UPDATED_CW_INTERNAL_ID,
            "cwInternalId.doesNotContain=" + DEFAULT_CW_INTERNAL_ID
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByCorporatePhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where corporatePhone equals to
        defaultPassContractFiltering(
            "corporatePhone.equals=" + DEFAULT_CORPORATE_PHONE,
            "corporatePhone.equals=" + UPDATED_CORPORATE_PHONE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByCorporatePhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where corporatePhone in
        defaultPassContractFiltering(
            "corporatePhone.in=" + DEFAULT_CORPORATE_PHONE + "," + UPDATED_CORPORATE_PHONE,
            "corporatePhone.in=" + UPDATED_CORPORATE_PHONE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByCorporatePhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where corporatePhone is not null
        defaultPassContractFiltering("corporatePhone.specified=true", "corporatePhone.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByCorporatePhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where corporatePhone contains
        defaultPassContractFiltering(
            "corporatePhone.contains=" + DEFAULT_CORPORATE_PHONE,
            "corporatePhone.contains=" + UPDATED_CORPORATE_PHONE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByCorporatePhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where corporatePhone does not contain
        defaultPassContractFiltering(
            "corporatePhone.doesNotContain=" + UPDATED_CORPORATE_PHONE,
            "corporatePhone.doesNotContain=" + DEFAULT_CORPORATE_PHONE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByCorporateEmailAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where corporateEmailAddress equals to
        defaultPassContractFiltering(
            "corporateEmailAddress.equals=" + DEFAULT_CORPORATE_EMAIL_ADDRESS,
            "corporateEmailAddress.equals=" + UPDATED_CORPORATE_EMAIL_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByCorporateEmailAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where corporateEmailAddress in
        defaultPassContractFiltering(
            "corporateEmailAddress.in=" + DEFAULT_CORPORATE_EMAIL_ADDRESS + "," + UPDATED_CORPORATE_EMAIL_ADDRESS,
            "corporateEmailAddress.in=" + UPDATED_CORPORATE_EMAIL_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByCorporateEmailAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where corporateEmailAddress is not null
        defaultPassContractFiltering("corporateEmailAddress.specified=true", "corporateEmailAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByCorporateEmailAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where corporateEmailAddress contains
        defaultPassContractFiltering(
            "corporateEmailAddress.contains=" + DEFAULT_CORPORATE_EMAIL_ADDRESS,
            "corporateEmailAddress.contains=" + UPDATED_CORPORATE_EMAIL_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByCorporateEmailAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where corporateEmailAddress does not contain
        defaultPassContractFiltering(
            "corporateEmailAddress.doesNotContain=" + UPDATED_CORPORATE_EMAIL_ADDRESS,
            "corporateEmailAddress.doesNotContain=" + DEFAULT_CORPORATE_EMAIL_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByRecCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where recCreatedDate equals to
        defaultPassContractFiltering(
            "recCreatedDate.equals=" + DEFAULT_REC_CREATED_DATE,
            "recCreatedDate.equals=" + UPDATED_REC_CREATED_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByRecCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where recCreatedDate in
        defaultPassContractFiltering(
            "recCreatedDate.in=" + DEFAULT_REC_CREATED_DATE + "," + UPDATED_REC_CREATED_DATE,
            "recCreatedDate.in=" + UPDATED_REC_CREATED_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByRecCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where recCreatedDate is not null
        defaultPassContractFiltering("recCreatedDate.specified=true", "recCreatedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByRecUpdatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where recUpdatedDate equals to
        defaultPassContractFiltering(
            "recUpdatedDate.equals=" + DEFAULT_REC_UPDATED_DATE,
            "recUpdatedDate.equals=" + UPDATED_REC_UPDATED_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByRecUpdatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where recUpdatedDate in
        defaultPassContractFiltering(
            "recUpdatedDate.in=" + DEFAULT_REC_UPDATED_DATE + "," + UPDATED_REC_UPDATED_DATE,
            "recUpdatedDate.in=" + UPDATED_REC_UPDATED_DATE
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByRecUpdatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where recUpdatedDate is not null
        defaultPassContractFiltering("recUpdatedDate.specified=true", "recUpdatedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByDcsLastModDttmIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where dcsLastModDttm equals to
        defaultPassContractFiltering(
            "dcsLastModDttm.equals=" + DEFAULT_DCS_LAST_MOD_DTTM,
            "dcsLastModDttm.equals=" + UPDATED_DCS_LAST_MOD_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByDcsLastModDttmIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where dcsLastModDttm in
        defaultPassContractFiltering(
            "dcsLastModDttm.in=" + DEFAULT_DCS_LAST_MOD_DTTM + "," + UPDATED_DCS_LAST_MOD_DTTM,
            "dcsLastModDttm.in=" + UPDATED_DCS_LAST_MOD_DTTM
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByDcsLastModDttmIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where dcsLastModDttm is not null
        defaultPassContractFiltering("dcsLastModDttm.specified=true", "dcsLastModDttm.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByObjectIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where objectId equals to
        defaultPassContractFiltering("objectId.equals=" + DEFAULT_OBJECT_ID, "objectId.equals=" + UPDATED_OBJECT_ID);
    }

    @Test
    @Transactional
    void getAllPassContractsByObjectIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where objectId in
        defaultPassContractFiltering("objectId.in=" + DEFAULT_OBJECT_ID + "," + UPDATED_OBJECT_ID, "objectId.in=" + UPDATED_OBJECT_ID);
    }

    @Test
    @Transactional
    void getAllPassContractsByObjectIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where objectId is not null
        defaultPassContractFiltering("objectId.specified=true", "objectId.specified=false");
    }

    @Test
    @Transactional
    void getAllPassContractsByObjectIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where objectId is greater than or equal to
        defaultPassContractFiltering(
            "objectId.greaterThanOrEqual=" + DEFAULT_OBJECT_ID,
            "objectId.greaterThanOrEqual=" + UPDATED_OBJECT_ID
        );
    }

    @Test
    @Transactional
    void getAllPassContractsByObjectIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where objectId is less than or equal to
        defaultPassContractFiltering("objectId.lessThanOrEqual=" + DEFAULT_OBJECT_ID, "objectId.lessThanOrEqual=" + SMALLER_OBJECT_ID);
    }

    @Test
    @Transactional
    void getAllPassContractsByObjectIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where objectId is less than
        defaultPassContractFiltering("objectId.lessThan=" + UPDATED_OBJECT_ID, "objectId.lessThan=" + DEFAULT_OBJECT_ID);
    }

    @Test
    @Transactional
    void getAllPassContractsByObjectIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        // Get all the passContractList where objectId is greater than
        defaultPassContractFiltering("objectId.greaterThan=" + SMALLER_OBJECT_ID, "objectId.greaterThan=" + DEFAULT_OBJECT_ID);
    }

    private void defaultPassContractFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPassContractShouldBeFound(shouldBeFound);
        defaultPassContractShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPassContractShouldBeFound(String filter) throws Exception {
        restPassContractMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(passContract.getId().intValue())))
            .andExpect(jsonPath("$.[*].procurementMethodDescription").value(hasItem(DEFAULT_PROCUREMENT_METHOD_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].agencyAcronym").value(hasItem(DEFAULT_AGENCY_ACRONYM)))
            .andExpect(jsonPath("$.[*].agencyName").value(hasItem(DEFAULT_AGENCY_NAME)))
            .andExpect(jsonPath("$.[*].rowId").value(hasItem(DEFAULT_ROW_ID.intValue())))
            .andExpect(jsonPath("$.[*].agency").value(hasItem(DEFAULT_AGENCY)))
            .andExpect(jsonPath("$.[*].awardDate").value(hasItem(DEFAULT_AWARD_DATE.toString())))
            .andExpect(jsonPath("$.[*].contractAmount").value(hasItem(sameNumber(DEFAULT_CONTRACT_AMOUNT))))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].contractNumber").value(hasItem(DEFAULT_CONTRACT_NUMBER)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].contractStatus").value(hasItem(DEFAULT_CONTRACT_STATUS)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].contractingOfficer").value(hasItem(DEFAULT_CONTRACTING_OFFICER)))
            .andExpect(jsonPath("$.[*].fiscalYear").value(hasItem(DEFAULT_FISCAL_YEAR)))
            .andExpect(jsonPath("$.[*].marketType").value(hasItem(DEFAULT_MARKET_TYPE)))
            .andExpect(jsonPath("$.[*].commodityCode").value(hasItem(DEFAULT_COMMODITY_CODE)))
            .andExpect(jsonPath("$.[*].commodityDescription").value(hasItem(DEFAULT_COMMODITY_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].currentOptionPeriod").value(hasItem(DEFAULT_CURRENT_OPTION_PERIOD)))
            .andExpect(jsonPath("$.[*].totalOptionPeriods").value(hasItem(DEFAULT_TOTAL_OPTION_PERIODS)))
            .andExpect(jsonPath("$.[*].supplier").value(hasItem(DEFAULT_SUPPLIER)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].contractTypeDescription").value(hasItem(DEFAULT_CONTRACT_TYPE_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].contractingOfficerEmail").value(hasItem(DEFAULT_CONTRACTING_OFFICER_EMAIL)))
            .andExpect(jsonPath("$.[*].vendorAddress").value(hasItem(DEFAULT_VENDOR_ADDRESS)))
            .andExpect(jsonPath("$.[*].vendorCity").value(hasItem(DEFAULT_VENDOR_CITY)))
            .andExpect(jsonPath("$.[*].vendorState").value(hasItem(DEFAULT_VENDOR_STATE)))
            .andExpect(jsonPath("$.[*].vendorZip").value(hasItem(DEFAULT_VENDOR_ZIP)))
            .andExpect(jsonPath("$.[*].publishedVersionId").value(hasItem(DEFAULT_PUBLISHED_VERSION_ID)))
            .andExpect(jsonPath("$.[*].documentVersion").value(hasItem(DEFAULT_DOCUMENT_VERSION)))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(DEFAULT_LAST_MODIFIED.toString())))
            .andExpect(jsonPath("$.[*].contractingSplst").value(hasItem(DEFAULT_CONTRACTING_SPLST)))
            .andExpect(jsonPath("$.[*].contractingSplstEmail").value(hasItem(DEFAULT_CONTRACTING_SPLST_EMAIL)))
            .andExpect(jsonPath("$.[*].source").value(hasItem(DEFAULT_SOURCE)))
            .andExpect(jsonPath("$.[*].contractDetailsLink").value(hasItem(DEFAULT_CONTRACT_DETAILS_LINK)))
            .andExpect(jsonPath("$.[*].contractAdministratorName").value(hasItem(DEFAULT_CONTRACT_ADMINISTRATOR_NAME)))
            .andExpect(jsonPath("$.[*].contractAdministratorEmail").value(hasItem(DEFAULT_CONTRACT_ADMINISTRATOR_EMAIL)))
            .andExpect(jsonPath("$.[*].contractAdministratorPhone").value(hasItem(DEFAULT_CONTRACT_ADMINISTRATOR_PHONE)))
            .andExpect(jsonPath("$.[*].contractOfficerPhone").value(hasItem(DEFAULT_CONTRACT_OFFICER_PHONE)))
            .andExpect(jsonPath("$.[*].cwInternalId").value(hasItem(DEFAULT_CW_INTERNAL_ID)))
            .andExpect(jsonPath("$.[*].corporatePhone").value(hasItem(DEFAULT_CORPORATE_PHONE)))
            .andExpect(jsonPath("$.[*].corporateEmailAddress").value(hasItem(DEFAULT_CORPORATE_EMAIL_ADDRESS)))
            .andExpect(jsonPath("$.[*].recCreatedDate").value(hasItem(DEFAULT_REC_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].recUpdatedDate").value(hasItem(DEFAULT_REC_UPDATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].dcsLastModDttm").value(hasItem(DEFAULT_DCS_LAST_MOD_DTTM.toString())))
            .andExpect(jsonPath("$.[*].objectId").value(hasItem(DEFAULT_OBJECT_ID.intValue())));

        // Check, that the count call also returns 1
        restPassContractMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPassContractShouldNotBeFound(String filter) throws Exception {
        restPassContractMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPassContractMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPassContract() throws Exception {
        // Get the passContract
        restPassContractMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPassContract() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the passContract
        PassContract updatedPassContract = passContractRepository.findById(passContract.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPassContract are not directly saved in db
        em.detach(updatedPassContract);
        updatedPassContract
            .procurementMethodDescription(UPDATED_PROCUREMENT_METHOD_DESCRIPTION)
            .agencyAcronym(UPDATED_AGENCY_ACRONYM)
            .agencyName(UPDATED_AGENCY_NAME)
            .rowId(UPDATED_ROW_ID)
            .agency(UPDATED_AGENCY)
            .awardDate(UPDATED_AWARD_DATE)
            .contractAmount(UPDATED_CONTRACT_AMOUNT)
            .endDate(UPDATED_END_DATE)
            .contractNumber(UPDATED_CONTRACT_NUMBER)
            .startDate(UPDATED_START_DATE)
            .contractStatus(UPDATED_CONTRACT_STATUS)
            .title(UPDATED_TITLE)
            .contractingOfficer(UPDATED_CONTRACTING_OFFICER)
            .fiscalYear(UPDATED_FISCAL_YEAR)
            .marketType(UPDATED_MARKET_TYPE)
            .commodityCode(UPDATED_COMMODITY_CODE)
            .commodityDescription(UPDATED_COMMODITY_DESCRIPTION)
            .currentOptionPeriod(UPDATED_CURRENT_OPTION_PERIOD)
            .totalOptionPeriods(UPDATED_TOTAL_OPTION_PERIODS)
            .supplier(UPDATED_SUPPLIER)
            .description(UPDATED_DESCRIPTION)
            .contractTypeDescription(UPDATED_CONTRACT_TYPE_DESCRIPTION)
            .contractingOfficerEmail(UPDATED_CONTRACTING_OFFICER_EMAIL)
            .vendorAddress(UPDATED_VENDOR_ADDRESS)
            .vendorCity(UPDATED_VENDOR_CITY)
            .vendorState(UPDATED_VENDOR_STATE)
            .vendorZip(UPDATED_VENDOR_ZIP)
            .publishedVersionId(UPDATED_PUBLISHED_VERSION_ID)
            .documentVersion(UPDATED_DOCUMENT_VERSION)
            .lastModified(UPDATED_LAST_MODIFIED)
            .contractingSplst(UPDATED_CONTRACTING_SPLST)
            .contractingSplstEmail(UPDATED_CONTRACTING_SPLST_EMAIL)
            .source(UPDATED_SOURCE)
            .contractDetailsLink(UPDATED_CONTRACT_DETAILS_LINK)
            .contractAdministratorName(UPDATED_CONTRACT_ADMINISTRATOR_NAME)
            .contractAdministratorEmail(UPDATED_CONTRACT_ADMINISTRATOR_EMAIL)
            .contractAdministratorPhone(UPDATED_CONTRACT_ADMINISTRATOR_PHONE)
            .contractOfficerPhone(UPDATED_CONTRACT_OFFICER_PHONE)
            .cwInternalId(UPDATED_CW_INTERNAL_ID)
            .corporatePhone(UPDATED_CORPORATE_PHONE)
            .corporateEmailAddress(UPDATED_CORPORATE_EMAIL_ADDRESS)
            .recCreatedDate(UPDATED_REC_CREATED_DATE)
            .recUpdatedDate(UPDATED_REC_UPDATED_DATE)
            .dcsLastModDttm(UPDATED_DCS_LAST_MOD_DTTM)
            .objectId(UPDATED_OBJECT_ID);
        PassContractDTO passContractDTO = passContractMapper.toDto(updatedPassContract);

        restPassContractMockMvc
            .perform(
                put(ENTITY_API_URL_ID, passContractDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(passContractDTO))
            )
            .andExpect(status().isOk());

        // Validate the PassContract in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPassContractToMatchAllProperties(updatedPassContract);
    }

    @Test
    @Transactional
    void putNonExistingPassContract() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        passContract.setId(longCount.incrementAndGet());

        // Create the PassContract
        PassContractDTO passContractDTO = passContractMapper.toDto(passContract);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPassContractMockMvc
            .perform(
                put(ENTITY_API_URL_ID, passContractDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(passContractDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PassContract in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPassContract() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        passContract.setId(longCount.incrementAndGet());

        // Create the PassContract
        PassContractDTO passContractDTO = passContractMapper.toDto(passContract);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPassContractMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(passContractDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PassContract in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPassContract() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        passContract.setId(longCount.incrementAndGet());

        // Create the PassContract
        PassContractDTO passContractDTO = passContractMapper.toDto(passContract);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPassContractMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(passContractDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PassContract in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePassContractWithPatch() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the passContract using partial update
        PassContract partialUpdatedPassContract = new PassContract();
        partialUpdatedPassContract.setId(passContract.getId());

        partialUpdatedPassContract
            .rowId(UPDATED_ROW_ID)
            .contractNumber(UPDATED_CONTRACT_NUMBER)
            .startDate(UPDATED_START_DATE)
            .contractingOfficer(UPDATED_CONTRACTING_OFFICER)
            .marketType(UPDATED_MARKET_TYPE)
            .commodityCode(UPDATED_COMMODITY_CODE)
            .currentOptionPeriod(UPDATED_CURRENT_OPTION_PERIOD)
            .totalOptionPeriods(UPDATED_TOTAL_OPTION_PERIODS)
            .supplier(UPDATED_SUPPLIER)
            .description(UPDATED_DESCRIPTION)
            .vendorAddress(UPDATED_VENDOR_ADDRESS)
            .vendorCity(UPDATED_VENDOR_CITY)
            .vendorState(UPDATED_VENDOR_STATE)
            .publishedVersionId(UPDATED_PUBLISHED_VERSION_ID)
            .lastModified(UPDATED_LAST_MODIFIED)
            .contractingSplst(UPDATED_CONTRACTING_SPLST)
            .contractingSplstEmail(UPDATED_CONTRACTING_SPLST_EMAIL)
            .cwInternalId(UPDATED_CW_INTERNAL_ID)
            .corporatePhone(UPDATED_CORPORATE_PHONE)
            .dcsLastModDttm(UPDATED_DCS_LAST_MOD_DTTM)
            .objectId(UPDATED_OBJECT_ID);

        restPassContractMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPassContract.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPassContract))
            )
            .andExpect(status().isOk());

        // Validate the PassContract in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPassContractUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPassContract, passContract),
            getPersistedPassContract(passContract)
        );
    }

    @Test
    @Transactional
    void fullUpdatePassContractWithPatch() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the passContract using partial update
        PassContract partialUpdatedPassContract = new PassContract();
        partialUpdatedPassContract.setId(passContract.getId());

        partialUpdatedPassContract
            .procurementMethodDescription(UPDATED_PROCUREMENT_METHOD_DESCRIPTION)
            .agencyAcronym(UPDATED_AGENCY_ACRONYM)
            .agencyName(UPDATED_AGENCY_NAME)
            .rowId(UPDATED_ROW_ID)
            .agency(UPDATED_AGENCY)
            .awardDate(UPDATED_AWARD_DATE)
            .contractAmount(UPDATED_CONTRACT_AMOUNT)
            .endDate(UPDATED_END_DATE)
            .contractNumber(UPDATED_CONTRACT_NUMBER)
            .startDate(UPDATED_START_DATE)
            .contractStatus(UPDATED_CONTRACT_STATUS)
            .title(UPDATED_TITLE)
            .contractingOfficer(UPDATED_CONTRACTING_OFFICER)
            .fiscalYear(UPDATED_FISCAL_YEAR)
            .marketType(UPDATED_MARKET_TYPE)
            .commodityCode(UPDATED_COMMODITY_CODE)
            .commodityDescription(UPDATED_COMMODITY_DESCRIPTION)
            .currentOptionPeriod(UPDATED_CURRENT_OPTION_PERIOD)
            .totalOptionPeriods(UPDATED_TOTAL_OPTION_PERIODS)
            .supplier(UPDATED_SUPPLIER)
            .description(UPDATED_DESCRIPTION)
            .contractTypeDescription(UPDATED_CONTRACT_TYPE_DESCRIPTION)
            .contractingOfficerEmail(UPDATED_CONTRACTING_OFFICER_EMAIL)
            .vendorAddress(UPDATED_VENDOR_ADDRESS)
            .vendorCity(UPDATED_VENDOR_CITY)
            .vendorState(UPDATED_VENDOR_STATE)
            .vendorZip(UPDATED_VENDOR_ZIP)
            .publishedVersionId(UPDATED_PUBLISHED_VERSION_ID)
            .documentVersion(UPDATED_DOCUMENT_VERSION)
            .lastModified(UPDATED_LAST_MODIFIED)
            .contractingSplst(UPDATED_CONTRACTING_SPLST)
            .contractingSplstEmail(UPDATED_CONTRACTING_SPLST_EMAIL)
            .source(UPDATED_SOURCE)
            .contractDetailsLink(UPDATED_CONTRACT_DETAILS_LINK)
            .contractAdministratorName(UPDATED_CONTRACT_ADMINISTRATOR_NAME)
            .contractAdministratorEmail(UPDATED_CONTRACT_ADMINISTRATOR_EMAIL)
            .contractAdministratorPhone(UPDATED_CONTRACT_ADMINISTRATOR_PHONE)
            .contractOfficerPhone(UPDATED_CONTRACT_OFFICER_PHONE)
            .cwInternalId(UPDATED_CW_INTERNAL_ID)
            .corporatePhone(UPDATED_CORPORATE_PHONE)
            .corporateEmailAddress(UPDATED_CORPORATE_EMAIL_ADDRESS)
            .recCreatedDate(UPDATED_REC_CREATED_DATE)
            .recUpdatedDate(UPDATED_REC_UPDATED_DATE)
            .dcsLastModDttm(UPDATED_DCS_LAST_MOD_DTTM)
            .objectId(UPDATED_OBJECT_ID);

        restPassContractMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPassContract.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPassContract))
            )
            .andExpect(status().isOk());

        // Validate the PassContract in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPassContractUpdatableFieldsEquals(partialUpdatedPassContract, getPersistedPassContract(partialUpdatedPassContract));
    }

    @Test
    @Transactional
    void patchNonExistingPassContract() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        passContract.setId(longCount.incrementAndGet());

        // Create the PassContract
        PassContractDTO passContractDTO = passContractMapper.toDto(passContract);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPassContractMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, passContractDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(passContractDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PassContract in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPassContract() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        passContract.setId(longCount.incrementAndGet());

        // Create the PassContract
        PassContractDTO passContractDTO = passContractMapper.toDto(passContract);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPassContractMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(passContractDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PassContract in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPassContract() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        passContract.setId(longCount.incrementAndGet());

        // Create the PassContract
        PassContractDTO passContractDTO = passContractMapper.toDto(passContract);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPassContractMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(passContractDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PassContract in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePassContract() throws Exception {
        // Initialize the database
        insertedPassContract = passContractRepository.saveAndFlush(passContract);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the passContract
        restPassContractMockMvc
            .perform(delete(ENTITY_API_URL_ID, passContract.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return passContractRepository.count();
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

    protected PassContract getPersistedPassContract(PassContract passContract) {
        return passContractRepository.findById(passContract.getId()).orElseThrow();
    }

    protected void assertPersistedPassContractToMatchAllProperties(PassContract expectedPassContract) {
        assertPassContractAllPropertiesEquals(expectedPassContract, getPersistedPassContract(expectedPassContract));
    }

    protected void assertPersistedPassContractToMatchUpdatableProperties(PassContract expectedPassContract) {
        assertPassContractAllUpdatablePropertiesEquals(expectedPassContract, getPersistedPassContract(expectedPassContract));
    }
}
