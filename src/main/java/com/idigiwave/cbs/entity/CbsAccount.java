package com.idigiwave.cbs.entity;

import com.idigiwave.cbs.enums.CbsAccountStatus;
import com.idigiwave.cbs.enums.CbsAccountType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cbs_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CbsAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * CBS Account Number like: 2100000000020220
     */
    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private CbsAccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    private CbsAccountStatus accountStatus;

    @Column(name = "product_code")
    private String productCode;   // e.g. CPSAVIUTAOO1

    @Column(name = "offer_code")
    private String offerCode;     // e.g. OFFERSAVIUTAOO1

    @Column(name = "branch_code")
    private String branchCode;

    @Column(name = "bank_code")
    private String bankCode;      // AXB

    @Column(name = "currency_code")
    private String currencyCode;  // INR

    @Column(name = "balance", precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(name = "mode_of_operation")
    private String modeOfOperation;  // SINGLE, ANYONE_OR_SURVIVOR, JOINTLY

    @Column(name = "account_name")
    private String accountName;

    // Interest details
    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "interest_type")
    private String interestType;  // CREDIT, DEBIT

    // Debit card
    @Column(name = "debit_card_issued")
    private boolean debitCardIssued;

    @Column(name = "debit_card_variant")
    private String debitCardVariant;

    // Net banking
    @Column(name = "net_banking_enabled")
    private boolean netBankingEnabled;

    @Column(name = "mobile_banking_enabled")
    private boolean mobileBankingEnabled;

    // Cheque book
    @Column(name = "cheque_book_issued")
    private boolean chequeBookIssued;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CbsCustomer customer;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CbsNominee> nominees;

    @CreationTimestamp
    @Column(name = "opened_at", updatable = false)
    private LocalDateTime openedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
