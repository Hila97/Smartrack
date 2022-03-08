package com.smartrack.smartrack.Model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlaceDao {
    @Query("select * from Place where id_trip=:tripId")
    List<Place> getPlaceOfTrip(String tripId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Place... places);

    @Delete
    void deleteTrip(Place place);
}