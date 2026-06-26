package com.example.store.domain;

import static com.example.store.domain.ProductCategoryTestSamples.*;
import static com.example.store.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.store.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Product.class);
        Product product1 = getProductSample1();
        Product product2 = new Product();
        assertThat(product1).isNotEqualTo(product2);

        product2.setId(product1.getId());
        assertThat(product1).isEqualTo(product2);

        product2 = getProductSample2();
        assertThat(product1).isNotEqualTo(product2);
    }

    @Test
    void categoryTest() {
        Product product = getProductRandomSampleGenerator();
        ProductCategory productCategoryBack = getProductCategoryRandomSampleGenerator();

        product.setCategory(productCategoryBack);
        assertThat(product.getCategory()).isEqualTo(productCategoryBack);

        product.category(null);
        assertThat(product.getCategory()).isNull();
    }
}
