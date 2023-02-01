package ru.practicum.ewm.exception.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class ErrorMessage {
    private final String status;
    private final String reason;
    private final String message;
    private LocalDateTime timestamp = LocalDateTime.now();
}
