package com.example.pathways;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;

import androidx.fragment.app.Fragment;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class EditNoteFragment extends DialogFragment {
    private Button editButton;
    private Button deleteButton;
    private Button cancelButton;
    private Note note;
    private EditText titleEditText;
    private EditText noteEditText;


    public EditNoteFragment(Note n) {
        note = n;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.edit_note_fragment, container, false);
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        cancelButton = getView().findViewById(R.id.cancel_edit_button);
        deleteButton = getView().findViewById(R.id.delete_note_button);
        editButton = getView().findViewById(R.id.save_edit_button);
        titleEditText = getView().findViewById(R.id.note_title);
        noteEditText = getView().findViewById(R.id.note_input_text);

        titleEditText.setText(note.title);
        noteEditText.setText(note.text);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoteActivity activity = ((NoteActivity) getActivity());
                View position = getView().findViewById(R.id.position);
                activity.deleteNote(note);
                dismiss();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NoteActivity activity = ((NoteActivity) getActivity());

                String titleText = titleEditText.getText().toString();

                String inputText = noteEditText.getText().toString();
                activity.updateNote(note, titleText, inputText);
                dismiss();
            }
        });



    }
}