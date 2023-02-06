package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.common.PageParam;
import ru.practicum.ewm.event.dto.EventFullRespDto;
import ru.practicum.ewm.event.dto.EventRespDto;
import ru.practicum.ewm.event.model.search.EventParams;
import ru.practicum.ewm.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping
    public List<EventRespDto> getAll(EventParams eventParams,
                                     @Valid PageParam pageParam) {
        return eventService.getAll(eventParams, pageParam);
    }

    @GetMapping("/{id}")
    public EventFullRespDto getById(@PathVariable Long id,
                                    HttpServletRequest request) {
        return eventService.getById(id, request.getRequestURI(), request.getRemoteAddr());
    }
}
