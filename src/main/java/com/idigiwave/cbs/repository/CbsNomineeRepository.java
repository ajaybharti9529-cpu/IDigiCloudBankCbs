package com.idigiwave.cbs.repository;

import com.idigiwave.cbs.entity.CbsNominee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CbsNomineeRepository extends JpaRepository<CbsNominee, Long> {
    List<CbsNominee> findByAccount_AccountNumber(String accountNumber);
}
