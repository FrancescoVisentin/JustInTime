package it.unipd.dei.esp2022.app_embedded.helpers

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.ui.*

private const val NUM_TABS = 7  //il numero delle tabs è costante (giorni della settimana)

//Classe per la gestione delle Tabs contenute nel Fragment Planner2
//(swipe tra tabs, creazione del contenuto relativo ad ogni tab)
class ViewPagerAdapter2(fragmentManager: FragmentManager, lifecycle: Lifecycle, private val planner: String, private val context: Context) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    //Ritorna numero tabs
    override fun getItemCount(): Int {
        return NUM_TABS
    }

    //Per ogni tab che identifica il giorno della settimana associo il contenuto relativo creato in un Fragment
    //parametri rappresentano il giorno della settimana e il planner selezionato (parametro classe)
    //Ritorna il fragment con giorno settimana e planner impostati correttamente in base alla tab
    override fun createFragment(position: Int): Fragment {
        val tab = DayFragment()
        when (position) {
          0 -> tab.setUp(context.getString(R.string.tab_lunedi), planner)
          1 -> tab.setUp(context.getString(R.string.tab_martedi), planner)
          2 -> tab.setUp(context.getString(R.string.tab_mercoledi), planner)
          3 -> tab.setUp(context.getString(R.string.tab_giovedi), planner)
          4 -> tab.setUp(context.getString(R.string.tab_venerdi), planner)
          5 -> tab.setUp(context.getString(R.string.tab_sabato), planner)
          6 -> tab.setUp(context.getString(R.string.tab_domenica), planner)
        }
        return tab
    }
}