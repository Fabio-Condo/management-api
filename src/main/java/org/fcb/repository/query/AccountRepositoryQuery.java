package org.fcb.repository.query;

import org.fcb.domain.Account;
import org.fcb.repository.filter.AccountFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountRepositoryQuery {
    public Page<Account> filter(AccountFilter accountFilter, Pageable pageable);
}
