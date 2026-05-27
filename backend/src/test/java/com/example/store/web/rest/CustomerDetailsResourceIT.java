package com.example.store.web.rest;

import static com.example.store.domain.CustomerDetailsAsserts.*;
import static com.example.store.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.store.IntegrationTest;
import com.example.store.domain.CustomerDetails;
import com.example.store.repository.CustomerDetailsRepository;
import com.example.store.repository.UserRepository;
import com.example.store.service.CustomerDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CustomerDetailsResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CustomerDetailsResourceIT {

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/customer-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CustomerDetailsRepository customerDetailsRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private CustomerDetailsRepository customerDetailsRepositoryMock;

    @Mock
    private CustomerDetailsService customerDetailsServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCustomerDetailsMockMvc;

    private CustomerDetails customerDetails;

    private CustomerDetails insertedCustomerDetails;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomerDetails createEntity() {
        return new CustomerDetails().phone(DEFAULT_PHONE).address(DEFAULT_ADDRESS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomerDetails createUpdatedEntity() {
        return new CustomerDetails().phone(UPDATED_PHONE).address(UPDATED_ADDRESS);
    }

    @BeforeEach
    void initTest() {
        customerDetails = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCustomerDetails != null) {
            customerDetailsRepository.delete(insertedCustomerDetails);
            insertedCustomerDetails = null;
        }
    }

    @Test
    @Transactional
    void createCustomerDetails() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CustomerDetails
        var returnedCustomerDetails = om.readValue(
            restCustomerDetailsMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customerDetails)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CustomerDetails.class
        );

        // Validate the CustomerDetails in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCustomerDetailsUpdatableFieldsEquals(returnedCustomerDetails, getPersistedCustomerDetails(returnedCustomerDetails));

        insertedCustomerDetails = returnedCustomerDetails;
    }

    @Test
    @Transactional
    void createCustomerDetailsWithExistingId() throws Exception {
        // Create the CustomerDetails with an existing ID
        customerDetails.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCustomerDetailsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customerDetails)))
            .andExpect(status().isBadRequest());

        // Validate the CustomerDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAddressIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customerDetails.setAddress(null);

        // Create the CustomerDetails, which fails.

        restCustomerDetailsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customerDetails)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCustomerDetails() throws Exception {
        // Initialize the database
        insertedCustomerDetails = customerDetailsRepository.saveAndFlush(customerDetails);

        // Get all the customerDetailsList
        restCustomerDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customerDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCustomerDetailsWithEagerRelationshipsIsEnabled() throws Exception {
        when(customerDetailsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCustomerDetailsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(customerDetailsServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCustomerDetailsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(customerDetailsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCustomerDetailsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(customerDetailsRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCustomerDetails() throws Exception {
        // Initialize the database
        insertedCustomerDetails = customerDetailsRepository.saveAndFlush(customerDetails);

        // Get the customerDetails
        restCustomerDetailsMockMvc
            .perform(get(ENTITY_API_URL_ID, customerDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(customerDetails.getId().intValue()))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS));
    }

    @Test
    @Transactional
    void getNonExistingCustomerDetails() throws Exception {
        // Get the customerDetails
        restCustomerDetailsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCustomerDetails() throws Exception {
        // Initialize the database
        insertedCustomerDetails = customerDetailsRepository.saveAndFlush(customerDetails);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customerDetails
        CustomerDetails updatedCustomerDetails = customerDetailsRepository.findById(customerDetails.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCustomerDetails are not directly saved in db
        em.detach(updatedCustomerDetails);
        updatedCustomerDetails.phone(UPDATED_PHONE).address(UPDATED_ADDRESS);

        restCustomerDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCustomerDetails.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedCustomerDetails))
            )
            .andExpect(status().isOk());

        // Validate the CustomerDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCustomerDetailsToMatchAllProperties(updatedCustomerDetails);
    }

    @Test
    @Transactional
    void putNonExistingCustomerDetails() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerDetails.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomerDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, customerDetails.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(customerDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomerDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCustomerDetails() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerDetails.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(customerDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomerDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCustomerDetails() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerDetails.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerDetailsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customerDetails)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CustomerDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCustomerDetailsWithPatch() throws Exception {
        // Initialize the database
        insertedCustomerDetails = customerDetailsRepository.saveAndFlush(customerDetails);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customerDetails using partial update
        CustomerDetails partialUpdatedCustomerDetails = new CustomerDetails();
        partialUpdatedCustomerDetails.setId(customerDetails.getId());

        partialUpdatedCustomerDetails.phone(UPDATED_PHONE).address(UPDATED_ADDRESS);

        restCustomerDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCustomerDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCustomerDetails))
            )
            .andExpect(status().isOk());

        // Validate the CustomerDetails in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomerDetailsUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCustomerDetails, customerDetails),
            getPersistedCustomerDetails(customerDetails)
        );
    }

    @Test
    @Transactional
    void fullUpdateCustomerDetailsWithPatch() throws Exception {
        // Initialize the database
        insertedCustomerDetails = customerDetailsRepository.saveAndFlush(customerDetails);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customerDetails using partial update
        CustomerDetails partialUpdatedCustomerDetails = new CustomerDetails();
        partialUpdatedCustomerDetails.setId(customerDetails.getId());

        partialUpdatedCustomerDetails.phone(UPDATED_PHONE).address(UPDATED_ADDRESS);

        restCustomerDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCustomerDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCustomerDetails))
            )
            .andExpect(status().isOk());

        // Validate the CustomerDetails in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomerDetailsUpdatableFieldsEquals(
            partialUpdatedCustomerDetails,
            getPersistedCustomerDetails(partialUpdatedCustomerDetails)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCustomerDetails() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerDetails.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomerDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, customerDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(customerDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomerDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCustomerDetails() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerDetails.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(customerDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomerDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCustomerDetails() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerDetails.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerDetailsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(customerDetails)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CustomerDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCustomerDetails() throws Exception {
        // Initialize the database
        insertedCustomerDetails = customerDetailsRepository.saveAndFlush(customerDetails);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the customerDetails
        restCustomerDetailsMockMvc
            .perform(delete(ENTITY_API_URL_ID, customerDetails.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return customerDetailsRepository.count();
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

    protected CustomerDetails getPersistedCustomerDetails(CustomerDetails customerDetails) {
        return customerDetailsRepository.findById(customerDetails.getId()).orElseThrow();
    }

    protected void assertPersistedCustomerDetailsToMatchAllProperties(CustomerDetails expectedCustomerDetails) {
        assertCustomerDetailsAllPropertiesEquals(expectedCustomerDetails, getPersistedCustomerDetails(expectedCustomerDetails));
    }

    protected void assertPersistedCustomerDetailsToMatchUpdatableProperties(CustomerDetails expectedCustomerDetails) {
        assertCustomerDetailsAllUpdatablePropertiesEquals(expectedCustomerDetails, getPersistedCustomerDetails(expectedCustomerDetails));
    }
}
