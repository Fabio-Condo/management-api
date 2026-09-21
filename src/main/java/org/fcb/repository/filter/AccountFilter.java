package org.fcb.repository.filter;

public class AccountFilter {

    private String number;

    private String sortBy;


    public AccountFilter(String number, String sortBy) {
        this.number = number;
        this.sortBy = sortBy;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
