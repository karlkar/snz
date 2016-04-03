package com.karol.sezonnazdrowie;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Stack;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FragmentsActivity extends AppCompatActivity {

    public static final String INTENT_WHAT = "WHAT";
    public static final String INTENT_WHAT_VEGETABLES = "VEGETABLES";
    public static final String INTENT_WHAT_FRUITS = "FRUITS";
	public static final String INTENT_WHAT_INCOMING = "INCOMING";
    public static final String INTENT_WHAT_CALENDAR = "CALENDAR";
    public static final String INTENT_WHAT_SHOPPING_LIST = "SHOPPING_LIST";
    public static final String INTENT_ITEM = "ITEM";

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView mActionBarTitle;
    private SnzDrawer mDrawer;
    private Toolbar mToolbar;

	private Stack<Fragment> mFragmentBackStack = new Stack<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mActionBarTitle = (TextView) mToolbar.findViewById(R.id.action_bar_title);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {

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

        String what = getIntent().getStringExtra(INTENT_WHAT);
        if (what.equals(INTENT_WHAT_CALENDAR)) {
            Fragment fragment = new CalendarFragment();
            mFragmentBackStack.push(fragment);
            getFragmentManager().beginTransaction().add(R.id.contentView, fragment).addToBackStack(null).commit();
        } else if (what.equals(INTENT_WHAT_SHOPPING_LIST)) {
            Fragment fragment = new ShoppingListFragment();
            mFragmentBackStack.push(fragment);
            getFragmentManager().beginTransaction().add(R.id.contentView, fragment).commit();
        } else {
            Fragment fragment = new ListFragment();
            mFragmentBackStack.push(fragment);
            Bundle bundle = new Bundle();
            bundle.putString(INTENT_WHAT, what);
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().add(R.id.contentView, fragment).addToBackStack(null).commit();
        }

        mDrawer = (SnzDrawer) findViewById(R.id.left_drawer);
        mDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = (String) parent.getItemAtPosition(position);
                if (text.equals(getString(R.string.season_vegetables))) {
                    Fragment fragment = new ListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(INTENT_WHAT, INTENT_WHAT_VEGETABLES);
                    fragment.setArguments(bundle);
                    replaceFragments(fragment);
                } else if (text.equals(getString(R.string.season_fruits))) {
                    Fragment fragment = new ListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(INTENT_WHAT, INTENT_WHAT_FRUITS);
                    fragment.setArguments(bundle);
                    replaceFragments(fragment);
                } else if (text.equals(getString(R.string.season_incoming))) {
					Fragment fragment = new ListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(INTENT_WHAT, INTENT_WHAT_INCOMING);
                    fragment.setArguments(bundle);
                    replaceFragments(fragment);
                } else if (text.equals(getString(R.string.calendar))) {
                    replaceFragments(new CalendarFragment());
                } else if (text.equals(getString(R.string.shopping_list))) {
                    replaceFragments(new ShoppingListFragment());
                } else if (text.equals(getString(R.string.settings))) {
                    replaceFragments(new SettingsFragment());
                }
            }
        });

        final AdView adView = (AdView) findViewById(R.id.adView);
        adView.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(getString(R.string.adMobTestDeviceNote5))
                .addTestDevice(getString(R.string.adMobTestDeviceS5))
                .build();
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (Build.VERSION.SDK_INT >= 19)
                    TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.rootView));
                adView.setVisibility(View.VISIBLE);
            }
        });
        adView.loadAd(adRequest);
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
        boolean isSameAsCurrent = false;
        Fragment currentFragment = mFragmentBackStack.peek();
        if (fragment.getClass().equals(ListFragment.class)) {
            isSameAsCurrent = currentFragment.getClass().equals(ListFragment.class)
                    && fragment.getArguments().getString(INTENT_WHAT).equals(currentFragment.getArguments().getString(INTENT_WHAT));
        } else
            isSameAsCurrent = currentFragment.getClass().equals(fragment.getClass());

        if (!isSameAsCurrent) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            if (!(fragment instanceof FoodItemPageFragment))
                getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentTransaction.replace(R.id.contentView, fragment);
            if (fragment instanceof CalendarFragment || fragment instanceof FoodItemPageFragment)
                fragmentTransaction.addToBackStack(null);
            else
                mFragmentBackStack.pop();
            fragmentTransaction.commit();
            mFragmentBackStack.push(fragment);
        }
        mDrawerLayout.closeDrawers();
    }

    public void setActionBarTitle(String text) {
        mActionBarTitle.setText(text);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
            mFragmentBackStack.pop();
        } else
            super.onBackPressed();
    }
}
