package com.joshimbriani.mymovement;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.ZonedDateTime;

@Entity(tableName = "movement_point_table", foreignKeys = @ForeignKey(entity = Movement.class, parentColumns = "id", childColumns = "movement_id", onDelete = ForeignKey.CASCADE))
public class MovementPoint {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    @ColumnInfo(name = "movement_id")
    private long mMovementId;

    @NonNull
    @ColumnInfo(name = "lat")
    private double mLat;

    @NonNull
    @ColumnInfo(name = "lon")
    private double mLon;

    @NonNull
    @ColumnInfo(name = "datetime")
    private ZonedDateTime mDateTime;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public MovementPoint(@NonNull long id, @NonNull long movementId, @NonNull double lat, @NonNull double lon, ZonedDateTime dateTime) {
        this.id = id;
        this.mMovementId = movementId;
        this.mLat = lat;
        this.mLon = lon;
        this.mDateTime = dateTime;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Ignore
    public MovementPoint(@NonNull long movementId, @NonNull double lat, @NonNull double lon) {
        this.mMovementId = movementId;
        this.mLat = lat;
        this.mLon = lon;
        this.mDateTime = ZonedDateTime.now();
    }

    public long getId(){return this.id;}
    public long getMovementId(){return this.mMovementId;}
    public double getLat(){return this.mLat;}
    public double getLon(){return this.mLon;}
    public ZonedDateTime getDateTime(){return this.mDateTime;}
}
