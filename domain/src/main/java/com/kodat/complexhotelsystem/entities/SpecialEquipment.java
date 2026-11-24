package com.kodat.complexhotelsystem.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

@Entity
public class SpecialEquipment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = true)
    private String description;
}
