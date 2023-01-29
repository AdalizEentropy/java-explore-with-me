package ru.practicum.stat.view;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ViewStatsDtoResp {
    private String app;
    private String uri;
    private Long hits;
}
