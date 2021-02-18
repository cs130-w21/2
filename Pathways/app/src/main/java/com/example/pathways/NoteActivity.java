package com.example.pathways;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NoteActivity extends AppCompatActivity {

    FloatingActionButton addNoteButton;

    private class Note {
        public String title;
        public String text;
        public Date created;
        public Note (String title, String text) {
            this.title = title;
            this.text = text;
            created = new Date();
        }
    }

    private class NotesAdapter extends ArrayAdapter<Note> {
        public NotesAdapter(Context context, ArrayList<Note> notes) {
            super(context, 0, notes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Note note = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.note_in_list, parent, false);
            }
            // Lookup view for data population
            TextView noteTitle = (TextView) convertView.findViewById(R.id.noteTitle);
            TextView noteDate = (TextView) convertView.findViewById(R.id.noteDate);
            TextView noteText = (TextView) convertView.findViewById(R.id.noteText);

            // Populate the data into the template view using the data object
            String pattern = "MM/dd/yyyy";
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            String dateStr = df.format(note.created);

            String textStr = note.text;
            if (note.text.length() > 20) {
                textStr = note.text.substring(0,17) + "...";
            }

            noteTitle.setText(note.title);
            noteDate.setText(dateStr);
            noteText.setText(textStr);
            // Return the completed view to render on screen
            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);
        ((AppCompatActivity)this).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        ArrayList notes = new ArrayList<Note>();

        ListView listView = findViewById(R.id.noteListView);

        final NotesAdapter notesAdapter = new NotesAdapter(this, notes);
        notesAdapter.add(new Note("first", "this is the note text"));
        notesAdapter.add(new Note("second", "this is the note text again"));
        listView.setAdapter(notesAdapter);

        addNoteButton = findViewById(R.id.addNoteButton);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
                notesAdapter.add(new Note("added by user", "this note is added by the user"));
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
}