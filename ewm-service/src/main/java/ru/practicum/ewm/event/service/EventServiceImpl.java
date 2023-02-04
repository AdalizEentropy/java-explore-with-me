package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.common.PageParam;
import ru.practicum.ewm.event.dao.EventRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.*;
import ru.practicum.ewm.exception.DataValidationException;
import ru.practicum.ewm.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.common.PageParam.pageRequest;
import static ru.practicum.ewm.event.dao.EventSpec.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final Sort SORT_TYPE = Sort.by("id").descending();
    private static final int HOURS_OFFSET = 2;
    private final EventRepository eventRepository;
    private final LocationService locationService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final EventMapper eventMapper;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public EventFullRespDto addEvent(Long userId, NewEventReqDto eventDto) {
        // Check event date
        checkEventDate(eventDto.getEventDate());

        // Check user
        var user = userService.getUserById(userId);

        // Check category
        var category = categoryService.getCategoryById(eventDto.getCategory());

        // Create location
        var location = locationService.addLocation(eventDto.getLocation());

        // Set created Time and State
        var event = eventMapper.toEvent(userId, location.getId(), eventDto);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);

        // Save event
        Event returnedEvent = eventRepository.save(event);
        returnedEvent.setInitiator(user);
        returnedEvent.setCategory(category);
        returnedEvent.setLocation(location);

        log.debug("Event saved: {}", returnedEvent);
        return eventMapper.toEventFullRespDto(returnedEvent);
    }

    @Transactional(readOnly = true)
    public List<EventRespDto> getAllEvents(Long userId, PageParam pageParam) {
        List<Event> foundEvents = eventRepository.findEventsByUser(userId, pageRequest(pageParam, SORT_TYPE));

        log.debug("Events found: {}", foundEvents);
        return eventMapper.toEventsRespDto(foundEvents);
    }

    @Transactional(readOnly = true)
    public EventFullRespDto getEvent(Long userId, Long eventId) {
        return eventMapper.toEventFullRespDto(findEventById(eventId, userId));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public EventFullRespDto updateEvent(Long userId, Long eventId, UpdateEventReqDto eventDto) {
        // Check event date
        checkEventDate(eventDto.getEventDate());

        // Find event
        var event = findEventById(eventId, userId);

        // Check state
        if (event.getState() == EventState.PUBLISHED) {
            throw new DataValidationException("Only pending or canceled events can be changed");
        }

        // Check category if changed
        checkCategory(event, eventDto.getCategory());

        // Update location if changed
        updateLocation(event, eventDto.getLocation());

        // Update event
        eventMapper.updateEventFromDto(eventDto, event);

        return eventMapper.toEventFullRespDto(event);
    }

    public List<RequestRespDto> getEventRequests(Long userId, Long eventId) {
        return null;
    }

    public EventRequestStatusResp editEventRequests(Long userId, Long eventId, EventRequestStatusReq eventRequestStatusReq) {
        return null;
    }

    public List<EventFullRespDto> searchEvents(EventParams eventParams, PageParam pageParam) {
        Specification<Event> spec = allParams(eventParams);
        List<Event> result = eventRepository.findAll(spec, pageRequest(pageParam, SORT_TYPE))
                .toList();

        return eventMapper.toEventsFullRespDto(result);
    }

    public EventFullRespDto editEvent(Long eventId, UpdateEventAdminDto eventDto) {
        // Check event date
        checkEventDate(eventDto.getEventDate());

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
        checkCategory(event, eventDto.getCategory());

        // Update location if changed
        updateLocation(event, eventDto.getLocation());

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

    private Event findEventById(Long eventId, Long userId) {
        Event event = eventRepository.findByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("EventID %s for userID %s does not exist",
                        eventId, userId)));

        log.debug("User event with id {} was found", eventId);
        return event;
    }

    private void checkCategory(Event event, Integer categoryId) {
        if (!categoryId.equals(event.getCategory().getId())) {
            var category = categoryService.getCategoryById(categoryId);
            event.setCategory(category);
        }
    }

    private static void updateLocation(Event event, LocationDto locationDto) {
        if (locationDto.getLon().equals(event.getLocation().getLon()) ||
                locationDto.getLat().equals(event.getLocation().getLat())) {
            event.getLocation().setLon(locationDto.getLon());
            event.getLocation().setLat(locationDto.getLat());
        }
    }

    private static void checkEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(HOURS_OFFSET))) {
            throw new DataValidationException(String.format(
                    "Event date must not be earlier that %s hours from now",
                    HOURS_OFFSET));
        }
    }
}
