package com.example.pathways;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TripView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_view);
        TextView text = (TextView) findViewById(R.id.textView);
        text.setText("New Trip Created!");

    }
}
