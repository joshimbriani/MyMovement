package com.joshimbriani.mymovement;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class MovementWithPoints {
    @Embedded public Movement movement;
    @Relation(
            parentColumn = "id",
            entityColumn = "movement_id"
    )
    public List<MovementPoint> points;
}
