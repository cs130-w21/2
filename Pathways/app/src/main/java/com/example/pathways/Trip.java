package com.example.pathways;

import java.util.ArrayList;
import java.util.List;

public class Trip {
    private ArrayList<Location> _locations = new ArrayList<>();
    private String _name;
    private List<String> _images;
    private List<Long> _noteIds;

    public Trip() {}

    public Trip(String trip_name){
        _name = trip_name;
    }

    public Trip(Location start, Location end){}

    public Location getStartLocation() {
        return _locations.get(0);
    }

    public Location getEndLocation() {
        return _locations.get(_locations.size() - 1);
    }

    public ArrayList<Location> getLocations() {
        return _locations;
    }

    public void setLocations(ArrayList<Location> locations) {
        this._locations = locations;
    }

    // First 2 locations added are start and stop location.
    public void addLocation(Location location) {
        if (_locations.size() < 2) {
            this._locations.add(location);
        } else {
            this._locations.add(_locations.size() - 1, location);
        }
    }


    public String getName(){
        return _name;
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

    public List<String> getImages() {
        return _images;
    }

    public void setImages(List<String> _images) {
        this._images = _images;
    }

    public List<Long> getNoteIds() {
        return _noteIds;
    }

    public void setNoteIds(List<Long> _noteIds) {
        this._noteIds = _noteIds;
    }
}
