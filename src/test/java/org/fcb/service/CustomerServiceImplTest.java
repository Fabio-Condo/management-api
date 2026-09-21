package org.fcb.service;

import org.fcb.domain.Customer;
import org.fcb.dto.CustomerRequest;
import org.fcb.dto.CustomerResponse;
import org.fcb.dtoMapper.CustomerMapper;
import org.fcb.exception.domain.BusinessException;
import org.fcb.exception.domain.ResourceNotFoundException;
import org.fcb.repository.CustomerRepository;
import org.fcb.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void shouldCreateCustomerSuccessfully() {

        CustomerRequest request = new CustomerRequest();

        request.setName("John Doe");
        request.setEmail("john@email.com");
        request.setPhone("841234567");

        Customer customer = new Customer();

        Customer savedCustomer = new Customer();

        savedCustomer.setId(1L);
        savedCustomer.setName("John Doe");
        savedCustomer.setEmail("john@email.com");
        savedCustomer.setPhone("841234567");

        CustomerResponse response = new CustomerResponse(
                1L,
                "John Doe",
                "john@email.com",
                "841234567"
        );

        when(customerRepository.existsByEmail(
                request.getEmail()
        )).thenReturn(false);

        when(customerMapper.toEntity(request))
                .thenReturn(customer);

        when(customerRepository.save(customer))
                .thenReturn(savedCustomer);

        when(customerMapper.toResponse(savedCustomer))
                .thenReturn(response);

        CustomerResponse result = customerService.create(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@email.com", result.getEmail());

        verify(customerRepository)
                .existsByEmail(request.getEmail());

        verify(customerRepository)
                .save(customer);

        verify(customerMapper)
                .toResponse(savedCustomer);
    }

    @Test
    void shouldNotCreateCustomerWhenEmailAlreadyExists() {

        CustomerRequest request = new CustomerRequest();

        request.setName("John Doe");
        request.setEmail("john@email.com");
        request.setPhone("841234567");

        when(customerRepository.existsByEmail(
                request.getEmail()
        )).thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> customerService.create(request)
        );

        verify(customerRepository, never())
                .save(any(Customer.class));

        verify(customerMapper, never())
                .toEntity(any(CustomerRequest.class));
    }

    @Test
    void shouldFindCustomerByIdSuccessfully() {

        Customer customer = new Customer();

        customer.setId(1L);
        customer.setName("John Doe");
        customer.setEmail("john@email.com");
        customer.setPhone("841234567");

        CustomerResponse response = new CustomerResponse(
                1L,
                "John Doe",
                "john@email.com",
                "841234567"
        );

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(customerMapper.toResponse(customer))
                .thenReturn(response);

        CustomerResponse result =
                customerService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());

        verify(customerRepository)
                .findById(1L);

        verify(customerMapper)
                .toResponse(customer);
    }

    @Test
    void shouldThrowExceptionWhenCustomerDoesNotExist() {

        when(customerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.findById(1L)
        );

        verify(customerRepository)
                .findById(1L);

        verify(customerMapper, never())
                .toResponse(any(Customer.class));
    }
}