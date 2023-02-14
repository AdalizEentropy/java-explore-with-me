package ru.practicum.stat.client;

import org.springframework.http.ResponseEntity;
import ru.practicum.stat.hit.HitDtoReq;
import ru.practicum.stat.view.ViewStatsDtoResp;
import ru.practicum.stat.view.ViewStatsParam;

import java.util.List;

public interface StatClient {

    /**
     * Saving information that a request was sent to the uri
     * of a particular service by the user.
     *
     * @param hitDtoReq the name of the service, uri and ip of the user.
     */
    ResponseEntity<Void> addHit(HitDtoReq hitDtoReq);

    /**
     * Get the statistics of visits
     *
     * @param statsDto parameters of search
     * @return list of visit statistics group by app and uri
     */
    List<ViewStatsDtoResp> getStat(ViewStatsParam statsDto);
}
