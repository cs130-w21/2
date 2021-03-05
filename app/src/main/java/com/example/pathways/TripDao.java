package com.example.pathways;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TripDao {
    @Query("SELECT * FROM tripentity WHERE tripid LIKE :trip_id")
    TripEntity findByID(Long trip_id);

    @Query("SELECT * FROM tripentity WHERE trip_name LIKE :trip_name")
    TripEntity findByName(String trip_name);

    @Update
    void updateTrips(TripEntity... trips);

    @Insert
    long insert(TripEntity trip);

    @Delete
    void delete(TripEntity trip);
}
