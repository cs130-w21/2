package com.example.pathways;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;


public class MainActivity extends AppCompatActivity {

    private boolean isFABOpen = false;
    FloatingActionButton trip_fab, fab2, fab3, note_fab;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        trip_fab = findViewById(R.id.trip_fab);
        trip_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTripDialog(MainActivity.this);
            }
        });
        fab2 = findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ImageViewActivity.class);
                startActivity(intent);

            }
        });

        fab3 = findViewById(R.id.fab3);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });

        // Added temporarily to easily move to note Activity in debug
        // TODO: remove button and navigation when proper application layout is set up
        note_fab = findViewById(R.id.noteButton);
        note_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NoteView.class);
                startActivity(intent);
            }
        });

        ListView listView = findViewById(R.id.trip_list);
        List<String> trip_list = new ArrayList<>();
        trip_list.add("France"); trip_list.add("England");
        trip_list.add("Australia"); trip_list.add("Austria");
        trip_list.add("Afghanistan"); trip_list.add("Europe");

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, trip_list);
        listView.setAdapter(arrayAdapter);
    }

    private void createTripDialog(Context c) {
        //Found online, credit to Alvin Alexander
        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Enter Name")
                .setMessage("Name your trip")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String trip_name = String.valueOf(taskEditText.getText());
                        Intent intent = new Intent(MainActivity.this, TripViewActivity.class);
                        intent.putExtra("TRIP_NAME", trip_name);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void showFABMenu(){
        isFABOpen=true;
        trip_fab.animate().translationY(-getResources().getDimension(R.dimen.standard_60));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_120));
        note_fab.animate().translationY(-getResources().getDimension(R.dimen.standard_180));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        trip_fab.animate().translationY(0);
        fab2.animate().translationY(0);
        note_fab.animate().translationY(0);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.search_icon);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Trips");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
