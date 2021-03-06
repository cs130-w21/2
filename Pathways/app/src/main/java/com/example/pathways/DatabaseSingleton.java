package com.example.pathways;

import android.content.Context;

import androidx.room.Room;

public class DatabaseSingleton {
    private static AppDatabase db;

    /**
     * Gets singleton database
     *
     * @param context - Context with which to create database
     * @return db - The database singleton
     */
    public static AppDatabase getInstance(Context context) {
        if (db == null) {
            db = Room.databaseBuilder(context,
                    AppDatabase.class, "database-name").fallbackToDestructiveMigration().build();
        }
        return db;
    }
}
