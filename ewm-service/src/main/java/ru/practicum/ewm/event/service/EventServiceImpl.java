package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import ru.practicum.stat.client.StatClient;
import ru.practicum.stat.hit.HitDtoReq;
import ru.practicum.stat.view.ViewStatsDtoResp;
import ru.practicum.stat.view.ViewStatsParam;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.ewm.common.PageParam.pageRequest;
import static ru.practicum.ewm.event.model.EventState.PUBLISHED;
import static ru.practicum.ewm.event.model.search.EventParams.SortType.EVENT_DATE;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final RequestService requestService;
    private final EventMapper eventMapper;
    private final StatClient statClient;

    @Value("${app.name}")
    private String appName;

    @Transactional(readOnly = true)
    public List<EventRespDto> getAll(EventParams eventParams, PageParam pageParam, String uri, String ip) {
        //TODO
        //информация о каждом событии должна включать в себя количество просмотров
        Sort sortType;
        if (EVENT_DATE == eventParams.getSort()) {
            sortType = Sort.by("eventDate").descending();
        } else {
            //TODO количество просмотров
            sortType = Sort.by("viewCount").descending();
        }

        // Find events with params
        Specification<Event> spec = EventSpec.allParams(eventParams, PUBLISHED);
        List<Event> events = eventRepository.findAll(spec, pageRequest(pageParam, sortType))
                .toList();

        // Find requests
        List<EventRespDto> eventsDto = eventMapper.toEventsRespDto(events);
        mapRequestCount(eventsDto);

        // Show only available
        if (eventParams.getOnlyAvailable()) {
            Map<Long, Event> tmpEventMap = events.stream()
                    .collect(Collectors.toMap(Event::getId, event -> event));
            var result = eventsDto.stream()
                    .filter(event -> tmpEventMap.get(event.getId())
                            .getParticipantLimit() > event.getConfirmedRequests())
                    .collect(Collectors.toList());
            eventsDto = result;
        }

        // Add view
        statClient.addHit(createHitDtoReq(uri, ip));

        return eventsDto;
    }

    @Transactional(readOnly = true)
    public EventFullRespDto getById(Long id, String uri, String ip) {
        Event event = eventRepository.findByIdAndState(id, PUBLISHED)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Published eventID %s does not exist",
                        id)));
        log.debug("Event with id {} was found", id);

        // Count requests
        int reqCount = requestService.getReqCount(id);

        // Count views
        var statData = createViewStatsDtoReq(List.of(uri), event.getPublishedOn());
        var viewCount = statClient.getStat(statData)
                .stream()
                .filter(Objects::nonNull)
                .map(ViewStatsDtoResp::getHits)
                .count();

        // Add view
        statClient.addHit(createHitDtoReq(uri, ip));

        return eventMapper.toEventFullRespDto(event, reqCount, viewCount);
    }

    private HitDtoReq createHitDtoReq(String uri, String ip) {
        return new HitDtoReq()
                .setApp(appName)
                .setUri(uri)
                .setIp(ip)
                .setTimestamp(LocalDateTime.now());
    }

    private ViewStatsParam createViewStatsDtoReq(List<String> uris, LocalDateTime start) {
        return new ViewStatsParam()
                .setUris(uris)
                .setStart(start)
                .setEnd(LocalDateTime.now())
                .setUnique(true);
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
}
