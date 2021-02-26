package com.example.pathways;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity
public class NoteEntity {
    @PrimaryKey(autoGenerate = true)
    public Long noteId;

    @TypeConverters(ListToStringConverter.class)
    @ColumnInfo(name = "note_placeId")
    public String placeId;

    @ColumnInfo(name = "note_title")
    public String title;

    @ColumnInfo(name = "note_text")
    public String text;

    @ColumnInfo(name = "note_date")
    public String date;

}
