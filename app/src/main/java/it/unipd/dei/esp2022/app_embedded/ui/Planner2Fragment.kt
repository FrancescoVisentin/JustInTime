package it.unipd.dei.esp2022.app_embedded.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.helpers.*
import org.w3c.dom.Text


class Planner2Fragment : Fragment(), PlannerCardAdapter2.ClickListener {
    private lateinit var day: String
    private lateinit var plannerName:String
    private lateinit var recyclerView : RecyclerView
    private lateinit var db : DBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_planner2, container, false)
        plannerName = Planner2FragmentArgs.fromBundle(requireArguments()).message
        view.findViewById<TextView>(R.id.planner_name).text = plannerName
        day="Lunedi"

        val tabTitle= arrayOf(getString(R.string.tab_lunedi), getString(R.string.tab_martedi), getString(R.string.tab_mercoledi), getString(R.string.tab_giovedi), getString(R.string.tab_venerdi), getString(R.string.tab_sabato), getString(R.string.tab_domenica))
        val tabLayout : TabLayout = view.findViewById(R.id.tabs)
        val viewPager : ViewPager2 = view.findViewById(R.id.view_pager)
        viewPager.adapter = ViewPagerAdapter2(childFragmentManager, lifecycle)

        TabLayoutMediator (tabLayout, viewPager) {
                tab, position -> tab.text = tabTitle[position]
        }.attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem=tab!!.position
                day = tab?.text.toString().lowercase().capitalize()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                //day = tab?.text.toString().lowercase().capitalize()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
            }
        })
        val bu = view.findViewById<Button>(R.id.add_train_button)
        bu.setOnClickListener(){
            val action = Planner2FragmentDirections.actionPlanner2FragmentToPlanner3Fragment("$day|$plannerName")
            view.findNavController().navigate(action)
        }
        return view
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_option -> {
                val number = (recyclerView.adapter as PlannerCardAdapter2).selectedTripNumber
                val contextView = (view as View).findViewById<View>(R.id.coordinator_layout)
                if (db.deleteTrip(number)){
                    Snackbar.make(contextView, "Viaggio $number eliminato", Snackbar.LENGTH_SHORT)
                        .setAction("Chiudi") {}
                        .show()

                    recyclerView.adapter = PlannerCardAdapter2(db.getTrips(plannerName,day), this)
                } else  {
                    Snackbar.make(contextView, "Errore", Snackbar.LENGTH_SHORT)
                        .setAction("Chiudi") {}
                        .show()
                }
            }
        }

        return super.onContextItemSelected(item)
    }

    override fun onEvent(number: String)
    {

    }
}