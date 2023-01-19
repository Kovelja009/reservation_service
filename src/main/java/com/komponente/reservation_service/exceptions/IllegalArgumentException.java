package com.komponente.reservation_service.exceptions;

import org.springframework.http.HttpStatus;

public class IllegalArgumentException extends CustomException{
    public IllegalArgumentException(String message) {
        super(message, ErrorCode.ILLEGAL_ARGUMENT, HttpStatus.BAD_REQUEST);
    }
}
