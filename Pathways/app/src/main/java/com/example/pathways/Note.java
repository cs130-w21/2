package com.example.pathways;

import java.util.Date;

public class Note {
    public Trip trip;
    public Location location;
    public String title;
    public String text;
    public Date created;
    public Note (String title, String text) {
        this.title = title;
        this.text = text;
        created = new Date();
    }
}