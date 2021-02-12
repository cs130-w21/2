package com.example.pathways;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

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
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.LatLng;


import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TripViewActivity extends FragmentActivity implements OnMapReadyCallback,
        LocationsListAdapter.AdapterCallbacks {
    private GoogleMap _map;
    private PlacesClient _placesClient;
    private BottomSheetBehavior _bottomSheetBehavior;
    private LocationsListAdapter _locationsListAdapter;
    private AutocompleteSupportFragment _autocompleteFragment;
    private GeoApiContext _geoApiContext;
    private Polyline _routePolyline;
    private HashMap<String, Marker> _markerMap = new HashMap<>();

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

        _locationsListAdapter = new LocationsListAdapter(this, _trip.getLocations(), this);
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
                String rating = "";
                if (place.getRating() != null){
                    rating = String.format("Rating: %s/5, ", place.getRating());
                }
                if (_trip.getNumLocations() == 0) {
                    _trip.addStartLocation(LocationConverter.PlaceToLocation(place));
                    markerOptions.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    markerOptions.snippet(rating + "Start");
                    _autocompleteFragment.setHint("Add end location");
                } else if (_trip.getNumLocations() == 1) {
                    _trip.addEndLocation(LocationConverter.PlaceToLocation(place));
                    markerOptions.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    markerOptions.snippet("Destination");
                    markerOptions.snippet(rating + "Destination");
                    _autocompleteFragment.setHint("Add stop");
                } else {
                    _trip.addLocation(location);
                    markerOptions.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    if (rating != ""){
                        markerOptions.snippet(rating.substring(0, rating.length() - 2));
                    }
                }

                _locationsListAdapter.notifyDataSetChanged();

                _map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 4f));
                _markerMap.put(place.getId(), _map.addMarker(markerOptions));

                createRoute();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });

        if (_geoApiContext == null) {
            _geoApiContext =
                    new GeoApiContext.Builder().apiKey("AIzaSyDXx6nHhNO_jNJiFm0ZMp7KPOSK6USBBEg").build();
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

                        for (int i = 1;  i < _trip.getNumLocations(); i++) {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        _map = googleMap;
    }

    @Override
    public void onStopDeleted(String placeId) {
        _trip.removeLocation(placeId);
        _locationsListAdapter.notifyDataSetChanged();
        _markerMap.get(placeId).remove();
        _markerMap.remove(placeId);
        createRoute();
    }

    @Override
    public void onItemClicked(String placeId) {
        Marker marker = _markerMap.get(placeId);
        marker.showInfoWindow();
        _bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        _map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 5));
    }
}
