package com.idigiwave.cbs.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CbsNomineeResponse {
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String dateOfBirth;
    private String relationship;
    private Integer sharePercentage;
    private String mobileNumber;
    private String email;
    private String addressLine1;
    private String country;
    private String postalCode;
}
