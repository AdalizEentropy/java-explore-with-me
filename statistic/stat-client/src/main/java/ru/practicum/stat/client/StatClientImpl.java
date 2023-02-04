package ru.practicum.stat.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.stat.hit.HitDtoReq;
import ru.practicum.stat.view.ViewStatsDtoReq;
import ru.practicum.stat.view.ViewStatsDtoResp;

import java.util.List;

@AllArgsConstructor
@Slf4j
public class StatClientImpl implements StatClient {
    private final WebClient webClient;

    public ResponseEntity<Void> addHit(HitDtoReq hitDtoReq) {
        log.debug("Adding hit: {}", hitDtoReq);
        return webClient
                .post()
                .uri("/hit")
                .body(BodyInserters.fromValue(hitDtoReq))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public List<ViewStatsDtoResp> getStat(ViewStatsDtoReq viewStatsDtoReq) {
        log.debug("Getting stat: {}", viewStatsDtoReq);
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", viewStatsDtoReq.getStart())
                        .queryParam("end", viewStatsDtoReq.getEnd())
                        .queryParam("uris", viewStatsDtoReq.getUris())
                        .queryParam("unique", viewStatsDtoReq.getUnique())
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ViewStatsDtoResp>>() {})
                .block();
    }
}
