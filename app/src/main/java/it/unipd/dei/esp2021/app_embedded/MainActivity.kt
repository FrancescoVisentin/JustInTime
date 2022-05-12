//MainActivity.kt

package it.unipd.dei.esp2021.app_embedded

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
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
        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.tabelloneFragment, R.id.statoTrenoFragment, R.id.plannerFragment))

        setupNavController()
        setupWithNavController(bottomNavigationView, navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.app_bar_menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                navController.navigate(R.id.settingsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
        }
    }
}