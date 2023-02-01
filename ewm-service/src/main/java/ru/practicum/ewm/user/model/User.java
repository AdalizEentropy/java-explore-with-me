package ru.practicum.ewm.user.model;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@ToString
@RequiredArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "name")
    private String name;
}
