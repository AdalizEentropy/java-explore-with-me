package ru.practicum.ewm.compilation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CompilationReqDto {
    private List<Long> events;
    private Boolean pinned = false;

    @NotNull
    private String title;
}
