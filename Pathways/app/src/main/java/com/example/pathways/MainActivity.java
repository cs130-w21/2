package com.example.pathways;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.lifecycle.Observer;

import android.provider.BaseColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean _isFabOpen = false;
    FloatingActionButton _tripFab, _fab2, _fab3, _noteFab;
    ArrayAdapter<String> _arrayAdapter;
    private AppDatabase _db;
    private TripDao _tripDao;
    private UserDao _userDao;
    private UserEntity _user;
    private Executor _executor = Executors.newSingleThreadExecutor();
    private List<TripEntity> _tripList;
    private List<String> _tripNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _db = DatabaseSingleton.getInstance(this);
        _tripDao = _db.tripDao();
        _userDao = _db.userDao();

        _tripList = new ArrayList<>();
        _tripNameList = new ArrayList<>();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final String email = getIntent().getStringExtra("EMAIL");
        _userDao.findByEmail(email).observe(this, new Observer<UserEntity>() {
            @Override
            public void onChanged(UserEntity userEntity) {
                if (userEntity == null) {
                    Log.v("New User Email: ", email);
                    _user = new UserEntity();
                    _user.email = email;
                    _user.tripIds = new ArrayList<Long>();
                    _executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            _userDao.insert(_user);
                        }
                    });
                } else {
                    Log.v("User Found: ", email);
                    _user = userEntity;
                    populateTrips(userEntity);
                    final ListView listView = findViewById(R.id.trip_list);
                    _arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, _tripNameList);
                    Log.v("Trip Name List", _tripNameList.toString());
                    listView.setAdapter(_arrayAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            final String selectedTripName = (String) listView.getItemAtPosition(position);
                            Log.v("Clicked on: ", selectedTripName);
                            Intent intent = new Intent(MainActivity.this, TripViewActivity.class);
                            intent.putExtra("TRIP", _tripList.get(position));
                            startActivity(intent);
                        }
                    });
                }
            }
        });
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


    }



    private void populateTrips(UserEntity userEntity) {
        if(userEntity == null || userEntity.tripIds == null){
            Log.e("populateTrips: ", "User null");
            return;
        }
        _tripNameList.clear();
        _tripList.clear();
        List<Long> trip_id_list = userEntity.tripIds;
        Log.v("Trip ID list: ", trip_id_list.toString());
        for(Long trip_id: trip_id_list){
            _tripDao.findByID(trip_id).observe(this, new Observer<TripEntity>() {
                @Override
                public void onChanged(TripEntity tripEntity) {
                    _tripList.add(tripEntity);
                    _tripNameList.add(tripEntity.tripName);
                }
            });
        }
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
                                Log.v("Inserting trip: ", trip.tripName);
                                Long tripId = _tripDao.insert(trip);
                                if (_user.tripIds == null){
                                    _user.tripIds = new ArrayList<Long>();
                                }
                                _user.tripIds.add(tripId);
                                _userDao.updateUser(_user);
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
        final CursorAdapter cAdapter = searchView.getSuggestionsAdapter();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                _arrayAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                _arrayAdapter.getFilter().filter(newText);
                return true;
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = cAdapter.getCursor();
                cursor.moveToPosition(position);
                final String selectedTripName = cursor.getString(cursor.getColumnIndex(BaseColumns._ID));
                _tripDao.findByName(selectedTripName).observe(new AppCompatActivity(), new Observer<TripEntity>() {
                    @Override
                    public void onChanged(TripEntity tripEntity) {

                        Intent intent = new Intent(MainActivity.this, TripViewActivity.class);
                        intent.putExtra("TRIP", tripEntity);
                        startActivity(intent);
                    }
                });
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

    @Override
    public void onClick(View v) {
        Log.v("Clicked View: ", v.toString());
    }
}
