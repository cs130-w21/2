package com.example.pathways;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.List;

@Entity
public class ImageEntity {
    @PrimaryKey(autoGenerate = true)
    public Long imageId;

    @ColumnInfo(name = "image_uri")
    public String imageUri;

    @ColumnInfo(name = "image_text")
    public String locationName;

    @ColumnInfo(name = "image_placeId")
    public String placeId;
}