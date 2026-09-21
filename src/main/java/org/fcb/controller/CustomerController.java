package org.fcb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.fcb.dto.CustomerRequest;
import org.fcb.dto.CustomerResponse;
import org.fcb.dto.HttpResponse;
import org.fcb.dto.PageResponse;
import org.fcb.repository.filter.CustomerFilter;
import org.fcb.service.CustomerService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Tag(
        name = "Customers",
        description = "Customer management operations"
)
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(
            summary = "Create a new customer",
            description = "Creates a new customer with name, email and phone information."
    )
    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {

        CustomerResponse customer = customerService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customer);
    }

    @Operation(
            summary = "Get all customers",
            description = "Retrieves all registered customers."
    )
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> findAll() {
        return ResponseEntity.ok(customerService.findAll());
    }

    @Operation(
            summary = "Filter customers",
            description = "Retrieves customers using filter criteria and pagination."
    )
    @GetMapping("/filter")
    public ResponseEntity<PageResponse<CustomerResponse>> filter(CustomerFilter customerFilter, Pageable pageable) {
        return ResponseEntity.ok(customerService.filter(customerFilter, pageable));
    }

    @Operation(
            summary = "Get customer by ID",
            description = "Retrieves a specific customer using its unique identifier."
    )
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @Operation(
            summary = "Update customer",
            description = "Updates the information of an existing customer."
    )
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.update(id, request));
    }

    @Operation(
            summary = "Delete customer",
            description = "Deletes an existing customer using its unique identifier."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        customerService.delete(id);

        return response(
                HttpStatus.OK,
                "Customer deleted successfully"
        );
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {

        return new ResponseEntity<>(
                new HttpResponse(
                        httpStatus.value(),
                        httpStatus,
                        httpStatus.getReasonPhrase().toUpperCase(),
                        message
                ),
                httpStatus
        );
    }
}