package com.idigiwave.cbs.repository;

import com.idigiwave.cbs.entity.CbsCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query(value = """
            SELECT COALESCE(MAX(CAST(SUBSTRING(customer_id, :prefixLen + 1) AS BIGINT)), 0)
            FROM cbs_customers
            WHERE customer_id LIKE CONCAT(:prefix, '%')
            """, nativeQuery = true)
    Long findMaxCustomerSuffix(@Param("prefix") String prefix, @Param("prefixLen") int prefixLen);
}
