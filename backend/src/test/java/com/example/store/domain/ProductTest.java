package com.example.store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void productWithValidRequiredFieldsShouldPassBeanValidation() {
        Product product = new Product().name("Teclado mecánico").price(new BigDecimal("150.00")).stock(20);

        assertThat(validator.validate(product)).isEmpty();
    }

    @Test
    void productWithNegativePriceShouldFailBeanValidation() {
        Product product = new Product().name("Mouse").price(new BigDecimal("-1.00")).stock(10);

        assertThat(validator.validate(product)).anyMatch(violation -> "price".equals(violation.getPropertyPath().toString()));
    }
}
