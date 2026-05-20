package com.idigiwave.cbs.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CbsTransactionLimitResponse {
    private String channel;
    private String paymentMode;
    private String paymentType;
    private BigDecimal minTransactionLimit;
    private BigDecimal maxTransactionLimit;
    private BigDecimal dailyLimit;
    private BigDecimal weeklyLimit;
    private BigDecimal monthlyLimit;
    private LocalDate effectiveDate;
}
