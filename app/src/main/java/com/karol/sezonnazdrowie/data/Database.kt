package com.karol.sezonnazdrowie.data

interface Database {
    val allFruits: List<FoodItem>
    val allVegetables: List<FoodItem>
}