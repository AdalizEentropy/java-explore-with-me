package ru.practicum.ewm.event.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.request.model.RequestStatus;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EventRequestStatusReq {

    private List<Long> requestIds;
    private RequestStatus status;
}
