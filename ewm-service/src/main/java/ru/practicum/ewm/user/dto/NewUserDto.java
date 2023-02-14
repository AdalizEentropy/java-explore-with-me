package ru.practicum.ewm.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class NewUserDto {

    @NotBlank
    @Email(message = "Incorrect E-mail")
    private String email;

    @NotBlank
    private String name;
}
