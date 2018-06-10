package com.karol.sezonnazdrowie.view.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.model.MainViewModel;
import com.karol.sezonnazdrowie.model.SnzAlarmManager;
import com.karol.sezonnazdrowie.view.MainActivity;
import com.karol.sezonnazdrowie.view.controls.TimePreference;
import com.karol.sezonnazdrowie.view.controls.TimePreferenceDialogFragmentCompat;

import java.util.Set;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private static final String TAG = "SETTINGSFRAGMENT";
    private MainViewModel mMainViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs, rootKey);

        setPreferenceSummary(getPreferenceManager().getSharedPreferences(), "pref_season_start");
        setPreferenceSummary(getPreferenceManager().getSharedPreferences(), "pref_season_end");
        setTimePreferenceSummary(getPreferenceManager().getSharedPreferences(), "pref_notification_hour");

        findPreference("pref_notification_fruit").setOnPreferenceClickListener(this);
        findPreference("pref_notification_vegetable").setOnPreferenceClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainViewModel.setActionBarTitle(getString(R.string.settings));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        if (mMainViewModel.isSettingsItemChanged()) {
            SnzAlarmManager.startSetAlarmsTask(getActivity(), mMainViewModel.getDatabase());
        }
        mMainViewModel.setSettingsItemChanged(false);
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

        if (!key.equals("maxReqCode")) {
            SnzAlarmManager.startSetAlarmsTask(getActivity(), mMainViewModel.getDatabase());
        }
    }

    private void setPreferenceSummary(SharedPreferences sharedPreferences, String key) {
        Set<String> stringSet = sharedPreferences.getStringSet(key, null);
        if (stringSet == null || stringSet.size() == 0) {
            findPreference(key).setSummary(R.string.no_notification);
        } else {
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
        if (hourStr == null) {
            findPreference(key).setSummary("20:00");
        } else {
            findPreference(key).setSummary(hourStr);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        Bundle bundle = new Bundle();
        if (preference.getKey().equals("pref_notification_fruit")) {
            bundle.putString(MainActivity.INTENT_WHAT, MainActivity.INTENT_WHAT_FRUITS);
        } else if (preference.getKey().equals("pref_notification_vegetable")) {
            bundle.putString(MainActivity.INTENT_WHAT, MainActivity.INTENT_WHAT_VEGETABLES);
        }

        Navigation
                .findNavController(getActivity(), R.id.nav_host_fragment)
                .navigate(R.id.settingsItemsFragment2, bundle);

        return true;
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment dialogFragment = null;
        if (preference instanceof TimePreference) {
            dialogFragment = new TimePreferenceDialogFragmentCompat();
            Bundle bundle = new Bundle(1);
            bundle.putString("key", preference.getKey());
            dialogFragment.setArguments(bundle);
        }

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(this.getFragmentManager(), "DIALOG");
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
