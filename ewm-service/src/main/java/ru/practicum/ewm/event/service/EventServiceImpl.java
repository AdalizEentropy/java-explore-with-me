package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.PageParam;
import ru.practicum.ewm.event.dao.EventRepository;
import ru.practicum.ewm.event.dao.specification.EventSpec;
import ru.practicum.ewm.event.dto.EventFullRespDto;
import ru.practicum.ewm.event.dto.EventRespDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.search.EventParams;
import ru.practicum.ewm.request.service.RequestService;
import ru.practicum.ewm.statistic.service.StatisticService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewm.common.PageParam.pageRequest;
import static ru.practicum.ewm.event.model.EventState.PUBLISHED;
import static ru.practicum.ewm.event.model.search.EventParams.SortType.VIEWS;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final String URI = "/events/";
    private static final int MAX_YEARS_OFFSET = 2;
    private final EventRepository eventRepository;
    private final RequestService requestService;
    private final StatisticService statisticService;
    private final EventMapper eventMapper;

    @Transactional(readOnly = true)
    public List<EventRespDto> getAll(EventParams eventParams, PageParam pageParam) {
        log.debug("Get views with params: {}, {}", eventParams, pageParam);
        var sortType = Sort.by("eventDate").descending();

        // Find events with params
        Specification<Event> spec = EventSpec.allParams(eventParams, PUBLISHED);
        List<Event> events = eventRepository.findAll(spec, pageRequest(pageParam, sortType))
                .toList();

        // Find requests
        List<EventRespDto> eventsDto = eventMapper.toEventsRespDto(events);
        mapRequestCount(eventsDto);

        // Show only available
        if (eventParams.getOnlyAvailable()) {
            log.debug("Take only available events");
            Map<Long, Event> tmpEventMap = events.stream()
                    .collect(Collectors.toMap(Event::getId, event -> event));
            eventsDto = eventsDto.stream()
                    .filter(event -> tmpEventMap.get(event.getId())
                            .getParticipantLimit() > event.getConfirmedRequests())
                    .collect(Collectors.toList());
        }

        // Find views
        var views = getViewsCount(events);
        eventsDto.forEach(e -> e.setViews(views.get(e.getId())));

        if (eventParams.getSort() == VIEWS) {
            log.debug("Sort events by views");
            eventsDto = eventsDto.stream()
                    .sorted(Comparator.comparing(EventRespDto::getViews))
                    .collect(Collectors.toList());
        }

        log.debug("Return events: {}", eventsDto);
        return eventsDto;
    }

    @Transactional(readOnly = true)
    public EventFullRespDto getById(Long id) {
        Event event = eventRepository.findByIdAndState(id, PUBLISHED)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Published eventID %s does not exist",
                        id)));
        log.debug("Event with id {} was found", id);

        // Count requests
        int reqCount = requestService.getReqCount(id);

        // Count views
        Long viewCount = statisticService.getViewCountsForOne(List.of(URI + id), event.getPublishedOn());

        var respDto = eventMapper.toEventFullRespDto(event, reqCount, viewCount);
        log.debug("Return event: {}", respDto);
        return respDto;
    }

    private void mapRequestCount(List<EventRespDto> events) {
        Map<Long, EventRespDto> tmpEventMap = events.stream()
                .collect(Collectors.toMap(EventRespDto::getId, event -> event));

        List<Long> eventsId = events.stream()
                .map(EventRespDto::getId)
                .collect(Collectors.toList());

        requestService.getRequestCount(eventsId)
                .forEach(c -> tmpEventMap.get(c.getEventId())
                        .setConfirmedRequests(c.getReqCount()));
    }

    private Map<Long, Long> getViewsCount(List<Event> events) {
        // Prepare data for stats
        List<String> uris = events.stream()
                .map(Event::getId)
                .map(id -> String.valueOf(URI + id))
                .collect(Collectors.toList());
        Optional<LocalDateTime> startTime = events.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder());

        return statisticService.getMapViewCounts(uris, startTime.orElseGet(() -> LocalDateTime
                .now()
                .minusYears(MAX_YEARS_OFFSET)));
    }
}
