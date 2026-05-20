package com.idigiwave.cbs.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CbsCreateCustomerRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String email;

    @NotBlank
    private String mobileNumber;

    private String aadhaarNumber;
    private String panNumber;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String branchCode;
    private String bankCode;
}
