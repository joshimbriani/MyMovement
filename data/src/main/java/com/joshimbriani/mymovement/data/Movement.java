package com.joshimbriani.mymovement.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.ZonedDateTime;

@Entity(tableName = "movement_table")
public class Movement {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

    @NonNull
    @ColumnInfo(name = "origin")
    private String mOrigin;

    @NonNull
    @ColumnInfo(name = "datetime")
    private ZonedDateTime mDateTime;

    public Movement(@NonNull long id, @NonNull String name, @NonNull String origin, @NonNull ZonedDateTime dateTime) {
        this.id = id;
        this.mName = name;
        this.mOrigin = origin;
        this.mDateTime = dateTime;
    }

    @Ignore
    public Movement(@NonNull String name, @NonNull String origin) {
        this.mName = name;
        this.mOrigin = origin;
        this.mDateTime = ZonedDateTime.now();
    }

    public long getId(){return this.id;}
    public String getName(){return this.mName;}
    public String getOrigin(){return this.mOrigin;}
    public ZonedDateTime getDateTime(){return this.mDateTime;}

    public void setName(String name) {
        mName = name;
    }
}
