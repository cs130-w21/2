package com.example.pathways;

public class Location {
    private String _name;
    private String _address;
    private double _latitude;
    private double _longitude;
    private double _duration;
    private String _rating;

    private Location(Builder builder) {
        this._name = builder.name;
        this._address = builder.address;
        this._latitude = builder.latitude;
        this._longitude = builder.longitude;
        this._duration = builder.duration;
        this._rating = builder.rating;
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

    public double get_latitude() {
        return _latitude;
    }

    public void set_latitude(double _latitude) {
        this._latitude = _latitude;
    }

    public double getLongitude() {
        return _longitude;
    }

    public void setLongitude(double longitude) {
        this._longitude = longitude;
    }

    public double getDuration() {
        return _duration;
    }

    public void setDuration(double duration) {
        this._duration = duration;
    }

    public String getRating() {
        return _rating;
    }

    public void setRating(String rating) {
        this._rating = rating;
    }

    public static class Builder {
        private String name;
        private String address;
        private double latitude;
        private double longitude;
        private double duration = -1;
        private String rating = "";

        public Location build() {
            return new Location(this);
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder setDuration(double duration) {
            this.duration = duration;
            return this;
        }

        public Builder setRating(String rating) {
            this.rating = rating;
            return this;
        }
    }
}
