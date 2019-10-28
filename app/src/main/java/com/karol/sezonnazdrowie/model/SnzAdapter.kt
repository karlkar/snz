package com.karol.sezonnazdrowie.model

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.karol.sezonnazdrowie.R
import com.karol.sezonnazdrowie.data.FoodItem
import com.karol.sezonnazdrowie.data.getImageResource
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_item.view.*
import java.util.Collections
import java.util.Comparator

typealias OnItemClickListener = (position: FoodItem, adapterPosition: Int) -> Unit
typealias OnItemLongClickListener = (position: FoodItem, adapterPosition: Int) -> Boolean

class SnzAdapter(
    private val items: List<FoodItem>,
    var gridMode: Boolean,
    private val onItemClickListener: OnItemClickListener,
    private val onItemLongClickListener: OnItemLongClickListener
) : RecyclerView.Adapter<SnzAdapter.SnzViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnzViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item, parent, false)
        return SnzViewHolder(view)
    }

    override fun onBindViewHolder(holder: SnzViewHolder, position: Int) {
        val item = items[position]
        if (gridMode) {
            val context = holder.image.context
            Picasso.get()
                .load(item.getImageResource(context))
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.image)
            with(holder) {
                image.colorFilter = if (item.isEnabled) null else grayScaleFilter
                text.visibility = View.GONE
                image.visibility = View.VISIBLE
            }
        } else {
            with(holder) {
                with(text) {
                    text = item.name.toLowerCase()
                    setTextColor(if (item.isEnabled) Color.BLACK else Color.GRAY)
                    visibility = View.VISIBLE
                }
                image.visibility = View.GONE
            }
        }
        holder.root.setOnClickListener {
            onItemClickListener.invoke(item, holder.adapterPosition)
        }
        holder.root.setOnLongClickListener {
            onItemLongClickListener.invoke(item, holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = items.size

    fun enableItemsAt(date: CalendarDay) {
        items.forEach {
            it.isEnabled = it.existsAt(date.date)
        }
        sortItems()
    }

    fun enableItemAt(position: Int) {
        items.withIndex().forEach {
            it.value.isEnabled = it.index == position
        }
        sortItems()
    }

    private fun sortItems() {
        Collections.sort(items, Comparator { lhs, rhs ->
            if (lhs.isEnabled == rhs.isEnabled) {
                return@Comparator lhs.compareTo(rhs)
            }
            if (lhs.isEnabled) -1 else 1
        })
    }

    inner class SnzViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
        val image: ImageView = root.image
        val text: TextView = root.text
    }

    companion object {

        private val grayScaleFilter: ColorMatrixColorFilter

        init {
            val matrix = ColorMatrix().apply {
                setSaturation(0f)
            }
            grayScaleFilter = ColorMatrixColorFilter(matrix)
        }
    }
}
