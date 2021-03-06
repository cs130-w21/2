package com.example.pathways;

import android.app.MediaRouteButton;
import android.content.Context;
import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import android.widget.TextView;


import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;

@RunWith(MockitoJUnitRunner.class)
public class NotesActivityUnitTest {
    private NoteActivity activity;

    @Mock
    Context mockContext;

    @Mock
    NotesAdapter mockAdapter;

    @Mock
    NoteDao mockNoteDao;

    @Before
    public void initAll() {
        activity = new NoteActivity();
        activity.notesAdapter = mockAdapter;
        activity._noteDao =  mockNoteDao;
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
    @Test
    public void testAddNoteToDb(){

//        Note testNote = new Note("db_test title", "db_test text");
//      //  TextView _emptyNotesTextView = null;
//
//        //_emptyNotesTextView.setVisibility(View.GONE);
//        activity.InitemptyNotes();
//        long id_check = activity.addNote(testNote);
//
//        NoteEntity note_check = mockNoteDao.findById(id_check);
//        Assert.assertEquals(testNote.title,note_check.title);


    }

//    @Test
//    public void testDeleteNoteFromDb(){
//        Note testNote = new Note("db_test title", "db_test text");
//        activity.addNote(testNote);
//        long id_check = activity.deleteNote(testNote);
//        NoteEntity note_check = mockNoteDao.findById(id_check);
//        Assert.assertEquals(testNote.title,note_check.title);
//    }
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
