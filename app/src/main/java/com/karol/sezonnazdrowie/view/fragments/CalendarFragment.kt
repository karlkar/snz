package com.karol.sezonnazdrowie.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.karol.sezonnazdrowie.R
import com.karol.sezonnazdrowie.data.FoodItem
import com.karol.sezonnazdrowie.model.MainViewModel
import com.karol.sezonnazdrowie.model.OnItemClickListener
import com.karol.sezonnazdrowie.model.OnItemLongClickListener
import com.karol.sezonnazdrowie.model.SnzAdapter
import com.karol.sezonnazdrowie.view.MainActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_calendar.*
import org.threeten.bp.DayOfWeek
import java.util.Calendar

class CalendarFragment : Fragment(), LayoutContainer {

    override lateinit var containerView: View

    private lateinit var fruitAdapter: SnzAdapter
    private lateinit var vegetableAdapter: SnzAdapter

    private lateinit var menuItemViewModeSwitch: MenuItem

    private var currentMonth: Int = 0
    private var selectedDate: CalendarDay? = null
    private var selectedFoodItem: FoodItem? = null
    private var gridViewMode = true

    private val onItemClickListener: OnItemClickListener = { foodItem, position ->
        selectedDate = null
        calendarView.selectedDate = null
        selectedFoodItem = foodItem

        with(fruitAdapter) {
            enableItemAt(if (foodItem.isFruit) position else -1)
            notifyDataSetChanged()
        }
        with(vegetableAdapter) {
            enableItemAt(if (foodItem.isFruit) -1 else position)
            notifyDataSetChanged()
        }

        if (gridViewMode) {
            Toast.makeText(activity, foodItem.name, Toast.LENGTH_SHORT).show()
        }
        fruitsRecyclerView.smoothScrollToPosition(0)
        vegetablesRecyclerView.smoothScrollToPosition(0)
        appbar_layout.setExpanded(true, true)

        val currentDay = calendarView.currentDate
        val properDay = foodItem.getNearestSeasonDay(currentDay.date)
        if (properDay == null || currentDay.month == properDay.month.value) {
            calendarView.invalidateDecorators()
        } else {
            calendarView.setCurrentDate(CalendarDay.from(properDay), true)
        }
    }

    private val onItemLongClickListener: OnItemLongClickListener = { foodItem, _ ->
        val bundle = Bundle().apply {
            putString(MainActivity.INTENT_ITEM, foodItem.name)
        }
        Navigation.findNavController(activity!!, R.id.nav_host_fragment)
            .navigate(R.id.foodItemPageFragment, bundle)
        true
    }

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: ")
        mainViewModel.setActionBarTitle(getString(R.string.calendar))
        setHasOptionsMenu(true)

        containerView = inflater.inflate(R.layout.fragment_calendar, container, false)

