package ru.practicum.stat.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.stat.hit.HitDtoReq;
import ru.practicum.stat.model.Statistic;
import ru.practicum.stat.model.StatisticCount;
import ru.practicum.stat.view.ViewStatsDtoResp;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StatMapper {

    List<ViewStatsDtoResp> toStatsDtoResp(List<StatisticCount> stat);

    @Mapping(target = "app.name", source = "hitDtoReq.app")
    Statistic toStat(HitDtoReq hitDtoReq);
}
