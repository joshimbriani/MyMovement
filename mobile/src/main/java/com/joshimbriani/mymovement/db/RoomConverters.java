package com.joshimbriani.mymovement.db;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class RoomConverters {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @TypeConverter
    public static ZonedDateTime fromTimestamp(Long value) {
        return value == null ? null : ZonedDateTime.ofInstant(Instant.ofEpochSecond(value), ZoneId.systemDefault());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @TypeConverter
    public static long dateToTimestamp(ZonedDateTime date) {
        return date == null ? null : date.toEpochSecond();
    }
}
