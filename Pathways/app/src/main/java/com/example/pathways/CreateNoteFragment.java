package com.example.pathways;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class CreateNoteFragment extends DialogFragment {
    private Button createButton;
    private Button cancelButton;

    /**
     * Initialize note fragment UI
     */
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

    /**
     * The system calls this to get the DialogFragment's layout, regardless
     * of whether it's being displayed as a dialog or an embedded fragment.
     *
     * @param inflater - Android tool for visualizing views
     * @param container - container of notes
     * @param savedInstanceState - Android specific object
     * @return a completed view to display
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        return inflater.inflate(R.layout.fragment_create_note, container, false);
    }


    /**
     * The system calls this only when creating the layout in a dialog.
     * @param savedInstanceState - Android specific object
     * @return Dialog window
     */
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

    /**
     * Initializes button to navigate from fragment
     *
     * @param view - View to be displayed
     * @param savedInstanceState - Android specific object
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        createButton = getView().findViewById(R.id.create_note_button);
        cancelButton = getView().findViewById(R.id.cancel_creation_button);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoteActivity activity = ((NoteActivity) getActivity());

                EditText titleEditText = getView().findViewById(R.id.note_title);
                String titleText = titleEditText.getText().toString();

                EditText noteEditText = getView().findViewById(R.id.note_input_text);
                String inputText = noteEditText.getText().toString();
                activity.addNote(new Note(titleText, inputText));
                dismiss();
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }
}