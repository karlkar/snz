package com.karol.sezonnazdrowie.view.controls

import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.ListView
import com.karol.sezonnazdrowie.R

class SnzDrawer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ListView(
    context,
    attrs,
    defStyle
) {
    init {
        val list = listOf(
            R.string.season_fruits,
            R.string.season_vegetables,
            R.string.season_incoming,
            R.string.calendar,
            R.string.shopping_list,
            R.string.settings
        ).map { context.getString(it) }
        adapter = ArrayAdapter(context, R.layout.drawer_row_layout, R.id.rowText, list)
    }
}
