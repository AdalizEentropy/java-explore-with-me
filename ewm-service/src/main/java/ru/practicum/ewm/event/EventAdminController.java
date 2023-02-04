package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.PageParam;
import ru.practicum.ewm.event.dto.EventFullRespDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminDto;
import ru.practicum.ewm.event.model.EventParams;
import ru.practicum.ewm.event.service.EventAdminService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class EventAdminController {
    private final EventAdminService eventService;

    @GetMapping()
    public List<EventFullRespDto> searchEvents(EventParams eventParams,
                                               @Valid PageParam pageParam) {
        return eventService.searchEvents(eventParams, pageParam);
    }

    @PatchMapping("/{eventId}")
    public EventFullRespDto editEvent(@PathVariable Long eventId,
                                      @NotNull @RequestBody UpdateEventAdminDto eventDto) {
        return eventService.editEvent(eventId, eventDto);
    }
}
