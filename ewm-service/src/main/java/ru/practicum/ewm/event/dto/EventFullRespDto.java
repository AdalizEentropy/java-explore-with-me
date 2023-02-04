package ru.practicum.ewm.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.category.dto.CategoryRespDto;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.user.dto.ShortUserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class EventFullRespDto {
    private Long id;
    private String title;
    private String annotation;
    private String description;
    private CategoryRespDto category;
    private Long confirmedRequests;
    private LocationDto location;
    private LocalDateTime createdOn;
    private LocalDateTime eventDate;
    private LocalDateTime publishedOn;
    private Integer participantLimit;
    private Boolean paid;
    private Boolean requestModeration;
    private ShortUserDto initiator;
    private EventState state;
    private Long views;
}
