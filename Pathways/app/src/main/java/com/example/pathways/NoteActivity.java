package com.example.pathways;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.RequiresPermission;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NoteActivity extends AppCompatActivity {
    NotesAdapter notesAdapter;
    FloatingActionButton addNoteButton;
    private Long tripId;

    private AppDatabase _db;
    private NoteDao _noteDao;
    private TripDao _tripDao;
    private TripEntity _tripEntity;
    private Executor _executor = Executors.newSingleThreadExecutor();
    private ArrayList<Note> notes = new ArrayList<>();
    private String _placeId = "";
    private String _locationName = "";
    private TextView _locationTextView;
    private TextView _emptyNotesTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);
        ((AppCompatActivity)this).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        _db = DatabaseSingleton.getInstance(this);
        _noteDao = _db.noteDao();
        _tripDao = _db.tripDao();

        ListView listView = findViewById(R.id.noteListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                Note clicked = (Note) listView.getItemAtPosition(position);
                showReadDialog(clicked);
            }
        });

        _emptyNotesTextView = findViewById(R.id.empty_notes_text);


        tripId = getIntent().getLongExtra("TRIP ID", 0);

        _locationTextView = findViewById(R.id.location_textview);
        String[] idAndName = getIntent().getStringArrayExtra("PLACE ID AND NAME");
        if (idAndName != null) {
            _placeId = idAndName[0];
            _locationName = idAndName[1];

            String locationText = "Location: " + _locationName;
            _locationTextView.setText(locationText);
            _locationTextView.setVisibility(View.VISIBLE);

            String emptyNotesText = "Add notes for the location: " + idAndName[1];
            _emptyNotesTextView.setText(emptyNotesText);
        }

        addNoteButton = findViewById(R.id.addNoteButton);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateDialog();
            }
        });

        Long tripId =  getIntent().getLongExtra("TRIP ID", 0);
        _executor.execute(() -> {
                _tripEntity = _tripDao.findByID(tripId);
                getSupportActionBar().setTitle(_tripEntity.tripName + " Journal");


                addNotesFromNoteIds(); //should add notes from tripEntity
        });

        notesAdapter = new NotesAdapter(this, notes);
        listView.setAdapter(notesAdapter);
    }

    // Reference: https://developer.android.com/guide/topics/ui/dialogs#FullscreenDialog
    public void showCreateDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        CreateNoteFragment newFragment = new CreateNoteFragment();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, newFragment).commit();

    }
    public void showReadDialog(Note note) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ReadNoteFragment newFragment = new ReadNoteFragment(note);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, newFragment).commit();
    }
    public void showEditDialog(Note note) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        EditNoteFragment newFragment = new EditNoteFragment(note);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, newFragment).commit();
    }

    @VisibleForTesting
    void addNoteToView(Note note) {
        note.location = _locationName;
        notesAdapter.add(note);
    }

    public void addNote(Note note)
    {
        _emptyNotesTextView.setVisibility(View.GONE);

        addNoteToView(note);

        //add note to database
        _executor.execute(() -> {
            NoteEntity noteEntity = new NoteEntity();
            noteEntity.date = note.created;
            noteEntity.text = note.text;
            noteEntity.title = note.title;
            noteEntity.placeId = _placeId;
            noteEntity.locationName = _locationName;
            //add placeId later
            //noteId auto generated here
            Long noteId = _noteDao.createNote(noteEntity);
            note.id = noteId;
            if(_tripEntity.noteIds == null){
                _tripEntity.noteIds = new ArrayList<>();
            }
            _tripEntity.noteIds.add(noteId);
            _tripDao.updateTrips(_tripEntity);
            Log.v("adding", note.title);
        });
    }

    public void deleteNote(Note note) {

        notesAdapter.remove(note);
        _executor.execute(() -> {
            _noteDao.deleteNote(_noteDao.findById(note.id));
            _tripEntity.noteIds.remove(_tripEntity.noteIds.indexOf(note.id));
            _tripDao.updateTrips(_tripEntity);
        });
    }

    public void updateNote(Note note, String title, String text) {
        note.title = title;
        note.text = text;
        notesAdapter.notifyDataSetChanged();
        _executor.execute(() -> {
            NoteEntity noteEntity = _noteDao.findById(note.id);
            noteEntity.text = note.text;
            noteEntity.title = note.title;
            _noteDao.updateNote(noteEntity);
        });
    }

    private void addNotesFromNoteIds() {
        NoteEntity noteEntity;
        if(_tripEntity.noteIds == null){
            _tripEntity.noteIds = new ArrayList<>();
        }
        for(Long noteId : _tripEntity.noteIds){
            noteEntity = _noteDao.findById(noteId);
            // If only looking at one location, filter by placeId.
            if (_placeId.equals("") || _placeId.equals(noteEntity.placeId)) {
                Note note = new Note(noteEntity);
                notesAdapter.add(note);
                _emptyNotesTextView.setVisibility(View.GONE);
            }
        }
    }
}