package ru.practicum.ewm.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.request.dto.RequestRespDto;
import ru.practicum.ewm.request.model.Request;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "event", source = "request.event.id")
    @Mapping(target = "requester", source = "request.requester.id")
    RequestRespDto toRequestRespDto(Request request);

    @Mapping(target = "event", source = "request.event.id")
    @Mapping(target = "requester", source = "request.requester.id")
    List<RequestRespDto> toRequestsRespDto(List<Request> requests);
}
