package ru.practicum.ewm.mapper;

import ru.practicum.ewm.category.dto.CategoryReqDto;
import ru.practicum.ewm.category.model.Category;
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

    public static CategoryReqDto toCategoryReqDto(Category category) {
        if (category == null) {
            return null;
        }

        CategoryReqDto categoryDto = new CategoryReqDto();

        categoryDto.setName(category.getName());

        return categoryDto;
    }
}
