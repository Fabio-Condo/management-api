package org.fcb.service;

import org.fcb.dto.TransactionRequest;
import org.fcb.dto.TransactionResponse;
import org.fcb.dto.TransferRequest;

import java.util.List;

public interface TransactionService {

    TransactionResponse deposit(TransactionRequest request);

    TransactionResponse withdraw(TransactionRequest request);

    TransactionResponse transfer(TransferRequest request);

    List<TransactionResponse> findByAccountId(Long accountId);
}