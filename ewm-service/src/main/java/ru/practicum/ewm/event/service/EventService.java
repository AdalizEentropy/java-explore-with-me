package ru.practicum.ewm.event.service;

import ru.practicum.ewm.common.PageParam;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.EventParams;
import ru.practicum.ewm.event.model.EventRequestStatusReq;
import ru.practicum.ewm.event.model.EventRequestStatusResp;

import java.util.List;

public interface EventService {

    EventFullRespDto addEvent(Long userId, NewEventReqDto eventDto);

    List<EventRespDto> getAllEvents(Long userId, PageParam pageParam);

    EventFullRespDto getEvent(Long userId, Long eventId);

    EventFullRespDto updateEvent(Long userId, Long eventId, UpdateEventReqDto eventDto);

    List<RequestRespDto> getEventRequests(Long userId, Long eventId);

    EventRequestStatusResp editEventRequests(Long userId, Long eventId, EventRequestStatusReq eventRequestStatusReq);

    List<EventFullRespDto> searchEvents(EventParams eventParams, PageParam pageParam);

    EventFullRespDto editEvent(Long eventId, UpdateEventAdminDto eventDto);
}
