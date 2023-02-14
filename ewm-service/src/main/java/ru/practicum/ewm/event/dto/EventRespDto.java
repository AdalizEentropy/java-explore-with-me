package ru.practicum.ewm.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.category.dto.CategoryRespDto;
import ru.practicum.ewm.user.dto.ShortUserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class EventRespDto {
    private Long id;
    private String title;
    private String annotation;
    private CategoryRespDto category;
    private Integer confirmedRequests;
    private LocalDateTime eventDate;
    private ShortUserDto initiator;
    private boolean paid;
    private Long views;
}
