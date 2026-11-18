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
    @Column(nullable = false, unique = true, length = 64, name = "token_hash")
    String tokenHash;
    @Column(nullable = false, unique = false, name = "family_id")
    UUID familyId;
    @Column(nullable = false, unique = false, name = "expires_at")
    Instant expiresAt;
    @Column(unique = false, name = "revoked_at")
    Instant revokedAt;
    @Column(unique = false,  name = "created_at")
    Instant createdAt;
    @Column(unique = false, name = "replace_by")
    UUID replaceBy;
}
