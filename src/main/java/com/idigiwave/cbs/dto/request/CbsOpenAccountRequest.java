package com.idigiwave.cbs.dto.request;

import com.idigiwave.cbs.enums.CbsAccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CbsOpenAccountRequest {

    @NotBlank
    private String customerId;

    @NotNull
    private CbsAccountType accountType;

    private String offerCode;
    private String productCode;

    @NotBlank
    private String branchCode;

    private String bankCode;
    private String currencyCode;
    private String modeOfOperation;   // SINGLE, ANYONE_OR_SURVIVOR
    private BigDecimal initialDepositAmount;
    private String accountName;
}
