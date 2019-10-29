package com.karol.sezonnazdrowie.model

import android.content.Context
import androidx.lifecycle.ViewModel
import com.karol.sezonnazdrowie.data.FoodItem

class FoodItemPageViewModel : ViewModel() {

    lateinit var foodItem: FoodItem
        private set

    fun setFoodItem(newFoodItem: FoodItem) {
        foodItem = newFoodItem
    }

    fun getThumbnailResource(context: Context): Int {
        return if (foodItem.image.isNullOrEmpty()) {
            android.R.drawable.ic_menu_gallery
        } else {
            context.resources.getIdentifier(
                foodItem.image,
                "drawable",
                context.packageName
            )
        }
    }
}