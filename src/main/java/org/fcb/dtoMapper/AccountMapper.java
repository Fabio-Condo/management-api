package org.fcb.dtoMapper;

import org.fcb.domain.Account;
import org.fcb.domain.Customer;
import org.fcb.dto.AccountRequest;
import org.fcb.dto.AccountResponse;
import org.fcb.dto.CustomerSummaryResponse;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public Account toEntity(AccountRequest request) {

        Customer customer = new Customer();

        customer.setId(request.getCustomerId());

        Account account = new Account();

        account.setNumber(request.getNumber());
        account.setCustomer(customer);

        return account;
    }

    public AccountResponse toResponse(Account account) {

        Customer customer = account.getCustomer();

        CustomerSummaryResponse customerResponse =
                new CustomerSummaryResponse(
                        customer.getId(),
                        customer.getName(),
                        customer.getEmail()
                );

        return new AccountResponse(
                account.getId(),
                account.getNumber(),
                account.getBalance(),
                customerResponse
        );
    }
}