package com.karol.sezonnazdrowie.view.controls

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet

import androidx.preference.DialogPreference

class TimePreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : DialogPreference(context, attrs, defStyleAttr, defStyleRes) {

    var hour: Int = 0
    var minute: Int = 0

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any? = a.getString(index)

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
        val time = if (restoreValue) {
            if (defaultValue == null) {
                getPersistedString("20:00")
            } else {
                getPersistedString(defaultValue.toString())
            }
        } else {
            defaultValue as String
        }

        hour = getHour(time)
        minute = getMinute(time)
    }

    fun persistStringValue(value: String) {
        persistString(value)
    }

    companion object {

        fun getHour(time: String): Int {
            val pieces = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            return Integer.parseInt(pieces[0])
        }

        fun getMinute(time: String): Int {
            val pieces = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            return Integer.parseInt(pieces[1])
        }

        fun timeToString(hour: Int, minute: Int): String = String.format("%02d:%02d", hour, minute)
    }
}
