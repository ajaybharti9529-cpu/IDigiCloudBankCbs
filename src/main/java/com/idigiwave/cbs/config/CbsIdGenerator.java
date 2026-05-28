package com.idigiwave.cbs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

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

    private final AtomicInteger customerCounter = new AtomicInteger(0);
    private final AtomicInteger accountCounter = new AtomicInteger(0);

    public String generateCustomerId() {
        long nowBucket = Instant.now().toEpochMilli() % 100000000L;
        int seq = customerCounter.updateAndGet(value -> (value + 1) % 10000);
        return customerPrefix + String.format("%08d%04d", nowBucket, seq);
    }

    public String generateAccountNumber() {
        long nowBucket = Instant.now().toEpochMilli() % 100000000L;
        int seq = accountCounter.updateAndGet(value -> (value + 1) % 10000);
        return accountPrefix + String.format("%08d%04d", nowBucket, seq);
    }

    public String getBankCode() {
        return bankCode;
    }
}
