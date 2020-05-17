package com.joshimbriani.mymovement.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MovementPointDao {
    @Insert
    void insert(MovementPoint... movementPoints);

    @Insert
    long insert(MovementPoint movementPoint);

    @Query("DELETE FROM movement_point_table")
    void deleteAll();

    @Query("SELECT * FROM movement_point_table WHERE movement_id = :id ORDER BY datetime desc")
    LiveData<List<MovementPoint>> getPointsByMovement(long id);

    @Query("SELECT * FROM movement_point_table WHERE movement_id = :id ORDER BY datetime desc")
    List<MovementPoint> getPointsByMovementList(long id);
}
