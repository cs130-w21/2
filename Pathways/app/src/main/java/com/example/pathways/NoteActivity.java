package com.example.pathways;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;
import android.widget.ListView;
import java.util.ArrayList;

public class NoteActivity extends AppCompatActivity {
    NotesAdapter notesAdapter;
    FloatingActionButton addNoteButton;
    private Long tripId;
    private Long locationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);
        ((AppCompatActivity)this).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        ArrayList notes = new ArrayList<Note>();

        ListView listView = findViewById(R.id.noteListView);

        notesAdapter = new NotesAdapter(this, notes);
        listView.setAdapter(notesAdapter);

        tripId = (Long) getIntent().getLongExtra("TRIP ID", 0);
        //locationId = (Long) getIntent().getLongExtra("LOC ID", 0);

        //addNote(new Note(tripId.toString(), tripId.toString()));

        addNoteButton = findViewById(R.id.addNoteButton);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    // Reference: https://developer.android.com/guide/topics/ui/dialogs#FullscreenDialog
    public void showDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        CreateNoteFragment newFragment = new CreateNoteFragment();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, newFragment)
                .addToBackStack(null).commit();

    }

    public void addNote(Note note) {
        notesAdapter.add(note);
    }
}