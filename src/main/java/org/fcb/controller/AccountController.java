package org.fcb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.fcb.dto.AccountRequest;
import org.fcb.dto.AccountResponse;
import org.fcb.repository.filter.AccountFilter;
import org.fcb.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@Tag(
        name = "Accounts",
        description = "Account management operations"
)
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(
            summary = "Create a new account",
            description = "Creates a new bank account and associates it with an existing customer."
    )
    @PostMapping
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody AccountRequest request) {

        AccountResponse account = accountService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(account);
    }

    @Operation(
            summary = "Get all accounts",
            description = "Retrieves all registered accounts."
    )
    @GetMapping
    public ResponseEntity<List<AccountResponse>> findAll() {
        return ResponseEntity.ok(accountService.findAll());
    }

    @Operation(
            summary = "Filter accounts",
            description = "Retrieves accounts using filter criteria and pagination."
    )
    @GetMapping("/filter")
    public ResponseEntity<Page<AccountResponse>> filter(AccountFilter accountFilter, Pageable pageable) {
        return ResponseEntity.ok(accountService.filter(accountFilter, pageable));
    }

    @Operation(
            summary = "Get account by ID",
            description = "Retrieves a specific account using its unique identifier."
    )
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.findById(id));
    }

    @Operation(
            summary = "Get accounts by customer",
            description = "Retrieves all accounts associated with a specific customer."
    )
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountResponse>> findByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(accountService.findByCustomerId(customerId));
    }
}