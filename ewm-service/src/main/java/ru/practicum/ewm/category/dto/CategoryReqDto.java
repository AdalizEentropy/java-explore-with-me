package ru.practicum.ewm.category.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class CategoryReqDto {

    @NotBlank
    private String name;
}
