package com.idigiwave.cbs.repository;

import com.idigiwave.cbs.entity.CbsTransactionLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CbsTransactionLimitRepository extends JpaRepository<CbsTransactionLimit, Long> {
    List<CbsTransactionLimit> findByAccountNumber(String accountNumber);
}
