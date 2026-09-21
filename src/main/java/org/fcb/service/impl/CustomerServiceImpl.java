package org.fcb.service.impl;

import org.fcb.constant.CacheNames;
import org.fcb.domain.Customer;
import org.fcb.dto.CustomerRequest;
import org.fcb.dto.CustomerResponse;
import org.fcb.dto.PageResponse;
import org.fcb.dtoMapper.CustomerMapper;
import org.fcb.exception.domain.BusinessException;
import org.fcb.exception.domain.ResourceNotFoundException;
import org.fcb.repository.CustomerRepository;
import org.fcb.repository.filter.CustomerFilter;
import org.fcb.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger =
            LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(
                    value = CacheNames.CUSTOMER_LIST,
                    allEntries = true
            ),
            @CacheEvict(
                    value = CacheNames.CUSTOMER_FILTER,
                    allEntries = true
            )
    })
    public CustomerResponse create(CustomerRequest request) {

        logger.info("Creating customer. Email: {}", request.getEmail());

        if (customerRepository.existsByEmail(request.getEmail())) {

            logger.warn("Customer creation failed. Email already exists: {}", request.getEmail());

            throw new BusinessException("Customer with this email already exists");
        }

        Customer customer = customerMapper.toEntity(request);

        Customer savedCustomer = customerRepository.save(customer);

        logger.info(
                "Customer created successfully. Customer ID: {}, Email: {}",
                savedCustomer.getId(),
                savedCustomer.getEmail()
        );

        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheNames.CUSTOMER_LIST,
            key = "'all'",
            unless = "#result == null"
    )
    public List<CustomerResponse> findAll() {

        logger.debug("Fetching all customers");

        List<Customer> customers = customerRepository.findAll();

        logger.info("Customers retrieved successfully. Total: {}", customers.size());

        return customers.stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheNames.CUSTOMER_FILTER,
            key =
                    "#customerFilter.name + '-' +" +
                    "#customerFilter.email + '-' +" +
                    "#customerFilter.phone + '-' +" +
                    "#pageable.pageNumber + '-' +" +
                    "#pageable.pageSize + '-' +" +
                    "#pageable.sort.toString()"
    )
    public PageResponse<CustomerResponse> filter(CustomerFilter customerFilter, Pageable pageable) {

        logger.debug(
                "Filtering customers. Page: {}, Size: {}",
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        Page<Customer> customers = customerRepository.filter(
                customerFilter,
                pageable
        );

        logger.info(
                "Customers filtered successfully. Total elements: {}",
                customers.getTotalElements()
        );

        Page<CustomerResponse> response = customers.map(
                customerMapper::toResponse
        );

        return new PageResponse<>(response);
    }

    @Override
    @Cacheable(
            value = CacheNames.CUSTOMER_DETAIL,
            key = "#id",
            unless = "#result == null"
    )
    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {

        logger.debug("Fetching customer by ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Customer not found. Customer ID: {}", id);
                    return new ResourceNotFoundException("Customer not found with id: " + id);
                });

        logger.info("Customer retrieved successfully. Customer ID: {}", customer.getId());

        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional
    @CacheEvict(
            value = {
                    CacheNames.CUSTOMER_DETAIL,
                    CacheNames.CUSTOMER_LIST,
                    CacheNames.CUSTOMER_FILTER,
            },
            allEntries = true
    )
    public CustomerResponse update(Long id, CustomerRequest request) {

        logger.info("Updating customer. Customer ID: {}", id);

        Customer existingCustomer = customerRepository.findById(id).orElseThrow(() -> {
            logger.warn("Customer update failed. Customer not found. Customer ID: {}", id);
            return new ResourceNotFoundException("Customer not found with id: " + id);
        });

        if (!existingCustomer.getEmail().equals(request.getEmail()) && customerRepository.existsByEmail(request.getEmail())) {
            logger.warn("Customer update failed. Email already exists: {}", request.getEmail());
            throw new BusinessException("Customer with this email already exists");
        }

        customerMapper.updateEntity(existingCustomer, request);

        Customer updatedCustomer = customerRepository.save(existingCustomer);

        logger.info("Customer updated successfully. Customer ID: {}", updatedCustomer.getId());

        return customerMapper.toResponse(updatedCustomer);
    }

    @Override
    @Transactional
    @CacheEvict(
            value = {
                    CacheNames.CUSTOMER_DETAIL,
                    CacheNames.CUSTOMER_LIST,
                    CacheNames.CUSTOMER_FILTER,
            },
            allEntries = true
    )
    public void delete(Long id) {

        logger.info("Deleting customer. Customer ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Customer deletion failed. Customer not found. Customer ID: {}", id);
                    return new ResourceNotFoundException("Customer not found with id: " + id);
                });

        customerRepository.delete(customer);

        logger.info("Customer deleted successfully. Customer ID: {}", id);
    }
}