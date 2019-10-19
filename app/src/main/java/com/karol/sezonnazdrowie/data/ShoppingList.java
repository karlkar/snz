package com.karol.sezonnazdrowie.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShoppingList {

    private final static String PREF_KEY = "SHOPPING_LIST";
    private final SharedPreferences mSharedPreferences;

    public ShoppingList(Application application) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
    }

    public void addItem(String itemName) {
        Set<String> stringSet = mSharedPreferences.getStringSet(
                PREF_KEY,
                null);
        if (stringSet == null) {
            stringSet = new HashSet<>();
        } else {
            stringSet = new HashSet<>(stringSet);
        }
        stringSet.add(itemName);
        saveData(stringSet);
    }

    public List<String> getItems() {
        Set<String> stringSet = mSharedPreferences.getStringSet(
                PREF_KEY,
                new HashSet<String>());
        return new ArrayList<>(stringSet);
    }

    public void deleteItem(String itemName) {
        Set<String> stringSet = mSharedPreferences.getStringSet(
                PREF_KEY,
                new HashSet<String>());
        if (!stringSet.isEmpty()) {
            stringSet = new HashSet<>(stringSet);
            stringSet.remove(itemName);
        }
        saveData(stringSet);
    }

    private void saveData(Set<String> set) {
        mSharedPreferences
                .edit()
                .remove(PREF_KEY)
                .putStringSet(PREF_KEY, set)
                .apply();
    }
}
