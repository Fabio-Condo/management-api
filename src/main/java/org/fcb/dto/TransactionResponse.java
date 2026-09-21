package org.fcb.dto;


import org.fcb.enumerations.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {

    private Long id;
    private TransactionType type;
    private BigDecimal amount;
    private Long accountId;
    private String accountNumber;
    private String description;
    private LocalDateTime createdAt;

    public TransactionResponse() {
    }

    public TransactionResponse(Long id, TransactionType type, BigDecimal amount, Long accountId, String accountNumber, String description, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}