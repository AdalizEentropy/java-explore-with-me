package ru.practicum.ewm.event.service.checker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.event.dto.LocationDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.DataValidationException;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
@Slf4j
public class EventCheckerImpl implements EventChecker {
    private static final int HOURS_OFFSET = 2;
    private final CategoryService categoryService;

    public void checkCategory(Event event, Integer categoryId) {
        if (categoryId != null && !categoryId.equals(event.getCategory().getId())) {
            var category = categoryService.getCategoryById(categoryId);
            event.setCategory(category);
            log.debug("Event category was updated");
        }
    }

    public void checkLocation(Event event, LocationDto locationDto) {
        if (locationDto != null) {
            if (!locationDto.getLon().equals(event.getLocation().getLon())) {
                event.getLocation().setLon(locationDto.getLon());
                log.debug("Event lon was updated");
            }
            if (!locationDto.getLat().equals(event.getLocation().getLat())) {
                event.getLocation().setLat(locationDto.getLat());
                log.debug("Event lat was updated");
            }
        }
    }

    public void checkEventDate(LocalDateTime eventDate) {
        if (eventDate != null
                && eventDate.isBefore(LocalDateTime.now().plusHours(HOURS_OFFSET))) {
                throw new DataValidationException(String.format(
                        "Event date must not be earlier that %s hours from now",
                        HOURS_OFFSET));
        }
    }
}
