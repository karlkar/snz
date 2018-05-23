package com.karol.sezonnazdrowie.view.fragments;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.data.Database;
import com.karol.sezonnazdrowie.data.FoodItem;
import com.karol.sezonnazdrowie.model.SnzAdapter;
import com.karol.sezonnazdrowie.view.FragmentsActivity;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.Calendar;

public class CalendarFragment extends Fragment {
    private static final String TAG = "CALENDARFRAGMENT";

    private static final String PREF_GRID_VIEW_MODE = "PREF_GRID_VIEW_MODE";

    private RecyclerView mFruitsRv;
    private RecyclerView mVegetablesRv;
    private SnzAdapter mFruitAdapter;
    private SnzAdapter mVegetableAdapter;

    private TextView mCalendarHeaderTextView;
    private MaterialCalendarView mCalendarView;
    private MenuItem mMenuItemViewModeSwitch;
    private AppBarLayout mAppBarLayout;

    private int mCurrentMonth;
    private CalendarDay mSelectedDate = null;
    private FoodItem mSelectedFoodItem;
    private boolean mGridViewMode = true;

    private final SnzAdapter.OnItemClickListener mOnItemClickListener = new SnzAdapter.OnItemClickListener() {
        @Override
        public void onClicked(FoodItem foodItem, int position) {
            mSelectedDate = null;
            mCalendarView.setSelectedDate((CalendarDay) null);
            mSelectedFoodItem = foodItem;

            mFruitAdapter.enableItemAt(foodItem.isFruit() ? position : -1);
            mFruitAdapter.notifyDataSetChanged();
            mVegetableAdapter.enableItemAt(foodItem.isFruit() ? -1 : position);
            mVegetableAdapter.notifyDataSetChanged();

            if (mGridViewMode)
                Toast.makeText(getActivity(), mSelectedFoodItem.getName(), Toast.LENGTH_SHORT).show();
            mFruitsRv.smoothScrollToPosition(0);
            mVegetablesRv.smoothScrollToPosition(0);
            mAppBarLayout.setExpanded(true, true);

            CalendarDay currentDay = mCalendarView.getCurrentDate();
            CalendarDay properDay = mSelectedFoodItem.getNearestSeasonDay(currentDay);
            if (properDay == null || currentDay.getMonth() == properDay.getMonth())
                mCalendarView.invalidateDecorators();
            else
                mCalendarView.setCurrentDate(properDay, true);
        }
    };

