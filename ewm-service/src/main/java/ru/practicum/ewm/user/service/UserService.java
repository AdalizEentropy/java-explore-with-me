package ru.practicum.ewm.user.service;

import ru.practicum.ewm.common.PageParam;
import ru.practicum.ewm.user.dto.NewUserDto;
import ru.practicum.ewm.user.dto.UserRespDto;

import java.util.List;

public interface UserService {

    List<UserRespDto> getUsers(PageParam pageParam, List<Long> usersId);

    UserRespDto addUser(NewUserDto newUserDto);

    void removeUser(Long userId);
}
