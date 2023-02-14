package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.PageParam;
import ru.practicum.ewm.compilation.dao.CompilationRepository;
import ru.practicum.ewm.compilation.dto.CompilationReqDto;
import ru.practicum.ewm.compilation.dto.CompilationRespDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventAdminService;
import ru.practicum.ewm.exception.DataValidationException;

import javax.persistence.EntityNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.common.PageParam.pageRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private static final Sort SORT_TYPE = Sort.by("id").descending();
    private final CompilationRepository compilationRepository;
    private final EventAdminService eventService;
    private final CompilationMapper mapper;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CompilationRespDto addCompilation(CompilationReqDto compilation) {
        Compilation compilationResp;
        try {
            compilationResp = compilationRepository.save(mapper.toCompilation(compilation));
        } catch (DataIntegrityViolationException e) {
            throw new DataValidationException(String.format("Compilation with title %s already exist",
                    compilation.getTitle()));
        }

        mapEventsData(compilation.getEvents(), compilationResp);

        log.debug("Compilation saved: {}", compilationResp);
        return mapper.toCompilationRespDto(compilationResp);
    }

    @Transactional(rollbackFor = SQLException.class, isolation = Isolation.SERIALIZABLE)
    public void deleteCompilation(Long compId) {
        findCompById(compId);
        compilationRepository.deleteById(compId);
        log.debug("Compilation Id {} was removed", compId);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CompilationRespDto updateCompilation(Long compId, CompilationReqDto compilation) {
        var comp = findCompById(compId);
        mapper.toCompilationUpdate(compilation, comp);

        List<Long> eventsId = comp.getEvents().stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        mapEventsData(eventsId, comp);

        log.debug("Compilation updated: {}", comp);
        return mapper.toCompilationRespDto(comp);
    }

    @Transactional(readOnly = true)
    public List<CompilationRespDto> getCompilations(PageParam pageParam, Boolean pinned) {
        List<Compilation> compilations;
        if (pinned == null) {
            compilations = compilationRepository.findAll(pageRequest(pageParam, SORT_TYPE)).toList();
        } else if (pinned) {
            compilations = compilationRepository.findAllByPinned(true, pageRequest(pageParam, SORT_TYPE));
        } else {
            compilations = compilationRepository.findAllByPinned(false, pageRequest(pageParam, SORT_TYPE));
        }

        log.debug("Compilations found: {}", compilations);
        return mapper.toCompilationsRespDto(compilations);
    }

    @Transactional(readOnly = true)
    public CompilationRespDto getCompilationById(Long compId) {
        return mapper.toCompilationRespDto(findCompById(compId));
    }

    private Compilation findCompById(Long compId) {
        var compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("CompilationID %s does not exist",
                        compId)));

        log.debug("Compilation with id {} was found", compId);
        return compilation;
    }

    private void mapEventsData(List<Long> eventsId, Compilation comp) {
        List<Event> events = eventService.findEvents(eventsId);
        comp.setEvents(new ArrayList<>());
        events.forEach(e -> comp.getEvents().add(e));
    }
}
