package org.fcb.service;

import org.fcb.domain.Account;
import org.fcb.domain.Customer;
import org.fcb.dto.AccountRequest;
import org.fcb.dto.AccountResponse;
import org.fcb.dto.CustomerSummaryResponse;
import org.fcb.dtoMapper.AccountMapper;
import org.fcb.exception.domain.BusinessException;
import org.fcb.exception.domain.ResourceNotFoundException;
import org.fcb.repository.AccountRepository;
import org.fcb.repository.CustomerRepository;
import org.fcb.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void shouldCreateAccountSuccessfully() {

        AccountRequest request = new AccountRequest();

        request.setNumber("00000000000000000001");
        request.setCustomerId(1L);

        Customer customer = new Customer();

        customer.setId(1L);
        customer.setName("John Doe");
        customer.setEmail("john@email.com");

        Account account = new Account();

        Account savedAccount = new Account();

        savedAccount.setId(10L);
        savedAccount.setNumber("00000000000000000001");
        savedAccount.setBalance(BigDecimal.ZERO);
        savedAccount.setCustomer(customer);

        CustomerSummaryResponse customerResponse =
                new CustomerSummaryResponse(
                        1L,
                        "John Doe",
                        "john@email.com"
                );

        AccountResponse response =
                new AccountResponse(
                        10L,
                        "00000000000000000001",
                        BigDecimal.ZERO,
                        customerResponse
                );

        when(accountRepository.existsByNumber(
                request.getNumber()
        )).thenReturn(false);

        when(customerRepository.findById(
                request.getCustomerId()
        )).thenReturn(Optional.of(customer));

        when(accountMapper.toEntity(request))
                .thenReturn(account);

        when(accountRepository.save(account))
                .thenReturn(savedAccount);

        when(accountMapper.toResponse(savedAccount))
                .thenReturn(response);

        AccountResponse result =
                accountService.create(request);

        assertNotNull(result);

        assertEquals(
                10L,
                result.getId()
        );

        assertEquals(
                "00000000000000000001",
                result.getNumber()
        );

        assertEquals(
                BigDecimal.ZERO,
                result.getBalance()
        );

        assertNotNull(result.getCustomer());

        assertEquals(
                1L,
                result.getCustomer().getId()
        );

        assertEquals(
                "John Doe",
                result.getCustomer().getName()
        );

        verify(accountRepository)
                .existsByNumber(request.getNumber());

        verify(customerRepository)
                .findById(request.getCustomerId());

        verify(accountMapper)
                .toEntity(request);

        verify(accountRepository)
                .save(account);

        verify(accountMapper)
                .toResponse(savedAccount);
    }

    @Test
    void shouldNotCreateAccountWhenNumberAlreadyExists() {

        AccountRequest request = new AccountRequest();

        request.setNumber("00000000000000000001");
        request.setCustomerId(1L);

        when(accountRepository.existsByNumber(
                request.getNumber()
        )).thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> accountService.create(request)
        );

        verify(accountRepository)
                .existsByNumber(request.getNumber());

        verify(customerRepository, never())
                .findById(anyLong());

        verify(accountRepository, never())
                .save(any(Account.class));

        verify(accountMapper, never())
                .toEntity(any(AccountRequest.class));
    }

    @Test
    void shouldNotCreateAccountWhenCustomerDoesNotExist() {

        AccountRequest request = new AccountRequest();

        request.setNumber("00000000000000000001");
        request.setCustomerId(99L);

        when(accountRepository.existsByNumber(
                request.getNumber()
        )).thenReturn(false);

        when(customerRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> accountService.create(request)
        );

        verify(accountRepository)
                .existsByNumber(request.getNumber());

        verify(customerRepository)
                .findById(99L);

        verify(accountRepository, never())
                .save(any(Account.class));

        verify(accountMapper, never())
                .toEntity(any(AccountRequest.class));
    }

    @Test
    void shouldFindAccountByIdSuccessfully() {

        Customer customer = new Customer();

        customer.setId(1L);
        customer.setName("John Doe");
        customer.setEmail("john@email.com");

        Account account = new Account();

        account.setId(10L);
        account.setNumber("00000000000000000001");
        account.setBalance(
                new BigDecimal("1000.00")
        );
        account.setCustomer(customer);

        CustomerSummaryResponse customerResponse =
                new CustomerSummaryResponse(
                        1L,
                        "John Doe",
                        "john@email.com"
                );

        AccountResponse response =
                new AccountResponse(
                        10L,
                        "00000000000000000001",
                        new BigDecimal("1000.00"),
                        customerResponse
                );

        when(accountRepository.findById(10L))
                .thenReturn(Optional.of(account));

        when(accountMapper.toResponse(account))
                .thenReturn(response);

        AccountResponse result =
                accountService.findById(10L);

        assertNotNull(result);

        assertEquals(
                10L,
                result.getId()
        );

        assertEquals(
                "00000000000000000001",
                result.getNumber()
        );

        assertEquals(
                new BigDecimal("1000.00"),
                result.getBalance()
        );

        assertNotNull(result.getCustomer());

        assertEquals(
                1L,
                result.getCustomer().getId()
        );

        verify(accountRepository)
                .findById(10L);

        verify(accountMapper)
                .toResponse(account);
    }

    @Test
    void shouldThrowExceptionWhenAccountDoesNotExist() {

        when(accountRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> accountService.findById(99L)
        );

        verify(accountRepository)
                .findById(99L);

        verify(accountMapper, never())
                .toResponse(any(Account.class));
    }

    @Test
    void shouldFindAccountsByCustomerIdSuccessfully() {

        Customer customer = new Customer();

        customer.setId(1L);
        customer.setName("John Doe");
        customer.setEmail("john@email.com");

        Account account1 = new Account();

        account1.setId(10L);
        account1.setNumber("00000000000000000001");
        account1.setBalance(
                new BigDecimal("1000.00")
        );
        account1.setCustomer(customer);

        Account account2 = new Account();

        account2.setId(11L);
        account2.setNumber("00000000000000000002");
        account2.setBalance(
                new BigDecimal("500.00")
        );
        account2.setCustomer(customer);

        CustomerSummaryResponse customerResponse =
                new CustomerSummaryResponse(
                        1L,
                        "John Doe",
                        "john@email.com"
                );

        AccountResponse response1 =
                new AccountResponse(
                        10L,
                        "00000000000000000001",
                        new BigDecimal("1000.00"),
                        customerResponse
                );

        AccountResponse response2 =
                new AccountResponse(
                        11L,
                        "00000000000000000002",
                        new BigDecimal("500.00"),
                        customerResponse
                );

        when(customerRepository.existsById(1L))
                .thenReturn(true);

        when(accountRepository.findByCustomerId(1L))
                .thenReturn(Arrays.asList(
                        account1,
                        account2
                ));

        when(accountMapper.toResponse(account1))
                .thenReturn(response1);

        when(accountMapper.toResponse(account2))
                .thenReturn(response2);

        List<AccountResponse> result =
                accountService.findByCustomerId(1L);

        assertNotNull(result);

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                10L,
                result.get(0).getId()
        );

        assertEquals(
                11L,
                result.get(1).getId()
        );

        assertEquals(
                1L,
                result.get(0).getCustomer().getId()
        );

        assertEquals(
                1L,
                result.get(1).getCustomer().getId()
        );

        verify(customerRepository)
                .existsById(1L);

        verify(accountRepository)
                .findByCustomerId(1L);

        verify(accountMapper)
                .toResponse(account1);

        verify(accountMapper)
                .toResponse(account2);
    }

    @Test
    void shouldThrowExceptionWhenCustomerDoesNotExistForAccounts() {

        when(customerRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> accountService.findByCustomerId(99L)
        );

        verify(customerRepository)
                .existsById(99L);

        verify(accountRepository, never())
                .findByCustomerId(anyLong());
    }
}