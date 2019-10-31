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
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs, rootKey)

        setPreferenceSummary(preferenceManager.sharedPreferences, "pref_season_start", true)
        setPreferenceSummary(preferenceManager.sharedPreferences, "pref_season_end", false)
        setTimePreferenceSummary(preferenceManager.sharedPreferences, "pref_notification_time")

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
            "pref_season_start" -> setPreferenceSummary(sharedPreferences, key, true)
            "pref_season_end" -> setPreferenceSummary(sharedPreferences, key, false)
            "pref_notification_time" -> setTimePreferenceSummary(sharedPreferences, key)
        }

        if (key in listOf("pref_season_start", "pref_season_end", "pref_notification_time")) {
            SnzAlarmManager.startSetAlarmsTask(activity!!, mainViewModel.database)
        }
    }

    private fun setPreferenceSummary(
        sharedPreferences: SharedPreferences,
        key: String,
        seasonStart: Boolean
    ) {
        val summary = sharedPreferences.getStringSet(key, null)
            ?.asSequence()
            ?.map { SnzAlarmManager.PreNotiTimePeriod.fromPrefValue(it) }
            ?.sortedWith(Comparator { lhs, _ ->
                if (lhs == SnzAlarmManager.PreNotiTimePeriod.AtDay) {
                    -1
                } else {
                    if (lhs == SnzAlarmManager.PreNotiTimePeriod.WeekBefore) -1 else 1
                }
            })
            ?.map {
                when (it) {
                    SnzAlarmManager.PreNotiTimePeriod.AtDay -> {
                        if (seasonStart) R.string.at_the_start_day else R.string.at_the_end_day
                    }
                    SnzAlarmManager.PreNotiTimePeriod.WeekBefore -> R.string.week_before
                    SnzAlarmManager.PreNotiTimePeriod.MonthBefore -> R.string.month_before
                }
            }
            ?.map { context?.getString(it) }
            ?.joinToString()
            ?.takeIf {
                it.isNotEmpty()
            } ?: context?.getString(R.string.no_notification)
        findPreference<Preference>(key)?.summary = summary
    }

    private fun setTimePreferenceSummary(sharedPreferences: SharedPreferences, key: String) {
        val timeNano = sharedPreferences.getLong(key, 72000000000000L)
        val hourStr = LocalTime.ofNanoOfDay(timeNano)
            .format(DateTimeFormatter.ofPattern("HH:mm"))
        findPreference<Preference>(key)?.let { it.summary = hourStr }
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        val bundle = Bundle().apply {
            when (preference.key) {
                "pref_notification_fruit" -> MainActivity.INTENT_WHAT_FRUITS
                "pref_notification_vegetable" -> MainActivity.INTENT_WHAT_VEGETABLES
                else -> return false
            }.let { putString(MainActivity.INTENT_WHAT, it) }
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

        TimePreferenceDialogFragmentCompat.newInstance(preference.key, this)
            .show(fragmentManager, "DIALOG")
    }

    companion object {

        private const val TAG = "SETTINGSFRAGMENT"
    }
}
