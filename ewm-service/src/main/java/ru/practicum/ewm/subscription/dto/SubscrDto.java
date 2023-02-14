package ru.practicum.ewm.subscription.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.user.dto.ShortUserDto;

@Getter
@Setter
@NoArgsConstructor
public class SubscrDto {
    private Long userId;
    private ShortUserDto follower;
}
