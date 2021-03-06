package com.example.pathways;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotesAdapterUnitTest {
    private NoteActivity activity;

    @Mock
    Context mockContext;

    @Mock
    NotesAdapter mockAdapter;

    @Before
    public void initAll() {
        activity = new NoteActivity();
        activity.notesAdapter = mockAdapter;
    }

    @Test
    public void testAddNote() {
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
}
