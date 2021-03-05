package com.example.pathways;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

    FloatingActionButton _tripFab;
    ArrayAdapter<String> _arrayAdapter;
    private AppDatabase _db;
    private TripDao _tripDao;
    private UserDao _userDao;
    private UserEntity _user;
    private String _userEmail;
    private Executor _executor = Executors.newSingleThreadExecutor();
    private List<TripEntity> _tripList;
    private List<String> _tripNameList;
    private FirebaseAuth _auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _auth = FirebaseAuth.getInstance();
        _db = DatabaseSingleton.getInstance(this);
        _tripDao = _db.tripDao();
        _userDao = _db.userDao();

        _tripList = new ArrayList<>();
        _tripNameList = new ArrayList<>();

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

        _userEmail = getIntent().getStringExtra("EMAIL");
        _executor.execute(new Runnable() {
            @Override
            public void run() {
                UserEntity userEntity = _userDao.findByEmail(_userEmail);
                if (userEntity == null) {
                    Log.v("New User Email: ", _userEmail);
                    _user = new UserEntity();
                    _user.email = _userEmail;
                    _user.tripIds = new ArrayList<Long>();
                    _executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            _userDao.insert(_user);
                            runOnUiThread(() -> recreate());
                        }
                    });
                } else {
                    Log.v("User Found: ", _userEmail);
                    _user = userEntity;
                    populateTrips(userEntity);
                    final ListView listView = findViewById(R.id.trip_list);
                    _arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, _tripNameList);
                    Log.v("Trip Name List", _tripNameList.toString());

                    runOnUiThread(() -> {
                        listView.setAdapter(_arrayAdapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                final String selectedTripName = (String) listView.getItemAtPosition(position);
                                Log.v("Clicked on: ", selectedTripName);
                                Intent intent = new Intent(MainActivity.this, TripViewActivity.class);
                                intent.putExtra("TRIP ID", _tripList.get(position).tripid);
                                intent.putExtra("EMAIL", _userEmail);
                                startActivity(intent);
                            }
                        });
                    });

                }
            }
        });
    }


    private void populateTrips(UserEntity userEntity) {
        if (userEntity == null || userEntity.tripIds == null) {
            Log.e("populateTrips: ", "User null");
            return;
        }
        _tripNameList.clear();
        _tripList.clear();
        List<Long> trip_id_list = userEntity.tripIds;
        Log.v("Trip ID list: ", trip_id_list.toString());
        for (Long trip_id : trip_id_list) {
            _executor.execute(new Runnable() {
                @Override
                public void run() {
                    TripEntity tripEntity = _tripDao.findByID(trip_id);
                    _tripList.add(tripEntity);
                    _tripNameList.add(tripEntity.tripName);
                    runOnUiThread(() -> _arrayAdapter.notifyDataSetChanged());
                }});
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
                        trip.tripName = trip_name.trim();
                        _executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                Log.v("Inserting trip: ", trip.tripName);
                                Long tripId = _tripDao.insert(trip);

                                Intent intent = new Intent(MainActivity.this, TripViewActivity.class);
                                intent.putExtra("TRIP ID", tripId);
                                intent.putExtra("EMAIL", _userEmail);
                                startActivity(intent);

                                trip.tripid = tripId;
                                _tripList.add(trip);
                                _tripNameList.add(trip.tripName);

                                runOnUiThread(() -> _arrayAdapter.notifyDataSetChanged());

                                if (_user.tripIds == null) {
                                    _user.tripIds = new ArrayList<Long>();
                                }
                                _user.tripIds.add(tripId);
                                _userDao.updateUser(_user);

                            }
                        });

                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
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


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            _auth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Log.v("Clicked View: ", v.toString());
    }
}
