package ru.practicum.ewm.compilation.mapper;

import org.mapstruct.*;
import ru.practicum.ewm.compilation.dto.CompilationReqDto;
import ru.practicum.ewm.compilation.dto.CompilationRespDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.model.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "events", source = "compilationDto.events", qualifiedByName = "SetEvents")
    Compilation toCompilation(CompilationReqDto compilationDto);

    @Mapping(target = "events", source = "compilation.events")
    CompilationRespDto toCompilationRespDto(Compilation compilation);

    @Mapping(target = "events", source = "compilation.events")
    List<CompilationRespDto> toCompilationsRespDto(List<Compilation> compilation);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "events", source = "compilationDto.events", qualifiedByName = "SetEvents")
    void toCompilationUpdate(CompilationReqDto compilationDto, @MappingTarget Compilation compilation);

    @Named("SetEvents")
    default List<Event> getEvents(List<Long> id) {
        if (id != null) {
            List<Event> events = new ArrayList<>();
            id.forEach(i -> {
                var event = new Event();
                event.setId(i);
                events.add(event);
            });

            return events;
        }

        return Collections.emptyList();
    }
}
