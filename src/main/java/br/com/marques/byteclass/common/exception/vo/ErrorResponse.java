package br.com.marques.byteclass.common.exception.vo;

import org.springframework.http.HttpStatusCode;

import java.util.List;


public record ErrorResponse(String message, HttpStatusCode statusCode, List<ValidationError> errors) {
    public ErrorResponse(String message, HttpStatusCode statusCode) {
        this(message, statusCode, null);
    }
}

