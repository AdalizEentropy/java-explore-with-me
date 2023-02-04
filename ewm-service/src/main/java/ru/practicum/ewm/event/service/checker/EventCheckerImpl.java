package ru.practicum.ewm.event.service.checker;

import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.event.dto.LocationDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.DataValidationException;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class EventCheckerImpl implements EventChecker {
    private static final int HOURS_OFFSET = 2;
    private final CategoryService categoryService;

    public void checkCategory(Event event, Integer categoryId) {
        if (!categoryId.equals(event.getCategory().getId())) {
            var category = categoryService.getCategoryById(categoryId);
            event.setCategory(category);
        }
    }

    public void checkLocation(Event event, LocationDto locationDto) {
        if (locationDto.getLon().equals(event.getLocation().getLon()) ||
                locationDto.getLat().equals(event.getLocation().getLat())) {
            event.getLocation().setLon(locationDto.getLon());
            event.getLocation().setLat(locationDto.getLat());
        }
    }

    public void checkEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(HOURS_OFFSET))) {
            throw new DataValidationException(String.format(
                    "Event date must not be earlier that %s hours from now",
                    HOURS_OFFSET));
        }
    }
}
