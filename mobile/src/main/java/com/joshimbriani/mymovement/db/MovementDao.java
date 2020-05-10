package com.joshimbriani.mymovement.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MovementDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Movement movement);

    @Query("DELETE FROM movement_table")
    void deleteAll();

    @Query("DELETE FROM movement_table WHERE id = :id")
    void delete(long id);

    // We include this extra useless column here to avoid having to make an extra MovementWithPoints class that includes a recent_point field
    @Query("SELECT *, 0 AS recent_point FROM movement_table WHERE id = :id")
    LiveData<MovementWithPoints> getMovement(long id);

    @Query("SELECT * FROM movement_table WHERE id = :id")
    Movement getRawMovement(long id);

    @Transaction
    @Query("SELECT id, name, MAX(datetime) AS recent_point FROM (SELECT * FROM movement_table INNER JOIN movement_point_table ON movement_table.id=movement_point_table.movement_id) GROUP BY id ORDER BY MAX(datetime) DESC")
    LiveData<List<MovementWithPoints>> getRecentMovementWithRecentPoint();

    @Update
    void updateMovement(Movement movement);
}