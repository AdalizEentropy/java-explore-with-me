package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationReqDto;
import ru.practicum.ewm.compilation.dto.CompilationRespDto;
import ru.practicum.ewm.compilation.service.CompilationService;
import ru.practicum.ewm.exception.DataValidationException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationRespDto addCompilation(@Valid @NotNull @RequestBody CompilationReqDto compilation) {
        return compilationService.addCompilation(compilation);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationRespDto updateCompilation(@PathVariable Long compId,
                                                @NotNull @RequestBody CompilationReqDto compilation) {
        CompilationRespDto compilationResp;

        try {
            compilationResp = compilationService.updateCompilation(compId, compilation);
        } catch (DataIntegrityViolationException e) {
            throw new DataValidationException(String.format("Compilation with title %s already exist",
                    compilation.getTitle()));
        }

        return compilationResp;
    }
}
