package com.karol.sezonnazdrowie;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "SETTINGSFRAGMENT";

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
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

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SnzAlarmManager.setAlarms(getActivity());
                return null;
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            task.execute();
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
        String prefVal = sharedPreferences.getString(key, null);
        if (prefVal == null || prefVal.length() == 0)
            findPreference(key).setSummary(R.string.no_notification);
        else {
            StringBuilder builder = new StringBuilder();
            String[] src = {","};
            String[] dest = {", "};
            findPreference(key).setSummary(TextUtils.replace(prefVal, src, dest));
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
