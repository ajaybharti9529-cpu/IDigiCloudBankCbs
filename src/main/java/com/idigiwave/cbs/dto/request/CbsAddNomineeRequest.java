package com.idigiwave.cbs.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CbsAddNomineeRequest {

    @NotBlank
    private String accountNumber;

    @NotBlank
    private String firstName;

    private String middleName;
    private String lastName;

    @NotBlank
    private String gender;

    @NotBlank
    private String dateOfBirth; // DD-MMM-YYYY

    @NotBlank
    private String relationship;

    @NotNull
    private Integer sharePercentage;

    private String mobileNumber;
    private String email;
    private String addressLine1;
    private String addressLine2;
    private String country;
    private String postalCode;
}
