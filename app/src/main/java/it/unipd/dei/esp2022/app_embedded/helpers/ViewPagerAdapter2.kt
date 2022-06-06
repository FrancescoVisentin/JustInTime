package it.unipd.dei.esp2022.app_embedded.helpers

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import it.unipd.dei.esp2022.app_embedded.ui.*

private const val NUM_TABS = 7

class ViewPagerAdapter2(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
      when (position) {
          0 -> return LunediFragment()
          1 -> return MartediFragment()
          2 -> return MercolediFragment()
          3 -> return GiovediFragment()
          4 -> return VenerdiFragment()
          5 -> return SabatoFragment()
          6 -> return DomenicaFragment()
        }
        return LunediFragment()
    }
}