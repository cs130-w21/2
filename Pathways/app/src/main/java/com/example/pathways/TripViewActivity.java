package com.example.pathways;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.LatLng;


import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TripViewActivity extends FragmentActivity implements OnMapReadyCallback,
        LocationsListAdapter.AdapterCallbacks {
    private GoogleMap _map;
    private PlacesClient _placesClient;
    private BottomSheetBehavior _bottomSheetBehavior;
    private LocationsListAdapter _locationsListAdapter;
    private AppDatabase _db;
    private TripDao _tripDao;
    private AutocompleteSupportFragment _autocompleteFragment;
    private GeoApiContext _geoApiContext;
    private Polyline _routePolyline;
    private HashMap<String, Marker> _markerMap = new HashMap<>();
    private Executor _executor = Executors.newSingleThreadExecutor();
    private HashMap<Integer, Place> _tempPlaces = new HashMap<>();
    private TextView _startLocationTextView;
    private TextView _destinationTextView;
    FloatingActionButton  _imageFab, _menuFab, _musicFab, _noteFab;
    private boolean _isFabOpen = false;

    enum LocationType {
        START,
        END,
        MIDDLE,
    }

    private Trip _trip;
    // Database entity
    private TripEntity _tripEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Create", "Create");

        setContentView(R.layout.activity_trip_view);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDXx6nHhNO_jNJiFm0ZMp7KPOSK6USBBEg");
        }

        _placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        _autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_frag);

        _db = DatabaseSingleton.getInstance(this);
        _tripDao = _db.tripDao();

        Long tripId = (Long) getIntent().getLongExtra("TRIP ID", 0);
        Log.v("ID", tripId + "");
        _executor.execute(() -> {
            TripEntity tripEntity = _tripDao.findByID(tripId);
            _tripEntity = tripEntity;
            Log.v("NAME", tripEntity.tripName);
            generateTripFromTripEntity();
            tripDependentInit();
        });

        _startLocationTextView = findViewById(R.id.locations_list_start);
        _destinationTextView = findViewById(R.id.locations_list_end);

        _autocompleteFragment.setHint("Add stop");

        if (_geoApiContext == null) {
            _geoApiContext =
                    new GeoApiContext.Builder().apiKey("AIzaSyDXx6nHhNO_jNJiFm0ZMp7KPOSK6USBBEg").build();
        }

        _imageFab = findViewById(R.id.image_button);
        _imageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TripViewActivity.this, ImageViewActivity.class);
                startActivity(intent);

            }
        });

        _menuFab = findViewById(R.id.menu_button);
        _menuFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!_isFabOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        _noteFab = findViewById(R.id.note_button);
        _noteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TripViewActivity.this, NoteView.class);
                startActivity(intent);
            }
        });

        _musicFab = findViewById(R.id.music_button);
        _musicFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TripViewActivity.this, SpotifyActivity.class);
                intent.putExtra("TRIP ID", _tripEntity.tripid);
                startActivity(intent);
            }
        });
    }

    private void tripDependentInit() {
        RecyclerView locationsList = findViewById(R.id.locations_list_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        locationsList.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(locationsList.getContext(),
                layoutManager.getOrientation());
        locationsList.addItemDecoration(dividerItemDecoration);

        _locationsListAdapter = new LocationsListAdapter(this, _trip.getLocations(), this);
        locationsList.setAdapter(_locationsListAdapter);

        _bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.locations_list));

        TextView textViewTripName = findViewById(R.id.itineraryTextViewTripName);
        textViewTripName.setText(_trip.getName().trim());

        String autoCompleteHint = "Add stop";
        if (_tripEntity.placeIds == null || _tripEntity.placeIds.size() == 0) {
            autoCompleteHint = "Add start location";
        } else if (_tripEntity.placeIds.size() == 1) {
            autoCompleteHint = "Add end location";
        }

        _autocompleteFragment.setHint(autoCompleteHint);

        // Specify the types of place data to return.
        _autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.RATING));

        Log.v("Num locs", _trip.getNumLocations() + "");

        _autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LocationType locationType = LocationType.MIDDLE;
                if (_trip.getNumLocations() == 0) {
                    locationType = LocationType.START;
                } else if (_trip.getNumLocations() == 1) {
                    locationType = LocationType.END;
                }

                addPlaceToMapAndTrip(place, locationType, true);

                ArrayList<String> placeIds = new ArrayList<>();
                for (Location location : _trip.getLocations()) {
                    placeIds.add(location.getPlaceId());
                }

                _tripEntity.placeIds = placeIds;

                _executor.execute(() -> _tripDao.updateTrips(_tripEntity));
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });
    }

    private void addPlaceToMapAndTrip(Place place, LocationType locationType, boolean createRoute) {
        MarkerOptions markerOptions =
                new MarkerOptions().position(place.getLatLng()).title(place.getName());

        Location location = new Location(place);
        String rating = "";
        if (place.getRating() != null) {
            rating = String.format("Rating: %s/5, ", place.getRating());
        }
        if (locationType == LocationType.START) {
            _trip.addStartLocation(location);
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            markerOptions.snippet(rating + "Start");
            _autocompleteFragment.setHint("Add end location");
            _startLocationTextView.setText(location.getName());
        } else if (locationType == LocationType.END) {
            _trip.addEndLocation(location);
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_RED));
            markerOptions.snippet("Destination");
            markerOptions.snippet(rating + "Destination");
            _autocompleteFragment.setHint("Add stop");
            _destinationTextView.setText(location.getName());
        } else {
            _trip.addLocation(location);
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            if (!rating.equals("")) {
                markerOptions.snippet(rating.substring(0, rating.length() - 2));
            }
        }

        _locationsListAdapter.notifyDataSetChanged();

        _map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 5f));
        _markerMap.put(place.getId(), _map.addMarker(markerOptions));

        if (createRoute) {
            createRoute();
        }

        for (Location l : _trip.getLocations()) {
            Log.v("locs", l.getName());
        }
    }


    private void createRoute() {
        if (_trip.getNumLocations() <= 1) {
            return;
        }
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                _trip.getEndLocation().getLatitude(),
                _trip.getEndLocation().getLongitude()
        );

        DirectionsApiRequest directions = new DirectionsApiRequest(_geoApiContext);

        directions.alternatives(false);
        directions.origin(
                new com.google.maps.model.LatLng(
                        _trip.getStartLocation().getLatitude(),
                        _trip.getStartLocation().getLongitude()
                )
        );

        if (_trip.getNumLocations() > 2) {
            LatLng[] wayPoints = new LatLng[_trip.getNumLocations() - 2];
            ArrayList<Location> locations = _trip.getLocations();
            for (int i = 1; i < _trip.getNumLocations() - 1; i++) {
                Location location = locations.get(i);
                wayPoints[i - 1] = new LatLng(location.getLatitude(), location.getLongitude());
            }
            directions.waypoints(wayPoints);
        }

        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(final DirectionsResult result) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (result.routes.length > 0) {

                        DirectionsRoute route = result.routes[0];

                        for (int i = 1; i < _trip.getNumLocations(); i++) {
                            _trip.getLocations().get(i).setDuration(route.legs[i - 1].duration.humanReadable);
                        }

                        _locationsListAdapter.notifyDataSetChanged();

                        List<LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                        List<com.google.android.gms.maps.model.LatLng> newDecodedPath = new ArrayList<>();

                        // This loops through all the LatLng coordinates of ONE polyline.
                        for (com.google.maps.model.LatLng latLng : decodedPath) {

                            newDecodedPath.add(new com.google.android.gms.maps.model.LatLng(
                                    latLng.lat,
                                    latLng.lng
                            ));
                        }

                        if (_routePolyline != null) {
                            _routePolyline.remove();
                        }
                        _routePolyline = _map.addPolyline(
                                new PolylineOptions().addAll(newDecodedPath).color(Color.argb(200, 82, 136, 242)));
                        _routePolyline.setClickable(true);

                    }
                });
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e("boo", "calculateDirections: Failed to get directions: " + e.getMessage());

            }
        });
    }

    private void generateTripFromTripEntity() {
        _trip = new Trip(_tripEntity.tripName);
        _trip.setImages(_tripEntity.imageUrls);
        _trip.setNoteIds(_tripEntity.noteIds);

        if (_tripEntity.placeIds == null) {
            Log.v("YO", "Place null");
            return;
        }

        for (int i = 0; i < _tripEntity.placeIds.size(); i++) {
            Log.v("YO", "Place not null?");
            String placeId = _tripEntity.placeIds.get(i);
            final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId,
                    Arrays.asList(Place.Field.ID, Place.Field.NAME,
                            Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.RATING));

            final int finalI = i;

            _placesClient.fetchPlace(request).addOnSuccessListener((fetchPlaceResponse -> {
                Place place = fetchPlaceResponse.getPlace();
                _tempPlaces.put(finalI, place);

                attemptToUpdateMapAndTrip();
            }));
        }

    }

    // Will only update places once all places are retrieved.
    // This is necessary because fetching places by placeid is async obviously.
    private void attemptToUpdateMapAndTrip() {
        if (_tripEntity.placeIds.size() != 0 && _tripEntity.placeIds.size() == _tempPlaces.size()) {
            // Reorder the places so the destination is second for addPlaceTMT()
            Place[] reorderedPlaces = new Place[_tempPlaces.size()];
            reorderedPlaces[0] = _tempPlaces.get(0);
            if (_tempPlaces.size() > 1) {
                reorderedPlaces[1] = _tempPlaces.get(_tempPlaces.size() - 1);
            }

            for (int i = 1; i < _tempPlaces.size() - 1; i++) {
                reorderedPlaces[i + 1] = _tempPlaces.get(i);
            }

            for (int i = 0; i < _tempPlaces.size(); i++) {
                LocationType locationType = LocationType.MIDDLE;
                if (i == 0) {
                    locationType = LocationType.START;
                } else if (i == 1) {
                    locationType = LocationType.END;
                }

                addPlaceToMapAndTrip(reorderedPlaces[i], locationType, false);
            }

            createRoute();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        _map = googleMap;
    }

    @Override
    public void onStopDeleted(String placeId) {
        _trip.removeLocation(placeId);
        _locationsListAdapter.notifyDataSetChanged();

        _tripEntity.placeIds.remove(placeId);
        _executor.execute(new Runnable() {
            @Override
            public void run() {
                _tripDao.updateTrips(_tripEntity);
            }
        });

        _markerMap.get(placeId).remove();
        _markerMap.remove(placeId);
        createRoute();
    }

    private void showFABMenu() {
        _isFabOpen = true;
        _imageFab.animate().translationY(getResources().getDimension(R.dimen.standard_60));
        _noteFab.animate().translationY(getResources().getDimension(R.dimen.standard_120));
        _musicFab.animate().translationY(getResources().getDimension(R.dimen.standard_180));
    }

    private void closeFABMenu() {
        _isFabOpen = false;
        _imageFab.animate().translationY(0);
        _noteFab.animate().translationY(0);
        _musicFab.animate().translationY(0);
    }

    @Override
    public void onItemClicked(String placeId) {
        Marker marker = _markerMap.get(placeId);
        marker.showInfoWindow();
        _bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        _map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 5));
    }
}
