package com.karol.sezonnazdrowie.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Entity
data class FoodItem(
    var isFruit: Boolean = false,
    @PrimaryKey var name: String,
    var conjugatedName: String? = null,
    var image: String? = null,

    var startDay1: CalendarDay? = null, // TODO: Use tenabp MonthDay https://github.com/JakeWharton/ThreeTenABP
    var endDay1: CalendarDay? = null,
    var startDay2: CalendarDay? = null,
    var endDay2: CalendarDay? = null,

    var desc: String? = null,
    var link: String? = null,

    var water: String? = null,
    var energy: String? = null,
    var protein: String? = null,
    var fat: String? = null,
    var carbohydrates: String? = null,
    var fiber: String? = null,
    var sugars: String? = null,

    var calcium: String? = null,
    var iron: String? = null,
    var magnesium: String? = null,
    var phosphorus: String? = null,
    var potassium: String? = null,
    var sodium: String? = null,
    var zinc: String? = null,

    var vitC: String? = null,
    var thiamin: String? = null,
    var riboflavin: String? = null,
    var niacin: String? = null,
    var vitB6: String? = null,
    var folate: String? = null,
    var vitA: String? = null,
    var vitE: String? = null,
    var vitK: String? = null,

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
        val start1 = startDay1?.calendar?.timeInMillis ?: return true
        val end1 = endDay1?.calendar?.timeInMillis ?: return true

        val cal = date.calendar
        cal.set(Calendar.YEAR, 1970)
        val relDate = cal.timeInMillis

        if (start1 <= end1) {
            if (relDate in start1..end1) {
                return true
            }
        } else {
            if (relDate !in end1..start1) {
                return true
            }
        }

        val start2 = startDay2?.calendar?.timeInMillis ?: return false
        val end2 = endDay2?.calendar?.timeInMillis ?: return false

        return if (start2 <= end2) {
            relDate in start2..end2
        } else {
            relDate !in start2..end2
        }
    }

    override fun toString(): String {
        return name
    }

    override fun compareTo(other: FoodItem): Int {
        return name.compareTo(other.name)
    }

    fun hasProximates(): Boolean {
        return (water != null && !water!!.isEmpty() || energy != null && !energy!!.isEmpty() || protein != null && !protein!!.isEmpty()
                || fat != null && !fat!!.isEmpty() || carbohydrates != null && !carbohydrates!!.isEmpty() || fiber != null && !fiber!!.isEmpty()
                || sugars != null && !sugars!!.isEmpty())
    }

    fun hasMinerals(): Boolean {
        return (carbohydrates != null && !carbohydrates!!.isEmpty() || iron != null && !iron!!.isEmpty() || magnesium != null && !magnesium!!.isEmpty()
                || phosphorus != null && !phosphorus!!.isEmpty() || potassium != null && !potassium!!.isEmpty() || sodium != null && !sodium!!.isEmpty()
                || zinc != null && !zinc!!.isEmpty())
    }

    fun hasVitamins(): Boolean {
        return (vitC != null && !vitC!!.isEmpty() || thiamin != null && !thiamin!!.isEmpty() || riboflavin != null && !riboflavin!!.isEmpty()
                || niacin != null && !niacin!!.isEmpty() || vitB6 != null && !vitB6!!.isEmpty() || folate != null && !folate!!.isEmpty()
                || vitA != null && !vitA!!.isEmpty() || vitE != null && !vitE!!.isEmpty() || vitK != null && !vitK!!.isEmpty())
    }

    fun getNearestSeasonDay(rel: CalendarDay): CalendarDay? {
        return if (startDay1 == null) {
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
        if (startDay1 == null) {
            return null
        }
        val relInDays = rel.month * 30 + rel.day
        val start1InDays = startDay1!!.month * 30 + startDay1!!.day

        val retVal1 = if (start1InDays >= relInDays) {
            CalendarDay.from(rel.year, startDay1!!.month, startDay1!!.day)
        } else {
            CalendarDay.from(rel.year + 1, startDay1!!.month, startDay1!!.day)
        }

        if (startDay2 == null) {
            return retVal1
        }

        val start2InDays = startDay2!!.month * 30 + startDay2!!.day
        val retVal2 = if (start2InDays >= relInDays) {
            CalendarDay.from(rel.year, startDay2!!.month, startDay2!!.day)
        } else {
            CalendarDay.from(rel.year + 1, startDay2!!.month, startDay2!!.day)
        }

        val start1Diff = start1InDays - relInDays
        val start2Diff = start2InDays - relInDays

        return if (start1Diff >= 0 && start2Diff >= 0) {
            if (start1Diff < start2Diff) {
                retVal1
            } else {
                retVal2
            }
        } else if (start1Diff < 0 && start2Diff >= 0) {
            retVal2
        } else {
            CalendarDay.from(rel.year + 1, startDay1!!.month, startDay1!!.day)
        }
    }

    fun getNearestSeasonEnd(rel: CalendarDay): CalendarDay? {
        if (endDay1 == null) {
            return null
        }
        val relInDays = rel.month * 30 + rel.day
        val end1InDays = endDay1!!.month * 30 + endDay1!!.day

        val retVal1 = if (end1InDays >= relInDays) {
            CalendarDay.from(rel.year, endDay1!!.month, endDay1!!.day)
        } else {
            CalendarDay.from(rel.year + 1, endDay1!!.month, endDay1!!.day)
        }

        if (endDay2 == null) {
            return retVal1
        }

        val end2InDays = endDay2!!.month * 30 + endDay2!!.day
        val retVal2 = if (end2InDays >= relInDays) {
            CalendarDay.from(rel.year, endDay2!!.month, endDay2!!.day)
        } else {
            CalendarDay.from(rel.year + 1, endDay2!!.month, endDay2!!.day)
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
            CalendarDay.from(rel.year + 1, endDay1!!.month, endDay1!!.day)
        }
    }

    companion object {

        val DATE_FORMAT_TEXT = SimpleDateFormat("d MMMM", Locale.getDefault())
    }
}
