package ru.practicum.stat.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.stat.hit.HitDtoReq;
import ru.practicum.stat.view.ViewStatsDtoResp;
import ru.practicum.stat.view.ViewStatsParam;

import java.time.format.DateTimeFormatter;
import java.util.List;

@AllArgsConstructor
@Slf4j
public class StatClientImpl implements StatClient {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final WebClient webClient;

    public ResponseEntity<Void> addHit(HitDtoReq hitDtoReq) {
        log.debug("Adding hit: {}", hitDtoReq);
        return webClient
                .post()
                .uri("/hit")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(hitDtoReq))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public List<ViewStatsDtoResp> getStat(ViewStatsParam statsDto) {
        log.debug("Getting stat: {}", statsDto);
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", statsDto.getStart().format(FORMATTER))
                        .queryParam("end", statsDto.getEnd().format(FORMATTER))
                        .queryParam("uris", statsDto.getUris())
                        .queryParam("unique", statsDto.getUnique())
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ViewStatsDtoResp>>() {})
                .block();
    }
}
