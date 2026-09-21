package org.fcb.service.impl;

import org.fcb.constant.CacheNames;
import org.fcb.domain.Account;
import org.fcb.domain.Customer;
import org.fcb.dto.AccountRequest;
import org.fcb.dto.AccountResponse;
import org.fcb.dtoMapper.AccountMapper;
import org.fcb.exception.domain.BusinessException;
import org.fcb.exception.domain.ResourceNotFoundException;
import org.fcb.repository.AccountRepository;
import org.fcb.repository.CustomerRepository;
import org.fcb.repository.filter.AccountFilter;
import org.fcb.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final AccountMapper accountMapper;

    public AccountServiceImpl(AccountRepository accountRepository, CustomerRepository customerRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    @Transactional
    public AccountResponse create(AccountRequest request) {

        logger.info(
                "Creating account. Account number: {}, Customer ID: {}",
                request.getNumber(),
                request.getCustomerId()
        );

        if (accountRepository.existsByNumber(request.getNumber())) {
            logger.warn("Account creation failed. Account number already exists: {}", request.getNumber());
            throw new BusinessException("Account number already exists");
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> {
                    logger.warn("Account creation failed. Customer not found. Customer ID: {}", request.getCustomerId());
                    return new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId());
                });

        Account account = accountMapper.toEntity(request);

        account.setCustomer(customer);

        Account savedAccount = accountRepository.save(account);

        logger.info(
                "Account created successfully. Account ID: {}, Account number: {}, Customer ID: {}",
                savedAccount.getId(),
                savedAccount.getNumber(),
                savedAccount.getCustomer().getId()
        );

        return accountMapper.toResponse(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheNames.ACCOUNT_LIST,
            key = "'all'",
            unless = "#result == null"
    )
    public List<AccountResponse> findAll() {

        logger.debug("Fetching all accounts");

        List<Account> accounts = accountRepository.findAll();

        logger.info("Accounts retrieved successfully. Total: {}", accounts.size());

        return accounts.stream()
                .map(accountMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountResponse> filter(AccountFilter accountFilter, Pageable pageable) {

        logger.debug(
                "Filtering accounts. Page: {}, Size: {}",
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        Page<Account> accounts = accountRepository.filter(accountFilter, pageable);

        logger.info("Accounts filtered successfully. Total elements: {}", accounts.getTotalElements());

        return accounts.map(accountMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheNames.ACCOUNT_DETAIL,
            key = "#id",
            unless = "#result == null"
    )
    public AccountResponse findById(Long id) {

        logger.debug("Fetching account by ID: {}", id);

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Account not found. Account ID: {}", id);
                    return new ResourceNotFoundException("Account not found with id: " + id);
                });

        logger.info(
                "Account retrieved successfully. Account ID: {}, Account number: {}",
                account.getId(),
                account.getNumber()
        );

        return accountMapper.toResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> findByCustomerId(Long customerId) {

        logger.debug("Fetching accounts for customer. Customer ID: {}", customerId);

        if (!customerRepository.existsById(customerId)) {
            logger.warn("Cannot fetch accounts. Customer not found. Customer ID: {}", customerId);
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }

        List<Account> accounts = accountRepository.findByCustomerId(customerId);

        logger.info(
                "Customer accounts retrieved successfully. Customer ID: {}, Total accounts: {}",
                customerId,
                accounts.size()
        );

        return accounts.stream()
                .map(accountMapper::toResponse)
                .collect(Collectors.toList());
    }
}