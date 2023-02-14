package ru.practicum.ewm.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestCount;
import ru.practicum.ewm.request.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequester_Id(Long requesterId);

    @Query("SELECT r FROM Request r " +
            "WHERE r.id IN (:id)")
    List<Request> findAllById(List<Long> id);

    List<Request> findAllByEvent_Id(Long eventId);

    Optional<Request> findByRequester_IdAndEvent_Id(Long requesterId, Long eventId);

    int countAllByEvent_IdAndStatus(Long eventId, RequestStatus status);

    Optional<Request> findByIdAndRequester_Id(Long id, Long requesterId);

    @Query("SELECT e.id as eventId, COUNT(r.id) as reqCount FROM Event e " +
            "JOIN Request r ON r.event.id = e.id " +
            "WHERE e.id IN (:events) " +
            "AND r.status = :status " +
            "GROUP BY e.id")
    List<RequestCount> findRequestCount(List<Long> events, RequestStatus status);
}
