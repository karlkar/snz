package com.karol.sezonnazdrowie.view.controls

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import androidx.fragment.app.Fragment

import androidx.preference.DialogPreference
import androidx.preference.Preference
import androidx.preference.PreferenceDialogFragmentCompat
import org.threeten.bp.LocalTime

@Suppress("DEPRECATION")
class TimePreferenceDialogFragmentCompat : PreferenceDialogFragmentCompat(),
    DialogPreference.TargetFragment {

    private lateinit var timePicker: TimePicker

    override fun onCreateDialogView(context: Context): View {
        return TimePicker(context)
            .apply {
                setIs24HourView(true)
            }
            .also { timePicker = it }
    }

    override fun onBindDialogView(v: View) {
        super.onBindDialogView(v)
        val pref = preference as TimePreference
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.hour = pref.time.hour
            timePicker.minute = pref.time.minute
        } else {
            timePicker.currentHour = pref.time.hour
            timePicker.currentMinute = pref.time.minute
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val pref = preference as TimePreference
            val chosenTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                LocalTime.of(timePicker.hour, timePicker.minute)
            } else {
                LocalTime.of(timePicker.currentHour, timePicker.currentMinute)
            }
            pref.time = chosenTime

            if (pref.callChangeListener(chosenTime)) {
                pref.persistValue(chosenTime)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Preference?> findPreference(key: CharSequence): T? = preference as T

    companion object {
        fun newInstance(preferenceKey: String, targetFragment: Fragment): TimePreferenceDialogFragmentCompat {
            val bundle = Bundle(1).apply {
                putString("key", preferenceKey) // TODO: Export constants
            }
            return TimePreferenceDialogFragmentCompat().apply {
                arguments = bundle
                setTargetFragment(targetFragment, 0)
            }
        }
    }
}
