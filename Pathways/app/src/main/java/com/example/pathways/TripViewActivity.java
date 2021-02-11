package com.example.pathways;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;


import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class TripViewActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap _map;
    private PlacesClient _placesClient;
    private BottomSheetBehavior _bottomSheetBehavior;
    private LocationsListAdapter _locationsListAdapter;
    private AutocompleteSupportFragment _autocompleteFragment;

    // We will pass this trip in from the home page.
    private Trip _trip = new Trip();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_view);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        _bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.locations_list));

        RecyclerView locationsList = findViewById(R.id.locations_list_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        locationsList.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(locationsList.getContext(),
                layoutManager.getOrientation());
        locationsList.addItemDecoration(dividerItemDecoration);

        _locationsListAdapter = new LocationsListAdapter(this, _trip.getLocations());
        locationsList.setAdapter(_locationsListAdapter);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDXx6nHhNO_jNJiFm0ZMp7KPOSK6USBBEg");
        }

        _placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        _autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_frag);

        String autoCompleteHint = "Add stop";
        if (_trip.hasNoLocations()){
            autoCompleteHint = "Add start location";
        } else if(_trip.getNumLocations() == 1) {
            autoCompleteHint = "Add end location";
        }

        _autocompleteFragment.setHint(autoCompleteHint);

        // Specify the types of place data to return.
        _autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.RATING));

        _autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                MarkerOptions markerOptions =
                        new MarkerOptions().position(place.getLatLng()).title(place.getName());

                Location location = LocationConverter.PlaceToLocation(place);
                String snippet = "";
                if (place.getRating() != null){
                    snippet = String.format("Rating: %s/5, ", place.getRating());
                }
                if (_trip.getNumLocations() == 0) {
                    _trip.setStartLocation(LocationConverter.PlaceToLocation(place));
                    markerOptions.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    markerOptions.snippet(snippet + "Start");
                    _autocompleteFragment.setHint("Add end location");
                } else if (_trip.getNumLocations() == 1) {
                    _trip.setEndLocation(LocationConverter.PlaceToLocation(place));
                    markerOptions.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    markerOptions.snippet("Destination");
                    markerOptions.snippet(snippet + "Destination");
                    _autocompleteFragment.setHint("Add stop");
                } else {
                    _trip.addLocation(location);
                    markerOptions.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    markerOptions.snippet(snippet.substring(0, snippet.length() - 2));
                }

                _locationsListAdapter.notifyDataSetChanged();

                _map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 4f));
                _map.addMarker(markerOptions);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        _map = googleMap;
    }
}
