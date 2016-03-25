package com.karol.sezonnazdrowie;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CalendarActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ScrollView mCalendarScrollView;
    private View mCalendarArrowLeft;
    private View mCalendarArrowRight;
    private TextView mCalendarHeaderTextView;
    private MaterialCalendarView mCalendarView;
    private ExpandableGridView mFruitsGridView;
    private ExpandableGridView mVegetablesGridView;

    private FoodItem mSelectedFoodItem = null;
    private int mCurrentMonth;

    private ColorMatrixColorFilter mGrayScaleFilter;

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSelectedFoodItem = (FoodItem) parent.getItemAtPosition(position);

            mFruitAdapter.enableItemAt(mSelectedFoodItem.isFruit() ? position : -1);
            mFruitAdapter.sortItems();
            mVegetableAdapter.enableItemAt(mSelectedFoodItem.isFruit() ? -1 : position);
            mVegetableAdapter.sortItems();

            Toast.makeText(CalendarActivity.this, mSelectedFoodItem.getName(), Toast.LENGTH_SHORT).show();
            mCalendarScrollView.fullScroll(ScrollView.FOCUS_UP);

            CalendarDay currentDay = mCalendarView.getCurrentDate();
            CalendarDay properDay = null;
            if (!mSelectedFoodItem.existsAt(currentDay)) {
                CalendarDay day1 = mSelectedFoodItem.getStartDay1();
                CalendarDay day2 = mSelectedFoodItem.getStartDay2();
                if (day2 == null)
                    properDay = day1;
                else {
                    if (currentDay.getMonth() < day1.getMonth() || (currentDay.getMonth() == day1.getMonth() && currentDay.getDay() < day1.getDay()))
                        properDay = day1;
                    else
                        properDay = day2;
                }
            }

            if (properDay == null)
                mCalendarView.invalidateDecorators();
            else
                mCalendarView.setCurrentDate(properDay, true);
        }
    };

    FoodItemAdapter mFruitAdapter;
    FoodItemAdapter mVegetableAdapter;
    private BorderDayDecorator mBorderDayDecorator;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        mCalendarScrollView = (ScrollView) findViewById(R.id.calendarScrollView);

        mFruitsGridView = (ExpandableGridView) findViewById(R.id.fruitsGridView);
        mFruitAdapter = new FoodItemAdapter(this, Database.getInstance().getAllFruits());
        mFruitsGridView.setAdapter(mFruitAdapter);
        mFruitsGridView.setFocusable(false);
        mFruitsGridView.setOnItemClickListener(mOnItemClickListener);

        mVegetablesGridView = (ExpandableGridView) findViewById(R.id.vegetablesGridView);
        mVegetableAdapter = new FoodItemAdapter(this, Database.getInstance().getAllVegetables());
        mVegetablesGridView.setAdapter(mVegetableAdapter);
        mVegetablesGridView.setFocusable(false);
        mVegetablesGridView.setOnItemClickListener(mOnItemClickListener);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView actionBarTitle = (TextView)toolbar.findViewById(R.id.action_bar_title);
        actionBarTitle.setText("Kalendarz");

        prepareCalendarView();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        mGrayScaleFilter = new ColorMatrixColorFilter(matrix);
    }

    private void prepareCalendarView() {
        mCalendarArrowLeft = findViewById(R.id.caledarArrowLeft);
        mCalendarArrowRight = findViewById(R.id.caledarArrowRight);
        mCalendarHeaderTextView = (TextView) findViewById(R.id.calendarHeader);
        mCalendarHeaderTextView.setText(String.format(getResources().getStringArray(R.array.monthsWithYear)[Calendar.getInstance().get(Calendar.MONTH)], Calendar.getInstance().get(Calendar.YEAR)));

        mCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
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

        mBorderDayDecorator = new BorderDayDecorator();
        mCalendarView.addDecorator(mBorderDayDecorator);
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
            private ImageView mGridImage;
        }

        public FoodItemAdapter(Context context, ArrayList<FoodItem> objects) {
            super(context, R.layout.grid_layout, objects);
            setNotifyOnChange(true);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            final ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.grid_layout, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mGridImage = (ImageView) view.findViewById(R.id.gridImageView);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) convertView.getTag();
            }

            FoodItem item = getItem(position);

            Drawable image = null;
            try {
                image = getDrawable(getResources().getIdentifier("mini_" + item.getImage(), "drawable", getPackageName()));
            } catch (Resources.NotFoundException ex) {
                image = getDrawable(android.R.drawable.ic_menu_gallery);
            }

            if (item.isEnabled())
                image.setColorFilter(null);
            else
                image.setColorFilter(mGrayScaleFilter);
            viewHolder.mGridImage.setImageDrawable(image);

            return view;
        }
    }

    private class BorderDayDecorator implements DayViewDecorator {

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return true;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setSelectionDrawable(getDrawable(R.drawable.day_normal_selector));
        }
    }

    private class SeasonDayDecorator implements DayViewDecorator {

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return mSelectedFoodItem != null && day.getMonth() == mCurrentMonth && mSelectedFoodItem.existsAt(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setSelectionDrawable(getDrawable(R.drawable.day_season_selector));
        }
    }

    private class SeasonOuterDayDecorator implements DayViewDecorator {

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return mSelectedFoodItem != null && day.getMonth() != mCurrentMonth && mSelectedFoodItem.existsAt(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setSelectionDrawable(getDrawable(R.drawable.day_outer_season_selector));
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
