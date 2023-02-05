package ru.practicum.ewm.request.service;

import ru.practicum.ewm.event.model.EventRequestStatusReq;
import ru.practicum.ewm.event.model.EventRequestStatusResp;
import ru.practicum.ewm.request.dto.RequestRespDto;
import ru.practicum.ewm.request.model.RequestCount;

import java.util.List;

public interface RequestService {

    List<RequestRespDto> getRequests(Long userId);

    RequestRespDto addRequest(Long userId, Long eventId);

    RequestRespDto cancelRequest(Long userId, Long requestId);

    List<RequestRespDto> getEventRequests(Long userId, Long eventId);

    EventRequestStatusResp editEventRequests(Long userId, Long eventId, EventRequestStatusReq eventStatus);

    List<RequestCount> getRequestCount(List<Long> eventId);

    int getReqCount(Long eventId);
}
