package com.karol.sezonnazdrowie;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Set;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private static final String TAG = "SETTINGSFRAGMENT";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs);

        setPreferenceSummary(getPreferenceManager().getSharedPreferences(), "pref_season_start");
        setPreferenceSummary(getPreferenceManager().getSharedPreferences(), "pref_season_end");

        findPreference("pref_notification_fruit").setOnPreferenceClickListener(this);
        findPreference("pref_notification_vegetable").setOnPreferenceClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((FragmentsActivity)getActivity()).setActionBarTitle(getString(R.string.settings));

        LinearLayout layout = (LinearLayout) super.onCreateView(inflater, container, savedInstanceState);

        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.width = (int) getActivity().getResources().getDimension(R.dimen.main_middle_bar_width);
        layout.setLayoutParams(params);

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        if (((FragmentsActivity)getActivity()).getSettingsItemsChanged())
            startSetAlarmsTask();
        ((FragmentsActivity)getActivity()).setSettingsItemsChanged(false);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    private void startSetAlarmsTask() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SnzAlarmManager.setAlarms(getActivity());
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged: key = " + key);
        if (key.equals("pref_season_start"))
            setPreferenceSummary(sharedPreferences, key);
        else if (key.equals("pref_season_end"))
            setPreferenceSummary(sharedPreferences, key);

        if (!key.equals("maxReqCode"))
            startSetAlarmsTask();
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

    @Override
    public boolean onPreferenceClick(Preference preference) {
        Fragment fragment = new SettingsItemsFragment();
        Bundle bundle = new Bundle();
        if (preference.getKey().equals("pref_notification_fruit"))
            bundle.putString(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_FRUITS);
        else if (preference.getKey().equals("pref_notification_vegetable"))
            bundle.putString(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_VEGETABLES);
        fragment.setArguments(bundle);
        ((FragmentsActivity)getActivity()).replaceFragments(fragment);
        return true;
    }
}
