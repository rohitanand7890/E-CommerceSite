package io.hireroo.ecsite.advice;

import io.hireroo.ecsite.exception.InsufficientItemStockException;
import io.hireroo.ecsite.exception.InsufficientUserBalanceException;
import io.hireroo.ecsite.exception.ItemNotFoundException;
import io.hireroo.ecsite.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> invalidArgumentHandler(MethodArgumentNotValidException exception) {
        Map<String, String> errorMap = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errorMap.put(error.getField(), error.getDefaultMessage()));
        return errorMap;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public Map<String, String> userNotFoundExceptionHandler(UserNotFoundException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error message", exception.getMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ItemNotFoundException.class)
    public Map<String, String> itemNotFoundExceptionHandler(ItemNotFoundException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error message", exception.getMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InsufficientItemStockException.class)
    public Map<String, String> insufficientStockExceptionHandler(InsufficientItemStockException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error message", exception.getMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InsufficientUserBalanceException.class)
    public Map<String, String> insufficientUserBalanceExceptionHandler(InsufficientUserBalanceException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error message", exception.getMessage());
        return errorMap;
    }
}
