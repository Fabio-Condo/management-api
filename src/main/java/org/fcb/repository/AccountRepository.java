package org.fcb.repository;

import org.fcb.domain.Account;
import org.fcb.repository.query.AccountRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long>, AccountRepositoryQuery {

    Optional<Account> findByNumber(String number);

    boolean existsByNumber(String number);

    List<Account> findByCustomerId(Long customerId);

}
