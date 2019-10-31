package com.karol.sezonnazdrowie.view.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceFragmentCompat
import com.karol.sezonnazdrowie.R
import com.karol.sezonnazdrowie.model.MainViewModel
import com.karol.sezonnazdrowie.view.MainActivity
import com.karol.sezonnazdrowie.view.MainActivity.Companion.INTENT_WHAT
import java.util.concurrent.atomic.AtomicBoolean

class SettingsItemsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var mainViewModel: MainViewModel

    private val listenerRegistered = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        mainViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_list, rootKey)

        mainViewModel.isSettingsItemChanged = false
        if (listenerRegistered.compareAndSet(false, true)) {
            preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }

        val screen = this.preferenceScreen
        val list = when (val intentWhat = arguments?.getString(INTENT_WHAT)
            ?: throw IllegalArgumentException("Missing arguments!")) {
            MainActivity.INTENT_WHAT_FRUITS ->
                mainViewModel.database.allFruits.filter { !it.isFullYear() }
            MainActivity.INTENT_WHAT_VEGETABLES ->
                mainViewModel.database.allVegetables.filter { !it.isFullYear() }
            else -> throw IllegalArgumentException("Unknown type of page $intentWhat")
        }

        list.filter { it.startDay1 != null }
            .map { it.name }
            .forEach { name ->
                CheckBoxPreference(screen.context)
                    .apply {
                        title = name
                        key = "pref_noti_$name"
                        isIconSpaceReserved = false
                        setDefaultValue(true)
                    }.also {
                        screen.addPreference(it)
                    }
            }
    }

    override fun onResume() {
        super.onResume()
        if (listenerRegistered.compareAndSet(false, true)) {
            preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }
    }

    override fun onPause() {
        if (listenerRegistered.compareAndSet(true, false)) {
            preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }
        super.onPause()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        mainViewModel.isSettingsItemChanged = true
    }
}
