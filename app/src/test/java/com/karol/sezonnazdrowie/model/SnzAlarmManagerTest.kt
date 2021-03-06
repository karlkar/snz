package com.karol.sezonnazdrowie.model

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import com.karol.sezonnazdrowie.data.Database
import com.karol.sezonnazdrowie.data.FoodItem
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.MonthDay
import org.threeten.bp.ZoneOffset

class SnzAlarmManagerTest {

    private val context: Context = mock()
    private val currentDayProvider: SnzAlarmManager.TimeDataProvider = mock {
        on { getCurrentDay() } doReturn LocalDate.of(2019, 10, 29)
        on { getZoneOffset() } doReturn ZoneOffset.ofHours(1)
    }
    private val persistentStorage: PersistentStorage = mock()
    private val alarmManager: AlarmManager = mock()
    private val database: Database = mock()

    @Test
    fun `should not set any alarm when database is empty`() {
        // given
        whenever(persistentStorage.notificationTime)
            .doReturn(LocalTime.of(20, 0))

        // when
        SnzAlarmManager.setAlarms(
            context,
            currentDayProvider,
            persistentStorage,
            alarmManager,
            database
        )

        // then
        verify(alarmManager, never()).set(any(), any(), any())
    }

    @Test
    fun `should set alarm for season start when only one fruit is in the database`() {
        // given
        whenever(database.allFruits).doReturn(
            listOf(
                FoodItem(
                    true,
                    "Fruit",
                    conjugatedName = "fruit",
                    image = "image",
                    startDay1 = MonthDay.of(1, 1),
                    endDay1 = MonthDay.of(2, 1)
                )
            )
        )
        setupMocks()

        // when
        SnzAlarmManager.setAlarms(
            context,
            currentDayProvider,
            persistentStorage,
            alarmManager,
            database
        )

        // then
        verify(alarmManager).set(
            eq(AlarmManager.RTC_WAKEUP),
            eq(1577905200000L),
            anyOrNull()
        )
    }

    @Test
    fun `should set alarm for season end when only one fruit is in the database`() {
        // given
        whenever(database.allFruits).doReturn(
            listOf(
                FoodItem(
                    true,
                    "Fruit",
                    conjugatedName = "fruit",
                    image = "image",
                    startDay1 = MonthDay.of(1, 1),
                    endDay1 = MonthDay.of(2, 1)
                )
            )
        )
        setupMocks()

        // when
        SnzAlarmManager.setAlarms(
            context,
            currentDayProvider,
            persistentStorage,
            alarmManager,
            database
        )

        // then
        verify(alarmManager).set(
            eq(AlarmManager.RTC_WAKEUP),
            eq(1577905200000L),
            anyOrNull()
        )
    }

    @Test
    fun `should set exactly two alarms when only one fruit is in the database`() {
        // given
        whenever(database.allFruits).doReturn(
            listOf(
                FoodItem(
                    true,
                    "Fruit",
                    conjugatedName = "fruit",
                    image = "image",
                    startDay1 = MonthDay.of(1, 1),
                    endDay1 = MonthDay.of(2, 1)
                )
            )
        )
        setupMocks()

        // when
        SnzAlarmManager.setAlarms(
            context,
            currentDayProvider,
            persistentStorage,
            alarmManager,
            database
        )

        // then
        verify(alarmManager, times(2)).set(
            any(),
            any(),
            anyOrNull()
        )
    }

    @Test
    fun `should set exactly 4 alarms when only one fruit is in the database and 3 dates chosen for start notification`() {
        // given
        whenever(database.allFruits).doReturn(
            listOf(
                FoodItem(
                    true,
                    "Fruit",
                    conjugatedName = "fruit",
                    image = "image",
                    startDay1 = MonthDay.of(1, 1),
                    endDay1 = MonthDay.of(2, 1)
                )
            )
        )
        setupMocks(
            seasonStartSet = setOf("DAY", "WEEK", "MONTH")
        )

        // when
        SnzAlarmManager.setAlarms(
            context,
            currentDayProvider,
            persistentStorage,
            alarmManager,
            database
        )

        // then
        verify(alarmManager, times(4)).set(
            any(),
            any(),
            anyOrNull()
        )
    }

