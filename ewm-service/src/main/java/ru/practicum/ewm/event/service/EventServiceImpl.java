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
import ru.practicum.stat.client.StatClient;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static ru.practicum.ewm.common.PageParam.pageRequest;
import static ru.practicum.ewm.event.model.EventState.PUBLISHED;
import static ru.practicum.ewm.event.model.search.EventParams.SortType.EVENT_DATE;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final StatClient statClient;

    @Transactional(readOnly = true)
    public List<EventRespDto> getAll(EventParams eventParams, PageParam pageParam) {
        //текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
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

        return eventMapper.toEventsRespDto(result);
    }

    @Transactional(readOnly = true)
    public EventFullRespDto getById(Long id) {
        //информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов
        //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики

        Event event = eventRepository.findByIdAndState(id, PUBLISHED)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Published eventID %s does not exist",
                        id)));

        log.debug("Event with id {} was found", id);

//        statClient.addHit(new HitDtoReq());
        return eventMapper.toEventFullRespDto(event);
    }
}
