package org.fcb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.fcb.dto.TransactionRequest;
import org.fcb.dto.TransactionResponse;
import org.fcb.dto.TransferRequest;
import org.fcb.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Tag(
        name = "Transactions",
        description = "Deposit, withdrawal and transfer operations"
)
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(
            summary = "Deposit money",
            description = "Deposits money into an existing account."
    )
    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody TransactionRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService.deposit(request));
    }

    @Operation(
            summary = "Withdraw money",
            description = "Withdraws money from an account if sufficient balance is available."
    )
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@Valid @RequestBody TransactionRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService.withdraw(request));
    }

    @Operation(
            summary = "Transfer money",
            description = "Transfers money from one account to another."
    )
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService.transfer(request));
    }

    @Operation(
            summary = "Get account transactions",
            description = "Retrieves all transactions associated with an account, ordered from newest to oldest."
    )
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> findByAccountId(@PathVariable Long accountId) {

        return ResponseEntity.ok(transactionService.findByAccountId(accountId));
    }
}