package it.unipd.dei.esp2022.app_embedded.helpers

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import it.unipd.dei.esp2022.app_embedded.ui.*

private const val NUM_TABS = 7

class ViewPagerAdapter2(fragmentManager: FragmentManager, lifecycle: Lifecycle, private val planner: String) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        val tab = DayFragment()
        when (position) {
          0 -> tab.setUp("Lunedi", planner)
          1 -> tab.setUp("Martedi", planner)
          2 -> tab.setUp("Mercoledi", planner)
          3 -> tab.setUp("Giovedi", planner)
          4 -> tab.setUp("Venerdi", planner)
          5 -> tab.setUp("Sabato", planner)
          6 -> tab.setUp("Domenica", planner)
        }
        return tab
    }
}