package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.exception.model.ErrorMessage;

import javax.persistence.EntityNotFoundException;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class ExceptionApiHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ErrorMessage handleException(BindingResult bindingResult) {
        var fieldErrorResult = Objects.requireNonNull(bindingResult.getFieldError());
        String field = fieldErrorResult.getField();
        String error = fieldErrorResult.getDefaultMessage();
        Object value = fieldErrorResult.getRejectedValue();

        var message = String.format("Field: %s. Error: %s. Value: %s", field, error, value);
        log.warn(message);

        return getErrorMessage(BAD_REQUEST, "Incorrectly made request.", message);
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleDataForbiddenException(final MissingServletRequestParameterException e) {
        String message = Objects.requireNonNull(e.getMessage());
        log.warn(message);

        return getErrorMessage(BAD_REQUEST, "Incorrectly made request.", message);
    }

    @ExceptionHandler({DataIntegrityViolationException.class, DataValidationException.class})
    @ResponseStatus(CONFLICT)
    public ErrorMessage handleDataForbiddenException(final Exception e) {
        String message = Objects.requireNonNull(e.getMessage());
        log.warn(message);

        return getErrorMessage(CONFLICT, "Integrity constraint has been violated.", message);
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public ErrorMessage handleEntityNotFoundException(final EntityNotFoundException e) {
        String message = Objects.requireNonNull(e.getMessage());
        log.warn(message);

        return getErrorMessage(NOT_FOUND, "The required object was not found.", message);
    }

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorMessage handleThrowable(final Throwable e) {
        String message = "Unexpected error";
        log.error(message);
        e.printStackTrace();

        return getErrorMessage(INTERNAL_SERVER_ERROR, "", message);
    }

    private ErrorMessage getErrorMessage(HttpStatus httpStatus, String reason, String message) {
        return new ErrorMessage(httpStatus.name(),
                reason,
                message);
    }
}
