package com.example.pathways;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;

public class Note {
    public Trip trip;
    public String location;
    public String title;
    public String text;
    public String created;
    public Note (String title, String text) {
        this.title = title;
        this.text = text;

        String pattern = "MM/dd/yyyy";
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        created = df.format(new Date());
    }
    public Note (NoteEntity noteEntity) {
        this.title = noteEntity.title;
        this.text = noteEntity.text;
        this.created = noteEntity.date;
        this.location = noteEntity.locationName;
    }
}