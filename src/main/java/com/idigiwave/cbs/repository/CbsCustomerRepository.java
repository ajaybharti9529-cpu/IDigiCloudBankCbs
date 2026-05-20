package com.idigiwave.cbs.repository;

import com.idigiwave.cbs.entity.CbsCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CbsCustomerRepository extends JpaRepository<CbsCustomer, Long> {
    Optional<CbsCustomer> findByCustomerId(String customerId);
    Optional<CbsCustomer> findByEmail(String email);
    Optional<CbsCustomer> findByMobileNumber(String mobileNumber);
    Optional<CbsCustomer> findByAadhaarNumber(String aadhaarNumber);
    Optional<CbsCustomer> findByPanNumber(String panNumber);
    boolean existsByEmail(String email);
    boolean existsByAadhaarNumber(String aadhaarNumber);
    boolean existsByPanNumber(String panNumber);
}
