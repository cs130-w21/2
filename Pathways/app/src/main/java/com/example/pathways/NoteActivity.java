package com.example.pathways;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NoteActivity extends AppCompatActivity {
    NotesAdapter notesAdapter;
    FloatingActionButton addNoteButton;
    private Long tripId;
    private Long locationId;
    private AppDatabase _db;
    private NoteDao _noteDao;
    private TripDao _tripDao;
    private TripEntity _tripEntity;
    private Executor _executor = Executors.newSingleThreadExecutor();
    private ArrayList<Note> notes = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);
        ((AppCompatActivity)this).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        _db = DatabaseSingleton.getInstance(this);
        _noteDao = _db.noteDao();
        _tripDao = _db.tripDao();

        ListView listView = findViewById(R.id.noteListView);

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
        Long tripId = (Long) getIntent().getLongExtra("TRIP ID", 0);
        Log.v("ID", tripId + ""); //change tripdao and everything here to note? which ones
        _executor.execute(() -> {
                TripEntity tripEntity = _tripDao.findByID(tripId);
                _tripEntity = tripEntity;
                Log.v("NAME", tripEntity.tripName);
                addNotesFromNoteIds(); //should add notes from tripEntity
        });
        notesAdapter = new NotesAdapter(this, notes);
        listView.setAdapter(notesAdapter);
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

    public void addNote(Note note)
    {
        notesAdapter.add(note);
        //add note to database
        _executor.execute(() -> {
            NoteEntity noteEntity = new NoteEntity();
            noteEntity.date = note.created;
            noteEntity.text = note.text;
            noteEntity.title = note.title;
            //add placeId later
            //noteId auto generated here
            Long noteId = _noteDao.createNote(noteEntity);
            if(_tripEntity.noteIds == null){
                _tripEntity.noteIds = new ArrayList<>();
            }
            _tripEntity.noteIds.add(noteId);
            _tripDao.updateTrips(_tripEntity);
            Log.v("adding", note.title);
        });
    }

    private void addNotesFromNoteIds() {
        NoteEntity noteEntity;
        if(_tripEntity.noteIds == null){
            _tripEntity.noteIds = new ArrayList<>();
        }
        for(Long noteId : _tripEntity.noteIds){
            noteEntity = _noteDao.findById(noteId); //returns a note entity
            Note note = new Note(noteEntity);
            notesAdapter.add(note);
            Log.v("noteAct", note.title);
        }

    }
}