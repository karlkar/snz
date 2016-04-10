package com.karol.sezonnazdrowie;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class SettingsItemsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
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
        LinearLayout layout = (LinearLayout) super.onCreateView(inflater, container, savedInstanceState);
        if (layout != null) {
            ViewGroup.LayoutParams params = layout.getLayoutParams();
            params.width = (int) getActivity().getResources().getDimension(R.dimen.main_middle_bar_width);
            layout.setLayoutParams(params);
        }

        return layout;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        ((FragmentsActivity)getActivity()).setSettingsItemsChanged(true);
    }
}
