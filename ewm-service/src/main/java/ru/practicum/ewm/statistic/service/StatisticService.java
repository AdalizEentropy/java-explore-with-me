package ru.practicum.ewm.statistic.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface StatisticService {

    void addView(String uri, String ip);

    Long getViewCountsForOne(List<String> uris, LocalDateTime startTime);

    Map<Long, Long> getMapViewCounts(List<String> uris, LocalDateTime startTime);
}
