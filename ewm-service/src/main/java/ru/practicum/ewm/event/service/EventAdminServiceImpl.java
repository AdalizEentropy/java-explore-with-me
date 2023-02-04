package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.PageParam;
import ru.practicum.ewm.event.dao.EventRepository;
import ru.practicum.ewm.event.dto.EventFullRespDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventParams;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.StateAction;
import ru.practicum.ewm.event.service.checker.EventChecker;
import ru.practicum.ewm.exception.DataValidationException;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static ru.practicum.ewm.common.PageParam.pageRequest;
import static ru.practicum.ewm.event.dao.EventSpec.allParams;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventAdminServiceImpl implements EventAdminService {
    private static final Sort SORT_TYPE = Sort.by("id").descending();
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventChecker checker;

    @Transactional(readOnly = true)
    public List<EventFullRespDto> searchEvents(EventParams eventParams, PageParam pageParam) {
        Specification<Event> spec = allParams(eventParams);
        List<Event> result = eventRepository.findAll(spec, pageRequest(pageParam, SORT_TYPE))
                .toList();

        return eventMapper.toEventsFullRespDto(result);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public EventFullRespDto editEvent(Long eventId, UpdateEventAdminDto eventDto) {
        // Check event date
        checker.checkEventDate(eventDto.getEventDate());

        // Find event
        var event = findEvent(eventId);

        // Check state
        if (event.getState() == EventState.PUBLISHED) {
            throw new DataValidationException("Only pending or canceled events can be changed");
        }
        if (eventDto.getStateAction() == StateAction.PUBLISH_EVENT
                && event.getState() != EventState.PENDING) {
            throw new DataValidationException("Only pending events can be published");
        }

        // Check category if changed
        checker.checkCategory(event, eventDto.getCategory());

        // Update location if changed
        checker.checkLocation(event, eventDto.getLocation());

        // Update events
        eventMapper.updateEventFromDto(eventDto, event);

        return eventMapper.toEventFullRespDto(event);
    }

    private Event findEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("EventID %s does not exist", eventId)));

        log.debug("Event with id {} was found", eventId);
        return event;
    }
}
