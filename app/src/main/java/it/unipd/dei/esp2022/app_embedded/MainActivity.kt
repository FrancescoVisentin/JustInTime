package it.unipd.dei.esp2022.app_embedded

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.color.DynamicColors
import com.google.android.material.switchmaterial.SwitchMaterial
import com.test.app_embedded.R

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigationView = findViewById(R.id.bottomNavigation)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.tabelloneFragment, R.id.statoTrenoFragment, R.id.plannerFragment))

        setupNavController()
        setupWithNavController(bottomNavigationView, navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.app_bar_menu, menu)

        val item = menu?.findItem(R.id.switch_item) ?: return true
        val root = item.actionView.findViewById<RelativeLayout>(R.id.menu_switch_root)
        val appBarSwitch = root.findViewById<SwitchMaterial>(R.id.menu_switch)
        setUpMenuSwitch(appBarSwitch)

        appBarSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveColorPreference(isChecked)
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    private fun setupNavController() {
        navController.addOnDestinationChangedListener {_, destination, _ ->
            when (destination.id) {
                R.id.settingsFragment              -> bottomNavigationView.visibility = View.GONE
                R.id.addPlannerFragment            -> bottomNavigationView.visibility = View.GONE
                R.id.ricercaViaggioFragment        -> bottomNavigationView.visibility = View.GONE
                R.id.ricercaViaggioResultFragment  -> bottomNavigationView.visibility = View.GONE
                else                               -> bottomNavigationView.visibility = View.VISIBLE
            }

            if (destination.id == R.id.homeFragment) {
                supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            else {
                val typedValue = TypedValue()
                theme.resolveAttribute(com.google.android.material.R.attr.colorSurface, typedValue, true)
                @ColorInt val color = typedValue.data

                supportActionBar?.setBackgroundDrawable(ColorDrawable(color))
            }
        }
    }

    private fun setUpMenuSwitch(switch : SwitchMaterial) {
        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorTertiary, typedValue, true)
        @ColorInt val colorOn = typedValue.data

        theme.resolveAttribute(com.google.android.material.R.attr.colorControlActivated, typedValue, true)
        @ColorInt val colorOff = typedValue.data

        val states = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(-android.R.attr.state_checked))
        switch.trackTintList = ColorStateList(states, intArrayOf(colorOn, colorOff))

        val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: return
        switch.isChecked = sharedPref.getBoolean("dynamic_colors", false)
    }

    private fun saveColorPreference(mode : Boolean) {
        if (DynamicColors.isDynamicColorAvailable()) {
            val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: return
            with(sharedPref.edit()) {
                putBoolean("dynamic_colors", mode)
                apply()
            }

            if (mode)
                DynamicColors.applyToActivitiesIfAvailable(application)
            else
                DynamicColors.applyToActivitiesIfAvailable(application, R.style.Theme_AppEmbedded)

            recreate()
        }
    }
}