package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.common.PageParam;
import ru.practicum.ewm.event.dao.EventRepository;
import ru.practicum.ewm.event.dto.EventFullRespDto;
import ru.practicum.ewm.event.dto.EventRespDto;
import ru.practicum.ewm.event.dto.NewEventReqDto;
import ru.practicum.ewm.event.dto.UpdateEventReqDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.service.checker.EventChecker;
import ru.practicum.ewm.exception.DataValidationException;
import ru.practicum.ewm.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.common.PageParam.pageRequest;
import static ru.practicum.ewm.event.model.EventState.CANCELED;
import static ru.practicum.ewm.event.model.EventState.PENDING;
import static ru.practicum.ewm.event.model.StateAction.CANCEL_REVIEW;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventUserServiceImpl implements EventUserService {
    private static final Sort SORT_TYPE = Sort.by("id").descending();
    private final EventRepository eventRepository;
    private final LocationService locationService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final EventMapper eventMapper;
    private final EventChecker checker;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public EventFullRespDto addEvent(Long userId, NewEventReqDto eventDto) {
        // Check event date
        checker.checkEventDate(eventDto.getEventDate());

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
        checker.checkEventDate(eventDto.getEventDate());

        // Find event
        var event = findEventById(eventId, userId);

        // Check state
        if (event.getState() == EventState.PUBLISHED) {
            throw new DataValidationException("Only pending or canceled events can be changed");
        }

        // Check category if changed
        checker.checkCategory(event, eventDto.getCategory());

        // Update location if changed
        checker.checkLocation(event, eventDto.getLocation());

        // Update event
        if (eventDto.getStateAction() == CANCEL_REVIEW) {
            event.setState(CANCELED);
        } else {
            event.setState(PENDING);
        }
        eventMapper.updateEventFromDto(eventDto, event);

        return eventMapper.toEventFullRespDto(event);
    }

    private Event findEventById(Long eventId, Long userId) {
        Event event = eventRepository.findByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("EventID %s for userID %s does not exist",
                        eventId, userId)));

        log.debug("User event with id {} was found", eventId);
        return event;
    }
}
