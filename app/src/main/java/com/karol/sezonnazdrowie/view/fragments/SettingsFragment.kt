package com.karol.sezonnazdrowie.view.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.karol.sezonnazdrowie.R
import com.karol.sezonnazdrowie.model.MainViewModel
import com.karol.sezonnazdrowie.model.SnzAlarmManager
import com.karol.sezonnazdrowie.view.MainActivity
import com.karol.sezonnazdrowie.view.controls.TimePreference
import com.karol.sezonnazdrowie.view.controls.TimePreferenceDialogFragmentCompat

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs, rootKey)

        setPreferenceSummary(preferenceManager.sharedPreferences, "pref_season_start")
        setPreferenceSummary(preferenceManager.sharedPreferences, "pref_season_end")
        setTimePreferenceSummary(preferenceManager.sharedPreferences, "pref_notification_hour")

        findPreference<Preference>("pref_notification_fruit")?.let {
            it.onPreferenceClickListener = this
        }
        findPreference<Preference>("pref_notification_vegetable")?.let {
            it.onPreferenceClickListener = this
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainViewModel.setActionBarTitle(getString(R.string.settings))
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        if (mainViewModel.isSettingsItemChanged) { // TODO move logic to viewmodel
            SnzAlarmManager.startSetAlarmsTask(activity!!, mainViewModel.database)
        }
        mainViewModel.isSettingsItemChanged = false
    }

    override fun onPause() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        Log.d(TAG, "onSharedPreferenceChanged: key = $key")
        when (key) {
            "pref_season_start", "pref_season_end" -> setPreferenceSummary(sharedPreferences, key)
            "pref_notification_hour" -> setTimePreferenceSummary(sharedPreferences, key)
        }

        if (key != "maxReqCode") {
            SnzAlarmManager.startSetAlarmsTask(activity!!, mainViewModel.database)
        }
    }

    private fun setPreferenceSummary(sharedPreferences: SharedPreferences, key: String) {
        val stringSet = sharedPreferences.getStringSet(key, null)
        if (stringSet.isNullOrEmpty()) {
            findPreference<Preference>(key)?.setSummary(R.string.no_notification)
        } else {
            val summary = stringSet.joinToString()
            findPreference<Preference>(key)?.summary = summary
        }
    }

    private fun setTimePreferenceSummary(sharedPreferences: SharedPreferences, key: String) {
        val hourStr = sharedPreferences.getString(key, null) ?: "20:00"
        findPreference<Preference>(key)?.let { it.summary = hourStr }
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        val bundle = Bundle().apply {
            if (preference.key == "pref_notification_fruit") {
                putString(MainActivity.INTENT_WHAT, MainActivity.INTENT_WHAT_FRUITS)
            } else if (preference.key == "pref_notification_vegetable") {
                putString(MainActivity.INTENT_WHAT, MainActivity.INTENT_WHAT_VEGETABLES)
            }
        }

        Navigation
            .findNavController(activity!!, R.id.nav_host_fragment)
            .navigate(R.id.settingsItemsFragment2, bundle)

        return true
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        if (preference !is TimePreference) return super.onDisplayPreferenceDialog(preference)

        val fragmentManager = fragmentManager ?: return
        val dialogFragment = fragmentManager.findFragmentByTag("DIALOG")
        if (dialogFragment != null) {
            return
        }

        TimePreferenceDialogFragmentCompat().apply {
            val bundle = Bundle(1).apply {
                putString("key", preference.key) // TODO: Export constants
            }
            arguments = bundle
            setTargetFragment(this@SettingsFragment, 0)
            show(fragmentManager, "DIALOG")
        }
    }

    companion object {

        private const val TAG = "SETTINGSFRAGMENT"
    }
}
