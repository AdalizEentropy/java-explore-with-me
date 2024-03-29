package ru.practicum.ewm.statistic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.stat.client.StatClient;
import ru.practicum.stat.hit.HitDtoReq;
import ru.practicum.stat.view.ViewStatsDtoResp;
import ru.practicum.stat.view.ViewStatsParam;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticServiceImpl implements StatisticService {
    private static final Pattern PATTERN = Pattern.compile("\\d+");
    private final StatClient statClient;

    @Value("${app.name}")
    private String appName;

    public void addView(String uri, String ip) {
        log.debug("Sent a view to statistic");
        log.trace("From uri {}, ip {}", uri, ip);
        statClient.addHit(createHitDtoReq(uri, ip));
    }

    public Long getViewCountsForOne(List<String> uris, LocalDateTime startTime) {
        log.debug("Get count views from statistic for one uri");
        return this.getStat(uris, startTime)
                .stream()
                .filter(Objects::nonNull)
                .map(ViewStatsDtoResp::getHits)
                .count();
    }

    public Map<Long, Long> getMapViewCounts(List<String> uris, LocalDateTime startTime) {
        List<ViewStatsDtoResp> views = this.getStat(uris, startTime);

        // Create map for events
        Map<Long, Long> tmpViewsMap = new HashMap<>();
        for (ViewStatsDtoResp view : views) {
            var id = PATTERN.matcher(view.getUri());
            if (id.find()) {
                var eventId = Long.parseLong(id.group(0));
                if (tmpViewsMap.containsKey(eventId)) {
                    var currentCount = tmpViewsMap.get(eventId);
                    tmpViewsMap.put(eventId, currentCount + view.getHits());
                } else {
                    tmpViewsMap.put(eventId, view.getHits());
                }
            }
        }

        log.debug("Map of count views from statistic for one many uris: {}", tmpViewsMap);
        return tmpViewsMap;
    }

    private List<ViewStatsDtoResp> getStat(List<String> uris, LocalDateTime startTime) {
        var stat = statClient.getStat(createViewStatsDtoReq(uris, startTime));
        log.debug("Get views from statistic: {}", stat);
        return stat;
    }

    private HitDtoReq createHitDtoReq(String uri, String ip) {
        return new HitDtoReq()
                .setApp(appName)
                .setUri(uri)
                .setIp(ip)
                .setTimestamp(LocalDateTime.now());
    }

    private ViewStatsParam createViewStatsDtoReq(List<String> uris, LocalDateTime start) {
        return new ViewStatsParam()
                .setUris(uris)
                .setStart(start)
                .setEnd(LocalDateTime.now())
                .setUnique(true);
    }
}
