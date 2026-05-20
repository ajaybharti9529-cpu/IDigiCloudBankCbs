package com.idigiwave.cbs.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cbs_transaction_limits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CbsTransactionLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "channel", nullable = false)  // BRANCH, INTERNET_BANKING, MOBILE_BANKING
    private String channel;

    @Column(name = "payment_mode", nullable = false) // IFT, NEFT, RTGS, IMPS
    private String paymentMode;

    @Column(name = "payment_type")
    private String paymentType; // Fund Transfer

    @Column(name = "min_transaction_limit", precision = 15, scale = 2)
    private BigDecimal minTransactionLimit;

    @Column(name = "max_transaction_limit", precision = 15, scale = 2)
    private BigDecimal maxTransactionLimit;

    @Column(name = "daily_limit", precision = 15, scale = 2)
    private BigDecimal dailyLimit;

    @Column(name = "weekly_limit", precision = 15, scale = 2)
    private BigDecimal weeklyLimit;

    @Column(name = "monthly_limit", precision = 15, scale = 2)
    private BigDecimal monthlyLimit;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;
}
