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

    @Transaction
    @Query("SELECT * FROM movement_table ORDER BY name ASC")
    LiveData<List<MovementWithPoints>> getMovementsWithPoints();

    @Query("SELECT * FROM movement_table WHERE id = :id")
    LiveData<MovementWithPoints> getMovement(long id);

    @Query("SELECT * FROM movement_table WHERE id = :id")
    Movement getRawMovement(long id);

    @Update
    void updateMovement(Movement movement);
}