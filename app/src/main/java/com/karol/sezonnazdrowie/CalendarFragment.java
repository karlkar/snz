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
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.Comparator;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Karol on 26.03.2016.
 */
public class CalendarFragment extends Fragment {

    private ScrollView mCalendarScrollView;
    private GridView mFruitsGridView;
    private FoodItemAdapter mFruitAdapter;
    private GridView mVegetablesGridView;
    private FoodItemAdapter mVegetableAdapter;
    private ColorMatrixColorFilter mGrayScaleFilter;
    private View mCalendarArrowLeft;
    private View mCalendarArrowRight;
    private TextView mCalendarHeaderTextView;
    private MaterialCalendarView mCalendarView;

    private View mRootView = null;

    private int mCurrentMonth;
    private CalendarDay mSelectedDate = null;
    private FoodItem mSelectedFoodItem;

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSelectedDate = null;
            mCalendarView.setSelectedDate(mSelectedDate);
            mSelectedFoodItem = (FoodItem) parent.getItemAtPosition(position);

            mFruitAdapter.enableItemAt(mSelectedFoodItem.isFruit() ? position : -1);
            mFruitAdapter.sortItems();
            mVegetableAdapter.enableItemAt(mSelectedFoodItem.isFruit() ? -1 : position);
            mVegetableAdapter.sortItems();

            Toast.makeText(getActivity(), mSelectedFoodItem.getName(), Toast.LENGTH_SHORT).show();
            mCalendarScrollView.fullScroll(ScrollView.FOCUS_UP);

            CalendarDay currentDay = mCalendarView.getCurrentDate();
            CalendarDay properDay = mSelectedFoodItem.getNearestSeasonDay(currentDay);
            if (properDay == null || currentDay.equals(properDay))
                mCalendarView.invalidateDecorators();
            else
                mCalendarView.setCurrentDate(properDay, true);
        }
    };

    private AdapterView.OnItemLongClickListener mOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((FragmentsActivity) getActivity()).setActionBarTitle(getString(R.string.calendar));

        if (mRootView != null)
            return mRootView;

        mRootView = inflater.inflate(R.layout.fragment_calendar, null);

        mCalendarScrollView = (ScrollView) mRootView.findViewById(R.id.calendarScrollView);

        mFruitsGridView = (GridView) mRootView.findViewById(R.id.fruitsGridView);
        mFruitAdapter = new FoodItemAdapter(getActivity(), Database.getInstance().getAllFruits());
        mFruitsGridView.setAdapter(mFruitAdapter);
        mFruitsGridView.setFocusable(false);
        mFruitsGridView.setOnItemClickListener(mOnItemClickListener);
        mFruitsGridView.setOnItemLongClickListener(mOnItemLongClickListener);

        mVegetablesGridView = (GridView) mRootView.findViewById(R.id.vegetablesGridView);
        mVegetableAdapter = new FoodItemAdapter(getActivity(), Database.getInstance().getAllVegetables());
        mVegetablesGridView.setAdapter(mVegetableAdapter);
        mVegetablesGridView.setFocusable(false);
        mVegetablesGridView.setOnItemClickListener(mOnItemClickListener);
        mVegetablesGridView.setOnItemLongClickListener(mOnItemLongClickListener);

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

    private void prepareCalendarView(View view) {
        mCalendarArrowLeft = view.findViewById(R.id.caledarArrowLeft);
        mCalendarArrowRight = view.findViewById(R.id.caledarArrowRight);
        mCalendarHeaderTextView = (TextView) view.findViewById(R.id.calendarHeader);
        mCalendarHeaderTextView.setText(String.format(getResources().getStringArray(R.array.monthsWithYear)[Calendar.getInstance().get(Calendar.MONTH)], Calendar.getInstance().get(Calendar.YEAR)));

        mCalendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        mCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        mCalendarView.setTopbarVisible(false);
        mCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
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
        mCalendarArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar date = mCalendarView.getCurrentDate().getCalendar();
                date.add(Calendar.MONTH, -1);
                mCalendarView.setCurrentDate(date);
            }
        });
        mCalendarArrowRight.setOnClickListener(new View.OnClickListener() {
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
        mFruitAdapter.enableItemsAt(date);
        mVegetableAdapter.enableItemsAt(date);
        mFruitAdapter.sortItems();
        mVegetableAdapter.sortItems();
        mCalendarView.invalidateDecorators();
    }

    private class FoodItemAdapter extends ArrayAdapter<FoodItem> {

        public void enableItemsAt(CalendarDay date) {
            for (int i = 0; i < getCount(); ++i) {
                FoodItem cur = getItem(i);
                cur.setEnabled(cur.existsAt(date));
            }
        }

        public void enableItemAt(int position) {
            for (int i = 0; i < getCount(); ++i) {
                getItem(i).setEnabled(i == position);
            }
        }

        public void sortItems() {
            sort(new Comparator<FoodItem>() {
                @Override
                public int compare(FoodItem lhs, FoodItem rhs) {
                    if (lhs.isEnabled() == rhs.isEnabled())
                        return lhs.compareTo(rhs);
                    return lhs.isEnabled() ? -1 : 1;
                }
            });
        }

        private class ViewHolder {
            private ImageView gridImage;
            private int position;
        }

        public FoodItemAdapter(Context context, ArrayList<FoodItem> objects) {
            super(context, R.layout.grid_layout, objects);
            setNotifyOnChange(true);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.i("CALENDARFRAGMENT", "getView: " + position);
            View view;
            final ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.grid_layout, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.gridImage = (ImageView) view.findViewById(R.id.gridImageView);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.position = position;
            FoodItem item = getItem(position);
            ImageLoader loadTask = new ImageLoader(viewHolder, position);
            loadTask.executeOnExecutor(sExecutor, item);

            return view;
        }
    }

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };

    private static int sCpuCount = Runtime.getRuntime().availableProcessors();
    private static final Executor sExecutor = new ThreadPoolExecutor(sCpuCount + 1, sCpuCount * 2 + 1, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), sThreadFactory);

    private class ImageLoader extends AsyncTask<FoodItem, Void, Drawable> {

        private FoodItemAdapter.ViewHolder mViewHolder;
        private int mPosition;

        public ImageLoader(FoodItemAdapter.ViewHolder viewHolder, int position) {
            mViewHolder = viewHolder;
            mPosition = position;
        }

        @Override
        protected Drawable doInBackground(FoodItem... params) {
            Drawable image = null;
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