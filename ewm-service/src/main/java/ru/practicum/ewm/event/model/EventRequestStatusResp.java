package ru.practicum.ewm.event.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.event.dto.RequestRespDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EventRequestStatusResp {

    private List<RequestRespDto> confirmedRequests;
    private List<RequestRespDto> rejectedRequests;
}
