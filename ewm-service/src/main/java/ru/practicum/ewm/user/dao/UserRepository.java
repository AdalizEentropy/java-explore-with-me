package ru.practicum.ewm.user.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u " +
            "WHERE u.id IN (:usersId)")
    List<User> findAllByUsersId(List<Long> usersId, Pageable pageable);
}
