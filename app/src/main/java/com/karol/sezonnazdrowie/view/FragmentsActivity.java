package com.karol.sezonnazdrowie.view;

import androidx.fragment.app.Fragment;

import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.view.fragments.CalendarFragment;
import com.karol.sezonnazdrowie.view.fragments.FoodItemPageFragment;
import com.karol.sezonnazdrowie.view.fragments.ListFragment;
import com.karol.sezonnazdrowie.view.fragments.SettingsFragment;
import com.karol.sezonnazdrowie.view.fragments.SettingsItemsFragment;
import com.karol.sezonnazdrowie.view.fragments.ShoppingListFragment;

public class FragmentsActivity extends AppCompatActivity {

    private static final String TAG = "FragmentsActivity";

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

    private boolean mSettingsItemsChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
//        setContentView(R.layout.activity_fragments);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mActionBarTitle = toolbar.findViewById(R.id.action_bar_title);
        mDrawerLayout = findViewById(R.id.drawer_layout);
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
        mDrawerLayout.addDrawerListener(mDrawerToggle);

//        String what = getIntent().getStringExtra(INTENT_WHAT);
//        switch (what) {
//            case INTENT_WHAT_CALENDAR: {
//                Fragment fragment = new CalendarFragment();
//                getFragmentManager().beginTransaction().add(R.id.contentView, fragment).commit();
//                break;
//            }
//            case INTENT_WHAT_SHOPPING_LIST: {
//                Fragment fragment = new ShoppingListFragment();
//                getFragmentManager().beginTransaction().add(R.id.contentView, fragment).commit();
//                break;
//            }
//            default: {
//                Fragment fragment = new ListFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString(INTENT_WHAT, what);
//                fragment.setArguments(bundle);
//                getFragmentManager().beginTransaction().add(R.id.contentView, fragment).commit();
//                break;
//            }
//        }
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

//    public void replaceFragments(Fragment fragment) {
//        boolean isSameAsCurrent;
//        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.contentView);
//        if (fragment.getClass().equals(ListFragment.class)) {
//            isSameAsCurrent = currentFragment.getClass().equals(ListFragment.class)
//                    && fragment.getArguments().getString(INTENT_WHAT).equals(currentFragment.getArguments().getString(INTENT_WHAT));
//        } else
//            isSameAsCurrent = currentFragment.getClass().equals(fragment.getClass());
//
//        if (!isSameAsCurrent) {
//            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//            if (!(fragment instanceof FoodItemPageFragment) && !(fragment instanceof SettingsFragment) && !(fragment instanceof SettingsItemsFragment))
//                getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            fragmentTransaction.replace(R.id.contentView, fragment);
//            if (fragment instanceof CalendarFragment || fragment instanceof FoodItemPageFragment || fragment instanceof SettingsFragment || fragment instanceof SettingsItemsFragment)
//                fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit();
//        }
//        mDrawerLayout.closeDrawers();
//    }

    public boolean getSettingsItemsChanged() {
        return mSettingsItemsChanged;
    }

    public void setSettingsItemsChanged(boolean changed) {
        mSettingsItemsChanged = changed;
    }

    public void setActionBarTitle(String text) {
        mActionBarTitle.setText(text);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else
            super.onBackPressed();
    }
}
