package com.kodat.complexhotelsystem.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Getter
@Setter
public class ResetPasswordToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne(mappedBy = "token")
    private Person person;
    private Instant expiration;
    private Instant usedAt;
    @Column(nullable = false, unique = true, length = 255)
    private String hashToken;
}
