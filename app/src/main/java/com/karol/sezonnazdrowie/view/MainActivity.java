package com.karol.sezonnazdrowie.view;

import android.os.Bundle;
import android.os.Handler;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import static androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
import static androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED;

public class MainActivity extends AppCompatActivity {

    private AdView mAdView;
    private TextView mActionBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final NavController navController = Navigation.findNavController(
                this,
                R.id.nav_host_fragment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();
        mActionBarTitle = toolbar.findViewById(R.id.action_bar_title);
        final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.left_drawer);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_drawer_start: {
                        navController.navigate(R.id.mainFragment);
                        return true;
                    }
                    case R.id.menu_drawer_fruits: {
                        Bundle bundle = new Bundle();
                        bundle.putString(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_FRUITS);
                        navController.navigate(R.id.listFragment, bundle);
                        return true;
                    }
                    case R.id.menu_drawer_vegetables: {
                        Bundle bundle = new Bundle();
                        bundle.putString(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_VEGETABLES);
                        navController.navigate(R.id.listFragment, bundle);
                        return true;
                    }
                    case R.id.menu_drawer_incoming: {
                        Bundle bundle = new Bundle();
                        bundle.putString(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_INCOMING);
                        navController.navigate(R.id.listFragment, bundle);
                        return true;
                    }
                    case R.id.menu_drawer_calendar: {
                        navController.navigate(R.id.calendarFragment);
                        return true;
                    }
                    case R.id.menu_drawer_shopping_list: {
                        navController.navigate(R.id.shoppingListFragment);
                        return true;
                    }
                }
                return false;
            }
        });

//        NavigationUI.setupWithNavController(
//                navigationView,
//                navController);

//        NavigationUI.setupActionBarWithNavController(
//                MainActivity.this,
//                navController,
//                drawerLayout);

        navController.addOnNavigatedListener(new NavController.OnNavigatedListener() {
            @Override
            public void onNavigated(NavController controller, NavDestination destination) {
                switch (destination.getId()) {
                    case R.id.mainFragment:
                        getSupportActionBar().hide();
                        mAdView.setVisibility(View.GONE);
                        drawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED);
                        break;
                    default:
                        getSupportActionBar().show();
                        mAdView.setVisibility(View.VISIBLE);
                        drawerLayout.setDrawerLockMode(LOCK_MODE_UNLOCKED);
                        break;
                }
            }
        });

        ArrayList<FoodItem> allFruits = Database.getInstance().getAllFruits();
        if (allFruits == null || allFruits.size() == 0) {
            Database.getInstance().loadData(this);
        }

        final View adBackground = findViewById(R.id.adBackground);
        mAdView = findViewById(R.id.adView);
        final AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(getString(R.string.adMobTestDeviceS5))
                .addTestDevice(getString(R.string.adMobTestDeviceS7))
                .build();
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mAdView.setAdListener(null);
                adBackground.setVisibility(View.VISIBLE);
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdView.loadAd(adRequest);
            }
        }, 500);
    }

    public void setActionBarTitle(String text) {
        mActionBarTitle.setText(text);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp();
    }
}
