package org.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LeaveLeftNotEnoughException extends RuntimeException {
    public LeaveLeftNotEnoughException(String message) {
        super(message);
    }
}
