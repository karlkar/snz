package com.karol.sezonnazdrowie.view.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.data.Database;
import com.karol.sezonnazdrowie.data.FoodItem;
import com.karol.sezonnazdrowie.view.MainActivity;

import java.util.ArrayList;

import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

public class SettingsItemsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_list, rootKey);

        PreferenceScreen screen = this.getPreferenceScreen();
        ArrayList<FoodItem> list = null;
        if (getArguments().getString(MainActivity.INTENT_WHAT)
                .equals(MainActivity.INTENT_WHAT_FRUITS)) {
            list = Database.getInstance().getAllFruits();
        } else if (getArguments().getString(MainActivity.INTENT_WHAT)
                .equals(MainActivity.INTENT_WHAT_VEGETABLES)) {
            list = Database.getInstance().getAllVegetables();
        }

        for (FoodItem item : list) {
            if (item.getStartDay1() == null) {
                continue;
            }
            CheckBoxPreference pref = new CheckBoxPreference(screen.getContext());
            pref.setTitle(item.getName());
            pref.setKey("pref_noti_" + item.getName());
            pref.setDefaultValue(true);
            screen.addPreference(pref);
        }

//        ((MainActivity)getActivity()).setSettingsItemsChanged(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
//        ((MainActivity)getActivity()).setSettingsItemsChanged(true);
    }

//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        ((MainActivity)getActivity()).setSettingsItemsChanged(true);
//    }
}
