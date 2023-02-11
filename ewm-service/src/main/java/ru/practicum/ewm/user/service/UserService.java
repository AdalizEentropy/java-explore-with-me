package ru.practicum.ewm.user.service;

import ru.practicum.ewm.common.PageParam;
import ru.practicum.ewm.user.dto.NewUserDto;
import ru.practicum.ewm.user.dto.ShortUserDto;
import ru.practicum.ewm.user.dto.UserRespDto;
import ru.practicum.ewm.user.model.User;

import java.util.List;

public interface UserService {

    List<UserRespDto> getUsers(PageParam pageParam, List<Long> usersId);

    UserRespDto addUser(NewUserDto newUserDto);

    void removeUser(Long userId);

    User getUserById(Long userId);

    List<ShortUserDto> getShortUsers(PageParam pageParam, List<Long> usersId);
}
