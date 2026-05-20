package com.idigiwave.cbs.service;

import com.idigiwave.cbs.dto.request.*;
import com.idigiwave.cbs.dto.response.*;

import java.util.List;

public interface CbsService {

    // Customer APIs
    CbsCustomerResponse createCustomer(CbsCreateCustomerRequest request);
    CbsCustomerResponse getCustomerById(String customerId);
    CbsCustomerResponse searchCustomer(String searchBy, String searchValue);

    // Account APIs
    CbsAccountResponse openAccount(CbsOpenAccountRequest request);
    CbsAccountResponse getAccountByNumber(String accountNumber);
    List<CbsAccountResponse> getAccountsByCustomerId(String customerId);
    CbsAccountResponse activateAccount(String accountNumber);
    CbsAccountResponse updateInitialFunding(CbsInitialFundingRequest request);

    // Nominee APIs
    CbsNomineeResponse addNominee(CbsAddNomineeRequest request);
    List<CbsNomineeResponse> getNomineesByAccount(String accountNumber);

    // Transaction Limits
    List<CbsTransactionLimitResponse> getTransactionLimits(String accountNumber);

    // Welcome Kit
    CbsWelcomeKitResponse issueWelcomeKit(String accountNumber);
}
