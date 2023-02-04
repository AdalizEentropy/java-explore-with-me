package ru.practicum.ewm.event.service;

import ru.practicum.ewm.common.PageParam;
import ru.practicum.ewm.event.dto.EventFullRespDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminDto;
import ru.practicum.ewm.event.model.EventParams;

import java.util.List;

public interface EventAdminService {

    List<EventFullRespDto> searchEvents(EventParams eventParams, PageParam pageParam);

    EventFullRespDto editEvent(Long eventId, UpdateEventAdminDto eventDto);
}
