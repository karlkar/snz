package com.karol.sezonnazdrowie.view.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.data.FoodItem;
import com.karol.sezonnazdrowie.model.MainViewModel;
import com.karol.sezonnazdrowie.view.MainActivity;

import java.util.List;

import androidx.lifecycle.ViewModelProviders;
import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

public class SettingsItemsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private MainViewModel mMainViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mMainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_list, rootKey);

        PreferenceScreen screen = this.getPreferenceScreen();
        List<FoodItem> list = null;
        if (getArguments().getString(MainActivity.INTENT_WHAT)
                .equals(MainActivity.INTENT_WHAT_FRUITS)) {
            list = mMainViewModel.getDatabase().getAllFruits();
        } else if (getArguments().getString(MainActivity.INTENT_WHAT)
                .equals(MainActivity.INTENT_WHAT_VEGETABLES)) {
            list = mMainViewModel.getDatabase().getAllVegetables();
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

        mMainViewModel.setSettingsItemChanged(false);
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
        mMainViewModel.setSettingsItemChanged(true);
    }
}
