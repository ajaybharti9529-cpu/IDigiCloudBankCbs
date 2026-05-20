package com.idigiwave.cbs.controller;

import com.idigiwave.cbs.dto.request.CbsAddNomineeRequest;
import com.idigiwave.cbs.dto.response.CbsApiResponse;
import com.idigiwave.cbs.dto.response.CbsNomineeResponse;
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
@RequestMapping("/api/v1/nominees")
@RequiredArgsConstructor
@Tag(name = "CBS - Nominee APIs", description = "Manage nominees for bank accounts")
public class CbsNomineeController {

    private final CbsService cbsService;

    @PostMapping
    @Operation(summary = "Step 7: Add nominee to an account")
    public ResponseEntity<CbsApiResponse<CbsNomineeResponse>> addNominee(
            @Valid @RequestBody CbsAddNomineeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CbsApiResponse.success("Nominee added successfully", cbsService.addNominee(request)));
    }

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Get all nominees for an account")
    public ResponseEntity<CbsApiResponse<List<CbsNomineeResponse>>> getNominees(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(CbsApiResponse.success("Nominees fetched",
                cbsService.getNomineesByAccount(accountNumber)));
    }
}
