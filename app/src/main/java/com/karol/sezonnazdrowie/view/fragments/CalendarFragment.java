package com.karol.sezonnazdrowie.view.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.data.Database;
import com.karol.sezonnazdrowie.data.FoodItem;
import com.karol.sezonnazdrowie.model.FoodItemAdapter;
import com.karol.sezonnazdrowie.model.FoodItemGridAdapter;
import com.karol.sezonnazdrowie.model.FoodItemListAdapter;
import com.karol.sezonnazdrowie.view.FragmentsActivity;
import com.karol.sezonnazdrowie.view.controls.ExpandableGridView;
import com.karol.sezonnazdrowie.view.controls.ExpandableListView;
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
    private NestedScrollView mScrollView;

    private FrameLayout mFruitsLayout;
    private GridView mFruitsGridView = null;
    private ExpandableListView mFruitsListView = null;
    private FoodItemAdapter mCurrentFruitAdapter;

    private FrameLayout mVegetablesLayout;
    private GridView mVegetablesGridView = null;
    private ExpandableListView mVegetablesListView = null;
    private FoodItemAdapter mCurrentVegetableAdapter;

    private TextView mCalendarHeaderTextView;
    private MaterialCalendarView mCalendarView;

    private int mCurrentMonth;
    private CalendarDay mSelectedDate = null;
    private FoodItem mSelectedFoodItem;
    private boolean mGridViewMode = true;

    private MenuItem mMenuItemViewModeSwitch;

    private final AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSelectedDate = null;
            mCalendarView.setSelectedDate(mSelectedDate);
            mSelectedFoodItem = (FoodItem) parent.getItemAtPosition(position);

            mCurrentFruitAdapter.enableItemAt(mSelectedFoodItem.isFruit() ? position : -1);
            mCurrentFruitAdapter.sortItems();
            mCurrentVegetableAdapter.enableItemAt(mSelectedFoodItem.isFruit() ? -1 : position);
            mCurrentVegetableAdapter.sortItems();

            if (mGridViewMode)
                Toast.makeText(getActivity(), mSelectedFoodItem.getName(), Toast.LENGTH_SHORT).show();
            mScrollView.scrollTo(0, 0);
            mAppBarLayout.setExpanded(true, true);

            CalendarDay currentDay = mCalendarView.getCurrentDate();
            CalendarDay properDay = mSelectedFoodItem.getNearestSeasonDay(currentDay);
            if (properDay == null || currentDay.getMonth() == properDay.getMonth())
                mCalendarView.invalidateDecorators();
            else
                mCalendarView.setCurrentDate(properDay, true);
        }
    };

    private final AdapterView.OnItemLongClickListener mOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Fragment fragment = new FoodItemPageFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(FragmentsActivity.INTENT_ITEM, (Parcelable) parent.getItemAtPosition(position));
            fragment.setArguments(bundle);
            ((FragmentsActivity) getActivity()).replaceFragments(fragment);
            return true;
        }
    };
    private AppBarLayout mAppBarLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        ((FragmentsActivity) getActivity()).setActionBarTitle(getString(R.string.calendar));
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_calendar, null);

        mAppBarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        mScrollView = (NestedScrollView) view.findViewById(R.id.scrollView);

        mFruitsLayout = (FrameLayout) view.findViewById(R.id.fruitsLayout);
        mVegetablesLayout = (FrameLayout) view.findViewById(R.id.vegetablesLayout);

        mGridViewMode = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(PREF_GRID_VIEW_MODE, true);

        if (mGridViewMode)
            showGridView();
        else
            showListView();

        prepareCalendarView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Fix for selection of grid, when coming back to calendarfragment from other fragment
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

    private void showGridView() {
        if (mFruitsGridView == null) {
            mFruitsGridView = new ExpandableGridView(getActivity());
            mFruitsGridView.setAdapter(new FoodItemGridAdapter(getActivity(), Database.getInstance().getAllFruits()));
            mFruitsGridView.setOnItemClickListener(mOnItemClickListener);
            mFruitsGridView.setOnItemLongClickListener(mOnItemLongClickListener);
        }
        mCurrentFruitAdapter = (FoodItemAdapter) mFruitsGridView.getAdapter();
        mFruitsLayout.removeAllViews();
        mFruitsLayout.addView(mFruitsGridView);

        if (mVegetablesGridView == null) {
            mVegetablesGridView = new ExpandableGridView(getActivity());
            mVegetablesGridView.setAdapter(new FoodItemGridAdapter(getActivity(), Database.getInstance().getAllVegetables()));
            mVegetablesGridView.setOnItemClickListener(mOnItemClickListener);
            mVegetablesGridView.setOnItemLongClickListener(mOnItemLongClickListener);
        }
        mCurrentVegetableAdapter = (FoodItemAdapter) mVegetablesGridView.getAdapter();
        mVegetablesLayout.removeAllViews();
        mVegetablesLayout.addView(mVegetablesGridView);
    }

    private void showListView() {
        if (mFruitsListView == null) {
            mFruitsListView = new ExpandableListView(getActivity());
            mFruitsListView.setAdapter(new FoodItemListAdapter(getActivity(), Database.getInstance().getAllFruits()));
            mFruitsListView.setOnItemClickListener(mOnItemClickListener);
            mFruitsListView.setOnItemLongClickListener(mOnItemLongClickListener);
        }
        mCurrentFruitAdapter = (FoodItemAdapter) mFruitsListView.getAdapter();
        mFruitsLayout.removeAllViews();
        mFruitsLayout.addView(mFruitsListView);

        if (mVegetablesListView == null) {
            mVegetablesListView = new ExpandableListView(getActivity());
            mVegetablesListView.setAdapter(new FoodItemListAdapter(getActivity(), Database.getInstance().getAllVegetables()));
            mVegetablesListView.setOnItemClickListener(mOnItemClickListener);
            mVegetablesListView.setOnItemLongClickListener(mOnItemLongClickListener);
        }
        mCurrentVegetableAdapter = (FoodItemAdapter) mVegetablesListView.getAdapter();
        mVegetablesLayout.removeAllViews();
        mVegetablesLayout.addView(mVegetablesListView);
    }

    private void changeViewMode() {
        mGridViewMode = !mGridViewMode;
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean(PREF_GRID_VIEW_MODE, mGridViewMode).apply();
        if (mGridViewMode)
            showGridView();
        else
            showListView();
        updateViewModeSwitchIcon();
    }

    private void prepareCalendarView(View view) {
        View calendarArrowLeft = view.findViewById(R.id.caledarArrowLeft);
        View calendarArrowRight = view.findViewById(R.id.caledarArrowRight);
        mCalendarHeaderTextView = (TextView) view.findViewById(R.id.calendarHeader);
        mCalendarHeaderTextView.setText(String.format(getResources().getStringArray(R.array.monthsWithYear)[Calendar.getInstance().get(Calendar.MONTH)], Calendar.getInstance().get(Calendar.YEAR)));

        mCalendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        mCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
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
                Calendar date = mCalendarView.getCurrentDate().getCalendar();
                date.add(Calendar.MONTH, -1);
                mCalendarView.setCurrentDate(date);
            }
        });
        calendarArrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar date = mCalendarView.getCurrentDate().getCalendar();
                date.add(Calendar.MONTH, 1);
                mCalendarView.setCurrentDate(date);
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
        onSelectedDateChanged(today);
    }

    private void onSelectedDateChanged(CalendarDay date) {
        mSelectedDate = date;
        mSelectedFoodItem = null;
        mCurrentFruitAdapter.enableItemsAt(date);
        mCurrentVegetableAdapter.enableItemsAt(date);
        mCurrentFruitAdapter.sortItems();
        mCurrentVegetableAdapter.sortItems();
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
