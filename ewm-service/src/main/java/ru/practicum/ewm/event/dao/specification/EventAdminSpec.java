package ru.practicum.ewm.event.dao.specification;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.search.EventAdminParams;
import ru.practicum.ewm.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class EventAdminSpec {

    public static Specification<Event> allParams(EventAdminParams eventParams) {
        return initiatorsIn(eventParams.getUsers())
                .and(statesIn(eventParams.getStates()))
                .and(categoriesIn(eventParams.getCategories()))
                .and(eventDateAfter(eventParams.getRangeStart()))
                .and(eventDateBefore(eventParams.getRangeEnd()));
    }

    private static Specification<Event> initiatorsIn(final List<Long> users) {
        return (root, query, builder) ->
            users != null && !users.isEmpty()
                    ? builder.in(root.get("initiator").get("id")).value(users)
                    : builder.conjunction();
    }

    private static Specification<Event> statesIn(final List<EventState> states) {
        return (root, query, builder) ->
            states != null && !states.isEmpty()
                    ? builder.in(root.get("state")).value(states)
                    : builder.conjunction();
    }

    private static Specification<Event> categoriesIn(final List<Integer> categories) {
        return (root, query, builder) ->
            categories != null && !categories.isEmpty()
                    ? builder.in(root.get("category").get("id")).value(categories)
                    : builder.conjunction();
    }

    private static Specification<Event> eventDateAfter(final LocalDateTime rangeStart) {
        return (root, query, builder) ->
                rangeStart == null
                        ? builder.conjunction()
                        : builder.greaterThan(root.get("eventDate"), rangeStart);
    }

    private static Specification<Event> eventDateBefore(final LocalDateTime rangeEnd) {
        return (root, query, builder) ->
                rangeEnd == null
                        ? builder.conjunction()
                        : builder.lessThan(root.get("eventDate"), rangeEnd);
    }
}
