package com.smartrack.smartrack.Model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteCategoriesDao {
    @Query("select FavoriteCategories.category from FavoriteCategories where travelerMail=:travelerMail")
    List<String> getAllFavoriteCategoriesOfTraveler(String travelerMail);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(FavoriteCategories... favoriteCategories);
}
