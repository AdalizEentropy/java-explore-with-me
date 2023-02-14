package ru.practicum.stat.service;

import ru.practicum.stat.hit.HitDtoReq;
import ru.practicum.stat.view.ViewStatsDtoResp;
import ru.practicum.stat.view.ViewStatsParam;

import java.util.List;

public interface StatService {

    /**
     * Saving information that a request was sent to the uri
     * of a particular service by the user.
     *
     * @param hitDtoReq the name of the service, uri and ip of the user.
     */
    void addHit(HitDtoReq hitDtoReq);

    /**
     * Get the statistics of visits
     *
     * @param statsParam parameters of search
     * @return list of visit statistics group by app and uri
     */
    List<ViewStatsDtoResp> getStat(ViewStatsParam statsParam);
}
