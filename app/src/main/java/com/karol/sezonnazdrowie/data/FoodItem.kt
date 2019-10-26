package com.karol.sezonnazdrowie.data

import android.annotation.SuppressLint
import com.prolificinteractive.materialcalendarview.CalendarDay
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale

data class FoodItem(
    val isFruit: Boolean = false,
    val name: String,
    val conjugatedName: String? = null,
    val image: String? = null,
    // dates
    val startDay1: CalendarDay? = null,
    val endDay1: CalendarDay? = null,
    val startDay2: CalendarDay? = null,
    val endDay2: CalendarDay? = null,
    // other texts
    val desc: String? = null,
    val link: String? = null,
    // proximates
    val water: String? = null,
    val energy: String? = null,
    val protein: String? = null,
    val fat: String? = null,
    val carbohydrates: String? = null,
    val fiber: String? = null,
    val sugars: String? = null,
    // minerals
    val calcium: String? = null,
    val iron: String? = null,
    val magnesium: String? = null,
    val phosphorus: String? = null,
    val potassium: String? = null,
    val sodium: String? = null,
    val zinc: String? = null,
    // vitamins
    val vitC: String? = null,
    val thiamin: String? = null,
    val riboflavin: String? = null,
    val niacin: String? = null,
    val vitB6: String? = null,
    val folate: String? = null,
    val vitA: String? = null,
    val vitE: String? = null,
    val vitK: String? = null,

    var isEnabled: Boolean = false
) : Comparable<FoodItem> {

    val nearestSeasonString: String
        get() {
            val today = CalendarDay.today()
            val start = getNearestSeasonStart(today) ?: return ""
            val end = getNearestSeasonEnd(today) ?: return ""
            val startDayStr = DATE_FORMAT_TEXT.format(start.date)
            val endDayStr = DATE_FORMAT_TEXT.format(end.date)
            return "$startDayStr - $endDayStr"
        }

    fun existsAt(date: CalendarDay): Boolean {
        if (isFullYear()) return true
        val relDate = CalendarDay.from(date.date.withYear(1970))

        val isInFirstRange = isDateInRange(relDate, startDay1!!, endDay1!!)
        if (isInFirstRange) return true

        if (startDay2 == null || endDay2 == null) return false

        return isDateInRange(relDate, startDay2, endDay2)
    }

    private fun isDateInRange(date: CalendarDay, start: CalendarDay, end: CalendarDay): Boolean {
        return if (start == end || start.isBefore(end)) {
            date.isInRange(start, end)
        } else {
            !date.isInRange(end, start)
        }
    }

    override fun toString(): String = name

    override fun compareTo(other: FoodItem): Int = name.compareTo(other.name)

    fun hasProximates(): Boolean =
        listOfNotNull(water, energy, protein, fat, carbohydrates, fiber, sugars)
            .any { it.isNotEmpty() }

    fun hasMinerals(): Boolean =
        listOfNotNull(calcium, iron, magnesium, phosphorus, potassium, sodium, zinc)
            .any{ it.isNotEmpty() }

    fun hasVitamins(): Boolean =
        listOfNotNull(vitC, thiamin, riboflavin, niacin, vitB6, folate, vitA, vitE, vitK)
            .any{ it.isNotEmpty() }

    fun getNearestSeasonDay(rel: CalendarDay): CalendarDay? {
        return if (isFullYear()) {
            rel
        } else {
            if (existsAt(rel)) {
                rel
            } else {
                getNearestSeasonStart(rel)
            }
        }
    }

    fun getNearestSeasonStart(rel: CalendarDay): CalendarDay? {
        val startDay1 = CalendarDay.from(startDay1?.date?.withYear(rel.year) ?: return null)

        val retVal1 = if (startDay1 == rel || startDay1.isAfter(rel)) {
            startDay1
        } else {
            CalendarDay.from(startDay1.date.withYear(startDay1.year + 1))
        }

        val startDay2 = CalendarDay.from(startDay2?.date?.withYear(rel.year) ?: return retVal1)

        val retVal2 = if (startDay2 == rel || startDay2.isAfter(rel)) {
            startDay2
        } else {
            CalendarDay.from(startDay2.date.withYear(startDay2.year + 1))
        }

        return if (retVal1.isBefore(retVal2)) {
            retVal1
        } else {
            retVal2
        }
    }

    fun getNearestSeasonEnd(rel: CalendarDay): CalendarDay? {
        if (endDay1 == null) {
            return null
        }
        val relInDays = rel.month * 30 + rel.day
        val end1InDays = endDay1.month * 30 + endDay1.day

        val retVal1 = if (end1InDays >= relInDays) {
            CalendarDay.from(rel.year, endDay1.month, endDay1.day)
        } else {
            CalendarDay.from(rel.year + 1, endDay1.month, endDay1.day)
        }

        if (endDay2 == null) {
            return retVal1
        }

        val end2InDays = endDay2.month * 30 + endDay2.day
        val retVal2 = if (end2InDays >= relInDays) {
            CalendarDay.from(rel.year, endDay2.month, endDay2.day)
        } else {
            CalendarDay.from(rel.year + 1, endDay2.month, endDay2.day)
        }

        val start1Diff = end1InDays - relInDays
        val start2Diff = end2InDays - relInDays

        return if (start1Diff >= 0 && start2Diff >= 0) {
            if (start1Diff < start2Diff) {
                retVal1
            } else {
                retVal2
            }
        } else if (start1Diff < 0 && start2Diff >= 0) {
            retVal2
        } else {
            CalendarDay.from(rel.year + 1, endDay1.month, endDay1.day)
        }
    }

    private fun isFullYear() = startDay1 == null || endDay1 == null

    companion object {

        @SuppressLint("ConstantLocale")
        val DATE_FORMAT_TEXT: DateTimeFormatter =
            DateTimeFormatter.ofPattern("d MMMM", Locale.getDefault())
    }
}
