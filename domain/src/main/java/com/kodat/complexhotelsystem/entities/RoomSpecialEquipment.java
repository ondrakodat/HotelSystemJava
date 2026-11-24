package com.kodat.complexhotelsystem.entities;

import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import java.util.ArrayList;
import java.util.List;

public class RoomSpecialEquipment {
    @OneToOne
    private Long roomId;
    @OneToMany
    private List<SpecialEquipment> specialEquipments = new ArrayList<>();
}
