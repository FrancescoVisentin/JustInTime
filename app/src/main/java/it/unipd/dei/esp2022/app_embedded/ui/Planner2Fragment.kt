package it.unipd.dei.esp2022.app_embedded.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.helpers.*


class Planner2Fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_planner2, container, false)

        val plannerName = Planner2FragmentArgs.fromBundle(requireArguments()).message
        view.findViewById<TextView>(R.id.planner_name).text = plannerName

        val tabTitle= arrayOf(getString(R.string.tab_lunedi).uppercase(), getString(R.string.tab_martedi).uppercase(), getString(R.string.tab_mercoledi).uppercase(), getString(R.string.tab_giovedi).uppercase(), getString(R.string.tab_venerdi).uppercase(), getString(R.string.tab_sabato).uppercase(), getString(R.string.tab_domenica).uppercase())
        val tabLayout : TabLayout = view.findViewById(R.id.tabs)
        val viewPager : ViewPager2 = view.findViewById(R.id.view_pager)
        viewPager.adapter = ViewPagerAdapter2(childFragmentManager, lifecycle, plannerName)

        TabLayoutMediator (tabLayout, viewPager) {
                tab, position -> tab.text = tabTitle[position]
        }.attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem=tab.position
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}

            override fun onTabUnselected(tab: TabLayout.Tab) {}
        })

        val bu = view.findViewById<Button>(R.id.add_train_button)
        bu.setOnClickListener {
            var day = ""
            when (viewPager.currentItem) {
                0 -> day = getString(R.string.tab_lunedi)
                1 -> day = getString(R.string.tab_lunedi)
                2 -> day = getString(R.string.tab_lunedi)
                3 -> day = getString(R.string.tab_lunedi)
                4 -> day = getString(R.string.tab_lunedi)
                5 -> day = getString(R.string.tab_lunedi)
                6 -> day = getString(R.string.tab_lunedi)
            }

            val action = Planner2FragmentDirections.actionPlanner2FragmentToPlanner3Fragment("$day|$plannerName")
            view.findNavController().navigate(action)
        }

        return view
    }
}