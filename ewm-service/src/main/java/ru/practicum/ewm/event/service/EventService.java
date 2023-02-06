package ru.practicum.ewm.event.service;

import ru.practicum.ewm.common.PageParam;
import ru.practicum.ewm.event.dto.EventFullRespDto;
import ru.practicum.ewm.event.dto.EventRespDto;
import ru.practicum.ewm.event.model.search.EventParams;

import java.util.List;

public interface EventService {

    List<EventRespDto> getAll(EventParams eventParams, PageParam pageParam,
                              String uri, String ip);

    EventFullRespDto getById(Long id, String uri, String ip);
}
