package ru.practicum.stat.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stat.model.Statistic;
import ru.practicum.stat.model.StatisticCount;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<Statistic, Long> {

    @Query("SELECT s.app.name as app, s.uri as uri, COUNT(s.id) as hits " +
            "FROM Statistic s " +
            "WHERE s.timestamp between :start AND :end " +
            "GROUP BY s.app.name, s.uri")
    List<StatisticCount> countAll(LocalDateTime start, LocalDateTime end, Sort sort);

    @Query("SELECT s.app.name as app, s.uri as uri, COUNT(s.id) as hits " +
            "FROM Statistic s " +
            "WHERE s.timestamp between :start AND :end " +
            "AND s.uri IN (:uris) " +
            "GROUP BY s.app.name, s.uri")
    List<StatisticCount> countUri(LocalDateTime start, LocalDateTime end, List<String> uris, Sort sort);

    @Query("SELECT s.app.name as app, s.uri as uri, COUNT(DISTINCT s.ip) as hits " +
            "FROM Statistic s " +
            "WHERE s.timestamp between :start AND :end " +
            "GROUP BY s.app.name, s.uri ")
    List<StatisticCount> countAllUniqueIp(LocalDateTime start, LocalDateTime end, Sort sort);

    @Query("SELECT s.app.name as app, s.uri as uri, COUNT(DISTINCT s.ip) as hits " +
            "FROM Statistic s " +
            "WHERE s.timestamp between :start AND :end " +
            "AND s.uri IN (:uris) " +
            "GROUP BY s.app.name, s.uri ")
    List<StatisticCount> countUrisUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris, Sort sort);
}
