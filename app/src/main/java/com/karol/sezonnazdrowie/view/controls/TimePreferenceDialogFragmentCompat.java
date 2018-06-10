package com.karol.sezonnazdrowie.view.controls;

import android.content.Context;
import android.view.View;
import android.widget.TimePicker;

import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceDialogFragmentCompat;

public class TimePreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat implements DialogPreference.TargetFragment {

    private TimePicker mTimePicker = null;

    @Override
    protected View onCreateDialogView(Context context) {
        mTimePicker = new TimePicker(context);
        return (mTimePicker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        mTimePicker.setIs24HourView(true);
        TimePreference pref = (TimePreference) getPreference();
        mTimePicker.setCurrentHour(pref.hour);
        mTimePicker.setCurrentMinute(pref.minute);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            TimePreference pref = (TimePreference) getPreference();
            pref.hour = mTimePicker.getCurrentHour();
            pref.minute = mTimePicker.getCurrentMinute();

            String value = TimePreference.timeToString(pref.hour, pref.minute);
            if (pref.callChangeListener(value)) {
                pref.persistStringValue(value);
            }
        }
    }

    @Override
    public Preference findPreference(CharSequence charSequence) {
        return getPreference();
    }
}
