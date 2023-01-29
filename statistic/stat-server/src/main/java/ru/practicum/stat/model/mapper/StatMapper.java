package ru.practicum.stat.model.mapper;

import org.mapstruct.Mapper;
import ru.practicum.stat.hit.HitDtoReq;
import ru.practicum.stat.model.Statistic;
import ru.practicum.stat.model.StatisticCount;
import ru.practicum.stat.view.ViewStatsDtoResp;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StatMapper {

    List<ViewStatsDtoResp> toStatsDtoResp(List<StatisticCount> stat);

    Statistic toStat(HitDtoReq hitDtoReq);
}
