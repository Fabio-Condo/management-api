package org.fcb.dtoMapper;

import org.fcb.domain.Account;
import org.fcb.domain.Transaction;
import org.fcb.dto.TransactionRequest;
import org.fcb.dto.TransactionResponse;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public Transaction toEntity(TransactionRequest request) {

        Account account = new Account();
        account.setId(request.getAccountId());

        Transaction transaction = new Transaction();

        transaction.setAccount(account);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());

        return transaction;
    }

    public TransactionResponse toResponse(Transaction transaction) {

        Account account = transaction.getAccount();

        return new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                account.getId(),
                account.getNumber(),
                transaction.getDescription(),
                transaction.getCreatedAt()
        );
    }
}