package com.karol.sezonnazdrowie;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import android.app.*;

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
        SimpleDateFormat format = new SimpleDateFormat("d MMMM", Locale.getDefault());
        mDateTextView.setText(format.format(today));

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
		if (Database.getInstance().getAllFruits() == null) {
        	Database.getInstance().loadData(this);
			String text = "pomidory, gruszki, sliwki, endywie, truskawki, brokuly, banany, porzeczke, rzodkiew korzen, fasole, marchew";
			Notification.Builder builder = new Notification.Builder(this);
			builder.setContentTitle("Juz wkrótce zaczyna się sezon na");
			builder.setContentText(text);
			builder.setSmallIcon(R.mipmap.ic_launcher);
			builder.setAutoCancel(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                builder.setStyle(new Notification.BigTextStyle().bigText(text));

            Intent notiIntent = new Intent(this, FragmentsActivity.class);
			notiIntent.putExtra(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_INCOMING);
            PendingIntent pIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(FragmentsActivity.class);
                stackBuilder.addNextIntent(notiIntent);
                pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            } else
                pIntent = PendingIntent.getActivity(this, 0, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pIntent);
			
			NotificationManager	notiMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                notiMgr.notify(2, builder.build());
            else
                notiMgr.notify(2, builder.getNotification());
        }
        super.onResume();
    }
}
