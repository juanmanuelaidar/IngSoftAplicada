package com.example.store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ShoppingCartTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shoppingCartWithRequiredFieldsShouldPassBeanValidation() {
        ShoppingCart shoppingCart = new ShoppingCart().createdDate(Instant.parse("2026-05-31T00:00:00Z")).status("OPEN");

        assertThat(validator.validate(shoppingCart)).isEmpty();
    }

    @Test
    void addOrdersShouldKeepBidirectionalCartRelation() {
        ShoppingCart shoppingCart = new ShoppingCart().createdDate(Instant.parse("2026-05-31T00:00:00Z")).status("OPEN");
        ProductOrder order = new ProductOrder().placedDate(Instant.parse("2026-05-31T00:00:00Z")).quantity(1);

        shoppingCart.addOrders(order);

        assertThat(shoppingCart.getOrders()).containsExactly(order);
        assertThat(order.getCart()).isSameAs(shoppingCart);
    }
}
