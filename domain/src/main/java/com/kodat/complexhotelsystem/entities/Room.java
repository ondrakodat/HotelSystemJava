package com.kodat.complexhotelsystem.entities;

import com.kodat.complexhotelsystem.enums.RoomState;
import com.kodat.complexhotelsystem.enums.RoomType;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private Long id;
    private int roomNumber;
    private int floor;
    private RoomType type;
    private int capacity;
    private RoomState state;
}
