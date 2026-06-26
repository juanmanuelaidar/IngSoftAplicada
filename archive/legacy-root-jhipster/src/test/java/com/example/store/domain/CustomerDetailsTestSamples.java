package com.example.store.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CustomerDetailsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CustomerDetails getCustomerDetailsSample1() {
        return new CustomerDetails().id(1L).phone("phone1").address("address1");
    }

    public static CustomerDetails getCustomerDetailsSample2() {
        return new CustomerDetails().id(2L).phone("phone2").address("address2");
    }

    public static CustomerDetails getCustomerDetailsRandomSampleGenerator() {
        return new CustomerDetails()
            .id(longCount.incrementAndGet())
            .phone(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString());
    }
}
