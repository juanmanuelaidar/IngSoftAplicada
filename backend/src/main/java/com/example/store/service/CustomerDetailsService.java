package com.example.store.service;

import com.example.store.domain.CustomerDetails;
import com.example.store.repository.CustomerDetailsRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.example.store.domain.CustomerDetails}.
 */
@Service
@Transactional
public class CustomerDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerDetailsService.class);

    private final CustomerDetailsRepository customerDetailsRepository;

    public CustomerDetailsService(CustomerDetailsRepository customerDetailsRepository) {
        this.customerDetailsRepository = customerDetailsRepository;
    }

    /**
     * Save a customerDetails.
     *
     * @param customerDetails the entity to save.
     * @return the persisted entity.
     */
    public CustomerDetails save(CustomerDetails customerDetails) {
        LOG.debug("Request to save CustomerDetails : {}", customerDetails);
        return customerDetailsRepository.save(customerDetails);
    }

    /**
     * Update a customerDetails.
     *
     * @param customerDetails the entity to save.
     * @return the persisted entity.
     */
    public CustomerDetails update(CustomerDetails customerDetails) {
        LOG.debug("Request to update CustomerDetails : {}", customerDetails);
        return customerDetailsRepository.save(customerDetails);
    }

    /**
     * Partially update a customerDetails.
     *
     * @param customerDetails the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CustomerDetails> partialUpdate(CustomerDetails customerDetails) {
        LOG.debug("Request to partially update CustomerDetails : {}", customerDetails);

        return customerDetailsRepository
            .findById(customerDetails.getId())
            .map(existingCustomerDetails -> {
                if (customerDetails.getPhone() != null) {
                    existingCustomerDetails.setPhone(customerDetails.getPhone());
                }
                if (customerDetails.getAddress() != null) {
                    existingCustomerDetails.setAddress(customerDetails.getAddress());
                }

                return existingCustomerDetails;
            })
            .map(customerDetailsRepository::save);
    }

    /**
     * Get all the customerDetails.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CustomerDetails> findAll() {
        LOG.debug("Request to get all CustomerDetails");
        return customerDetailsRepository.findAll();
    }

    /**
     * Get all the customerDetails with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<CustomerDetails> findAllWithEagerRelationships(Pageable pageable) {
        return customerDetailsRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     *  Get all the customerDetails where ShoppingCart is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CustomerDetails> findAllWhereShoppingCartIsNull() {
        LOG.debug("Request to get all customerDetails where ShoppingCart is null");
        return StreamSupport.stream(customerDetailsRepository.findAll().spliterator(), false)
            .filter(customerDetails -> customerDetails.getShoppingCart() == null)
            .toList();
    }

    /**
     * Get one customerDetails by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CustomerDetails> findOne(Long id) {
        LOG.debug("Request to get CustomerDetails : {}", id);
        return customerDetailsRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the customerDetails by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CustomerDetails : {}", id);
        customerDetailsRepository.deleteById(id);
    }
}
