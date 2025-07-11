package org.project.apiaddvice;

import org.project.exception.ErrorResponse;
import org.project.exception.ResourceNotFoundException;
import org.project.exception.page.InvalidPageException;
import org.project.exception.page.PageNotFoundException;
import org.project.exception.sql.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ErrorResponse.class)
    public ResponseEntity<String> handleErrorResponse(ErrorResponse e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(InvalidPageException.class)
    public ResponseEntity<String> handleInvalidPageException(InvalidPageException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(exception = {
            PageNotFoundException.class,
            EntityNotFoundException.class,
            ResourceNotFoundException.class
    })
    public ResponseEntity<String> handlePageNotFoundException(PageNotFoundException e) {
        return ResponseEntity.status(404).body("Không tìm thấy");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(errorMessage);
    }
}
