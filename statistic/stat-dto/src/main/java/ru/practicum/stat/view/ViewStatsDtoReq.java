package ru.practicum.stat.view;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Validated
public class ViewStatsDtoReq {
    @NotNull private String start;
    @NotNull private String end;
    private List<String> uris;
    private Boolean unique;
}
