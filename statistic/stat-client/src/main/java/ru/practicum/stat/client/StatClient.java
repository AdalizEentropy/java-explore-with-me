package ru.practicum.stat.client;

import ru.practicum.stat.hit.HitDtoReq;
import ru.practicum.stat.view.ViewStatsDtoReq;

public interface StatClient {

    /**
     * Saving information that a request was sent to the uri
     * of a particular service by the user.
     *
     * @param hitDtoReq the name of the service, uri and ip of the user.
     */
    Object addHit(HitDtoReq hitDtoReq);

    /**
     * Get the statistics of visits
     *
     * @param viewStatsDtoReq parameters of search
     * @return list of visit statistics group by app and uri
     */
    Object getStat(ViewStatsDtoReq viewStatsDtoReq);
}
