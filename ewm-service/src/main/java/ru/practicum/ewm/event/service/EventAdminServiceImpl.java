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
import ru.practicum.ewm.event.dao.specification.EventAdminSpec;
import ru.practicum.ewm.event.dto.EventFullRespDto;
import ru.practicum.ewm.event.dto.EventRespDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.AdminStateAction;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.search.EventAdminParams;
import ru.practicum.ewm.event.service.checker.EventChecker;
import ru.practicum.ewm.exception.DataValidationException;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.common.PageParam.pageRequest;
import static ru.practicum.ewm.event.model.AdminStateAction.PUBLISH_EVENT;
import static ru.practicum.ewm.event.model.EventState.CANCELED;
import static ru.practicum.ewm.event.model.EventState.PUBLISHED;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventAdminServiceImpl implements EventAdminService {
    private static final Sort SORT_TYPE = Sort.by("id").descending();
    private static final Sort SORT_FOLLOWERS = Sort.by("eventDate").descending();
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventChecker checker;

    @Transactional(readOnly = true)
    public List<EventFullRespDto> searchEvents(EventAdminParams eventParams, PageParam pageParam) {
        Specification<Event> spec = EventAdminSpec.allParams(eventParams);
        List<Event> result = eventRepository.findAll(spec, pageRequest(pageParam, SORT_TYPE))
                .toList();

        log.debug("Events was founded: {}", result);
        return eventMapper.toEventsFullRespDto(result);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public EventFullRespDto editEvent(Long eventId, UpdateEventAdminDto eventDto) {
        // Check event date
        checker.checkEventDate(eventDto.getEventDate());

        // Find event
        var event = findEvent(eventId);

        // Check state
        if (event.getState() == PUBLISHED) {
            throw new DataValidationException("Only pending or canceled events can be changed");
        }
        if (eventDto.getStateAction() == PUBLISH_EVENT
                && event.getState() != EventState.PENDING) {
            throw new DataValidationException("Only pending events can be published");
        }

        // Check category if changed
        checker.checkCategory(event, eventDto.getCategory());

        // Update location if changed
        checker.checkLocation(event, eventDto.getLocation());

        // Update status
        changeStatus(eventDto.getStateAction(), event);

        // Update events
        eventMapper.updateEventFromDto(eventDto, event);

        log.debug("Events was updated: {}", event);
        return eventMapper.toEventFullRespDto(event);
    }

    public Event findEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("EventID %s does not exist", eventId)));

        log.debug("Event with id {} was found", eventId);
        return event;
    }

    public List<Event> findEvents(List<Long> ids) {
        return eventRepository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    public List<EventRespDto> findEventsByInitiator(Long initiatorId, PageParam pageParam) {
        var events = eventRepository.findByInitiator_IdAndState(initiatorId,
                PUBLISHED, pageRequest(pageParam, SORT_FOLLOWERS));

        log.debug("Events was found: {}", events);
        return eventMapper.toEventsRespDto(events);
    }

    @Transactional(readOnly = true)
    public List<EventRespDto> findEventsByInitiators(List<Long> initiatorsId, PageParam pageParam) {
        var events = eventRepository.findByInitiatorsIdAndState(initiatorsId,
                PUBLISHED, pageRequest(pageParam, SORT_FOLLOWERS));

        log.debug("Events was found: {}", events);
        return eventMapper.toEventsRespDto(events);
    }

    private static void changeStatus(AdminStateAction state, Event event) {
        switch (state) {
            case PUBLISH_EVENT:
                event.setState(PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
                break;
            case REJECT_EVENT:
                event.setState(CANCELED);
                break;
            default:
                throw new DataValidationException("Incorrect state action");
        }
    }
}
