package com.example.pathways;

import com.google.android.libraries.places.api.model.Place;

public class Location {
    private String _name;
    private String _address;
    private double _latitude;
    private double _longitude;
    private String _duration;
    private String _rating;
    private String _placeId;


    public Location() {

    }

    public Location (Place place) {
        String rating = "";
        if (place.getRating() != null) {
            rating = place.getRating() + "";
        }

        String address = "";
        if (place.getAddress() != null) {
            address = place.getAddress();
        }

        this._name = place.getName();
        this._address = address;
        this._latitude = place.getLatLng().latitude;
        this._longitude = place.getLatLng().longitude;
        this._duration = "";
        this._rating = rating;
        this._placeId = place.getId();
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public String getAddress() {
        return _address;
    }

    public void setAddress(String address) {
        this._address = address;
    }

    public double getLatitude() {
        return _latitude;
    }

    public void setLatitude(double _latitude) {
        this._latitude = _latitude;
    }

    public double getLongitude() {
        return _longitude;
    }

    public void setLongitude(double longitude) {
        this._longitude = longitude;
    }

    public String getDuration() {
        return _duration;
    }

    public void setDuration(String duration) {
        this._duration = duration;
    }

    public String getRating() {
        return _rating;
    }

    public void setRating(String rating) {
        this._rating = rating;
    }

    public String getPlaceId() {
        return _placeId;
    }

    public void setPlaceId(String placeId) {
        this._placeId = placeId;
    }
}
