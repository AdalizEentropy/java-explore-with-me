package ru.practicum.ewm.subscription.service;

import ru.practicum.ewm.common.PageParam;
import ru.practicum.ewm.event.dto.EventRespDto;
import ru.practicum.ewm.subscription.dto.SubscrDto;
import ru.practicum.ewm.user.dto.ShortUserDto;

import java.util.List;

public interface SubscrService {

    List<EventRespDto> getAllSubscr(Long userId, PageParam pageParam);

    List<EventRespDto> getSubscrByFollower(Long userId, Long followerId, PageParam pageParam);

    SubscrDto addSubscr(Long userId, Long followerId);

    void cancelSubscr(Long userId, Long followerId);

    List<ShortUserDto> getAllFollowers(Long userId, PageParam pageParam);
}
