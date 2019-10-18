package com.karol.sezonnazdrowie.data;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface FoodItemDao {

    @Query("SELECT * FROM FoodItem WHERE mIsFruit = 1")
    List<FoodItem> getAllFruits();

    @Query("SELECT * FROM FoodItem WHERE mIsFruit = 0")
    List<FoodItem> getAllVegetables();

    @Insert
    void insertAll(List<FoodItem> items);

    @Query("SELECT * FROM FoodItem WHERE mName = :itemName")
    FoodItem getItem(String itemName);

    @Insert
    void insertAll(FoodItem[] items);
}
