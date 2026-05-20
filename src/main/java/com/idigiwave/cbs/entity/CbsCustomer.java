package com.idigiwave.cbs.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cbs_customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CbsCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique CBS Customer ID like: 100000051986
     */
    @Column(name = "customer_id", nullable = false, unique = true, length = 20)
    private String customerId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;

    @Column(name = "aadhaar_number", unique = true)
    private String aadhaarNumber;

    @Column(name = "pan_number", unique = true)
    private String panNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "customer_status")
    private String customerStatus; // ACTIVE, INACTIVE

    @Column(name = "branch_code")
    private String branchCode;

    @Column(name = "bank_code")
    private String bankCode;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CbsAccount> accounts;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