        return containerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclers()
        prepareCalendarView()
    }

    private fun setupRecyclers() {
        gridViewMode = PreferenceManager.getDefaultSharedPreferences(activity)
            .getBoolean(PREF_GRID_VIEW_MODE, true)
        if (gridViewMode) {
            fruitsRecyclerView.layoutManager = GridLayoutManager(activity, 2)
            vegetablesRecyclerView.layoutManager = GridLayoutManager(activity, 2)
        } else {
            fruitsRecyclerView.layoutManager = LinearLayoutManager(activity)
            vegetablesRecyclerView.layoutManager = LinearLayoutManager(activity)
        }
        fruitAdapter = SnzAdapter(
            mainViewModel.database.allFruits,
            gridViewMode,
            onItemClickListener,
            onItemLongClickListener
        )
        fruitsRecyclerView.adapter = fruitAdapter
        vegetableAdapter = SnzAdapter(
            mainViewModel.database.allVegetables,
            gridViewMode,
            onItemClickListener,
            onItemLongClickListener
        )
        vegetablesRecyclerView.adapter = vegetableAdapter
    }

    override fun onStart() {
        super.onStart()
        // Fix for selection of grid, when coming back to calendarfragment from other fragment
        selectedDate?.let {
            calendarView.selectedDate = it
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.calendar_menu, menu)
        menuItemViewModeSwitch = menu.findItem(R.id.menu_item_switch)
        updateViewModeSwitchIcon()
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun updateViewModeSwitchIcon() {
        menuItemViewModeSwitch.setIcon(if (gridViewMode) R.drawable.icon_text_view else R.drawable.icon_image_view)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_item_switch) {
            toggleViewMode()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toggleViewMode() {
        gridViewMode = !gridViewMode
        PreferenceManager.getDefaultSharedPreferences(activity)
            .edit()
            .putBoolean(PREF_GRID_VIEW_MODE, gridViewMode)
            .apply()

        fruitAdapter.gridMode = gridViewMode
        fruitsRecyclerView.layoutManager = if (gridViewMode) {
            GridLayoutManager(activity, 2)
        } else {
            LinearLayoutManager(activity)
        }
        fruitAdapter.notifyDataSetChanged()
        vegetableAdapter.gridMode = gridViewMode
        vegetablesRecyclerView.layoutManager = if (gridViewMode) {
            GridLayoutManager(activity, 2)
        } else {
            LinearLayoutManager(activity)
        }
        vegetableAdapter.notifyDataSetChanged()
        updateViewModeSwitchIcon()
    }

    private fun prepareCalendarView() {
        calendarHeader.text = String.format(
            resources.getStringArray(R.array.monthsWithYear)[Calendar.getInstance().get(Calendar.MONTH)],
            Calendar.getInstance().get(Calendar.YEAR)
        )

        caledarArrowLeft.setOnClickListener { calendarView.goToPrevious() }
        caledarArrowRight.setOnClickListener { calendarView.goToNext() }

        with(calendarView) {
            state().edit().setFirstDayOfWeek(DayOfWeek.MONDAY).commit()
            topbarVisible = false
            setOnDateChangedListener { _, date, _ ->
                onSelectedDateChanged(date)
            }
            setOnMonthChangedListener { _, date ->
                calendarHeader.text =
                    String.format(
                        resources.getStringArray(R.array.monthsWithYear)[date.month],
                        date.year
                    )
                currentMonth = date.month
                invalidateDecorators()
            }

            addDecorator(BorderDayDecorator())
            addDecorator(SeasonDayDecorator())
            addDecorator(SeasonOuterDayDecorator())

            selectionMode = MaterialCalendarView.SELECTION_MODE_SINGLE
            showOtherDates = MaterialCalendarView.SHOW_OTHER_MONTHS
        }

        val today = CalendarDay.today()
        calendarView.selectedDate = today
        currentMonth = calendarView.currentDate.month
        if (selectedFoodItem == null) {
            onSelectedDateChanged(today)
        }
    }

    private fun onSelectedDateChanged(date: CalendarDay) {
        selectedDate = date
        selectedFoodItem = null

        with(fruitAdapter) {
            enableItemsAt(date)
            notifyDataSetChanged()
        }
        with(vegetableAdapter) {
            enableItemsAt(date)
            notifyDataSetChanged()
        }

        calendarView.invalidateDecorators()
    }

    private inner class BorderDayDecorator : DayViewDecorator {

        override fun shouldDecorate(day: CalendarDay): Boolean = true

        override fun decorate(view: DayViewFacade) {
            view.setSelectionDrawable(
                ContextCompat.getDrawable(
                    activity!!,
                    R.drawable.day_normal_selector
                )!!
            )
        }
    }

    private inner class SeasonDayDecorator : DayViewDecorator {

        override fun shouldDecorate(day: CalendarDay): Boolean =
            day.month == currentMonth && selectedFoodItem?.existsAt(day.date) == true

        override fun decorate(view: DayViewFacade) {
            view.setSelectionDrawable(
                ContextCompat.getDrawable(
                    activity!!,
                    R.drawable.day_season_selector
                )!!
            )
        }
    }

    private inner class SeasonOuterDayDecorator : DayViewDecorator {

        override fun shouldDecorate(day: CalendarDay): Boolean =
            day.month != currentMonth && selectedFoodItem?.existsAt(day.date) == true

        override fun decorate(view: DayViewFacade) {
            view.setSelectionDrawable(
                ContextCompat.getDrawable(
                    activity!!,
                    R.drawable.day_outer_season_selector
                )!!
            )
        }
    }

    companion object {
        private const val TAG = "CALENDARFRAGMENT"

        private const val PREF_GRID_VIEW_MODE = "PREF_GRID_VIEW_MODE"
    }
}
