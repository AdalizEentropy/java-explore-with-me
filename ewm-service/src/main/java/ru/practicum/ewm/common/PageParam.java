package ru.practicum.ewm.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class PageParam {
    private static final int DEFAULT_FROM = 0;
    private static final int DEFAULT_SIZE = 10;

    @PositiveOrZero
    private Integer from = DEFAULT_FROM;

    @Positive
    private Integer size = DEFAULT_SIZE;

    public static Pageable pageRequest(PageParam param, Sort sort) {
        return PageRequest.of(param.from, param.size, sort);
    }
}
