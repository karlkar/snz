package com.karol.sezonnazdrowie;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

public class TimePreference extends DialogPreference {

    private TimePicker mTimePicker;
    private int mLastHour;
    private int mLastMinute;

    public static int getHour(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[0]));
    }

    public static int getMinute(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[1]));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setPositiveButtonText(R.string.time_dialog_set);
        setNegativeButtonText(R.string.cancel);
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setPositiveButtonText(R.string.time_dialog_set);
        setNegativeButtonText(R.string.cancel);
    }

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPositiveButtonText(R.string.time_dialog_set);
        setNegativeButtonText(R.string.cancel);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TimePreference(Context context) {
        super(context);

        setPositiveButtonText(R.string.time_dialog_set);
        setNegativeButtonText(R.string.cancel);
    }

    @Override
    protected View onCreateDialogView() {
        mTimePicker = new TimePicker(getContext());
        mTimePicker.setIs24HourView(true);
        return(mTimePicker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        onSetInitialValue(true, "20:00");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(mLastHour);
            mTimePicker.setMinute(mLastMinute);
        } else {
            mTimePicker.setCurrentHour(mLastHour);
            mTimePicker.setCurrentMinute(mLastMinute);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mLastHour = mTimePicker.getHour();
                mLastMinute = mTimePicker.getMinute();
            } else {
                mLastHour = mTimePicker.getCurrentHour();
                mLastMinute = mTimePicker.getCurrentMinute();
            }

            String time = String.valueOf(mLastHour) + ":" + String.format("%02d", mLastMinute);

            if (callChangeListener(time))
                persistString(time);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time;

        if (restoreValue) {
            if (defaultValue == null)
                time = getPersistedString("20:00");
            else
                time = getPersistedString(defaultValue.toString());
        }
        else
            time = defaultValue.toString();

        mLastHour = getHour(time);
        mLastMinute = getMinute(time);
    }
}
