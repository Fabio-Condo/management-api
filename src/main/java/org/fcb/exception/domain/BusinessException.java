package org.fcb.exception.domain;

public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}