package com.idigiwave.cbs.dto.response;

import com.idigiwave.cbs.enums.CbsAccountStatus;
import com.idigiwave.cbs.enums.CbsAccountType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CbsAccountResponse {
    private String accountNumber;
    private CbsAccountType accountType;
    private CbsAccountStatus accountStatus;
    private String productCode;
    private String offerCode;
    private String branchCode;
    private String bankCode;
    private String currencyCode;
    private BigDecimal balance;
    private String modeOfOperation;
    private String accountName;
    private BigDecimal interestRate;
    private String interestType;
    private boolean debitCardIssued;
    private boolean netBankingEnabled;
    private boolean mobileBankingEnabled;
    private boolean chequeBookIssued;
    private String customerId;
    private String customerName;
    private List<CbsNomineeResponse> nominees;
    private LocalDateTime openedAt;
}
