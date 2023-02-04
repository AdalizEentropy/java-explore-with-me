package ru.practicum.ewm.event.dao.specification;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.search.EventParams;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class EventSpec {

    public static Specification<Event> allParams(EventParams eventParams, @NotNull EventState state) {
        return (stateEqual(state))
                .and(categoriesIn(eventParams.getCategories()))
                .and(paidEqual(eventParams.getPaid()))
                .and(eventDateRange(eventParams.getRangeStart(), eventParams.getRangeEnd()))
                .and(annotationLike(eventParams.getText()).or(descriptionLike(eventParams.getText())));
    }

    private static Specification<Event> annotationLike(final String text) {
        return (root, query, builder) ->
                text != null && !text.isBlank()
                        ? builder.like(root.get("annotation"), "%"+text+"%")
                        : builder.conjunction();
    }

    private static Specification<Event> descriptionLike(final String text) {
        return (root, query, builder) ->
                text != null && !text.isBlank()
                        ? builder.like(root.get("description"), "%"+text+"%")
                        : builder.conjunction();
    }

    private static Specification<Event> stateEqual(final EventState state){
        return (root, query, builder) -> builder.equal(root.get("state"), state);
    }

    private static Specification<Event> categoriesIn(final List<Integer> categories) {
        return (root, query, builder) ->
            categories == null
                    ? builder.conjunction()
                    : builder.in(root.get("category").get("id")).value(categories);
    }

    private static Specification<Event> paidEqual(final Boolean paid) {
        return (root, query, builder) ->
                paid == null
                        ? builder.conjunction()
                        : builder.equal(root.get("paid"), paid);
    }

    private static Specification<Event> eventDateRange(final LocalDateTime rangeStart, final LocalDateTime rangeEnd) {
        return (root, query, builder) ->
                rangeStart != null && rangeEnd != null
                        ? builder.between(root.get("eventDate"), rangeStart, rangeEnd)
                        : builder.greaterThan(root.get("eventDate"), LocalDateTime.now());
    }

    //TODO добавить расчет, что событие доступно
    /*private static Specification<Event> onlyAvailable(final Boolean available) {
        return (root, query, builder) ->
                available != null
                        ? builder.equal(root.get("participantLimit"), )
                        : builder.greaterThan(root.get("participantLimit"), );
    }*/
}
