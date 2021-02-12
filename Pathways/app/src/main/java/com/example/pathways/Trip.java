package com.example.pathways;

import java.util.ArrayList;

public class Trip {
    private Location _startLocation;
    private Location _endLocation;
    private ArrayList<Location> _locations = new ArrayList<>();
    private String _name;

    public Trip() {}

    public Trip(String trip_name){
        _name = trip_name;
    }

    public Trip(Location start, Location end){}

    public Location getStartLocation() {
        return _startLocation;
    }

    public void addStartLocation(Location startLocation) {
        this._startLocation = startLocation;
        _locations.add(startLocation);
    }

    public Location getEndLocation() {
        return _endLocation;
    }

    public void addEndLocation(Location endLocation) {
        this._endLocation = endLocation;
        _locations.add(endLocation);
    }

    public ArrayList<Location> getLocations() {
        return _locations;
    }

    public void setLocations(ArrayList<Location> locations) {
        this._locations = locations;
    }

    public void addLocation(Location location) {
        this._locations.add(_locations.size() - 1, location);
    }

    public int getNumLocations() {
        return _locations.size();
    }

    public boolean hasNoLocations() {
        return _locations.isEmpty();
    }

    public void removeLocation(String placeId) {
        for (Location location : _locations) {
            if (location.getPlaceId() == placeId) {
                _locations.remove(location);
                break;
            }
        }
    }
}
