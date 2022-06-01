package it.unipd.dei.esp2022.app_embedded.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.helpers.TabelloneCardAdapter
import org.w3c.dom.Text


class Planner2Fragment : Fragment() {
    private lateinit var day: String
    private lateinit var plannerName:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_planner2, container, false)
        plannerName = Planner2FragmentArgs.fromBundle(requireArguments()).message
        view.findViewById<TextView>(R.id.planner_name).text = plannerName
        day = "Lunedi"
        val tabLayout : TabLayout = view.findViewById(R.id.tabs)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                day = tab?.text.toString().lowercase().capitalize()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                day = tab?.text.toString().lowercase().capitalize()
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
}