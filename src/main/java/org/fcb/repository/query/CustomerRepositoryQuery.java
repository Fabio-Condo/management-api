package org.fcb.repository.query;

import org.fcb.domain.Customer;
import org.fcb.repository.filter.CustomerFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerRepositoryQuery {
    public Page<Customer> filter(CustomerFilter customerFilter, Pageable pageable);
}
