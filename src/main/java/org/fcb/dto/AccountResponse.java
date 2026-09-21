package org.fcb.dto;

import java.math.BigDecimal;

public class AccountResponse {

    private Long id;
    private String number;
    private BigDecimal balance;
    private CustomerSummaryResponse customer;

    public AccountResponse() {
    }

    public AccountResponse(Long id, String number, BigDecimal balance, CustomerSummaryResponse customer) {
        this.id = id;
        this.number = number;
        this.balance = balance;
        this.customer = customer;
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public CustomerSummaryResponse getCustomer() {
        return customer;
    }

}