package ru.practicum.ewm.request.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.request.model.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RequestRespDto {
    private Long id;
    private LocalDateTime created;
    private Long event;
    private Long requester;
    private RequestStatus status;
}
