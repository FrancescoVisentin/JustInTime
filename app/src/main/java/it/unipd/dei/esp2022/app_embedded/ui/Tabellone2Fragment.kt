package it.unipd.dei.esp2022.app_embedded.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.tabs.TabLayoutMediator
import com.test.app_embedded.R
import java.util.*

class Tabellone2Fragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_tabellone2, container, false)

        val message = Tabellone2FragmentArgs.fromBundle(requireArguments()).message
        view.findViewById<TextView>(R.id.station).text = message.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        val tabTitle= arrayOf(getString(R.string.tab_arrivi), getString(R.string.tab_partenze))
        val tabLayout : TabLayout = view.findViewById(R.id.tabs)
        val viewPager : ViewPager2 = view.findViewById(R.id.view_pager)
        viewPager.adapter = ViewPagerAdapter(childFragmentManager, lifecycle)

        TabLayoutMediator (tabLayout, viewPager) {
            tab, position -> tab.text = tabTitle[position]
        }.attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem=tab!!.position
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })

        return view
    }
}