package br.com.marques.byteclass.common.exception;

import org.springframework.http.HttpStatus;

public class GenericException extends RuntimeException{
    private final HttpStatus status;
    public GenericException(String message, HttpStatus status){
        super(message);
        this.status = status;
    }

    public HttpStatus getStatusCode() {
        return this.status;
    }
}
