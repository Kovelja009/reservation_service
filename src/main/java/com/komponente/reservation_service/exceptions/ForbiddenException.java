package com.komponente.reservation_service.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends CustomException{
    public ForbiddenException(String message) {
        super(message, ErrorCode.FORBIDDEN, HttpStatus.FORBIDDEN);
    }
}
