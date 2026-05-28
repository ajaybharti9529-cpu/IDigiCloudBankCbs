package com.idigiwave.cbs.service.impl;

import com.idigiwave.cbs.config.CbsIdGenerator;
import com.idigiwave.cbs.dto.request.*;
import com.idigiwave.cbs.dto.response.*;
import com.idigiwave.cbs.entity.*;
import com.idigiwave.cbs.enums.CbsAccountStatus;
import com.idigiwave.cbs.enums.CbsAccountType;
import com.idigiwave.cbs.exception.CbsDuplicateResourceException;
import com.idigiwave.cbs.exception.CbsResourceNotFoundException;
import com.idigiwave.cbs.repository.*;
import com.idigiwave.cbs.service.CbsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CbsServiceImpl implements CbsService {

    private final CbsCustomerRepository customerRepository;
    private final CbsAccountRepository accountRepository;
    private final CbsNomineeRepository nomineeRepository;
    private final CbsTransactionLimitRepository transactionLimitRepository;
    private final CbsIdGenerator idGenerator;

    // ─────────────────────────────────────────
    //  CUSTOMER
    // ─────────────────────────────────────────

    @Override
    @Transactional
    public CbsCustomerResponse createCustomer(CbsCreateCustomerRequest request) {
        log.info("CBS: Creating customer for email={}", request.getEmail());

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new CbsDuplicateResourceException("Customer with email already exists in CBS: " + request.getEmail());
        }
        if (request.getAadhaarNumber() != null &&
                customerRepository.existsByAadhaarNumber(request.getAadhaarNumber())) {
            throw new CbsDuplicateResourceException("Aadhaar number already registered in CBS");
        }
        if (request.getPanNumber() != null &&
                customerRepository.existsByPanNumber(request.getPanNumber())) {
            throw new CbsDuplicateResourceException("PAN number already registered in CBS");
        }

        CbsCustomer saved = persistCustomerWithRetry(request);
        log.info("CBS: Customer created with customerId={}", saved.getCustomerId());
        return mapToCustomerResponse(saved);
    }

    @Override
    public CbsCustomerResponse getCustomerById(String customerId) {
        CbsCustomer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CbsResourceNotFoundException("Customer not found in CBS: " + customerId));
        return mapToCustomerResponse(customer);
    }

    @Override
    public CbsCustomerResponse searchCustomer(String searchBy, String searchValue) {
        CbsCustomer customer = switch (searchBy.toUpperCase()) {
            case "CUSTOMER_ID"    -> customerRepository.findByCustomerId(searchValue)
                    .orElseThrow(() -> new CbsResourceNotFoundException("No customer found with ID: " + searchValue));
            case "EMAIL"          -> customerRepository.findByEmail(searchValue)
                    .orElseThrow(() -> new CbsResourceNotFoundException("No customer found with email: " + searchValue));
            case "MOBILE_NUMBER"  -> customerRepository.findByMobileNumber(searchValue)
                    .orElseThrow(() -> new CbsResourceNotFoundException("No customer found with mobile: " + searchValue));
            case "AADHAAR"        -> customerRepository.findByAadhaarNumber(searchValue)
                    .orElseThrow(() -> new CbsResourceNotFoundException("No customer found with Aadhaar: " + searchValue));
            case "PAN"            -> customerRepository.findByPanNumber(searchValue)
                    .orElseThrow(() -> new CbsResourceNotFoundException("No customer found with PAN: " + searchValue));
            default -> throw new IllegalArgumentException("Invalid searchBy value: " + searchBy);
        };
        return mapToCustomerResponse(customer);
    }

    // ─────────────────────────────────────────
    //  ACCOUNT
    // ─────────────────────────────────────────

    @Override
    @Transactional
    public CbsAccountResponse openAccount(CbsOpenAccountRequest request) {
        log.info("CBS: Opening account for customerId={}", request.getCustomerId());

        CbsCustomer customer = customerRepository.findByCustomerId(request.getCustomerId())
                .orElseThrow(() -> new CbsResourceNotFoundException("Customer not found in CBS: " + request.getCustomerId()));

        CbsAccount saved = persistAccountWithRetry(request, customer);

        // Automatically seed default transaction limits
        seedDefaultTransactionLimits(saved.getAccountNumber());

        log.info("CBS: Account opened with accountNumber={}", saved.getAccountNumber());
        return mapToAccountResponse(saved);
    }

    @Override
    public CbsAccountResponse getAccountByNumber(String accountNumber) {
        CbsAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new CbsResourceNotFoundException("Account not found in CBS: " + accountNumber));
        return mapToAccountResponse(account);
    }

    @Override
    public List<CbsAccountResponse> getAccountsByCustomerId(String customerId) {
        if (!customerRepository.existsById(customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CbsResourceNotFoundException("Customer not found: " + customerId)).getId())) {
            throw new CbsResourceNotFoundException("Customer not found: " + customerId);
        }
        return accountRepository.findByCustomer_CustomerId(customerId)
                .stream().map(this::mapToAccountResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CbsAccountResponse activateAccount(String accountNumber) {
        CbsAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new CbsResourceNotFoundException("Account not found: " + accountNumber));
        account.setAccountStatus(CbsAccountStatus.ACTIVE);
        account.setNetBankingEnabled(true);
        account.setMobileBankingEnabled(true);
        account.setChequeBookIssued(true);
        account.setDebitCardIssued(true);
        return mapToAccountResponse(accountRepository.save(account));
    }

    @Override
    @Transactional
    public CbsAccountResponse updateInitialFunding(CbsInitialFundingRequest request) {
        log.info("CBS: Initial funding for accountNumber={}, amount={}", request.getAccountNumber(), request.getAmount());

        CbsAccount account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new CbsResourceNotFoundException("Account not found: " + request.getAccountNumber()));

        account.setBalance(account.getBalance().add(request.getAmount()));
        account.setAccountStatus(CbsAccountStatus.KYC_VERIFIED);
        return mapToAccountResponse(accountRepository.save(account));
    }

    // ─────────────────────────────────────────
    //  NOMINEE
    // ─────────────────────────────────────────

    @Override
    @Transactional
    public CbsNomineeResponse addNominee(CbsAddNomineeRequest request) {
        CbsAccount account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new CbsResourceNotFoundException("Account not found: " + request.getAccountNumber()));

        CbsNominee nominee = CbsNominee.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .gender(request.getGender())
                .dateOfBirth(parseFlexibleDate(request.getDateOfBirth()))
                .relationship(request.getRelationship())
                .sharePercentage(request.getSharePercentage())
                .mobileNumber(request.getMobileNumber())
                .email(request.getEmail())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .account(account)
                .build();

        return mapToNomineeResponse(nomineeRepository.save(nominee));
    }

    @Override
    public List<CbsNomineeResponse> getNomineesByAccount(String accountNumber) {
        return nomineeRepository.findByAccount_AccountNumber(accountNumber)
                .stream().map(this::mapToNomineeResponse).collect(Collectors.toList());
    }

    // ─────────────────────────────────────────
    //  TRANSACTION LIMITS
    // ─────────────────────────────────────────

    @Override
    public List<CbsTransactionLimitResponse> getTransactionLimits(String accountNumber) {
        return transactionLimitRepository.findByAccountNumber(accountNumber)
                .stream().map(this::mapToLimitResponse).collect(Collectors.toList());
    }

    // ─────────────────────────────────────────
    //  WELCOME KIT
    // ─────────────────────────────────────────

    @Override
    @Transactional
    public CbsWelcomeKitResponse issueWelcomeKit(String accountNumber) {
        log.info("CBS: Issuing welcome kit for accountNumber={}", accountNumber);

        CbsAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new CbsResourceNotFoundException("Account not found: " + accountNumber));

        account.setDebitCardIssued(true);
        account.setNetBankingEnabled(true);
        account.setMobileBankingEnabled(true);
        account.setChequeBookIssued(true);
        account.setAccountStatus(CbsAccountStatus.ACTIVE);
        accountRepository.save(account);

        CbsCustomer customer = account.getCustomer();
        String debitCardNumber = "4" + generateNumericString(15);
        String netBankingUserId = customer.getEmail().split("@")[0] + "_" + account.getAccountNumber().substring(10);

        return CbsWelcomeKitResponse.builder()
                .accountNumber(accountNumber)
                .customerId(customer.getCustomerId())
                .customerName(customer.getFirstName() + " " + customer.getLastName())
                .debitCardNumber(maskCardNumber(debitCardNumber))
                .debitCardVariant("VISA CLASSIC")
                .netBankingUserId(netBankingUserId)
                .passbookIssued("YES")
                .chequeBookReference("CHQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .welcomeKitReference("WK-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase())
                .issuedAt(LocalDateTime.now())
                .build();
    }

    // ─────────────────────────────────────────
    //  PRIVATE HELPERS
    // ─────────────────────────────────────────

    private void seedDefaultTransactionLimits(String accountNumber) {
        List<CbsTransactionLimit> limits = List.of(
                buildLimit(accountNumber, "BRANCH",          "IFT",  new BigDecimal("1000"),   new BigDecimal("50000000"),  new BigDecimal("60000000"),  new BigDecimal("70000000"),  new BigDecimal("800000000")),
                buildLimit(accountNumber, "BRANCH",          "NEFT", BigDecimal.ONE,            new BigDecimal("500000"),    new BigDecimal("600000"),    new BigDecimal("9000000"),   new BigDecimal("999999999")),
                buildLimit(accountNumber, "BRANCH",          "RTGS", new BigDecimal("100"),     new BigDecimal("2000000"),   new BigDecimal("3000000"),   new BigDecimal("50000000"),  new BigDecimal("50000000000")),
                buildLimit(accountNumber, "INTERNET_BANKING","IFT",  BigDecimal.ONE,            new BigDecimal("1000000"),   new BigDecimal("1000001"),   new BigDecimal("1000002"),   new BigDecimal("1000003")),
                buildLimit(accountNumber, "INTERNET_BANKING","NEFT", BigDecimal.ONE,            new BigDecimal("1000000"),   new BigDecimal("1000001"),   new BigDecimal("1000002"),   new BigDecimal("1000003")),
                buildLimit(accountNumber, "MOBILE_BANKING",  "IMPS", BigDecimal.ONE,            new BigDecimal("200000"),    new BigDecimal("200001"),    new BigDecimal("200002"),    new BigDecimal("200003"))
        );
        transactionLimitRepository.saveAll(limits);
    }

    private CbsTransactionLimit buildLimit(String acctNo, String channel, String mode,
                                           BigDecimal min, BigDecimal max,
                                           BigDecimal daily, BigDecimal weekly, BigDecimal monthly) {
        return CbsTransactionLimit.builder()
                .accountNumber(acctNo)
                .channel(channel)
                .paymentMode(mode)
                .paymentType("Fund Transfer")
                .minTransactionLimit(min)
                .maxTransactionLimit(max)
                .dailyLimit(daily)
                .weeklyLimit(weekly)
                .monthlyLimit(monthly)
                .effectiveDate(LocalDate.now())
                .build();
    }

    private String generateNumericString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) sb.append((int) (Math.random() * 10));
        return sb.toString();
    }

    private String maskCardNumber(String card) {
        return card.substring(0, 4) + "-XXXX-XXXX-" + card.substring(12);
    }

    private CbsCustomer persistCustomerWithRetry(CbsCreateCustomerRequest request) {
        final int maxAttempts = 5;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            CbsCustomer customer = CbsCustomer.builder()
                    .customerId(idGenerator.generateCustomerId())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .mobileNumber(request.getMobileNumber())
                    .aadhaarNumber(request.getAadhaarNumber())
                    .panNumber(request.getPanNumber())
                    .address(request.getAddress())
                    .city(request.getCity())
                    .state(request.getState())
                    .pincode(request.getPincode())
                    .branchCode(request.getBranchCode())
                    .bankCode(idGenerator.getBankCode())
                    .customerStatus("ACTIVE")
                    .build();
            try {
                return customerRepository.save(customer);
            } catch (DataIntegrityViolationException ex) {
                if (!isIdCollision(ex, "customer_id")) {
                    throw ex;
                }
                log.warn("CBS: customer ID collision detected on attempt {}", attempt);
            }
        }

        throw new CbsDuplicateResourceException("Unable to generate unique CBS customer ID. Please retry.");
    }

    private CbsAccount persistAccountWithRetry(CbsOpenAccountRequest request, CbsCustomer customer) {
        final int maxAttempts = 5;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            String accountNumber = idGenerator.generateAccountNumber();
            CbsAccount account = CbsAccount.builder()
                    .accountNumber(accountNumber)
                    .accountType(request.getAccountType())
                    .accountStatus(CbsAccountStatus.INITIATED)
                    .productCode(request.getProductCode() != null ? request.getProductCode() : "CPSAVIUTAOO1")
                    .offerCode(request.getOfferCode() != null ? request.getOfferCode() : "OFFERSAVIUTAOO1")
                    .branchCode(request.getBranchCode())
                    .bankCode(idGenerator.getBankCode())
                    .currencyCode(request.getCurrencyCode() != null ? request.getCurrencyCode() : "INR")
                    .balance(BigDecimal.ZERO)
                    .modeOfOperation(request.getModeOfOperation() != null ? request.getModeOfOperation() : "SINGLE")
                    .accountName(request.getAccountName() != null ? request.getAccountName() : customer.getFirstName() + " " + customer.getLastName())
                    .interestRate(new BigDecimal("3.5"))
                    .interestType("CREDIT")
                    .debitCardIssued(false)
                    .netBankingEnabled(false)
                    .mobileBankingEnabled(false)
                    .chequeBookIssued(false)
                    .effectiveDate(LocalDate.now())
                    .customer(customer)
                    .build();
            try {
                return accountRepository.save(account);
            } catch (DataIntegrityViolationException ex) {
                if (!isIdCollision(ex, "account_number")) {
                    throw ex;
                }
                log.warn("CBS: account number collision detected on attempt {}", attempt);
            }
        }

        throw new CbsDuplicateResourceException("Unable to generate unique CBS account number. Please retry.");
    }

    private boolean isIdCollision(DataIntegrityViolationException ex, String constraintField) {
        String message = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        return message != null && message.toLowerCase().contains(constraintField.toLowerCase());
    }

    private LocalDate parseFlexibleDate(String dateOfBirth) {
        if (dateOfBirth == null || dateOfBirth.isBlank()) {
            throw new IllegalArgumentException("Nominee dateOfBirth is required");
        }
        try { return LocalDate.parse(dateOfBirth); } catch (Exception ignored) {}
        try { return LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("dd-MMM-yyyy")); } catch (Exception ignored) {}
        try { return LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("d-MMM-yyyy")); } catch (Exception ignored) {}
        try { return LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("dd/MM/yyyy")); } catch (Exception ignored) {}
        throw new IllegalArgumentException("Invalid nominee date format: " + dateOfBirth);
    }

    private CbsCustomerResponse mapToCustomerResponse(CbsCustomer c) {
        return CbsCustomerResponse.builder()
                .customerId(c.getCustomerId()).firstName(c.getFirstName()).lastName(c.getLastName())
                .email(c.getEmail()).mobileNumber(c.getMobileNumber()).aadhaarNumber(c.getAadhaarNumber())
                .panNumber(c.getPanNumber()).address(c.getAddress()).city(c.getCity()).state(c.getState())
                .pincode(c.getPincode()).customerStatus(c.getCustomerStatus()).branchCode(c.getBranchCode())
                .bankCode(c.getBankCode()).createdAt(c.getCreatedAt()).build();
    }

    private CbsAccountResponse mapToAccountResponse(CbsAccount a) {
        List<CbsNomineeResponse> nominees = a.getNominees() == null ? List.of() :
                a.getNominees().stream().map(this::mapToNomineeResponse).collect(Collectors.toList());
        return CbsAccountResponse.builder()
                .accountNumber(a.getAccountNumber()).accountType(a.getAccountType())
                .accountStatus(a.getAccountStatus()).productCode(a.getProductCode())
                .offerCode(a.getOfferCode()).branchCode(a.getBranchCode()).bankCode(a.getBankCode())
                .currencyCode(a.getCurrencyCode()).balance(a.getBalance()).modeOfOperation(a.getModeOfOperation())
                .accountName(a.getAccountName()).interestRate(a.getInterestRate()).interestType(a.getInterestType())
                .debitCardIssued(a.isDebitCardIssued()).netBankingEnabled(a.isNetBankingEnabled())
                .mobileBankingEnabled(a.isMobileBankingEnabled()).chequeBookIssued(a.isChequeBookIssued())
                .customerId(a.getCustomer().getCustomerId())
                .customerName(a.getCustomer().getFirstName() + " " + a.getCustomer().getLastName())
                .nominees(nominees).openedAt(a.getOpenedAt()).build();
    }

    private CbsNomineeResponse mapToNomineeResponse(CbsNominee n) {
        return CbsNomineeResponse.builder()
                .id(n.getId()).firstName(n.getFirstName()).middleName(n.getMiddleName())
                .lastName(n.getLastName()).gender(n.getGender())
                .dateOfBirth(n.getDateOfBirth() != null ? n.getDateOfBirth().toString() : null)
                .relationship(n.getRelationship()).sharePercentage(n.getSharePercentage())
                .mobileNumber(n.getMobileNumber()).email(n.getEmail())
                .addressLine1(n.getAddressLine1()).country(n.getCountry()).postalCode(n.getPostalCode())
                .build();
    }

    private CbsTransactionLimitResponse mapToLimitResponse(CbsTransactionLimit l) {
        return CbsTransactionLimitResponse.builder()
                .channel(l.getChannel()).paymentMode(l.getPaymentMode()).paymentType(l.getPaymentType())
                .minTransactionLimit(l.getMinTransactionLimit()).maxTransactionLimit(l.getMaxTransactionLimit())
                .dailyLimit(l.getDailyLimit()).weeklyLimit(l.getWeeklyLimit()).monthlyLimit(l.getMonthlyLimit())
                .effectiveDate(l.getEffectiveDate()).build();
    }
}
