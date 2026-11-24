package com.kodat.complexhotelsystem.entities;

import com.kodat.complexhotelsystem.enums.RoomState;
import com.kodat.complexhotelsystem.enums.RoomType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, unique = true)
    private int roomNumber;
    @Column(nullable = false)
    private int floor;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomType type;
    @Column(nullable = false)
    private int capacity;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomState state;
    @ManyToOne
    @JoinTable(
            name = "room_special_equipment_items",
            joinColumns = @JoinColumn(name = "room_special_equipment_id"),
            inverseJoinColumns = @JoinColumn(name = "equipment_id")
    )
    private List<SpecialEquipment> specialEquipments = new ArrayList<>();
}
