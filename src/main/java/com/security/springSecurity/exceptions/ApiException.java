package com.security.springSecurity.exceptions;


import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApiException extends RuntimeException {

    private final String message;

    private final HttpStatus httpStatus;


    public ApiException(String message, HttpStatus httpStatus) {
        super();
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public ApiException(HttpStatus httpStatus, String message, Throwable exception) {
        super(exception);
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
