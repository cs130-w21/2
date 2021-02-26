package com.example.pathways;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Bitmap;

public class Trip {
    private ArrayList<Location> _locations = new ArrayList<>();
    private String _name;
    private List<Bitmap> _images = new ArrayList<>();
    private List<String> _image_locations = new ArrayList<>();
    private List<Long> _noteIds;

    public Trip() {}

    public Trip(String trip_name){
        _name = trip_name;
    }

    public Trip(Location start, Location end){}

    public void addStartLocation(Location location) {
        this._locations.add(location);
    }

    public void addEndLocation(Location location) {
        this._locations.add(location);
    }

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

    public List<Bitmap> getImages() {
        return _images;
    }
    public List<String> getImageNames() {
        return _image_locations;
    }

    public void addImage(Bitmap bitmap) {
        this._images.add(bitmap);
        this._image_locations.add(this._name);
    }
    public void addImage(Bitmap bitmap, String place) {
        this._images.add(bitmap);
        this._image_locations.add(place);
    }

    public void setImages(List<Bitmap> _images, List<String> _image_locations) {
        this._images = _images;
        this._image_locations = _image_locations;
    }
    public int getNumImages() {return _images.size();}

    public List<Long> getNoteIds() {
        return _noteIds;
    }

    public void setNoteIds(List<Long> _noteIds) {
        this._noteIds = _noteIds;
    }
}
