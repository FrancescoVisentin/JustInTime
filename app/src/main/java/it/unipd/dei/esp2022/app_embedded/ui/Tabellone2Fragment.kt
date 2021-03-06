package it.unipd.dei.esp2022.app_embedded.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.helpers.ViewPagerAdapter
import java.util.*

//Classe per la visualizzazione dei risultati della ricerca fatta in Tabellone1
class Tabellone2Fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_tabellone2, container, false)

        //Visualizzazione del nome della stazione ricercata
        val message = Tabellone2FragmentArgs.fromBundle(requireArguments()).message
        view.findViewById<TextView>(R.id.station).text = message.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        val tabTitle= arrayOf(getString(R.string.tab_arrivi), getString(R.string.tab_partenze))     //Etichette delle tabs
        val tabLayout : TabLayout = view.findViewById(R.id.tabs)
        val viewPager : ViewPager2 = view.findViewById(R.id.view_pager)
        viewPager.adapter = ViewPagerAdapter(childFragmentManager, lifecycle)

        //Mediatore per collegare TabLayout con il ViewPager2
        //Sincronizzazione tra la posizione di ViewPager2 e la tab selezionata (in caso di selezione della tab)
        //Sincronizzazione con la posizione di scorrimento di TabLayout (in caso di scorrimento da parte dell'utente)
        TabLayoutMediator (tabLayout, viewPager) {
            tab, position -> tab.text = tabTitle[position]
        }.attach()

        //Selezione di tab e regolazione del ViewPager2
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