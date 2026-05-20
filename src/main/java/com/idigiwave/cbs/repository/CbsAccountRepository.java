package com.idigiwave.cbs.repository;

import com.idigiwave.cbs.entity.CbsAccount;
import com.idigiwave.cbs.enums.CbsAccountStatus;
import com.idigiwave.cbs.enums.CbsAccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CbsAccountRepository extends JpaRepository<CbsAccount, Long> {
    Optional<CbsAccount> findByAccountNumber(String accountNumber);
    List<CbsAccount> findByCustomer_CustomerId(String customerId);
    List<CbsAccount> findByCustomer_CustomerIdAndAccountType(String customerId, CbsAccountType type);
    List<CbsAccount> findByAccountStatus(CbsAccountStatus status);
    boolean existsByAccountNumber(String accountNumber);
}
