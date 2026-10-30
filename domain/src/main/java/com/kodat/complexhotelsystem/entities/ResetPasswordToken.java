package com.kodat.complexhotelsystem.entities;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class ResetPasswordToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false, unique = true)
    private Person person;
    private Instant expiration;
    private Instant usedAt;
    @Column(nullable = false, unique = true, length = 64)
    private String hashToken;
}
