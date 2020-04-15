package com.joshimbriani.mymovement;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "movement_table")
public class Movement {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

    public Movement(@NonNull long id, @NonNull String name) {
        this.id = id;
        this.mName = name;
    }

    @Ignore
    public Movement(@NonNull String name) {this.mName = name;}

    public long getId(){return this.id;}
    public String getName(){return this.mName;}
}
