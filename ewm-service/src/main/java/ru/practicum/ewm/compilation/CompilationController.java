package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.PageParam;
import ru.practicum.ewm.compilation.dto.CompilationRespDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class CompilationController {
    private final CompilationService compilationService;

    @GetMapping()
    public List<CompilationRespDto> getCompilations(@Valid PageParam pageParam,
                                                    @RequestParam(required = false) Boolean pinned) {
        return compilationService.getCompilations(pageParam, pinned);
    }

    @GetMapping("/{compId}")
    public CompilationRespDto getCompilationById(@PathVariable Long compId) {
        return compilationService.getCompilationById(compId);
    }
}
