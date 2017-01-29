package com.karol.sezonnazdrowie.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.data.Database;
import com.karol.sezonnazdrowie.data.FoodItem;

import java.util.Date;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView dateTextView = (TextView) findViewById(R.id.dateTextView);
        Button fruitsBtn = (Button) findViewById(R.id.fruitsBtn);
        Button vegetablesBtn = (Button) findViewById(R.id.vegetablesBtn);
        Button incomingSeasonBtn = (Button) findViewById(R.id.incomingSeasonBtn);
        Button calendarBtn = (Button) findViewById(R.id.calendarBtn);
        Button shopListBtn = (Button) findViewById(R.id.shopListBtn);

        Date today = new Date();
        dateTextView.setText(FoodItem.DATE_FORMAT_TEXT.format(today));

        fruitsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FragmentsActivity.class);
                intent.putExtra(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_FRUITS);
                startActivity(intent);
            }
        });

        vegetablesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FragmentsActivity.class);
                intent.putExtra(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_VEGETABLES);
                startActivity(intent);
            }
        });

        incomingSeasonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, FragmentsActivity.class);
                intent.putExtra(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_INCOMING);
                startActivity(intent);
            }
        });

        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FragmentsActivity.class);
                intent.putExtra(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_CALENDAR);
                startActivity(intent);
            }
        });

        shopListBtn.setOnClickListener(new View.OnClickListener() {
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
