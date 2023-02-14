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
import ru.practicum.ewm.statistic.service.StatisticService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final StatisticService statisticService;

    @GetMapping
    public List<EventRespDto> getAll(EventParams eventParams,
                                     @Valid PageParam pageParam,
                                     HttpServletRequest request) {
        var events = eventService.getAll(eventParams, pageParam);
        statisticService.addView(request.getRequestURI(), request.getRemoteAddr());
        return events;
    }

    @GetMapping("/{id}")
    public EventFullRespDto getById(@PathVariable Long id,
                                    HttpServletRequest request) {
        var event = eventService.getById(id);
        statisticService.addView(request.getRequestURI(), request.getRemoteAddr());
        return event;
    }
}
