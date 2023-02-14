package ru.practicum.stat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stat.dao.ApplicationRepository;
import ru.practicum.stat.dao.StatRepository;
import ru.practicum.stat.hit.HitDtoReq;
import ru.practicum.stat.model.Application;
import ru.practicum.stat.model.StatisticCount;
import ru.practicum.stat.model.mapper.StatMapper;
import ru.practicum.stat.view.ViewStatsDtoResp;
import ru.practicum.stat.view.ViewStatsParam;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;
    private final ApplicationRepository appRepository;
    private final StatMapper statMapper;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void addHit(HitDtoReq hitDtoReq) {
        var item = statMapper.toStat(hitDtoReq);
        Application app = appRepository
                .findByName(item.getApp().getName())
                .or(() -> Optional.of(appRepository.save(item.getApp())))
                .orElseThrow(() -> new NoSuchElementException("Unexpected error"));

        item.getApp().setId(app.getId());
        statRepository.save(item);
        log.debug("Added hit: {}", hitDtoReq);
    }

    @Transactional(readOnly = true)
    public List<ViewStatsDtoResp> getStat(ViewStatsParam statsParam) {
        log.debug("Get stat: {}", statsParam);
        List<StatisticCount> result;
        var sort = Sort.by("hits").descending();

        if (statsParam.getUnique() != null && statsParam.getUnique()) {
            if (statsParam.getUris() != null) {
                result = statRepository.countUrisUniqueIp(statsParam.getStart(),
                        statsParam.getEnd(),
                        statsParam.getUris(),
                        sort);
            } else {
                result = statRepository.countAllUniqueIp(statsParam.getStart(),
                        statsParam.getEnd(),
                        sort);
            }
        } else {
            if (statsParam.getUris() != null) {
                result = statRepository.countUri(statsParam.getStart(),
                        statsParam.getEnd(),
                        statsParam.getUris(),
                        sort);
            } else {
                result = statRepository.countAll(statsParam.getStart(),
                        statsParam.getEnd(),
                        sort);
            }
        }

        var stats = statMapper.toStatsDtoResp(result);
        log.debug("Return stat: {}", stats);
        return stats;
    }
}
