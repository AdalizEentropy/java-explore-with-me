package ru.practicum.stat.exception.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class ErrorMessage {
    private final Integer status;
    private final String error;
    private LocalDateTime timestamp = LocalDateTime.now();
}
