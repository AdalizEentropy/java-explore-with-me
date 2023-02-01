package ru.practicum.ewm.mapper;

import ru.practicum.ewm.user.dto.NewUserDto;
import ru.practicum.ewm.user.model.User;

public class TestMapper {
    public static NewUserDto toNewUserDto(User user) {
        if (user == null) {
            return null;
        }

        NewUserDto newUserDto = new NewUserDto();

        newUserDto.setEmail(user.getEmail());
        newUserDto.setName(user.getName());

        return newUserDto;
    }
}
