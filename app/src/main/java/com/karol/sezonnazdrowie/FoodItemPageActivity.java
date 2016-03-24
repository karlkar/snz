package com.karol.sezonnazdrowie;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodItemPageActivity extends AppCompatActivity {

    private FoodItem mItem;

    private ImageView mPagePreviewImageView;
    private LinearLayout mAdditionalTextsLayout;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item_page);

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

        Intent intent = getIntent();
        mItem = intent.getParcelableExtra("ITEM");

        actionBarTitle.setText(mItem.getName());

        mPagePreviewImageView = (ImageView) findViewById(R.id.pagePreviewImageView);
        if (!mItem.getImage().isEmpty())
            mPagePreviewImageView.setImageResource(getResources().getIdentifier(mItem.getImage(), "drawable", getPackageName()));
        else
            mPagePreviewImageView.setImageResource(android.R.drawable.ic_menu_gallery);

        mAdditionalTextsLayout = (LinearLayout) findViewById(R.id.additionalTextsLayout);
        TextView tmp = new TextView(this);
        tmp.setText(mItem.getDesc());
        mAdditionalTextsLayout.addView(tmp);
        addSpacer();
        if (mItem.hasProximates()) {
            // add wartość odżywcza
            TextView title = new TextView(this);
            title.setText("Wartość odżywcza (100g)");
            mAdditionalTextsLayout.addView(title);

            addElementView("woda", mItem.getWater());
            addElementView("kalorie", mItem.getEnergy());
            addElementView("białka", mItem.getProtein());
            addElementView("tłuszcze", mItem.getFat());
            addElementView("węglowodany", mItem.getCarbohydrates());
            addElementView("błonnik", mItem.getFiber());
            addElementView("cukry", mItem.getSugars());
            addSpacer();
        }

        if (mItem.hasMinerals()) {
            // add zawartość minerałów
            TextView title = new TextView(this);
            title.setText("Zawartość mikro- i makroelementów (100g)");
            mAdditionalTextsLayout.addView(title);

            addElementView("wapń", mItem.getCalcium());
            addElementView("żelazo", mItem.getIron());
            addElementView("magnez", mItem.getMagnesium());
            addElementView("fosfor", mItem.getPhosphorus());
            addElementView("potas", mItem.getPotassium());
            addElementView("sód", mItem.getSodium());
            addElementView("cynk", mItem.getZinc());
            addSpacer();
        }

        if (mItem.hasVitamins()) {
           // add zawartość witamin
            TextView title = new TextView(this);
            title.setText("Witaminy (100g)");
            mAdditionalTextsLayout.addView(title);

            addElementView("witamina A", mItem.getVitA());
            addElementView("witamina C", mItem.getVitC());
            addElementView("witamina E", mItem.getVitE());
            addElementView("witamina B1 (tiamina)", mItem.getThiamin());
            addElementView("witamina B2 (ryboflawina)", mItem.getRiboflavin());
            addElementView("witamina B3 (niacyna)", mItem.getNiacin());
            addElementView("witamina B6", mItem.getVitB6());
            addElementView("witamina K", mItem.getVitK());
            addElementView("kwas foliowy", mItem.getFolate());
            addSpacer();
        }

        if (mItem.hasProximates() || mItem.hasMinerals() || mItem.hasVitamins()) {
            TextView title = new TextView(this);
            title.setText("Źródło: USDA National Nutrient Database");
            mAdditionalTextsLayout.addView(title);
            addSpacer();
        }
    }

    private void addElementView(String title, String value) {
        if (!value.isEmpty()) {
            TextView tmp = new TextView(this);
            tmp.setText(title + " - " + value);
            mAdditionalTextsLayout.addView(tmp);
        }
    }

    private void addSpacer() {
        TextView tmp = new TextView(this);
        mAdditionalTextsLayout.addView(tmp);
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
