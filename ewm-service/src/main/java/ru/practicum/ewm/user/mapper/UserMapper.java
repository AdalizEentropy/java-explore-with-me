package ru.practicum.ewm.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.user.dto.NewUserDto;
import ru.practicum.ewm.user.dto.ShortUserDto;
import ru.practicum.ewm.user.dto.UserRespDto;
import ru.practicum.ewm.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    List<UserRespDto> toUsersRespDto(List<User> users);

    UserRespDto toUserRespDto(User user);

    User toUser(NewUserDto userDto);

    List<ShortUserDto> toShortUserDto(List<User> users);
}
