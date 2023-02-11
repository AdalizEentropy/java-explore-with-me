package ru.practicum.ewm.subscription.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.subscription.model.Subscription;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Modifying
    @Query("DELETE FROM Subscription " +
            "WHERE userId = :userId AND follower.id = :followerId")
    void deleteSubscr(Long userId, Long followerId);

    @Query("SELECT s.follower.id FROM Subscription s " +
            "WHERE s.userId = :userId")
    List<Long> findAllFollowersByUserId(Long userId);

    @Query("SELECT s FROM Subscription s " +
            "WHERE s.userId = :userId " +
            "AND s.follower.id = :followerId")
    Subscription findSubscription(Long userId, Long followerId);
}