    @Test
    fun `should set 3 start alarms when only one fruit is in the database and 3 dates chosen for start notification`() {
        // given
        whenever(database.allFruits).doReturn(
            listOf(
                FoodItem(
                    true,
                    "Fruit",
                    conjugatedName = "fruit",
                    image = "image",
                    startDay1 = MonthDay.of(1, 1),
                    endDay1 = MonthDay.of(2, 1)
                )
            )
        )
        setupMocks(
            seasonStartSet = setOf("DAY", "WEEK", "MONTH")
        )

        // when
        SnzAlarmManager.setAlarms(
            context,
            currentDayProvider,
            persistentStorage,
            alarmManager,
            database
        )

        // then
        verify(alarmManager).set(
            eq(AlarmManager.RTC_WAKEUP),
            eq(1577905200000L),
            anyOrNull()
        )
        verify(alarmManager).set(
            eq(AlarmManager.RTC_WAKEUP),
            eq(1577300400000L),
            anyOrNull()
        )
        verify(alarmManager).set(
            eq(AlarmManager.RTC_WAKEUP),
            eq(1575226800000L),
            anyOrNull()
        )
    }

    @Test
    fun `should not set any alarms when only one fruit is in the database and no dates chosen for start and end notification`() {
        // given
        whenever(database.allFruits).doReturn(
            listOf(
                FoodItem(
                    true,
                    "Fruit",
                    conjugatedName = "fruit",
                    image = "image",
                    startDay1 = MonthDay.of(1, 1),
                    endDay1 = MonthDay.of(2, 1)
                )
            )
        )
        setupMocks(
            seasonStartSet = emptySet(),
            seasonEndSet = emptySet()
        )

        // when
        SnzAlarmManager.setAlarms(
            context,
            currentDayProvider,
            persistentStorage,
            alarmManager,
            database
        )

        // then
        verify(alarmManager, never()).set(
            any(),
            any(),
            anyOrNull()
        )
    }

    @Test
    fun `should clear all invalid alarms when previously more alarms were set`() {
        // given
        whenever(database.allFruits).doReturn(
            listOf(
                FoodItem(
                    true,
                    "Fruit",
                    conjugatedName = "fruit",
                    image = "image",
                    startDay1 = MonthDay.of(1, 1),
                    endDay1 = MonthDay.of(2, 1)
                )
            )
        )
        setupMocks()
        whenever(persistentStorage.lastSetAlarmId)
            .doReturn(10)

        // when
        SnzAlarmManager.setAlarms(
            context,
            currentDayProvider,
            persistentStorage,
            alarmManager,
            database
        )

        // then
        verify(alarmManager, times(8)).cancel(anyOrNull<PendingIntent>())
    }

    @Test
    fun `should not set any alarms when set a preceding period for end notification of month`() {
        // given
        whenever(database.allFruits).doReturn(
            listOf(
                FoodItem(
                    true,
                    "Fruit",
                    conjugatedName = "fruit",
                    image = "image",
                    startDay1 = MonthDay.of(1, 1),
                    endDay1 = MonthDay.of(2, 1)
                )
            )
        )
        setupMocks(
            seasonStartSet = setOf(),
            seasonEndSet = setOf("MONTH")
        )

        // when
        SnzAlarmManager.setAlarms(
            context,
            currentDayProvider,
            persistentStorage,
            alarmManager,
            database
        )

        // then
        verify(alarmManager, never()).set(
            any(),
            any(),
            anyOrNull()
        )
    }

    private fun setupMocks(
        notificationTime: LocalTime = LocalTime.of(20, 0),
        seasonStartSet: Set<String> = setOf("DAY"),
        seasonEndSet: Set<String> = setOf("DAY")
    ) {
        whenever(persistentStorage.notificationTime)
            .doReturn(notificationTime)
        whenever(persistentStorage.isFoodItemNotificationEnabled(any()))
            .doReturn(true)
        whenever(persistentStorage.seasonStartPeriods)
            .doReturn(seasonStartSet)
        whenever(persistentStorage.seasonEndPeriods)
            .doReturn(seasonEndSet)
    }
}