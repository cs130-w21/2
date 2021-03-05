package com.example.pathways;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

public class NoteActivityTest extends TestCase {
    private NoteActivity note_act = new NoteActivity();
    private NoteDao _notedao;
    private AppDatabase _db;
    @Test
    public void testAddNote() {
        _db = DatabaseSingleton.getInstance(new NoteActivity());
        _notedao = _db.noteDao();
        NoteEntity note_ent= new NoteEntity();
        note_ent.noteId= 0l;
        note_ent.title= "test";
        note_ent.text="This is a test";
        note_ent.date="03/04/2021";
        note_ent.locationName="Los Angeles";
        Note note = new Note(note_ent);
        note_act.addNote(note);
        NoteEntity note_check = _notedao.findById(0l);
        Assert.assertEquals("test",note_check.title);
        Assert.assertEquals("This is a test", note_check.text);
        Assert.assertEquals("03/04/2021", note_check.date);
        Assert.assertEquals("Los Angeles", note_check.locationName);
    }

    public void testDeleteNote() {
    }

    public void testUpdateNote() {
    }
}