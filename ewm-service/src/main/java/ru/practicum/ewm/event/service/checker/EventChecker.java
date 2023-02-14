package ru.practicum.ewm.event.service.checker;

import ru.practicum.ewm.event.dto.LocationDto;
import ru.practicum.ewm.event.model.Event;

import java.time.LocalDateTime;

public interface EventChecker {

    void checkCategory(Event event, Integer categoryId);

    void checkLocation(Event event, LocationDto locationDto);

    void checkEventDate(LocalDateTime eventDate);
}
