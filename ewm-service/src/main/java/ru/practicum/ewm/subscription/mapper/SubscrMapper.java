package ru.practicum.ewm.subscription.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.subscription.dto.SubscrDto;
import ru.practicum.ewm.subscription.model.Subscription;
import ru.practicum.ewm.user.model.User;

@Mapper(componentModel = "spring")
public interface SubscrMapper {

    @Mapping(target = "follower", source = "follower")
    @Mapping(target = "userId", source = "userId")
    Subscription toSubscription(Long userId, User follower);

    SubscrDto toSubscrDto(Subscription subsc);
}
