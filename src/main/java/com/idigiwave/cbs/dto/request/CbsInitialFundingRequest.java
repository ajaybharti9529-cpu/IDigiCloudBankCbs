package com.idigiwave.cbs.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CbsInitialFundingRequest {

    @NotBlank
    private String accountNumber;

    @NotNull
    private BigDecimal amount;

    @NotBlank
    private String fundingMode;  // CASH, CHEQUE, FUND_TRANSFER

    private String remarks;
}
