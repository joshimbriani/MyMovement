package com.joshimbriani.mymovement;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Movement.class}, version = 1, exportSchema = false)
public abstract class MovementRoomDatabase extends RoomDatabase {
    public abstract MovementDao movementDao();

    private static volatile MovementRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static MovementRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MovementRoomDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MovementRoomDatabase.class, "movement_database").addCallback(sRoomDatabaseCallback).build();
            }
        }
        return INSTANCE;
    }

    // TODO(joshimbriani): Take this out when it's time to launch app
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                MovementDao dao = INSTANCE.movementDao();
                dao.deleteAll();

                Movement movement = new Movement("First trip");
                dao.insert(movement);
                movement = new Movement("Cedar Point - March 23rd 2019");
                dao.insert(movement);
                movement = new Movement("HHN - October 28th 2019");
                dao.insert(movement);
            });
        }
    };
}
