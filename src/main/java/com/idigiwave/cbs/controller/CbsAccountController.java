package com.idigiwave.cbs.controller;

import com.idigiwave.cbs.dto.request.CbsInitialFundingRequest;
import com.idigiwave.cbs.dto.request.CbsOpenAccountRequest;
import com.idigiwave.cbs.dto.response.*;
import com.idigiwave.cbs.service.CbsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "CBS - Account APIs", description = "Manage bank accounts in Core Banking System")
public class CbsAccountController {

    private final CbsService cbsService;

    @PostMapping
    @Operation(summary = "Step 6-7: Open a new account in CBS")
    public ResponseEntity<CbsApiResponse<CbsAccountResponse>> openAccount(
            @Valid @RequestBody CbsOpenAccountRequest request) {
        CbsAccountResponse response = cbsService.openAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CbsApiResponse.success("Account opened in CBS successfully", response));
    }

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Get account details by account number")
    public ResponseEntity<CbsApiResponse<CbsAccountResponse>> getAccount(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(CbsApiResponse.success("Account fetched",
                cbsService.getAccountByNumber(accountNumber)));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get all accounts for a customer")
    public ResponseEntity<CbsApiResponse<List<CbsAccountResponse>>> getCustomerAccounts(
            @PathVariable String customerId) {
        return ResponseEntity.ok(CbsApiResponse.success("Accounts fetched",
                cbsService.getAccountsByCustomerId(customerId)));
    }

    @PatchMapping("/{accountNumber}/activate")
    @PostMapping("/{accountNumber}/activate")
    @Operation(summary = "Activate account after KYC verification")
    public ResponseEntity<CbsApiResponse<CbsAccountResponse>> activateAccount(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(CbsApiResponse.success("Account activated successfully",
                cbsService.activateAccount(accountNumber)));
    }

    @PostMapping("/funding")
    @Operation(summary = "Step 8: Update initial funding amount for account")
    public ResponseEntity<CbsApiResponse<CbsAccountResponse>> initialFunding(
            @Valid @RequestBody CbsInitialFundingRequest request) {
        return ResponseEntity.ok(CbsApiResponse.success("Initial funding applied successfully",
                cbsService.updateInitialFunding(request)));
    }

    @GetMapping("/{accountNumber}/transaction-limits")
    @Operation(summary = "Step 6: Get transaction limits for an account")
    public ResponseEntity<CbsApiResponse<List<CbsTransactionLimitResponse>>> getTransactionLimits(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(CbsApiResponse.success("Transaction limits fetched",
                cbsService.getTransactionLimits(accountNumber)));
    }

    @PostMapping("/{accountNumber}/welcome-kit")
    @Operation(summary = "Step 9: Issue welcome kit (debit card, passbook, cheque book, net banking)")
    public ResponseEntity<CbsApiResponse<CbsWelcomeKitResponse>> issueWelcomeKit(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(CbsApiResponse.success("Welcome kit issued successfully",
                cbsService.issueWelcomeKit(accountNumber)));
    }
}
