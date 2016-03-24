package com.karol.sezonnazdrowie;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ListActivity extends AppCompatActivity {

    ListView mListView = null;
    FoodItemAdapter mAdapter = null;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView actionBarTitle = (TextView)toolbar.findViewById(R.id.action_bar_title);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        String[] titles = new String[2];
        titles[0] = "DSADSA";
        titles[1] = "AAAAA";
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titles));
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


        ArrayList<FoodItem> items = null;
        Intent source = getIntent();
        String what = source.getStringExtra("WHAT");
        if (what.equals("FRUITS")) {
            actionBarTitle.setText("SEZON NA OWOCE");
            items = Database.getInstance().getCurrentFruits();
//            items = Database.getInstance().getAllFruits();
        } else if (what.equals("VEGETABLES")) {
            actionBarTitle.setText("SEZON NA WARZYWA");
            items = Database.getInstance().getCurrentVegetables();
//            items = Database.getInstance().getAllVegetables();
        }

        mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new FoodItemAdapter(this, items);
        mListView.setAdapter(mAdapter);
        
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListActivity.this, FoodItemPageActivity.class);
                intent.putExtra("ITEM", (Parcelable) parent.getItemAtPosition(position));
                startActivity(intent);
            }
        });

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("456841FFFB7E3C53875E713E9A638290") // Note 5
                .addTestDevice("F0E0DF548AD32B9C9DF78977B9079AD2") // S5
                .build();
        adView.loadAd(adRequest);
    }

    private class FoodItemAdapter extends ArrayAdapter<FoodItem> {

        private class ViewHolder {
            TextView mText;
        }

        public FoodItemAdapter(Context context, ArrayList<FoodItem> objects) {
            super(context, R.layout.row_layout, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.row_layout, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mText = (TextView) view.findViewById(R.id.rowText);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            FoodItem item = getItem(position);
            viewHolder.mText.setText(item.getName());

            return view;
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
