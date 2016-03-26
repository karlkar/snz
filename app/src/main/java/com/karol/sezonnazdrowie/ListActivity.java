package com.karol.sezonnazdrowie;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ListActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView mActionBarTitle;
    private SnzDrawer mDrawer;

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
        mActionBarTitle = (TextView)toolbar.findViewById(R.id.action_bar_title);
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

        Fragment fg = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("WHAT", getIntent().getStringExtra("WHAT"));
        fg.setArguments(bundle);
        getFragmentManager().beginTransaction().add(R.id.contentView, fg).addToBackStack(null).commit();

        mDrawer = (SnzDrawer) findViewById(R.id.left_drawer);
        mDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = (String) parent.getItemAtPosition(position);
                if (text.equals("SEZON NA WARZYWA")) {
                    Fragment fragment = new ListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("WHAT", "VEGETABLES");
                    fragment.setArguments(bundle);
                    replaceFragments(fragment);
                } else if (text.equals("SEZON NA OWOCE")) {
                    Fragment fragment = new ListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("WHAT", "FRUITS");
                    fragment.setArguments(bundle);
                    replaceFragments(fragment);
                } else if (text.equals("WKRÓTCE SEZON NA")) {

                } else if (text.equals("KALENDARZ")) {
                    Intent intent = new Intent(ListActivity.this, CalendarActivity.class);
                    startActivity(intent);
                } else if (text.equals("LISTA ZAKUPÓW")) {

                }
            }
        });
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

    public void replaceFragments(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if (fragment instanceof ListFragment)
            getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentTransaction.replace(R.id.contentView, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void setActionBarTitle(String text) {
        mActionBarTitle.setText(text);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1)
            getFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }
}
