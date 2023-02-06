package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.stream.Collectors;

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

        request.setStatus(CANCELED);

        return mapper.toRequestRespDto(request);
    }

    @Transactional(readOnly = true)
    public List<RequestRespDto> getEventRequests(Long userId, Long eventId) {
        return mapper.toRequestsRespDto(requestRepository.findAllByEvent_Id(eventId));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public EventRequestStatusResp editEventRequests(Long userId, Long eventId, EventRequestStatusReq eventStatus) {
        // Check event
        var event = eventService.findEvent(eventId);

        // Limit was reached
        if (eventStatus.getStatus() == CONFIRMED
                && event.getParticipantLimit().equals(getReqCount(eventId))) {
            throw new DataValidationException("Event participant limit was reached");
        }

        // Confirmation not necessary
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new DataValidationException("Confirmation not necessary");
        }

        List<Request> requests = requestRepository.findAllById(eventStatus.getRequestIds());

        if (requests.stream()
                .anyMatch(req -> req.getStatus() != PENDING)) {
            throw new DataValidationException("Request status could not be changed");
        }

        // Confirm until limit
        switch (eventStatus.getStatus()) {
            case CONFIRMED:
                int limit = event.getParticipantLimit() - getReqCount(eventId);
                confirmRequests(requests, limit);
                break;
            case REJECTED:
                rejectRequests(requests);
                break;
            default:
                throw new DataValidationException("Unexpected event status");
        }

        return fillEventReqStatus(requests);
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

    private static void confirmRequests(List<Request> requests, int limit) {
        int limitLeft = limit;
        for (Request request : requests) {
            if (limitLeft >= 0) {
                request.setStatus(CONFIRMED);
                limitLeft--;
            } else {
                request.setStatus(REJECTED);
            }
        }
    }

    private static void rejectRequests(List<Request> requests) {
        requests.forEach(req -> req.setStatus(REJECTED));
    }

    private EventRequestStatusResp fillEventReqStatus(List<Request> requests) {
        var confirmed = requests.stream()
                .filter(req -> req.getStatus() == CONFIRMED)
                .collect(Collectors.toList());

        var rejected = requests.stream()
                .filter(req -> req.getStatus() == REJECTED)
                .collect(Collectors.toList());

        return new EventRequestStatusResp()
                .setConfirmedRequests(mapper.toRequestsRespDto(confirmed))
                .setRejectedRequests(mapper.toRequestsRespDto(rejected));
    }
}
