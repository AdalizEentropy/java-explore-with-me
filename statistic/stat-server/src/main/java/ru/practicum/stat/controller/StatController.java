package ru.practicum.stat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stat.hit.HitDtoReq;
import ru.practicum.stat.service.StatService;
import ru.practicum.stat.view.ViewStatsDtoResp;
import ru.practicum.stat.view.ViewStatsParam;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatController {
    private final StatService statService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@Valid @NotNull @RequestBody HitDtoReq hitDtoReq) {
        statService.addHit(hitDtoReq);
    }

    @GetMapping("/stats")
    public List<ViewStatsDtoResp> getStat(@Valid ViewStatsParam statsParam) {
        return statService.getStat(statsParam);
    }
}
