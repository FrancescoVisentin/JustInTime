package it.unipd.dei.esp2022.app_embedded.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import it.unipd.dei.esp2022.app_embedded.helpers.*
import java.text.SimpleDateFormat
import java.util.*

//Fragment che permette la visualizzazione dei risultati di Ricerca Viaggio di planner 3
class Planner3ResultFragment : PopUpRecyclerFragment(), PlannerCardAdapter.ClickListener {
    private val solutionsModel : SolutionsViewModel by activityViewModels()
    private lateinit var resObserver : Observer<HTTParser.TrainInfo>
    private lateinit var db: DBHelper
    private var day = ""
    private var plannerName = ""
    private var lastTrip = ""
    private var lastDepartureStation = ""
    private var lastArrivalStation = ""
    private var lastDepartureTime = ""
    private var lastArrivalTime = ""
    private var lastDuration = ""
    private var lastChanges = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ricerca_viaggio_result, container, false)

        //Recupero il database
        db = DBHelper(context as Context)
        //Observer per ottenere i dati elaborati dal parsing quando si clicca su una card per ottenere maggiori informazioni (tramite popup)
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

        //Interpreto informazioni di Ricerca Viaggio tramite Navigation Safe Args
        val message = Planner3ResultFragmentArgs.fromBundle(requireArguments()).message
        val tmp = message.split("|")
        view.findViewById<TextView>(R.id.departure).text = capitalize(tmp[0])
        view.findViewById<TextView>(R.id.arrival).text = capitalize(tmp[1])
        view.findViewById<TextView>(R.id.time2).text = tmp[2]
        view.findViewById<TextView>(R.id.date2).text = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date())
        day = tmp[3]
        plannerName = tmp[4]
        lastDepartureStation = tmp[0]
        lastArrivalStation = tmp[1]

        //Creo la recyclerView che contiene tutte le card rappresentanti i vari viaggi
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

        //Recupero Viaggi corrispondenti alla ricerca effettuata
        solutionsModel.getSolutions().observe(viewLifecycleOwner) { solutionsInfo ->
            recyclerView.adapter = PlannerCardAdapter(solutionsInfo, this)
        }

        return view
    }

    //chiamato quando si clicca su una card
    override fun onEvent(number: String, departureTime: String, arrivalTime: String, duration: String, changes: String) {
        lastTrip = number
        lastDepartureTime = departureTime
        lastArrivalTime = arrivalTime
        lastDuration = duration
        lastChanges = changes
        trainModel.updated = false
        trainModel.searchTrain(number.split(" ")[0]).observe(viewLifecycleOwner, resObserver)
        startFade()
    }

    //Configura il pulsante (contenuto nella popupView)per aggiungere un viaggio al database
    override fun setupAddButton(popupView: View, trainID: String) {
        val addButton = popupView.findViewById<Button>(R.id.add_button)
        addButton.setOnClickListener {
            if (db.addTripToPlanner(lastTrip, day, plannerName, lastDepartureStation, lastArrivalStation, lastDepartureTime, lastArrivalTime, lastDuration, lastChanges)) {
                val contextView = (view as View).findViewById<View>(R.id.coordinator_layout)
                contextView.bringToFront()
                Snackbar.make(contextView, "Treno $trainID aggiunto al planner $plannerName", Snackbar.LENGTH_SHORT)
                    .setAction("Chiudi") {}
                    .show()
            } else {
                val contextView = (view as View).findViewById<View>(R.id.coordinator_layout)
                contextView.bringToFront()
                Snackbar.make(contextView, "Errore", Snackbar.LENGTH_SHORT)
                    .setAction("Chiudi") {}
                    .show()
            }

            popupWindow?.dismiss()
        }
    }

    private fun capitalize(word : String) : String {
        return word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}