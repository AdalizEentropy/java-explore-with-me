package ru.practicum.ewm.event.service;

import ru.practicum.ewm.common.PageParam;
import ru.practicum.ewm.event.dto.EventFullRespDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.search.EventAdminParams;

import java.util.List;

public interface EventAdminService {

    List<EventFullRespDto> searchEvents(EventAdminParams eventParams, PageParam pageParam);

    EventFullRespDto editEvent(Long eventId, UpdateEventAdminDto eventDto);

    Event findEvent(Long eventId);

    List<Event> findEvents(List<Long> ids);
}
