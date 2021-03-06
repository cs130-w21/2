package com.example.pathways;

import junit.framework.Assert;
import junit.framework.TestCase;
import android.os.SystemClock;
import android.provider.ContactsContract;

import org.junit.Test;

public class NoteActivityTest extends TestCase {
    private NoteActivity note_act = new NoteActivity();
    private AppDatabase _db;
    private NoteDao _notedao;
    private DatabaseSingleton db;
    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();

    }
    @Test
    public void testAddNote() {
//        _db = DatabaseSingleton.getInstance(new NoteActivity());
//        _notedao = _db.noteDao();
        NoteEntity note_ent= new NoteEntity();
//        note_ent.noteId= 0l;
//        note_ent.title= "test";
//        note_ent.text="This is a test";
//        note_ent.date="03/04/2021";
//        note_ent.locationName="Los Angeles";

        Note note = new Note(note_ent);
        //note_act.addNote(note);

        long id_check = note_act.addNote(note);
        callback
        SystemClock.sleep(7000);
        NoteEntity note_check = _notedao.findById(id_check);

        Assert.assertEquals(note.title,note_check.title);
        Assert.assertEquals(note.text, note_check.text);
        Assert.assertEquals(note.created, note_check.date);
        Assert.assertEquals(note.location, note_check.locationName);
    }

    public void testDeleteNote() {
    }

    public void testUpdateNote() {
    }
}

