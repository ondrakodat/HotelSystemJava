package com.kodat.complexhotelsystem.dtos;

import com.kodat.complexhotelsystem.enums.RoomType;

import java.math.BigDecimal;
import java.util.List;

public class RoomDto {
    private Long id;
    private int roomNumber;
    private int floor;
    private RoomType type;
    private int capacity;
    private List<SpecialEquipmentDto> specialEquipments;
    private float price;

}
