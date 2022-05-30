package it.unipd.dei.esp2022.app_embedded.ui

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import it.unipd.dei.esp2022.app_embedded.helpers.*
import java.text.SimpleDateFormat
import java.util.*


class Planner3ResultFragment : Fragment(), PlannerCardAdapter.ClickListener {
    private val solutionsModel : SolutionsViewModel by activityViewModels()
    private val trainModel : TrainViewModel by activityViewModels()
    private lateinit var resObserver : Observer<HTTParser.TrainInfo>
    private lateinit var db: DBHelper
    private var day :String = ""
    private var plannerName = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ricerca_viaggio_result, container, false)

        db = DBHelper(context as Context)

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
        Log.e("Info a planner 3 result", message)
        var tmp = message.split("|")
        view.findViewById<TextView>(R.id.departure).text = tmp[0].capitalize()
        view.findViewById<TextView>(R.id.arrival).text = tmp[1].capitalize()
        view.findViewById<TextView>(R.id.time2).text = tmp[2]
        view.findViewById<TextView>(R.id.date2).text = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date())
        day = tmp[3]
        plannerName = tmp[4]

        val solutionsInfo = solutionsModel.getSolutions() ?: return view

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = PlannerCardAdapter(solutionsInfo, this)

        return view
    }

    override fun onEvent(number: String) {
        trainModel.updated = false
        trainModel.searchTrain(number).observe(viewLifecycleOwner, resObserver)
        startFade()
    }

    private fun createPopup(trainInfo: HTTParser.TrainInfo) {
        val inflater = LayoutInflater.from((view as View).context)
        val popupView = inflater.inflate(R.layout.popup_train_description_planner, (view as View).parent as ViewGroup, false)

        popupView.findViewById<TextView>(R.id.train_number).text = trainInfo.trainID
        popupView.findViewById<TextView>(R.id.train_route).text = getString(R.string.train_route, trainInfo.stops.first().stationName, trainInfo.stops.last().stationName)

        val width = ((view as View).width*0.85).toInt()
        val height = ((view as View).height*0.6).toInt()
        val popupWindow = PopupWindow(popupView, width, height,false)

        popupWindow.animationStyle = androidx.appcompat.R.style.Animation_AppCompat_DropDownUp
        popupWindow.elevation = 100F
        popupWindow.isOutsideTouchable = true
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        val exitButton = popupView.findViewById<Button>(R.id.exit_button)
        exitButton.setOnClickListener {
            popupWindow.dismiss()
        }

        val addButton = popupView.findViewById<Button>(R.id.add_button)
        addButton.setOnClickListener {
            //TODO da implementare l'aggiunta
            if (db.addTrainToPlanner()) {
                val contextView = (view as View).findViewById<View>(R.id.coordinator_layout)
                contextView.bringToFront()
                Snackbar.make(contextView, "Treno aggiunto", Snackbar.LENGTH_SHORT)
                    .setAction("Chiudi") {}
                    .show()
            } else {
                val contextView = (view as View).findViewById<View>(R.id.coordinator_layout)
                contextView.bringToFront()
                Snackbar.make(contextView, "Errore", Snackbar.LENGTH_SHORT)
                    .setAction("Chiudi") {}
                    .show()
            }

            popupWindow.dismiss()
        }

        addStationsBar(popupView, trainInfo.stops, trainInfo.currentIndex)
    }

    private fun addStationsBar(popupView: View, stops: MutableList<HTTParser.StationInfo>, currentIndex : Int) {
        val stationsAdapter = StationsListAdapter(stops, currentIndex)
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