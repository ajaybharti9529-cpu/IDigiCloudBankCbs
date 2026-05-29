package com.idigiwave.cbs.repository;

import com.idigiwave.cbs.entity.CbsAccount;
import com.idigiwave.cbs.enums.CbsAccountStatus;
import com.idigiwave.cbs.enums.CbsAccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query(value = """
            SELECT COALESCE(MAX(CAST(SUBSTRING(account_number, :prefixLen + 1) AS BIGINT)), 0)
            FROM cbs_accounts
            WHERE account_number LIKE CONCAT(:prefix, '%')
            """, nativeQuery = true)
    Long findMaxAccountSuffix(@Param("prefix") String prefix, @Param("prefixLen") int prefixLen);
}
