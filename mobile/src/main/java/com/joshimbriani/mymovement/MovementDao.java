package com.joshimbriani.mymovement;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface MovementDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Movement movement);

    @Query("DELETE FROM movement_table")
    void deleteAll();

    @Query("SELECT * from movement_table ORDER BY name ASC")
    LiveData<List<Movement>> getMovements();
}
