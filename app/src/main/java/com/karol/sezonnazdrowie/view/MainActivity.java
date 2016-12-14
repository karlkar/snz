package com.karol.sezonnazdrowie.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.karol.sezonnazdrowie.data.Database;
import com.karol.sezonnazdrowie.data.FoodItem;
import com.karol.sezonnazdrowie.R;

import java.util.Date;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private TextView mDateTextView = null;
    private Button mFruitsBtn = null;
    private Button mVegetablesBtn = null;
    private Button mCalendarBtn = null;
    private Button mShopListBtn = null;
    private Button mIncomingSeasonBtn;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDateTextView = (TextView) findViewById(R.id.dateTextView);
        mFruitsBtn = (Button) findViewById(R.id.fruitsBtn);
        mVegetablesBtn = (Button) findViewById(R.id.vegetablesBtn);
        mIncomingSeasonBtn = (Button) findViewById(R.id.incomingSeasonBtn);
        mCalendarBtn = (Button) findViewById(R.id.calendarBtn);
        mShopListBtn = (Button) findViewById(R.id.shopListBtn);

        Date today = new Date();
        mDateTextView.setText(FoodItem.DATE_FORMAT_TEXT.format(today));

        mFruitsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FragmentsActivity.class);
                intent.putExtra(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_FRUITS);
                startActivity(intent);
            }
        });

        mVegetablesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FragmentsActivity.class);
                intent.putExtra(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_VEGETABLES);
                startActivity(intent);
            }
        });

        mIncomingSeasonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, FragmentsActivity.class);
                intent.putExtra(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_INCOMING);
                startActivity(intent);
            }
        });

        mCalendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FragmentsActivity.class);
                intent.putExtra(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_CALENDAR);
                startActivity(intent);
            }
        });

        mShopListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FragmentsActivity.class);
                intent.putExtra(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_SHOPPING_LIST);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
		if (Database.getInstance().getAllFruits() == null)
        	Database.getInstance().loadData(this);
        super.onResume();
    }
}