package com.karol.sezonnazdrowie.view.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceFragment;
import androidx.preference.Preference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.model.SnzAlarmManager;
import com.karol.sezonnazdrowie.view.FragmentsActivity;

import java.util.Set;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private static final String TAG = "SETTINGSFRAGMENT";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs);

        setPreferenceSummary(getPreferenceManager().getSharedPreferences(), "pref_season_start");
        setPreferenceSummary(getPreferenceManager().getSharedPreferences(), "pref_season_end");
        setTimePreferenceSummary(getPreferenceManager().getSharedPreferences(), "pref_notification_hour");

        findPreference("pref_notification_fruit").setOnPreferenceClickListener(this);
        findPreference("pref_notification_vegetable").setOnPreferenceClickListener(this);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((FragmentsActivity)getActivity()).setActionBarTitle(getString(R.string.settings));
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        if (((FragmentsActivity)getActivity()).getSettingsItemsChanged())
            SnzAlarmManager.startSetAlarmsTask(getActivity());
        ((FragmentsActivity)getActivity()).setSettingsItemsChanged(false);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged: key = " + key);
        switch (key) {
            case "pref_season_start":
                setPreferenceSummary(sharedPreferences, key);
                break;
            case "pref_season_end":
                setPreferenceSummary(sharedPreferences, key);
                break;
            case "pref_notification_hour":
                setTimePreferenceSummary(sharedPreferences, key);
                break;
        }

        if (!key.equals("maxReqCode"))
            SnzAlarmManager.startSetAlarmsTask(getActivity());
    }

    private void setPreferenceSummary(SharedPreferences sharedPreferences, String key) {
        Set<String> stringSet = sharedPreferences.getStringSet(key, null);
        if (stringSet == null || stringSet.size() == 0)
            findPreference(key).setSummary(R.string.no_notification);
        else {
            StringBuilder builder = new StringBuilder();
            for (String str : stringSet) {
                if (builder.length() != 0)
                    builder.append(", ");
                builder.append(str);
            }
            findPreference(key).setSummary(builder.toString());
        }
    }

    private void setTimePreferenceSummary(SharedPreferences sharedPreferences, String key) {
        String hourStr = sharedPreferences.getString(key, null);
        if (hourStr == null)
            findPreference(key).setSummary("20:00");
        else
            findPreference(key).setSummary(hourStr);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
//        Fragment fragment = new SettingsItemsFragment();
//        Bundle bundle = new Bundle();
//        if (preference.getKey().equals("pref_notification_fruit"))
//            bundle.putString(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_FRUITS);
//        else if (preference.getKey().equals("pref_notification_vegetable"))
//            bundle.putString(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_VEGETABLES);
//        fragment.setArguments(bundle);
//        ((FragmentsActivity)getActivity()).replaceFragments(fragment);
        return true;
    }
}
