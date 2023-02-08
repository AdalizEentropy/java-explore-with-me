package ru.practicum.ewm.compilation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.event.dto.EventRespDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CompilationRespDto {
    private Long id;
    private List<EventRespDto> events;
    private Boolean pinned;
    private String title;
}
