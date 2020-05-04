package com.joshimbriani.mymovement.db;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.joshimbriani.mymovement.db.Movement;
import com.joshimbriani.mymovement.db.MovementPoint;

import java.util.List;

public class MovementWithPoints {
    @Embedded public Movement movement;
    @Relation(
            parentColumn = "id",
            entityColumn = "movement_id"
    )
    public List<MovementPoint> points;

    public String getDatetimeRange() {
        return "12/31/2019 7:12am - 7:34pm";
    }
}
