package com.karol.sezonnazdrowie.data

import android.annotation.SuppressLint
import org.threeten.bp.LocalDate
import org.threeten.bp.MonthDay
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale

data class FoodItem(
    val isFruit: Boolean = false,
    val name: String,
    val conjugatedName: String? = null,
    val image: String? = null,
    // dates
    val startDay1: MonthDay? = null,
    val endDay1: MonthDay? = null,
    val startDay2: MonthDay? = null,
    val endDay2: MonthDay? = null,
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
            val today = LocalDate.now()
            val start = getNearestSeasonStart(today) ?: return ""
            val end = getNearestSeasonEnd(today) ?: return ""
            val startDayStr = DATE_FORMAT_TEXT.format(start)
            val endDayStr = DATE_FORMAT_TEXT.format(end)
            return "$startDayStr - $endDayStr"
        }

    fun existsAt(date: LocalDate): Boolean {
        if (isFullYear()) return true

        val isInFirstRange = isDateInRange(MonthDay.from(date), startDay1!!, endDay1!!)
        if (isInFirstRange) return true

        if (startDay2 == null || endDay2 == null) return false

        return isDateInRange(MonthDay.from(date), startDay2, endDay2)
    }

    private fun isDateInRange(date: MonthDay, start: MonthDay, end: MonthDay): Boolean {
        return if (start <= end) {
            date.isAfter(start) && date.isBefore(end)
        } else {
            date.isAfter(start) || date.isBefore(end)
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

    fun getNearestSeasonDay(rel: LocalDate): LocalDate? {
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

    private fun MonthDay.findFirstOccurrenceAfter(date: LocalDate): LocalDate {
        val localDate = this.atYear(date.year)
        return if (localDate >= date) {
            localDate
        } else {
            localDate.plusYears(1)
        }
    }

    fun getNearestSeasonStart(rel: LocalDate): LocalDate? {
        if (isFullYear()) return null

        val retVal1 = startDay1!!.findFirstOccurrenceAfter(rel)
        val retVal2 = startDay2?.findFirstOccurrenceAfter(rel) ?: return retVal1

        return if (retVal1 <= retVal2) {
            retVal1
        } else {
            retVal2
        }
    }

    fun getNearestSeasonEnd(rel: LocalDate): LocalDate? {
        if (isFullYear()) return null

        val retVal1 = endDay1!!.findFirstOccurrenceAfter(rel)
        val retVal2 = endDay2?.findFirstOccurrenceAfter(rel) ?: return retVal1

        return if (retVal1 <= retVal2) {
            retVal1
        } else {
            retVal2
        }
    }

    private fun isFullYear() = startDay1 == null || endDay1 == null

    companion object {

        @SuppressLint("ConstantLocale")
        val DATE_FORMAT_TEXT: DateTimeFormatter =
            DateTimeFormatter.ofPattern("d MMMM", Locale.getDefault())
    }
}
