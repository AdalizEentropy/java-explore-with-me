package ru.practicum.stat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stat.dao.StatRepository;
import ru.practicum.stat.hit.HitDtoReq;
import ru.practicum.stat.model.StatisticCount;
import ru.practicum.stat.model.mapper.StatMapper;
import ru.practicum.stat.view.ViewStatsDtoResp;
import ru.practicum.stat.view.ViewStatsParam;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;
    private final StatMapper statMapper;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void addHit(HitDtoReq hitDtoReq) {
        statRepository.save(statMapper.toStat(hitDtoReq));
        log.debug("Added hit: {}", hitDtoReq);
    }

    @Transactional(readOnly = true)
    public List<ViewStatsDtoResp> getStat(ViewStatsParam statsParam) {
        log.debug("Get stat: {}", statsParam);
        List<ViewStatsDtoResp> result;
        var sort = Sort.by("hits").descending();

        if (statsParam.getUnique() != null) {
            var allUnique = statRepository.countAllUniqueIp(statsParam.getStart(),
                    statsParam.getEnd(),
                    sort);
            result = getUri(allUnique, statsParam.getUris());
        } else {
            var all = statRepository.countAll(statsParam.getStart(),
                    statsParam.getEnd(),
                    sort);
            result = getUri(all, statsParam.getUris());
        }

        log.debug("Return stat: {}", result);
        return result;
    }

    private List<ViewStatsDtoResp> getUri(List<StatisticCount> all, String[] uris) {
        if (uris == null) {
            return statMapper.toStatsDtoResp(all);
        }

        var urisList = Arrays.asList(uris);
        return statMapper.toStatsDtoResp(all.stream()
                .filter(stat -> urisList.contains(stat.getUri()))
                .collect(Collectors.toList()));
    }
}
