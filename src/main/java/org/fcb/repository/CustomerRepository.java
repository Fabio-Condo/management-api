package org.fcb.repository;

import org.fcb.domain.Customer;
import org.fcb.repository.query.CustomerRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long>, CustomerRepositoryQuery {
    Optional<Customer> findByEmail(String email);
    boolean existsByEmail(String email);
}
