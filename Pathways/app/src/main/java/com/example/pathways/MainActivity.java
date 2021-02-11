package com.example.pathways;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;


public class MainActivity extends AppCompatActivity {

    private boolean _isFabOpen = false;
    FloatingActionButton _tripFab, _fab2, _fab3, _noteFab;
    ArrayAdapter<String> _arrayAdapter;
    private AppDatabase _db;
    private TripDao _tripDao;
    private UserDao _userDao;
    private Executor _executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _db = DatabaseSingleton.getInstance(this);
        _tripDao = _db.tripDao();
        _userDao = _db.userDao();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _tripFab = findViewById(R.id.trip_fab);
        _tripFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTripDialog(MainActivity.this);
            }
        });
        _fab2 = findViewById(R.id.fab2);
        _fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ImageViewActivity.class);
                startActivity(intent);

            }
        });

        _fab3 = findViewById(R.id.fab3);
        _fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!_isFabOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });

        // Added temporarily to easily move to note Activity in debug
        // TODO: remove button and navigation when proper application layout is set up
        _noteFab = findViewById(R.id.noteButton);
        _noteFab.setOnClickListener(new View.OnClickListener() {
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

        _arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, trip_list);
        listView.setAdapter(_arrayAdapter);
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
                        final TripEntity trip = new TripEntity();
                        trip.tripName = trip_name;
                        _executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                Long tripId = _tripDao.insert(trip);
                                Intent intent = new Intent(MainActivity.this, TripViewActivity.class);
                                intent.putExtra("TRIP", trip);
                                startActivity(intent);
                            }
                        });

                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void showFABMenu(){
        _isFabOpen =true;
        _tripFab.animate().translationY(-getResources().getDimension(R.dimen.standard_60));
        _fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_120));
        _noteFab.animate().translationY(-getResources().getDimension(R.dimen.standard_180));
    }

    private void closeFABMenu(){
        _isFabOpen =false;
        _tripFab.animate().translationY(0);
        _fab2.animate().translationY(0);
        _noteFab.animate().translationY(0);

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
                _arrayAdapter.getFilter().filter(newText);
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
