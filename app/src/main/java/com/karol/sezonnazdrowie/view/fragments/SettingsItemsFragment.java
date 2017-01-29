package com.karol.sezonnazdrowie.view.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.data.Database;
import com.karol.sezonnazdrowie.data.FoodItem;
import com.karol.sezonnazdrowie.view.FragmentsActivity;

import java.util.ArrayList;

public class SettingsItemsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs_list);

        PreferenceScreen screen = this.getPreferenceScreen();
        ArrayList<FoodItem> list = null;
        if (getArguments().getString(FragmentsActivity.INTENT_WHAT).equals(FragmentsActivity.INTENT_WHAT_FRUITS))
            list = Database.getInstance().getAllFruits();
        else if (getArguments().getString(FragmentsActivity.INTENT_WHAT).equals(FragmentsActivity.INTENT_WHAT_VEGETABLES))
            list = Database.getInstance().getAllVegetables();

        for (FoodItem item : list) {
            if (item.getStartDay1() == null)
                continue;
            CheckBoxPreference pref = new CheckBoxPreference(screen.getContext());
            pref.setTitle(item.getName());
            pref.setKey("pref_noti_" + item.getName());
            pref.setDefaultValue(true);
            screen.addPreference(pref);
        }

        ((FragmentsActivity)getActivity()).setSettingsItemsChanged(false);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        ((FragmentsActivity)getActivity()).setSettingsItemsChanged(true);
    }
}
