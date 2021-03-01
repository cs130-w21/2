package com.example.pathways;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.List;


@Entity
public class TripEntity implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public Long tripid;

    @ColumnInfo(name = "trip_name")
    public String tripName;

    @TypeConverters(ListToStringConverter.class)
    @ColumnInfo(name = "location_ids")
    public List<String> placeIds;

    @TypeConverters(ListToStringConverter.class)
    @ColumnInfo(name = "note_ids")
    public List<Long> noteIds;

    @TypeConverters(ListToStringConverter.class)
    @ColumnInfo(name = "image_ids")
    public List<Long> imageIds;

    @TypeConverters(ListToStringConverter.class)
    @ColumnInfo(name = "song_infos")
    public List<SpotifyActivity.SongInfo> songInfos;
}
