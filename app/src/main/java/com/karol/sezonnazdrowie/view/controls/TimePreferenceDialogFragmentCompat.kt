package com.karol.sezonnazdrowie.view.controls

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.TimePicker

import androidx.preference.DialogPreference
import androidx.preference.Preference
import androidx.preference.PreferenceDialogFragmentCompat

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
            timePicker.hour = pref.hour
            timePicker.minute = pref.minute
        } else {
            timePicker.currentHour = pref.hour
            timePicker.currentMinute = pref.minute
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val pref = preference as TimePreference
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                pref.hour = timePicker.hour
                pref.minute = timePicker.minute
            } else {
                pref.hour = timePicker.currentHour
                pref.minute = timePicker.currentMinute
            }

            val value = TimePreference.timeToString(pref.hour, pref.minute)
            if (pref.callChangeListener(value)) {
                pref.persistStringValue(value)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Preference?> findPreference(key: CharSequence): T? = preference as T
}
