package com.example.store.domain;

import static com.example.store.domain.CustomerDetailsTestSamples.*;
import static com.example.store.domain.ShoppingCartTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.store.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CustomerDetailsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CustomerDetails.class);
        CustomerDetails customerDetails1 = getCustomerDetailsSample1();
        CustomerDetails customerDetails2 = new CustomerDetails();
        assertThat(customerDetails1).isNotEqualTo(customerDetails2);

        customerDetails2.setId(customerDetails1.getId());
        assertThat(customerDetails1).isEqualTo(customerDetails2);

        customerDetails2 = getCustomerDetailsSample2();
        assertThat(customerDetails1).isNotEqualTo(customerDetails2);
    }

    @Test
    void shoppingCartTest() {
        CustomerDetails customerDetails = getCustomerDetailsRandomSampleGenerator();
        ShoppingCart shoppingCartBack = getShoppingCartRandomSampleGenerator();

        customerDetails.setShoppingCart(shoppingCartBack);
        assertThat(customerDetails.getShoppingCart()).isEqualTo(shoppingCartBack);
        assertThat(shoppingCartBack.getCustomer()).isEqualTo(customerDetails);

        customerDetails.shoppingCart(null);
        assertThat(customerDetails.getShoppingCart()).isNull();
        assertThat(shoppingCartBack.getCustomer()).isNull();
    }
}
