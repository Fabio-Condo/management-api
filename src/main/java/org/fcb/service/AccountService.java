package org.fcb.service;

import org.fcb.dto.AccountRequest;
import org.fcb.dto.AccountResponse;
import org.fcb.repository.filter.AccountFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountService {

    AccountResponse create(AccountRequest request);

    List<AccountResponse> findAll();

    Page<AccountResponse> filter(
            AccountFilter accountFilter,
            Pageable pageable
    );

    AccountResponse findById(Long id);

    List<AccountResponse> findByCustomerId(Long customerId);
}