package org.fcb.dto;

public class CustomerSummaryResponse {

    private Long id;
    private String name;
    private String email;

    public CustomerSummaryResponse() {
    }

    public CustomerSummaryResponse(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}