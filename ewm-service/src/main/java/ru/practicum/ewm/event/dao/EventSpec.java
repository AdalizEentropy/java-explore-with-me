package ru.practicum.ewm.event.dao;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventParams;
import ru.practicum.ewm.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class EventSpec {

    public static Specification<Event> allParams(EventParams eventParams) {
        return initiators(eventParams.getUsers())
                .and(states(eventParams.getStates()))
                .and(categories(eventParams.getCategories()))
                .and(rangeStart(eventParams.getRangeStart()))
                .and(rangeEnd(eventParams.getRangeEnd()));
    }

    private static Specification<Event> initiators(final List<Long> users){
        return (root, query, builder) ->
            users == null
                    ? builder.conjunction()
                    : builder.in(root.get("initiator").get("id")).value(users);
    }

    private static Specification<Event> states(final List<EventState> states){
        return (root, query, builder) ->
            states == null
                    ? builder.conjunction()
                    : builder.in(root.get("state")).value(states);
    }

    private static Specification<Event> categories(final List<Integer> categories){
        return (root, query, builder) ->
            categories == null
                    ? builder.conjunction()
                    : builder.in(root.get("category").get("id")).value(categories);
    }

    private static Specification<Event> rangeStart(final LocalDateTime rangeStart){
        return (root, query, builder) ->
                rangeStart == null
                        ? builder.conjunction()
                        : builder.greaterThan(root.get("eventDate"), rangeStart);
    }

    private static Specification<Event> rangeEnd(final LocalDateTime rangeEnd){
        return (root, query, builder) ->
                rangeEnd == null
                        ? builder.conjunction()
                        : builder.lessThan(root.get("eventDate"), rangeEnd);
    }
}
