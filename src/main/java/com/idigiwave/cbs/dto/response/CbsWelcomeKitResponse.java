package com.idigiwave.cbs.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CbsWelcomeKitResponse {
    private String accountNumber;
    private String customerId;
    private String customerName;
    private String debitCardNumber;
    private String debitCardVariant;
    private String netBankingUserId;
    private String passbookIssued;
    private String chequeBookReference;
    private String welcomeKitReference;
    private LocalDateTime issuedAt;
}
