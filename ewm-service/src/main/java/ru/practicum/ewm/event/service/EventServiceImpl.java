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
import java.util.HashMap;
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
    public List<EventRespDto> getAll(EventParams eventParams, PageParam pageParam) {
        //TODO
        //информация о каждом событии должна включать в себя количество просмотров и количество уже одобренных заявок на участие
        //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
        Sort sortType;
        if (EVENT_DATE == eventParams.getSort()) {
            sortType = Sort.by("eventDate").descending();
        } else {
            //TODO количество просмотров
            sortType = Sort.by("viewCount").descending();
        }

        Specification<Event> spec = EventSpec.allParams(eventParams, PUBLISHED);
        List<Event> result = eventRepository.findAll(spec, pageRequest(pageParam, sortType))
                .toList();

        List<Long> eventsId = result.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Integer> requestCount = new HashMap<>();
        requestService.getRequestCount(eventsId)
                .forEach(reqCount -> requestCount.put(reqCount.getEventId(), reqCount.getReqCount()));

        if (eventParams.getOnlyAvailable()) {
            result.stream()
                    .filter(event -> event.getParticipantLimit() > requestCount.get(event.getId()));
        }

        return eventMapper.toEventsRespDto(result);
    }

    @Transactional(readOnly = true)
    public EventFullRespDto getById(Long id, String uri, String ip) {
        //TODO refactor!!!

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
                .findFirst()
                .map(ViewStatsDtoResp::getHits);

        // Add view
        statClient.addHit(createHitDtoReq(uri, ip));

        return eventMapper.toEventFullRespDto(event, reqCount, viewCount.orElse(null));
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
}
