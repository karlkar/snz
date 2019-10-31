package com.karol.sezonnazdrowie.view.controls

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet

import androidx.preference.DialogPreference
import org.threeten.bp.LocalTime

class TimePreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : DialogPreference(context, attrs, defStyleAttr, defStyleRes) {

    var time: LocalTime = LocalTime.of(20, 0)

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any? = a.getString(index)

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
        val defaultAsLong = (defaultValue as String?)?.toLongOrNull() ?: 72000000000000L
        val nanoOfDay = if (restoreValue) {
            getPersistedLong(defaultAsLong)
        } else {
            defaultAsLong
        }

        time = LocalTime.ofNanoOfDay(nanoOfDay)
    }

    fun persistValue(value: LocalTime) {
        persistLong(value.toNanoOfDay())
    }
}
