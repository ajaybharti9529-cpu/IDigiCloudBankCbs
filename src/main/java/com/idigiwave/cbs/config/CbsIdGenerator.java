package com.idigiwave.cbs.config;

import com.idigiwave.cbs.repository.CbsAccountRepository;
import com.idigiwave.cbs.repository.CbsCustomerRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates unique CBS IDs. Counters are synced from PostgreSQL before each ID
 * so Render cold-starts do not reuse customer/account numbers already in the DB.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CbsIdGenerator {

    @Value("${cbs.customer-prefix:10000005}")
    private String customerPrefix;

    @Value("${cbs.account-prefix:2100000000}")
    private String accountPrefix;

    @Value("${cbs.bank-code:AXB}")
    private String bankCode;

    private final CbsCustomerRepository customerRepository;
    private final CbsAccountRepository accountRepository;

    private final AtomicLong customerSequence = new AtomicLong(1000);
    private final AtomicLong accountSequence = new AtomicLong(10000);

    @PostConstruct
    void seedSequencesFromDatabase() {
        syncCustomerSequenceFromDatabase();
        syncAccountSequenceFromDatabase();
        log.info("CBS ID generator ready (v3). Next customer suffix={}, next account suffix={}",
                customerSequence.get(), accountSequence.get());
    }

    public String generateCustomerId() {
        syncCustomerSequenceFromDatabase();
        long suffix = customerSequence.getAndIncrement();
        return customerPrefix + suffix;
    }

    public String generateAccountNumber() {
        syncAccountSequenceFromDatabase();
        long suffix = accountSequence.getAndIncrement();
        return accountPrefix + suffix;
    }

    public String getBankCode() {
        return bankCode;
    }

    private void syncCustomerSequenceFromDatabase() {
        try {
            Long maxSuffix = customerRepository.findMaxCustomerSuffix(customerPrefix, customerPrefix.length());
            if (maxSuffix != null) {
                customerSequence.updateAndGet(current -> Math.max(current, Math.max(maxSuffix + 1, 1000)));
            }
        } catch (Exception ex) {
            log.warn("Could not sync customer ID sequence from database: {}", ex.getMessage());
        }
    }

    private void syncAccountSequenceFromDatabase() {
        try {
            Long maxSuffix = accountRepository.findMaxAccountSuffix(accountPrefix, accountPrefix.length());
            if (maxSuffix != null) {
                accountSequence.updateAndGet(current -> Math.max(current, Math.max(maxSuffix + 1, 10000)));
            }
        } catch (Exception ex) {
            log.warn("Could not sync account ID sequence from database: {}", ex.getMessage());
        }
    }
}
