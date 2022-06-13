package it.unipd.dei.esp2022.app_embedded.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.transition.MaterialFadeThrough
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.helpers.*
import java.text.SimpleDateFormat
import java.util.*


//Fragment resposabile della presentation logic per la schermata 'Home'.
class HomeFragment : PopUpSeekBarFragment(), HomeListAdapter.ClickListener {
    private lateinit var resObserver : Observer<HTTParser.TrainInfo>
    private lateinit var db : DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        //Observer sul LiveData restituito da TrainViewModel.searchTrain()
        //Apre una PopupWindow quando il LiveData viene aggiornato.
        resObserver = Observer<HTTParser.TrainInfo> { info ->
            if (info.trainID.compareTo("null") == 0) {
                stopFade()
                return@Observer
            }

            if (!trainModel.updated) {
                return@Observer
            }

            stopFade()
            createPopup(info)
        }

        db = DBHelper(context as Context)

        //Recupero i treni del giorno dal database.
        val today = getDay()
        var trainsInfo = mutableListOf<DBHelper.TripInfo>()
        for (planner in db.getPlannersName()) {
            trainsInfo = trainsInfo.plus(db.getTrips(planner, today)) as MutableList<DBHelper.TripInfo>
        }

        if (trainsInfo.isEmpty()) {
            view.findViewById<ConstraintLayout>(R.id.empty_list_popup).visibility = View.VISIBLE
            view.findViewById<Button>(R.id.add_button).setOnClickListener {
                (activity as Activity).findViewById<BottomNavigationView>(R.id.bottomNavigation)
                    .selectedItemId = R.id.plannerFragment
            }
        }

        val recyclerView : RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.adapter = HomeListAdapter(trainsInfo, this)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, LinearLayoutManager.VERTICAL))

        val fab = view.findViewById<ExtendedFloatingActionButton>(R.id.home_fab)
        fab.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_homeFragment_to_ricercaViaggioFragment)
        }

        return view
    }

    //OnClickListener per la RecyclerView.
    //Sfrutta TrainViewModel ed un observer per aprire un PopupWindow contenente le informazioni del treno.
    override fun onEvent(number: String) {
        trainModel.updated = false
        trainModel.searchTrain(number).observe(viewLifecycleOwner, resObserver)
        startFade()
    }

    private fun getDay() : String {
        return when (SimpleDateFormat("u", Locale.ENGLISH).format(Date())) {
            "1"   -> "Lunedi"
            "2"   -> "Martedi"
            "3"   -> "Mercoledi"
            "4"   -> "Giovedi"
            "5"   -> "Venerdi"
            "6"   -> "Sabato"
            else  -> "Domenica"
        }
    }
}