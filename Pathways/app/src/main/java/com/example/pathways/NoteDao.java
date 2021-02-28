package com.example.pathways;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;

@Dao
public interface NoteDao {
    // Get note by ID
    @Query("SELECT * FROM noteentity WHERE noteId LIKE :note_id")
    NoteEntity findById(Long note_id);

    // Get all notes associated with a location within a trip
    //@Query("SELECT * FROM noteentity WHERE note_placeId LIKE :placeId") //would this only grab from current trip
    //ArrayList<NoteEntity> getNotesForLocation(String placeId); // or location ID or whatever

    // Update note of ID with new information in Note
    @Update
    void updateNote(NoteEntity note);

    // Create a new note
    @Insert
    long createNote(NoteEntity note);

    // Delete note with given ID
    @Delete
    void deleteNote(NoteEntity note);
}