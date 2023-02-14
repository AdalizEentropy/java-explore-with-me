package ru.practicum.stat.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.stat.exception.model.ErrorMessage;

import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
public class ExceptionApiHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ErrorMessage handleException(BindingResult bindingResult) {
        String error = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
        Object value = Objects.requireNonNull(bindingResult.getFieldError()).getRejectedValue();
        log.warn(String.format("%s. Current: %s", error, value));

        return getErrorMessage(BAD_REQUEST, error);
    }

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorMessage handleThrowable(final Throwable e) {
        String error = "Unexpected error";
        log.error(error);
        e.printStackTrace();

        return getErrorMessage(INTERNAL_SERVER_ERROR, error);
    }

    private ErrorMessage getErrorMessage(HttpStatus httpStatus, String error) {
        return new ErrorMessage(httpStatus.value(),
                error);
    }
}
