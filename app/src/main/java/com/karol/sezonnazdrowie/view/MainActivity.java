package com.karol.sezonnazdrowie.view;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.model.MainViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import static androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
import static androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED;

public class MainActivity extends AppCompatActivity {

    public static final String INTENT_WHAT = "WHAT";
    public static final String INTENT_WHAT_VEGETABLES = "VEGETABLES";
    public static final String INTENT_WHAT_FRUITS = "FRUITS";
    public static final String INTENT_WHAT_INCOMING = "INCOMING";
    public static final String INTENT_WHAT_CALENDAR = "CALENDAR";
    public static final String INTENT_WHAT_SHOPPING_LIST = "SHOPPING_LIST";
    public static final String INTENT_ITEM = "ITEM";

    private AdView mAdView;
    private TextView mActionBarTitle;
    private View mAdBackground;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavController mNavController;
    private MainViewModel mMainViewModel;

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

        setupDrawer();

        NavigationUI.setupActionBarWithNavController(this, mNavController, mDrawerLayout);

        setupAdView();

        setupNavController();

        mMainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mMainViewModel.getActionBarTitle().observe(
                this,
                new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String title) {
                        mActionBarTitle.setText(title);
                    }
                });
    }

    private void setupNavController() {
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
    }

    private void setupDrawer() {
        ListView drawer = findViewById(R.id.snz_drawer);
        drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.mainFragment, true)
                        .build();
                String text = (String) parent.getItemAtPosition(position);
                if (text.equals(getString(R.string.season_vegetables))) {
                    mNavController.getGraph().setStartDestination(R.id.listFragment);
                    Bundle bundle = new Bundle();
                    bundle.putString(
                            MainActivity.INTENT_WHAT,
                            MainActivity.INTENT_WHAT_VEGETABLES);
                    mNavController.navigate(
                            R.id.listFragment,
                            bundle,
                            navOptions);
                } else if (text.equals(getString(R.string.season_fruits))) {
                    mNavController.getGraph().setStartDestination(R.id.listFragment);
                    Bundle bundle = new Bundle();
                    bundle.putString(
                            MainActivity.INTENT_WHAT,
                            MainActivity.INTENT_WHAT_FRUITS);
                    mNavController.navigate(
                            R.id.listFragment,
                            bundle,
                            navOptions);
                } else if (text.equals(getString(R.string.season_incoming))) {
                    mNavController.getGraph().setStartDestination(R.id.listFragment);
                    Bundle bundle = new Bundle();
                    bundle.putString(
                            MainActivity.INTENT_WHAT,
                            MainActivity.INTENT_WHAT_INCOMING);
                    mNavController.navigate(
                            R.id.listFragment,
                            bundle,
                            navOptions);
                } else if (text.equals(getString(R.string.calendar))) {
                    mNavController.getGraph().setStartDestination(R.id.calendarFragment);
                    mNavController.navigate(
                            R.id.calendarFragment,
                            null,
                            navOptions);
                } else if (text.equals(getString(R.string.shopping_list))) {
                    mNavController.getGraph().setStartDestination(R.id.shoppingListFragment);
                    mNavController.navigate(
                            R.id.shoppingListFragment,
                            null,
                            navOptions);
                } else if (text.equals(getString(R.string.settings))) {
                    mNavController.navigate(
                            R.id.settingsFragment,
                            null,
                            navOptions);
                }
            }
        });
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
