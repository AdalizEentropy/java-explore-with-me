package ru.practicum.ewm.event.mapper;

import org.mapstruct.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "initiator.id", source = "userId")
    @Mapping(target = "location.id", source = "locationId")
    @Mapping(target = "category.id", source = "eventDto.category")
    Event toEvent(Long userId, Long locationId, NewEventReqDto eventDto);

    @Mapping(target = "confirmedRequests", source = "reqCount")
    @Mapping(target = "views", source = "viewCount")
    EventFullRespDto toEventFullRespDto(Event event, int reqCount, Long viewCount);

    EventFullRespDto toEventFullRespDto(Event event);

    List<EventFullRespDto> toEventsFullRespDto(List<Event> event);

    List<EventRespDto> toEventsRespDto(List<Event> event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category.id", source = "eventDto.category")
    void updateEventFromDto(UpdateEventReqDto eventDto, @MappingTarget Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category.id", source = "eventDto.category")
    void updateEventFromDto(UpdateEventAdminDto eventDto, @MappingTarget Event event);
}
