package com.example.pathways;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;

@Dao
public interface ImageDao {
    // Get image by ID
    @Query("SELECT * FROM imageentity WHERE imageId LIKE :imageId")
    ImageEntity findById(Long imageId);

    // Update note of ID with new information in Note
    @Update
    void updateImage(ImageEntity image);

    // Create a new note
    @Insert
    long insertImage(ImageEntity image);

    // Delete note with given ID
    @Delete
    void deleteImage(ImageEntity image);
}