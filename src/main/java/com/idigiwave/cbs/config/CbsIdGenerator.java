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
 * Generates unique CBS IDs that mimic a real Core Banking System.
 * Counters are seeded from the database on startup so restarts do not reuse IDs.
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

    /** Next numeric suffix after prefix (e.g. prefix 10000005 + 1004 => 100000051004). */
    private final AtomicLong customerSequence = new AtomicLong(1000);
    private final AtomicLong accountSequence = new AtomicLong(10000);

    @PostConstruct
    void seedSequencesFromDatabase() {
        long maxCustomerSuffix = customerRepository.findAll().stream()
                .map(c -> c.getCustomerId())
                .filter(id -> id != null && id.startsWith(customerPrefix) && id.length() > customerPrefix.length())
                .mapToLong(id -> parseNumericSuffix(id.substring(customerPrefix.length())))
                .max()
                .orElse(999L);

        long maxAccountSuffix = accountRepository.findAll().stream()
                .map(a -> a.getAccountNumber())
                .filter(id -> id != null && id.startsWith(accountPrefix) && id.length() > accountPrefix.length())
                .mapToLong(id -> parseNumericSuffix(id.substring(accountPrefix.length())))
                .max()
                .orElse(9999L);

        customerSequence.set(Math.max(maxCustomerSuffix + 1, 1000));
        accountSequence.set(Math.max(maxAccountSuffix + 1, 10000));

        log.info("CBS ID generator ready (v2). Next customer suffix={}, next account suffix={}",
                customerSequence.get(), accountSequence.get());
    }

    public String generateCustomerId() {
        long suffix = customerSequence.getAndIncrement();
        return customerPrefix + suffix;
    }

    public String generateAccountNumber() {
        long suffix = accountSequence.getAndIncrement();
        return accountPrefix + suffix;
    }

    public String getBankCode() {
        return bankCode;
    }

    private long parseNumericSuffix(String suffix) {
        try {
            return Long.parseLong(suffix.replaceAll("\\D", ""));
        } catch (NumberFormatException ex) {
            return 0L;
        }
    }
}
