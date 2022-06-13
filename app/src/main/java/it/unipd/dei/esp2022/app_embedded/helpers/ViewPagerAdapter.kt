package it.unipd.dei.esp2022.app_embedded.helpers

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import it.unipd.dei.esp2022.app_embedded.ui.RoutesFragment


private const val NUM_TABS = 2  //il numero delle tabs è costante

//Classe per la gestione delle Tabs contenute nel Fragment Tabellone2 (swipe tra tabs, creazione del contenuto relativo ad ogni tab)
class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    //Numero totale di tabs
    override fun getItemCount(): Int {
        return NUM_TABS
    }

    //Per ogni tab associo il contenuto relativo (Arrivi o Partenze) creato in un Fragment
    override fun createFragment(position: Int): Fragment {
        val tab = RoutesFragment()
        when (position) {
            0 -> tab.setUp(RoutesFragment.ARRIVALS)
            1 -> tab.setUp(RoutesFragment.DEPARTURES)
        }

        return tab
    }
}
