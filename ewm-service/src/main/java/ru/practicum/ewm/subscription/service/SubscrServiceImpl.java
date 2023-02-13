package ru.practicum.ewm.subscription.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.PageParam;
import ru.practicum.ewm.event.dto.EventRespDto;
import ru.practicum.ewm.event.service.EventAdminService;
import ru.practicum.ewm.exception.DataValidationException;
import ru.practicum.ewm.subscription.dao.SubscriptionRepository;
import ru.practicum.ewm.subscription.dto.SubscrDto;
import ru.practicum.ewm.subscription.mapper.SubscrMapper;
import ru.practicum.ewm.subscription.model.Subscription;
import ru.practicum.ewm.user.dto.ShortUserDto;
import ru.practicum.ewm.user.service.UserService;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscrServiceImpl implements SubscrService {
    private final SubscriptionRepository subscrRepository;
    private final UserService userService;
    private final EventAdminService eventService;
    private final SubscrMapper mapper;

    @Transactional(readOnly = true)
    public List<EventRespDto> getAllSubscr(Long userId, PageParam pageParam) {
        List<Long> followers =  subscrRepository.findAllFollowersByUserId(userId);
        log.debug("User followersId was found: {}", followers);
        return !followers.isEmpty()
                ? eventService.findEventsByInitiators(followers, pageParam)
                : Collections.emptyList();
    }

    @Transactional(readOnly = true)
    public List<EventRespDto> getSubscrByFollower(Long userId, Long followerId, PageParam pageParam) {
        return eventService.findEventsByInitiator(followerId, pageParam);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SubscrDto addSubscr(Long userId, Long followerId) {
        var foundedSubscr = subscrRepository.findSubscription(userId, followerId);
        if (foundedSubscr != null) {
            throw new DataValidationException(String.format("Subscription from user %s to follower %s already exist",
                    userId, followerId));
        }

        var follower = userService.getUserById(followerId);
        Subscription subscr = subscrRepository.save(mapper.toSubscription(userId, follower));
        log.debug("Subscription from user {} to follower {} was added", userId, followerId);
        return mapper.toSubscrDto(subscr);
    }

    @Transactional(rollbackFor = SQLException.class, isolation = Isolation.SERIALIZABLE)
    public void cancelSubscr(Long userId, Long followerId) {
        subscrRepository.deleteSubscr(userId, followerId);
        log.debug("Subscription from user {} to follower {} was removed", userId, followerId);
    }

    @Transactional(readOnly = true)
    public List<ShortUserDto> getAllFollowers(Long userId, PageParam pageParam) {
        List<Long> followers =  subscrRepository.findAllFollowersByUserId(userId);
        return userService.getShortUsers(pageParam, followers);
    }
}
