package ru.practicum.ewm.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRespDto {
    private Long id;
    private String email;
    private String name;
}
