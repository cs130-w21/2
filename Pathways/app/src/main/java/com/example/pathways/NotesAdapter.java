package com.example.pathways;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class NotesAdapter extends ArrayAdapter<Note> {
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
