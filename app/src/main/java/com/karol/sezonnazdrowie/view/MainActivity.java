package com.karol.sezonnazdrowie.view;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.navigation.NavigationView;
import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.data.Database;
import com.karol.sezonnazdrowie.data.FoodItem;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import static androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
import static androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED;

public class MainActivity extends AppCompatActivity {

    private AdView mAdView;
    private TextView mActionBarTitle;
    private View mAdBackground;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavController mNavController;

    private class MyNavigationItemSelectedListener implements
            NavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.mainFragment, true)
                    .build();
            switch (menuItem.getItemId()) {
                case R.id.menu_drawer_start: {
                    mNavController.navigate(
                            R.id.mainFragment,
                            null,
                            navOptions);
                    return true;
                }
                case R.id.menu_drawer_fruits: {
                    mNavController.getGraph().setStartDestination(R.id.listFragment);
                    Bundle bundle = new Bundle();
                    bundle.putString(
                            FragmentsActivity.INTENT_WHAT,
                            FragmentsActivity.INTENT_WHAT_FRUITS);
                    mNavController.navigate(
                            R.id.listFragment,
                            bundle,
                            navOptions);
                    return true;
                }
                case R.id.menu_drawer_vegetables: {
                    mNavController.getGraph().setStartDestination(R.id.listFragment);
                    Bundle bundle = new Bundle();
                    bundle.putString(
                            FragmentsActivity.INTENT_WHAT,
                            FragmentsActivity.INTENT_WHAT_VEGETABLES);
                    mNavController.navigate(
                            R.id.listFragment,
                            bundle,
                            navOptions);
                    return true;
                }
                case R.id.menu_drawer_incoming: {
                    mNavController.getGraph().setStartDestination(R.id.listFragment);
                    Bundle bundle = new Bundle();
                    bundle.putString(
                            FragmentsActivity.INTENT_WHAT,
                            FragmentsActivity.INTENT_WHAT_INCOMING);
                    mNavController.navigate(
                            R.id.listFragment,
                            bundle,
                            navOptions);
                    return true;
                }
                case R.id.menu_drawer_calendar: {
                    mNavController.getGraph().setStartDestination(R.id.calendarFragment);
                    mNavController.navigate(
                            R.id.calendarFragment,
                            null,
                            navOptions);
                    return true;
                }
                case R.id.menu_drawer_shopping_list: {
                    mNavController.getGraph().setStartDestination(R.id.shoppingListFragment);
                    mNavController.navigate(
                            R.id.shoppingListFragment,
                            null,
                            navOptions);
                    return true;
                }
                case R.id.menu_drawer_settings: {
                    mNavController.getGraph().setStartDestination(R.id.settingsFragment);
                    mNavController.navigate(
                            R.id.settingsFragment,
                            null,
                            navOptions);
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavController = Navigation.findNavController(
                this,
                R.id.nav_host_fragment);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mActionBarTitle = mToolbar.findViewById(R.id.action_bar_title);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        final NavigationView navigationView = findViewById(R.id.left_drawer);
        navigationView.setNavigationItemSelectedListener(
                new MyNavigationItemSelectedListener());

        NavigationUI.setupActionBarWithNavController(this, mNavController, mDrawerLayout);

        setupAdView();

        mNavController.addOnNavigatedListener(new NavController.OnNavigatedListener() {
            @Override
            public void onNavigated(
                    @NonNull NavController controller,
                    @NonNull NavDestination destination) {
                switch (destination.getId()) {
                    case R.id.mainFragment:
                        getSupportActionBar().hide();
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        mAdBackground.setVisibility(View.GONE);
                        mDrawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED);
                        break;
                    default:
                        getSupportActionBar().show();
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        mAdBackground.setVisibility(View.VISIBLE);
                        mDrawerLayout.setDrawerLockMode(LOCK_MODE_UNLOCKED);
                        break;
                }
            }
        });

        ArrayList<FoodItem> allFruits = Database.getInstance().getAllFruits();
        if (allFruits == null || allFruits.size() == 0) {
            Database.getInstance().loadData(this);
        }
    }

    private void setupAdView() {
        mAdBackground = findViewById(R.id.adBackground);
        mAdView = findViewById(R.id.adView);
        final AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(getString(R.string.adMobTestDeviceS5))
                .addTestDevice(getString(R.string.adMobTestDeviceS7))
                .build();
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mAdView.setAdListener(null);
                mAdBackground.setVisibility(View.VISIBLE);
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdView.loadAd(adRequest);
            }
        }, 500);
    }

    private ActionBarDrawerToggle createDrawerToggle() {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        return drawerToggle;
    }

    public void setActionBarTitle(String text) {
        mActionBarTitle.setText(text);
    }

    @Override
    public boolean onSupportNavigateUp() {
        int curDestId = mNavController.getCurrentDestination().getId();
        if (curDestId == R.id.listFragment
                || curDestId == R.id.calendarFragment
                || curDestId == R.id.shoppingListFragment) {
            mDrawerLayout.openDrawer(Gravity.START);
            return true;
        }
        return mNavController.navigateUp();
    }
}
