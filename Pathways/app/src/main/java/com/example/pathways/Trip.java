package com.example.pathways;

import java.util.ArrayList;

public class Trip {
    private Location startLocation;
    private Location endLocation;
    private ArrayList<Location> locations = new ArrayList<>();

    public Trip(){}

    public Trip(Location start, Location end){}

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
        locations.add(startLocation);
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
        locations.add(endLocation);
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    public void addLocation(Location location) {
        this.locations.add(locations.size() - 1, location);
    }

}
