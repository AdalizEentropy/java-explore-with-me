package ru.practicum.ewm.subscription.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@IdClass(SubscriptionId.class)
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_Id", nullable = false)
    private User follower;
}
