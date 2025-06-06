package br.com.marques.byteclass.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.CONFLICT)
public class AlreadyExistsException extends ResponseStatusException {
    public AlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