    private final SnzAdapter.OnItemLongClickListener mOnItemLongClickListener = new SnzAdapter.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(FoodItem foodItem, int position) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(FragmentsActivity.INTENT_ITEM, foodItem);
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.action_food_detail, bundle);
            return true;
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        ((FragmentsActivity) getActivity()).setActionBarTitle(getString(R.string.calendar));
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        mAppBarLayout = view.findViewById(R.id.appbar_layout);
        mFruitsRv = view.findViewById(R.id.fruitsRecyclerView);
        mVegetablesRv = view.findViewById(R.id.vegetablesRecyclerView);

        setupRecyclers();
        prepareCalendarView(view);
        return view;
    }

    private void setupRecyclers() {
        mGridViewMode = PreferenceManager.getDefaultSharedPreferences(
                getActivity()).getBoolean(PREF_GRID_VIEW_MODE, true);
        if (mGridViewMode) {
            mFruitsRv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            mVegetablesRv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            mFruitsRv.setLayoutManager(new LinearLayoutManager(getActivity()));
            mVegetablesRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        mFruitAdapter = new SnzAdapter(
                Database.getInstance().getAllFruits(),
                mGridViewMode,
                mOnItemClickListener,
                mOnItemLongClickListener);
        mFruitsRv.setAdapter(mFruitAdapter);
        mVegetableAdapter = new SnzAdapter(
                Database.getInstance().getAllVegetables(),
                mGridViewMode,
                mOnItemClickListener,
                mOnItemLongClickListener);
        mVegetablesRv.setAdapter(mVegetableAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Fix for selection of grid, when coming back to calendarfragment from other fragment
        if (mSelectedDate != null)
            mCalendarView.setSelectedDate(mSelectedDate);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.calendar_menu, menu);
        mMenuItemViewModeSwitch = menu.findItem(R.id.menu_item_switch);
        updateViewModeSwitchIcon();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateViewModeSwitchIcon() {
        if (mMenuItemViewModeSwitch != null)
            mMenuItemViewModeSwitch.setIcon(mGridViewMode ? R.drawable.icon_text_view : R.drawable.icon_image_view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_switch) {
            changeViewMode();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeViewMode() {
        mGridViewMode = !mGridViewMode;
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .edit()
                .putBoolean(PREF_GRID_VIEW_MODE, mGridViewMode)
                .apply();

        mFruitAdapter.setGridMode(mGridViewMode);
        mFruitsRv.setLayoutManager(
                mGridViewMode ?
                        new GridLayoutManager(getActivity(), 2)
                        : new LinearLayoutManager(getActivity()));
        mFruitAdapter.notifyDataSetChanged();
        mVegetableAdapter.setGridMode(mGridViewMode);
        mVegetablesRv.setLayoutManager(
                mGridViewMode ?
                        new GridLayoutManager(getActivity(), 2)
                        : new LinearLayoutManager(getActivity()));
        mVegetableAdapter.notifyDataSetChanged();
        updateViewModeSwitchIcon();
    }

    private void prepareCalendarView(View view) {
        View calendarArrowLeft = view.findViewById(R.id.caledarArrowLeft);
        View calendarArrowRight = view.findViewById(R.id.caledarArrowRight);
        mCalendarHeaderTextView = view.findViewById(R.id.calendarHeader);
        mCalendarHeaderTextView.setText(String.format(getResources().getStringArray(R.array.monthsWithYear)[Calendar.getInstance().get(Calendar.MONTH)], Calendar.getInstance().get(Calendar.YEAR)));

        mCalendarView = view.findViewById(R.id.calendarView);
        mCalendarView.state().edit().setFirstDayOfWeek(Calendar.MONDAY).commit();
        mCalendarView.setTopbarVisible(false);
        mCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                onSelectedDateChanged(date);
            }
        });
        mCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                mCalendarHeaderTextView.setText(String.format(getResources().getStringArray(R.array.monthsWithYear)[date.getMonth()], date.getYear()));
                mCurrentMonth = date.getMonth();
                mCalendarView.invalidateDecorators();
            }
        });
        calendarArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.goToPrevious();
            }
        });
        calendarArrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.goToNext();
            }
        });

        mCalendarView.addDecorator(new BorderDayDecorator());
        mCalendarView.addDecorator(new SeasonDayDecorator());
        mCalendarView.addDecorator(new SeasonOuterDayDecorator());
        mCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        mCalendarView.setShowOtherDates(MaterialCalendarView.SHOW_OTHER_MONTHS);

        CalendarDay today = CalendarDay.today();
        mCalendarView.setSelectedDate(today);
        mCurrentMonth = mCalendarView.getCurrentDate().getMonth();
        if (mSelectedFoodItem == null)
            onSelectedDateChanged(today);
    }

    private void onSelectedDateChanged(CalendarDay date) {
        mSelectedDate = date;
        mSelectedFoodItem = null;

        mFruitAdapter.enableItemsAt(date);
        mFruitAdapter.notifyDataSetChanged();
        mVegetableAdapter.enableItemsAt(date);
        mVegetableAdapter.notifyDataSetChanged();

        mCalendarView.invalidateDecorators();
    }

    private class BorderDayDecorator implements DayViewDecorator {

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return true;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setSelectionDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.day_normal_selector));
        }
    }

    private class SeasonDayDecorator implements DayViewDecorator {

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return mSelectedFoodItem != null && day.getMonth() == mCurrentMonth && mSelectedFoodItem.existsAt(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setSelectionDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.day_season_selector));
        }
    }

    private class SeasonOuterDayDecorator implements DayViewDecorator {

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return mSelectedFoodItem != null && day.getMonth() != mCurrentMonth && mSelectedFoodItem.existsAt(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setSelectionDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.day_outer_season_selector));
        }
    }
}
