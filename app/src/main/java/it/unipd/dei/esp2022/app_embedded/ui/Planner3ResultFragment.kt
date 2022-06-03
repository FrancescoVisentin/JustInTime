package it.unipd.dei.esp2022.app_embedded.ui

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import it.unipd.dei.esp2022.app_embedded.helpers.*
import java.text.SimpleDateFormat
import java.util.*


class Planner3ResultFragment : Fragment(), PlannerCardAdapter.ClickListener {
    private val solutionsModel : SolutionsViewModel by activityViewModels()
    private val trainModel : TrainViewModel by activityViewModels()
    private lateinit var resObserver : Observer<HTTParser.TrainInfo>
    private lateinit var db: DBHelper
    private var popupWindow: PopupWindow? = null
    private var popupWindowActivated: Boolean = false
    private var day :String = ""
    private var plannerName = ""
    private var lastTrip = ""
    private var lastDepartureStation = ""
    private var lastArrivalStation = ""
    private var lastDepartureTime = ""
    private var lastArrivalTime = ""
    private var lastDuration = ""
    private var lastChanges = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ricerca_viaggio_result, container, false)

        db = DBHelper(context as Context)
        db.checkTable()
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

        val solutionsInfo = solutionsModel.getSolutions() ?: return view

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = PlannerCardAdapter(solutionsInfo, this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            popupWindowActivated= savedInstanceState.getBoolean("popup_visibility")

            if (popupWindowActivated) {
                val info = trainModel.getTrainState() ?: return
                createPopup(info)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("popup_visibility", popupWindowActivated)
    }

    override fun onStop() {
        super.onStop()
        val restore = popupWindowActivated
        popupWindow?.dismiss()
        popupWindowActivated = restore
    }

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

    private fun createPopup(trainInfo: HTTParser.TrainInfo) {
        val inflater = LayoutInflater.from((view as View).context)
        val popupView = inflater.inflate(R.layout.popup_train_description_planner, (view as View).parent as ViewGroup, false)

        popupView.findViewById<TextView>(R.id.train_number).text = trainInfo.trainID
        popupView.findViewById<TextView>(R.id.train_route).text = getString(R.string.train_route, trainInfo.stops.first().stationName, trainInfo.stops.last().stationName)

        val width = ((view as View).width*0.85).toInt()
        val height = ((view as View).height*0.6).toInt()
        popupWindow = PopupWindow(popupView, width, height,true)

        popupWindow?.animationStyle = androidx.appcompat.R.style.Animation_AppCompat_DropDownUp
        popupWindow?.elevation = 100F
        popupWindow?.isOutsideTouchable = true
        popupWindowActivated = true
        popupWindow?.setOnDismissListener {
            popupWindowActivated = false
            popupWindow = null
        }

        val popupContainerView = (view as View).findViewById<View>(R.id.popup_container)

        if (height == 0 || width == 0) {
            popupContainerView.post {
                val updatedWidth = ((view as View).width*0.85).toInt()
                val updatedHeight = ((view as View).height*0.6).toInt()
                popupWindow?.update(0,0, updatedWidth, updatedHeight)
                popupWindow?.showAtLocation(popupContainerView, Gravity.CENTER, 0, 0)
            }
        } else {
            popupWindow?.showAtLocation(popupContainerView, Gravity.CENTER, 0, 0)
        }

        val exitButton = popupView.findViewById<Button>(R.id.exit_button)
        exitButton.setOnClickListener {
            popupWindow?.dismiss()
        }

        val addButton = popupView.findViewById<Button>(R.id.add_button)
        addButton.setOnClickListener {
            if (db.addTripToPlanner(lastTrip, day, plannerName, lastDepartureStation, lastArrivalStation, lastDepartureTime, lastArrivalTime, lastDuration, lastChanges)) {
                val contextView = (view as View).findViewById<View>(R.id.coordinator_layout)
                contextView.bringToFront()
                Snackbar.make(contextView, "Treno ${trainInfo.trainID} aggiunto al planner $plannerName", Snackbar.LENGTH_SHORT)
                    .setAction("Chiudi") {}
                    .show()
            } else {
                val contextView = (view as View).findViewById<View>(R.id.coordinator_layout)
                contextView.bringToFront()
                Snackbar.make(contextView, "Errore", Snackbar.LENGTH_SHORT)
                    .setAction("Chiudi") {}
                    .show()
            }

            db.checkTable()
            popupWindow?.dismiss()
        }

        addStationsBar(popupView, trainInfo.stops)
    }

    private fun addStationsBar(popupView: View, stops: MutableList<HTTParser.StationInfo>) {
        val stationsAdapter = StationsListAdapter(stops, -1)
        val recyclerView = popupView.findViewById<RecyclerView>(R.id.stations_recycler_view)
        recyclerView.adapter = stationsAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun capitalize(word : String) : String {
        return word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    private fun startFade() {
        val loadingView = (view as View).findViewById<View>(R.id.loading_spinner)
        val time = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        loadingView.apply {
            loadingView.alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).duration = time
        }
    }

    private fun stopFade() {
        val loadingView = (view as View).findViewById<View>(R.id.loading_spinner)

        loadingView.apply {
            loadingView.alpha = 0f
            visibility = View.INVISIBLE
        }
    }
}