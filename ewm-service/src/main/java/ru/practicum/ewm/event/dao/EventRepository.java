package ru.practicum.ewm.event.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    @Query("SELECT e FROM Event e " +
            "JOIN FETCH User u ON u.id = e.initiator.id " +
            "JOIN FETCH Category c ON c.id = e.category.id " +
            "WHERE e.initiator.id = :userId")
    List<Event> findEventsByUser(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiator_Id(Long id, Long initiatorId);

    Page<Event> findAll(Specification<Event> spec, Pageable pageable);

    Optional<Event> findByIdAndState(Long id, EventState state);
}
