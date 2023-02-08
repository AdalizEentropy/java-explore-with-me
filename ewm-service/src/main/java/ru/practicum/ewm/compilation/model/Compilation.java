package ru.practicum.ewm.compilation.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.ewm.event.model.Event;

import javax.persistence.*;
import java.util.List;

@Data
@EqualsAndHashCode(of = {"id"})
@Entity
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> events;

    @Column(name = "pinned")
    private Boolean pinned;

    @Column(name = "title", nullable = false, unique = true)
    private String title;
}
