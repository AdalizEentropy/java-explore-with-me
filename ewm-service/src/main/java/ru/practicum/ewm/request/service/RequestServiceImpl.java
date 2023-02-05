package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventRequestStatusReq;
import ru.practicum.ewm.event.model.EventRequestStatusResp;
import ru.practicum.ewm.event.service.EventAdminService;
import ru.practicum.ewm.exception.DataValidationException;
import ru.practicum.ewm.request.dao.RequestRepository;
import ru.practicum.ewm.request.dto.RequestRespDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestCount;
import ru.practicum.ewm.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.event.model.EventState.PUBLISHED;
import static ru.practicum.ewm.request.model.RequestStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final UserService userService;
    private final EventAdminService eventService;
    private final RequestRepository requestRepository;
    private final RequestMapper mapper;

    @Transactional(readOnly = true)
    public List<RequestRespDto> getRequests(Long userId) {
        // Check user
        userService.getUserById(userId);

        return mapper.toRequestsRespDto(requestRepository.findAllByRequester_Id(userId));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public RequestRespDto addRequest(Long userId, Long eventId) {
        // Check user
        var user = userService.getUserById(userId);

        // Check duplicate
        findDuplRequest(userId, eventId);

        // Check event
        var event = eventService.findEvent(eventId);
        if (userId.equals(event.getInitiator().getId())) {
            throw new DataValidationException("Event initiator could not add request");
        }
        if (event.getState() != PUBLISHED) {
            throw new DataValidationException("Event hasn't published");
        }
        if (event.getParticipantLimit().equals(getReqCount(eventId))) {
            throw new DataValidationException("Event participant limit was reached");
        }

        // Create request
        Request request = new Request()
                .setRequester(user)
                .setEvent(event)
                .setCreated(LocalDateTime.now())
                .setStatus(!event.getRequestModeration() || event.getParticipantLimit() == 0 ? CONFIRMED : PENDING);

        var returnedRequest = requestRepository.save(request);

        return mapper.toRequestRespDto(returnedRequest);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public RequestRespDto cancelRequest(Long userId, Long requestId) {
        var request = requestRepository.findByIdAndRequester_Id(requestId, userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("RequestId %s does not exist",
                        requestId)));

        request.setStatus(REJECTED);

        return mapper.toRequestRespDto(request);
    }

    @Transactional(readOnly = true)
    public List<RequestRespDto> getEventRequests(Long userId, Long eventId) {
        // Check event initiator
        var event = eventService.findEvent(eventId);
        checkInitiatorEvent(userId, event);

        return mapper.toRequestsRespDto(requestRepository.findAllByEvent_Id(eventId));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public EventRequestStatusResp editEventRequests(Long userId, Long eventId, EventRequestStatusReq eventStatus) {
        // Check event
        var event = eventService.findEvent(eventId);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            //TODO что возвращать?
            return null;
        }
        if (eventStatus.getStatus() == CONFIRMED
                && event.getParticipantLimit().equals(getReqCount(eventId))) {
            throw new DataValidationException("Event participant limit was reached");
        }

        /*List<Request> requests = requestRepository.findAllById(eventStatus.getRequestIds());

        var limitLeft = new AtomicInteger(event.getParticipantLimit()-getReqCount(eventId));
        Map<Long, Integer> lim = new HashMap<>();
        for (Request req : requests) {
            lim.put(req.getId(), limitLeft.decrementAndGet());
        }

        var ll = List.of(lim).stream().map(m -> m.values().stream().filter(v -> v > 0)).collect(Collectors.toList());


        Map<Long, AtomicInteger> EVENT_REQUESTS_LEFT = new HashMap<>();
        requests.forEach(r -> EVENT_REQUESTS_LEFT.put(r.getId(), null));

        List.of(EVENT_REQUESTS_LEFT).stream()
                .map(m -> m.put())

        var values = requests.stream()
                .filter(r -> r.getStatus() == PENDING)
                .flatMap(r -> limitLeft.decrementAndGet())
                .allMatch(v -> v >= 0);

        EVENT_REQUESTS_LEFT.putIfAbsent(eventId, new AtomicInteger(event.getParticipantLimit()-getReqCount(eventId)));*/

        return null;
    }

    public List<RequestCount> getRequestCount(List<Long> eventId) {
        return requestRepository.findRequestCount(eventId, CONFIRMED);
    }

    public int getReqCount(Long eventId) {
        return requestRepository.countAllByEvent_IdAndStatus(eventId, CONFIRMED);
    }

    private void findDuplRequest(Long requesterId, Long eventId) {
        var request = requestRepository.findByRequester_IdAndEvent_Id(requesterId, eventId);

        if (request.isPresent()) {
            throw new DataValidationException(String.format(
                    "Request to event %s from user %s already exist", eventId, requesterId));
        }
    }

    private static void checkInitiatorEvent(Long userId, Event event) {
        if (userId.equals(event.getInitiator().getId())) {
            throw new DataValidationException(String.format("UserId %s does not have eventId %s",
                    userId,
                    event.getId()));
        }
    }
}
