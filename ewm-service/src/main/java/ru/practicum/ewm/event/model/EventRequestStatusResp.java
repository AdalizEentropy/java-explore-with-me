package ru.practicum.ewm.event.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.practicum.ewm.request.dto.RequestRespDto;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class EventRequestStatusResp {

    private List<RequestRespDto> confirmedRequests;
    private List<RequestRespDto> rejectedRequests;
}
