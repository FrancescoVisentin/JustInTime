package it.unipd.dei.esp2022.app_embedded.helpers

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.test.app_embedded.R
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
          0 -> tab.setUp(Resources.getSystem().getString(R.string.tab_lunedi), planner)
          1 -> tab.setUp(Resources.getSystem().getString(R.string.tab_martedi), planner)
          2 -> tab.setUp(Resources.getSystem().getString(R.string.tab_mercoledi), planner)
          3 -> tab.setUp(Resources.getSystem().getString(R.string.tab_giovedi), planner)
          4 -> tab.setUp(Resources.getSystem().getString(R.string.tab_venerdi), planner)
          5 -> tab.setUp(Resources.getSystem().getString(R.string.tab_sabato), planner)
          6 -> tab.setUp(Resources.getSystem().getString(R.string.tab_domenica), planner)
        }
        return tab
    }
}