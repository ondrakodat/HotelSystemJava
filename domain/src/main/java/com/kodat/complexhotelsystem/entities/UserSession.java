package com.kodat.complexhotelsystem.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    long person_id;
    String token_hash;
    long family_id;
    long expires_at;
    long revoked_at;
    long created_at;
    long replace_by;
}
