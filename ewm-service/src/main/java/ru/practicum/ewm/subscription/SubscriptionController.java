package ru.practicum.ewm.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.PageParam;
import ru.practicum.ewm.event.dto.EventRespDto;
import ru.practicum.ewm.subscription.dto.SubscrDto;
import ru.practicum.ewm.subscription.service.SubscrService;
import ru.practicum.ewm.user.dto.ShortUserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscrService subscrService;

    @GetMapping("/{userId}/subscr")
    public List<EventRespDto> getAllSubscr(@PathVariable Long userId,
                                           @Valid PageParam pageParam) {
        return subscrService.getAllSubscr(userId, pageParam);
    }

    @GetMapping("/{userId}/subscr/{followerId}")
    public List<EventRespDto> getSubscrByFollower(@PathVariable Long userId,
                                           @PathVariable Long followerId,
                                           @Valid PageParam pageParam) {
        return subscrService.getSubscrByFollower(userId, followerId, pageParam);
    }

    @PostMapping("/{userId}/subscr/{followerId}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public SubscrDto addSubscr(@PathVariable Long userId,
                               @PathVariable Long followerId) {
        return subscrService.addSubscr(userId, followerId);
    }

    @DeleteMapping("/{userId}/subscr/{followerId}")
    public void cancelSubscr(@PathVariable Long userId,
                             @PathVariable Long followerId) {
        subscrService.cancelSubscr(userId, followerId);
    }

    @GetMapping("/{userId}/followers")
    public List<ShortUserDto> getAllFollowers(@PathVariable Long userId,
                                              @Valid PageParam pageParam) {
        return subscrService.getAllFollowers(userId, pageParam);
    }
}
