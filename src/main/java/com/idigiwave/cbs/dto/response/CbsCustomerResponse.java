package com.idigiwave.cbs.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CbsCustomerResponse {
    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private String aadhaarNumber;
    private String panNumber;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String customerStatus;
    private String branchCode;
    private String bankCode;
    private LocalDateTime createdAt;
}
