package com.example.pathways;

import android.app.MediaRouteButton;
import android.content.Context;
import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;
import android.widget.TextView;
import android.view.View;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;




import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;

@RunWith(MockitoJUnitRunner.class)
public class NotesActivityUnitTest {
    private NoteActivity activity;
    private Context test;

    @Mock
    Context mockContext;

    @Mock
    NotesAdapter mockAdapter;

    @Mock
    NoteDao mockNoteDao;

    @Mock
    TripDao mockTripDao;

    private TextView textView;

    @Before
    public void initAll() {
        activity = new NoteActivity();
        activity.notesAdapter = mockAdapter;
        activity._noteDao =  mockNoteDao;
        activity._tripDao = mockTripDao;
    }

    @Test
    public void testAddNoteToView() {
        Note testNote = new Note("test title", "test text");

        ArrayList<Note> addedNotes = new ArrayList<Note>();

        doAnswer(new Answer<Void>() {
             @Override
             public Void answer(InvocationOnMock invocation) throws Throwable {
                 addedNotes.add(testNote);
                 return null;
             }
         }).when(mockAdapter).add(testNote);

        activity.addNoteToView(testNote);

        assertTrue("Note note added to notesAdapter in addNote() call", addedNotes.contains(testNote));
    }
    // I think you do not need this Rule tag
    //@Rule
    //public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.LENIENT);
    @Test
    public void testAddNote() throws InterruptedException {

        Note testNote = new Note("db_test title", "db_test text");
       // activity._emptyNotesTextView = new TextView(mockContext);

        // you don't need to call addNotetoDb until everything is set up
        //activity.addNotetoDb(testNote);

        //NoteEntity note_ent = new NoteEntity();

        // I used any() here to just accept any NoteEntity object
        TripEntity tripEntity = new TripEntity();
        activity._tripEntity = tripEntity;
        when(mockNoteDao.createNote(any(NoteEntity.class))).thenReturn(0l);
        doNothing().when(mockTripDao).updateTrips(tripEntity);
        activity.addNotetoDb(testNote);

        Thread.sleep(2000);

        //activity.addNote(note_ent);
       // doNothing().when(mockNoteDao).updateUser(user);
        //NoteEntity note_check = mockNoteDao.findById(id_check);
        assertTrue("Note ID added to trip entity", tripEntity.noteIds.contains(0l));
        Assert.assertEquals(testNote.id, 0l);
    }


    @Test
    public void testDeleteNoteFromDb() throws InterruptedException{
        Note testNote = new Note("db_test title", "db_test text");
       // activity.addNotetoDb(testNote);

        TripEntity tripEntity = new TripEntity();
        activity._tripEntity = tripEntity;
        tripEntity.noteIds = new ArrayList<>();
        tripEntity.noteIds.add(0l);
        testNote.id = 0l;
        when(mockNoteDao.findById(testNote.id)).thenReturn(null);
        doNothing().when(mockTripDao).updateTrips(tripEntity);
        activity.deleteNoteFromDb(testNote);
        Thread.sleep(2000);
        assertTrue("Note Id removed from trip entity", !tripEntity.noteIds.contains(0l));

       // long id_check = activity.deleteNote(testNote);
       // NoteEntity note_check = mockNoteDao.findById(id_check);
        //Assert.assertEquals(testNote.title,note_check.title);
    }

    @Test
    public void testUpdateNoteDb() throws InterruptedException{
        Note testNote = new Note("db_test title", "db_test text");
        TripEntity tripEntity = new TripEntity();
        activity._tripEntity = tripEntity;
        tripEntity.noteIds = new ArrayList<>();
        tripEntity.noteIds.add(0l);
        testNote.id = 0l;
       // when(mockNoteDao.findById(testNote.id)).thenReturn(null);
       // doNothing().when(mockTripDao).updateTrips(tripEntity);
        //when(mockNoteDao.createNote(any(NoteEntity.class))).thenReturn(0l);
        //doNothing().when(mockTripDao).updateTrips(tripEntity);
        activity.updateNoteDb(testNote, "changed title", "changed text");
        Thread.sleep(2000);
        assertEquals("Note updated", "changed title", testNote.title);
        assertTrue("Note updated in trip entity", tripEntity.noteIds.contains(0l));

    }
    @Test
    public void testDeleteNoteFromView(){
        Note testNote = new Note("test2 title", "test2 text");
        ArrayList<Note> deletedNotes = new ArrayList<Note>();
        deletedNotes.add(testNote);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                deletedNotes.remove(testNote);
                return null;
            }
        }).when(mockAdapter).remove(testNote);
        activity.deleteNoteFromView(testNote);
        assertTrue("Note note deleted in notesAdapter in deleteNote() call", !deletedNotes.contains(testNote));
    }

    @Test
    public void testUpdateNoteInView(){
        Note testNote = new Note("test3 title", "test3 text");
        ArrayList<Note> updatedNotes = new ArrayList<Note>();
        updatedNotes.add(testNote);
        String title = "new title";
        String text = "new text";
        //mockAdapter.notifyDataSetChanged();
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                testNote.title = title;
                testNote.text = text;
                return null;
            }
        }).when(mockAdapter).notifyDataSetChanged();
        activity.updateNoteInView(testNote, title, text);
        assertTrue("Note note contained in notesAdapter in updateNote() call", updatedNotes.contains(testNote));
        assertEquals("Note note updated in notesAdapter in updateNote() call", testNote.title, "new title");
    }



}
