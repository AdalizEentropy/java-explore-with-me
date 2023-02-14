package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.common.PageParam;
import ru.practicum.ewm.compilation.dto.CompilationReqDto;
import ru.practicum.ewm.compilation.dto.CompilationRespDto;

import java.util.List;

public interface CompilationService {

    CompilationRespDto addCompilation(CompilationReqDto compilation);

    void deleteCompilation(Long compId);

    CompilationRespDto updateCompilation(Long compId, CompilationReqDto compilation);

    List<CompilationRespDto> getCompilations(PageParam pageParam, Boolean pinned);

    CompilationRespDto getCompilationById(Long compId);
}
