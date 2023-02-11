package ru.practicum.ewm.subscription.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
public class SubscriptionId implements Serializable {
    protected Long userId;
    protected Long follower;
}
