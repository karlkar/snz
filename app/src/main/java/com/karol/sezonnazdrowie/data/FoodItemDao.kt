package com.karol.sezonnazdrowie.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FoodItemDao {

    @get:Query("SELECT * FROM FoodItem WHERE isFruit = 1")
    val allFruits: List<FoodItem>

    @get:Query("SELECT * FROM FoodItem WHERE isFruit = 0")
    val allVegetables: List<FoodItem>

    @Insert
    fun insertAll(items: List<FoodItem>)

    @Query("SELECT * FROM FoodItem WHERE name = :itemName")
    fun getItem(itemName: String): FoodItem

    @Insert
    fun insertAll(items: Array<FoodItem>)
}
