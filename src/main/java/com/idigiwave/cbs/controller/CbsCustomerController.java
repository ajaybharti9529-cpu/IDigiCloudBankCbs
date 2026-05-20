package com.idigiwave.cbs.controller;

import com.idigiwave.cbs.dto.request.CbsCreateCustomerRequest;
import com.idigiwave.cbs.dto.response.CbsApiResponse;
import com.idigiwave.cbs.dto.response.CbsCustomerResponse;
import com.idigiwave.cbs.service.CbsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "CBS - Customer APIs", description = "Manage customers in Core Banking System")
public class CbsCustomerController {

    private final CbsService cbsService;

    @PostMapping
    @Operation(summary = "Create a new customer in CBS")
    public ResponseEntity<CbsApiResponse<CbsCustomerResponse>> createCustomer(
            @Valid @RequestBody CbsCreateCustomerRequest request) {
        CbsCustomerResponse response = cbsService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CbsApiResponse.success("Customer created successfully in CBS", response));
    }

    @GetMapping("/{customerId}")
    @Operation(summary = "Get customer by CBS Customer ID")
    public ResponseEntity<CbsApiResponse<CbsCustomerResponse>> getCustomer(
            @PathVariable String customerId) {
        return ResponseEntity.ok(CbsApiResponse.success("Customer fetched", cbsService.getCustomerById(customerId)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search customer by various parameters",
               description = "searchBy can be: CUSTOMER_ID, EMAIL, MOBILE_NUMBER, AADHAAR, PAN")
    public ResponseEntity<CbsApiResponse<CbsCustomerResponse>> searchCustomer(
            @RequestParam String searchBy,
            @RequestParam String searchValue) {
        return ResponseEntity.ok(CbsApiResponse.success("Customer found",
                cbsService.searchCustomer(searchBy, searchValue)));
    }
}
