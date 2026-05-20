package com.idigiwave.cbs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates unique CBS IDs that mimic a real Core Banking System
 * Customer ID format: 100000051986
 * Account Number format: 2100000000020220
 */
@Component
public class CbsIdGenerator {

    @Value("${cbs.customer-prefix:10000005}")
    private String customerPrefix;

    @Value("${cbs.account-prefix:2100000000}")
    private String accountPrefix;

    @Value("${cbs.bank-code:AXB}")
    private String bankCode;

    private final AtomicLong customerCounter = new AtomicLong(1000);
    private final AtomicLong accountCounter = new AtomicLong(10000);

    public String generateCustomerId() {
        return customerPrefix + customerCounter.getAndIncrement();
    }

    public String generateAccountNumber() {
        return accountPrefix + accountCounter.getAndIncrement();
    }

    public String getBankCode() {
        return bankCode;
    }
}
