package com.karol.sezonnazdrowie;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by Karol on 03.04.2016.
 */
public class SettingsItemsFragment extends PreferenceFragment {

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
            CheckBoxPreference pref = new CheckBoxPreference(screen.getContext());
            pref.setTitle(item.getName());
            pref.setKey("pref_noti_" + item.getName());
            pref.setDefaultValue(true);
            screen.addPreference(pref);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) super.onCreateView(inflater, container, savedInstanceState);

        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.width = (int) getActivity().getResources().getDimension(R.dimen.main_middle_bar_width);
        layout.setLayoutParams(params);

        return layout;
    }
}
