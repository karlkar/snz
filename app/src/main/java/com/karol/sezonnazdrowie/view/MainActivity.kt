package com.karol.sezonnazdrowie.view

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.karol.sezonnazdrowie.R
import com.karol.sezonnazdrowie.model.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_actionbar.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(
            this,
            R.id.nav_host_fragment
        )
        val toolbar = toolbar as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setupDrawer()

        val topLevelDestinationIds = setOf(
            R.id.listFragment,
            R.id.calendarFragment,
            R.id.shoppingListFragment,
            R.id.settingsFragment
        )
        val appBarConfiguration = AppBarConfiguration.Builder(topLevelDestinationIds)
            .setDrawerLayout(drawer_layout)
            .build()
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        setupAdView()

        setupNavController()

        val mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mainViewModel.actionBarTitle.observe(
            this,
            Observer { title -> toolbar.action_bar_title.text = title })
    }

    private fun setupNavController() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            }
            if (destination.id == R.id.mainFragment) {
                supportActionBar?.hide()
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                adBackground.visibility = View.GONE
                drawer_layout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)
            } else {
                supportActionBar?.show()
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                adBackground.visibility = View.VISIBLE
                drawer_layout.setDrawerLockMode(LOCK_MODE_UNLOCKED)
            }
        }
    }

    private fun setupDrawer() {
        val drawer = findViewById<ListView>(R.id.snz_drawer)
        drawer.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.mainFragment, true)
                .build()
            when (parent.getItemAtPosition(position) as String) {
                getString(R.string.season_vegetables) -> {
                    navController.graph.startDestination = R.id.listFragment
                    val bundle = Bundle()
                    bundle.putString(
                        INTENT_WHAT,
                        INTENT_WHAT_VEGETABLES
                    )
                    navController.navigate(
                        R.id.listFragment,
                        bundle,
                        navOptions
                    )
                }
                getString(R.string.season_fruits) -> {
                    navController.graph.startDestination = R.id.listFragment
                    val bundle = Bundle()
                    bundle.putString(
                        INTENT_WHAT,
                        INTENT_WHAT_FRUITS
                    )
                    navController.navigate(
                        R.id.listFragment,
                        bundle,
                        navOptions
                    )
                }
                getString(R.string.season_incoming) -> {
                    navController.graph.startDestination = R.id.listFragment
                    val bundle = Bundle()
                    bundle.putString(
                        INTENT_WHAT,
                        INTENT_WHAT_INCOMING
                    )
                    navController.navigate(
                        R.id.listFragment,
                        bundle,
                        navOptions
                    )
                }
                getString(R.string.calendar) -> {
                    navController.graph.startDestination = R.id.calendarFragment
                    navController.navigate(
                        R.id.calendarFragment, null,
                        navOptions
                    )
                }
                getString(R.string.shopping_list) -> {
                    navController.graph.startDestination = R.id.shoppingListFragment
                    navController.navigate(
                        R.id.shoppingListFragment, null,
                        navOptions
                    )
                }
                getString(R.string.settings) -> navController.navigate(
                    R.id.settingsFragment, null,
                    navOptions
                )
            }
        }
    }

    private fun setupAdView() {
        val adRequest = AdRequest.Builder().build()
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                adView.adListener = null
                adBackground.visibility = View.VISIBLE
            }
        }
        Handler().postDelayed({ adView.loadAd(adRequest) }, 500)
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.currentDestination?.let { dest ->
            if (listOf(
                    R.id.listFragment,
                    R.id.calendarFragment,
                    R.id.shoppingListFragment,
                    R.id.settingsFragment
                ).any { it == dest.id }
            ) {
                drawer_layout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return navController.navigateUp()
    }

    companion object {

        const val INTENT_WHAT = "WHAT"
        const val INTENT_WHAT_VEGETABLES = "VEGETABLES"
        const val INTENT_WHAT_FRUITS = "FRUITS"
        const val INTENT_WHAT_INCOMING = "INCOMING"
        const val INTENT_WHAT_CALENDAR = "CALENDAR"
        const val INTENT_WHAT_SHOPPING_LIST = "SHOPPING_LIST"
        const val INTENT_ITEM = "ITEM"
    }
}
