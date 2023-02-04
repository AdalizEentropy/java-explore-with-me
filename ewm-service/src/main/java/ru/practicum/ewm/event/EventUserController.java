package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.PageParam;
import ru.practicum.ewm.event.model.EventRequestStatusReq;
import ru.practicum.ewm.event.model.EventRequestStatusResp;
import ru.practicum.ewm.event.dto.RequestRespDto;
import ru.practicum.ewm.event.dto.EventFullRespDto;
import ru.practicum.ewm.event.dto.EventRespDto;
import ru.practicum.ewm.event.dto.NewEventReqDto;
import ru.practicum.ewm.event.dto.UpdateEventReqDto;
import ru.practicum.ewm.event.service.EventUserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class EventUserController {
    private final EventUserService eventUserService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(code = HttpStatus.CREATED)
    public EventFullRespDto addEvent(@PathVariable Long userId,
                                     @Valid @NotNull @RequestBody NewEventReqDto newEventReqDto) {
        return eventUserService.addEvent(userId, newEventReqDto);
    }

    @GetMapping("/{userId}/events")
    public List<EventRespDto> getAllEvents(@PathVariable Long userId,
                                           @Valid PageParam pageParam) {
        return eventUserService.getAllEvents(userId, pageParam);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullRespDto getEvent(@PathVariable Long userId,
                                 @PathVariable Long eventId) {
        return eventUserService.getEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullRespDto updateEvent(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @Valid @NotNull @RequestBody UpdateEventReqDto updateEventReqDto) {
        return eventUserService.updateEvent(userId, eventId, updateEventReqDto);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<RequestRespDto> getEventRequests(@PathVariable Long userId,
                                                 @PathVariable Long eventId) {
        return eventUserService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusResp editEventRequests(@PathVariable Long userId,
                                                    @PathVariable Long eventId,
                                                    @NotNull @RequestBody EventRequestStatusReq eventRequestStatusReq) {
        return eventUserService.editEventRequests(userId, eventId, eventRequestStatusReq);
    }
}
