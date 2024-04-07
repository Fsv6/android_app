package com.example.web_app.localDb;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {SignalementEntity.class}, version = 2)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "signalements-db").addMigrations(MIGRATION_1_2).allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE signalements ADD COLUMN date INTEGER");
            database.execSQL("ALTER TABLE signalements ADD COLUMN UserId TEXT");
            database.execSQL("ALTER TABLE signalements ADD COLUMN denomination TEXT");
        }
    };


    public abstract SignalementDao signalementDao();
}

