package com.kodat.complexhotelsystem.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @JoinColumn(name = "person_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    Person person;
    @Column(nullable = false, unique = true, length = 64)
    String token_hash;
    @Column(nullable = false)
    UUID family_id;
    @Column(nullable = false)
    Instant expires_at;
    Instant revoked_at;
    @Column(nullable = false)
    Instant created_at;
    UUID replace_by;
}
