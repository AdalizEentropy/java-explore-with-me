package ru.practicum.ewm.event.model.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EventParams {
    private String text;
    private List<Integer> categories;
    private Boolean paid;
    private Boolean onlyAvailable = false;
    private SortType sort = SortType.EVENT_DATE;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;

    public enum SortType {
        EVENT_DATE,
        VIEWS
    }
}
