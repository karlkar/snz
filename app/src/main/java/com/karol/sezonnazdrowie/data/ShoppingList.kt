package com.karol.sezonnazdrowie.data

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class ShoppingList(application: Application) {

    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(application)

    val items: MutableList<String>
        get() {
            val stringSet = sharedPreferences.getStringSet(PREF_KEY, null) ?: emptySet()
            return stringSet.toMutableList()
        }

    fun addItem(itemName: String) {
        val stringSet = sharedPreferences.getStringSet(PREF_KEY, null) ?: emptySet()
        val copiedStringSet = stringSet.toMutableSet().apply { add(itemName) }
        saveData(copiedStringSet)
    }

    fun deleteItem(itemName: String) {
        val stringSet = sharedPreferences.getStringSet(PREF_KEY, null) ?: emptySet()
        if (stringSet.isNotEmpty()) {
            val copiedStringSet = stringSet.toMutableSet().apply { remove(itemName) }
            saveData(copiedStringSet)
        }
    }

    private fun saveData(set: Set<String>) {
        sharedPreferences
            .edit()
            .remove(PREF_KEY)
            .putStringSet(PREF_KEY, set)
            .apply()
    }

    companion object {

        private const val PREF_KEY = "SHOPPING_LIST"
    }
}
