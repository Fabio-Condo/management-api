package org.fcb.service;

import org.fcb.dto.CustomerRequest;
import org.fcb.dto.CustomerResponse;
import org.fcb.dto.PageResponse;
import org.fcb.repository.filter.CustomerFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {

    CustomerResponse create(CustomerRequest request);

    List<CustomerResponse> findAll();

    PageResponse<CustomerResponse> filter(
            CustomerFilter customerFilter,
            Pageable pageable
    );

    CustomerResponse findById(Long id);

    CustomerResponse update(
            Long id,
            CustomerRequest request
    );

    void delete(Long id);
}