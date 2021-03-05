package com.example.pathways;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

@Entity
public class UserEntity {
    @NonNull
    @PrimaryKey
    public String email;

    @TypeConverters(ListToStringConverter.class)
    @ColumnInfo(name = "trip_ids")
    public List<Long> tripIds;
}
