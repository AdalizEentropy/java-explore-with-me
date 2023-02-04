package ru.practicum.ewm.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.event.model.EventState;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RequestRespDto {
    private Long id;
    private LocalDateTime created;
    private Long event;
    private Long requester;
    private EventState status;
}
