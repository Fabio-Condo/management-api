package org.fcb.service.impl;

import org.fcb.domain.Account;
import org.fcb.domain.Transaction;
import org.fcb.dto.TransactionRequest;
import org.fcb.dto.TransactionResponse;
import org.fcb.dto.TransferRequest;
import org.fcb.dtoMapper.TransactionMapper;
import org.fcb.enumerations.TransactionType;
import org.fcb.exception.domain.BusinessException;
import org.fcb.exception.domain.ResourceNotFoundException;
import org.fcb.repository.AccountRepository;
import org.fcb.repository.TransactionRepository;
import org.fcb.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger =
            LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    @Transactional
    public TransactionResponse deposit(TransactionRequest request) {

        logger.info("Starting deposit. Account ID: {}", request.getAccountId());

        validatePositiveAmount(request.getAmount());

        Account account = findAccount(request.getAccountId());

        account.setBalance(
                account.getBalance()
                        .add(request.getAmount())
        );

        accountRepository.save(account);

        Transaction transaction = transactionMapper.toEntity(request);

        transaction.setAccount(account);
        transaction.setType(TransactionType.DEPOSIT);

        Transaction savedTransaction = transactionRepository.save(transaction);

        logger.info(
                "Deposit completed successfully. Transaction ID: {}, Account ID: {}",
                savedTransaction.getId(),
                account.getId()
        );

        return transactionMapper.toResponse(savedTransaction);
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(TransactionRequest request) {

        logger.info("Starting withdrawal. Account ID: {}", request.getAccountId());

        validatePositiveAmount(request.getAmount());

        Account account = findAccount(request.getAccountId());

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            logger.warn("Withdrawal rejected due to insufficient balance. Account ID: {}", account.getId());
            throw new BusinessException("Insufficient account balance");
        }

        account.setBalance(
                account.getBalance()
                        .subtract(request.getAmount())
        );

        accountRepository.save(account);

        Transaction transaction = transactionMapper.toEntity(request);

        transaction.setAccount(account);
        transaction.setType(TransactionType.WITHDRAWAL);

        Transaction savedTransaction = transactionRepository.save(transaction);

        logger.info(
                "Withdrawal completed successfully. Transaction ID: {}, Account ID: {}",
                savedTransaction.getId(),
                account.getId()
        );

        return transactionMapper.toResponse(savedTransaction);
    }

    @Override
    @Transactional
    public TransactionResponse transfer(TransferRequest request) {

        logger.info(
                "Starting transfer. Source account ID: {}, Target account ID: {}",
                request.getSourceAccountId(),
                request.getTargetAccountId()
        );

        validatePositiveAmount(request.getAmount());

        if (request.getSourceAccountId().equals(request.getTargetAccountId())) {
            logger.warn("Transfer rejected. Source and target accounts are the same. Account ID: {}", request.getSourceAccountId());
            throw new BusinessException("Source and target accounts must be different");
        }

        Account source = findAccount(request.getSourceAccountId());

        Account target = findAccount(request.getTargetAccountId());

        if (source.getBalance().compareTo(request.getAmount()) < 0) {
            logger.warn("Transfer rejected due to insufficient balance. Source account ID: {}", source.getId());
            throw new BusinessException("Insufficient account balance");
        }

        source.setBalance(
                source.getBalance()
                        .subtract(request.getAmount())
        );

        target.setBalance(
                target.getBalance()
                        .add(request.getAmount())
        );

        accountRepository.save(source);
        accountRepository.save(target);

        Transaction withdrawal = new Transaction(
                TransactionType.TRANSFER,
                request.getAmount(),
                source,
                request.getDescription()
        );

        Transaction deposit = new Transaction(
                TransactionType.TRANSFER,
                request.getAmount(),
                target,
                request.getDescription()
        );

        Transaction savedWithdrawal = transactionRepository.save(withdrawal);

        transactionRepository.save(deposit);

        logger.info(
                "Transfer completed successfully. Source account ID: {}, Target account ID: {}, Transaction ID: {}",
                source.getId(),
                target.getId(),
                savedWithdrawal.getId()
        );

        return transactionMapper.toResponse(savedWithdrawal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> findByAccountId(Long accountId) {

        logger.debug("Fetching transactions for account ID: {}", accountId);

        if (!accountRepository.existsById(accountId)) {
            logger.warn("Cannot fetch transactions. Account not found. Account ID: {}", accountId);
            throw new ResourceNotFoundException("Account not found with id: " + accountId);
        }

        List<Transaction> transactions = transactionRepository
                .findByAccountIdOrderByCreatedAtDesc(accountId);

        logger.info(
                "Transactions retrieved successfully. Account ID: {}, Total: {}",
                accountId,
                transactions.size()
        );

        return transactions.stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    private Account findAccount(Long accountId) {

        logger.debug("Finding account. Account ID: {}", accountId);

        return accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    logger.warn("Account not found. Account ID: {}", accountId);
                    return new ResourceNotFoundException("Account not found with id: " + accountId);
                });
    }

    private void validatePositiveAmount(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Transaction rejected. Amount must be greater than zero");
            throw new BusinessException("Amount must be greater than zero");
        }
    }
}