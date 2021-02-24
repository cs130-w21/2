package com.example.pathways;

import androidx.room.Dao;

import java.util.ArrayList;

@Dao
public interface NoteDao {
    class NoteEntity{} // temporary

    // Get all notes associated with a trip
    ArrayList<NoteEntity> getNotesForTrip(Trip trip); // or trip ID

    // Get all notes associated with a location within a trip
    ArrayList<NoteEntity> getNotesForLocation(Trip trip, Location location); // or location ID or whatever

    // Get a note given the note ID
    NoteEntity getNote(int id);

    // Delete note with given ID
    NoteEntity deleteNote(int id);

    // Update note of ID with new information in Note
    NoteEntity updateNote(Note updated, int id);

    // Create a new note
    NoteEntity createNote(Note newNote);
}
