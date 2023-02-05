package ru.practicum.ewm.event.service;

import ru.practicum.ewm.common.PageParam;
import ru.practicum.ewm.event.dto.EventFullRespDto;
import ru.practicum.ewm.event.dto.EventRespDto;
import ru.practicum.ewm.event.dto.NewEventReqDto;
import ru.practicum.ewm.event.dto.UpdateEventReqDto;

import java.util.List;

public interface EventUserService {

    EventFullRespDto addEvent(Long userId, NewEventReqDto eventDto);

    List<EventRespDto> getAllEvents(Long userId, PageParam pageParam);

    EventFullRespDto getEvent(Long userId, Long eventId);

    EventFullRespDto updateEvent(Long userId, Long eventId, UpdateEventReqDto eventDto);
}
