package ru.practicum.stat.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.stat.model.Application;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByName(String name);
}
