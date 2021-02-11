package com.example.pathways;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

public class LocationConverter {
    private LocationConverter() {

    }

    public static Location PlaceToLocation(Place place) {
        Location.Builder builder = new Location.Builder();
        Double rating = place.getRating();

        if (rating == null) {
            rating = -1d;
        }

        return builder.setName(place.getName()).setAddress(place.getAddress())
                .setLatitude(place.getLatLng().latitude).setLongitude(place.getLatLng().longitude)
                .setRating(rating.toString()).build();

    }
}
