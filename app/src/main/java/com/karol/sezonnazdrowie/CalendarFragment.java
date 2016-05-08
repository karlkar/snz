package com.karol.sezonnazdrowie;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CalendarFragment extends Fragment {

    private View mRootView = null;
    private ScrollView mCalendarScrollView;

    private FrameLayout mFruitsLayout;
    private GridView mFruitsGridView = null;
    private ExpandableListView mFruitsListView = null;
    private FoodItemAdapter mCurrentFruitAdapter;

    private FrameLayout mVegetablesLayout;
    private GridView mVegetablesGridView = null;
    private ExpandableListView mVegetablesListView = null;
    private FoodItemAdapter mCurrentVegetableAdapter;

    private ColorMatrixColorFilter mGrayScaleFilter;
    private TextView mCalendarHeaderTextView;
    private MaterialCalendarView mCalendarView;

    private int mCurrentMonth;
    private CalendarDay mSelectedDate = null;
    private FoodItem mSelectedFoodItem;
    private boolean mGridViewMode = true;

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

            if (!mGridViewMode)
                Toast.makeText(getActivity(), mSelectedFoodItem.getName(), Toast.LENGTH_SHORT).show();
            mCalendarScrollView.fullScroll(ScrollView.FOCUS_UP);

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((FragmentsActivity) getActivity()).setActionBarTitle(getString(R.string.calendar));
        setHasOptionsMenu(true);

        if (mRootView != null)
            return mRootView;

        mRootView = inflater.inflate(R.layout.fragment_calendar, null);

        mCalendarScrollView = (ScrollView) mRootView.findViewById(R.id.calendarScrollView);

        mFruitsLayout = (FrameLayout) mRootView.findViewById(R.id.fruitsLayout);
        mVegetablesLayout = (FrameLayout) mRootView.findViewById(R.id.vegetablesLayout);

        if (mGridViewMode)
            showGridView();
        else
            showListView();

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        mGrayScaleFilter = new ColorMatrixColorFilter(matrix);

        prepareCalendarView(mRootView);
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mRootView != null && mRootView.getParent() != null)
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
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
        super.onCreateOptionsMenu(menu, inflater);
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
            mFruitsGridView.setNumColumns(2);
            mFruitsGridView.setGravity(Gravity.CENTER);
            mFruitsGridView.setAdapter(new FoodItemGridAdapter(getActivity(), Database.getInstance().getAllFruits()));
            mFruitsGridView.setFocusable(false);
            mFruitsGridView.setOnItemClickListener(mOnItemClickListener);
            mFruitsGridView.setOnItemLongClickListener(mOnItemLongClickListener);
        }
        mCurrentFruitAdapter = (FoodItemAdapter) mFruitsGridView.getAdapter();
        mFruitsLayout.removeAllViews();
        mFruitsLayout.addView(mFruitsGridView);

        if (mVegetablesGridView == null) {
            mVegetablesGridView = new ExpandableGridView(getActivity());
            mVegetablesGridView.setNumColumns(2);
            mVegetablesGridView.setGravity(Gravity.CENTER);
            mVegetablesGridView.setAdapter(new FoodItemGridAdapter(getActivity(), Database.getInstance().getAllVegetables()));
            mVegetablesGridView.setFocusable(false);
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
            mFruitsListView.setVerticalScrollBarEnabled(false);
            mFruitsListView.setFocusable(false);
            mFruitsListView.setOnItemClickListener(mOnItemClickListener);
            mFruitsListView.setOnItemLongClickListener(mOnItemLongClickListener);
        }
        mCurrentFruitAdapter = (FoodItemAdapter) mFruitsListView.getAdapter();
        mFruitsLayout.removeAllViews();
        mFruitsLayout.addView(mFruitsListView);

        if (mVegetablesListView == null) {
            mVegetablesListView = new ExpandableListView(getActivity());
            mVegetablesListView.setAdapter(new FoodItemListAdapter(getActivity(), Database.getInstance().getAllVegetables()));
            mVegetablesListView.setVerticalScrollBarEnabled(false);
            mVegetablesListView.setFocusable(false);
            mVegetablesListView.setOnItemClickListener(mOnItemClickListener);
            mVegetablesListView.setOnItemLongClickListener(mOnItemLongClickListener);
        }
        mCurrentVegetableAdapter = (FoodItemAdapter) mVegetablesListView.getAdapter();
        mVegetablesLayout.removeAllViews();
        mVegetablesLayout.addView(mVegetablesListView);
    }

    private void changeViewMode() {
        mGridViewMode = !mGridViewMode;
        if (mGridViewMode)
            showGridView();
        else
            showListView();
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

    private class FoodItemGridAdapter extends FoodItemAdapter {

        private class ViewHolder {
            private ImageView gridImage;
            private int position;
        }

        public FoodItemGridAdapter(Context context, ArrayList<FoodItem> objects) {
            super(context, R.layout.grid_layout, objects);
            setNotifyOnChange(true);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.i("CALENDARFRAGMENT", "getView: " + position);
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_layout, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.gridImage = (ImageView) convertView.findViewById(R.id.gridImageView);
                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();

            viewHolder.position = position;
            ImageLoader loadTask = new ImageLoader(viewHolder, position);
            loadTask.executeOnExecutor(sExecutor, getItem(position));

            return convertView;
        }
    }

    private class FoodItemListAdapter extends FoodItemAdapter {

        private class ViewHolder {
            private TextView fruitName;
        }

        public FoodItemListAdapter(Context context, ArrayList<FoodItem> objects) {
            super(context, R.layout.row_calendar_layout, objects);
            setNotifyOnChange(true);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.i("CALENDARFRAGMENT", "getView: " + position);
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_calendar_layout, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.fruitName = (TextView) convertView.findViewById(R.id.rowText);
                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();

            viewHolder.fruitName.setText(getItem(position).getName());
            return convertView;
        }
    }

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };

    private static final int sCpuCount = Runtime.getRuntime().availableProcessors();
    private static final Executor sExecutor = new ThreadPoolExecutor(sCpuCount + 1, sCpuCount * 2 + 1, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), sThreadFactory);

    private class ImageLoader extends AsyncTask<FoodItem, Void, Drawable> {

        private final FoodItemGridAdapter.ViewHolder mViewHolder;
        private final int mPosition;

        public ImageLoader(FoodItemGridAdapter.ViewHolder viewHolder, int position) {
            mViewHolder = viewHolder;
            mPosition = position;
        }

        @Override
        protected Drawable doInBackground(FoodItem... params) {
            Drawable image;
            try {
                image = ContextCompat.getDrawable(getActivity(), getResources().getIdentifier("mini_" + params[0].getImage(), "drawable", getActivity().getPackageName()));
            } catch (Resources.NotFoundException ex) {
                image = ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_menu_gallery);
            }
            if (params[0].isEnabled())
                image.setColorFilter(null);
            else
                image.setColorFilter(mGrayScaleFilter);
            return image;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            if (mPosition == mViewHolder.position)
                mViewHolder.gridImage.setImageDrawable(drawable);
        }
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
