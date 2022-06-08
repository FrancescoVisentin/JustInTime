package it.unipd.dei.esp2022.app_embedded.ui

import android.content.Context
import it.unipd.dei.esp2022.app_embedded.helpers.*
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.test.app_embedded.R

class DayFragment : PopUpRecyclerFragment(), PlannerCardAdapter2.ClickListener {
    private lateinit var resObserver : Observer<HTTParser.TrainInfo>
    lateinit var day: String
    private lateinit var plannerName:String
    private lateinit var recyclerView : RecyclerView
    private lateinit var db : DBHelper
    private lateinit var tripsImageView : LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_planner_tabs, container, false)

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
        tripsImageView = view.findViewById(R.id.no_trips_image)

        if (savedInstanceState != null) {
            plannerName = savedInstanceState.getString("Planner", "")
            day = savedInstanceState.getString("Day", "")
        }

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = PlannerCardAdapter2(db.getTrips(plannerName,day), this)
        checkTripsCount()
        return view
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_option -> {
                val number = (recyclerView.adapter as PlannerCardAdapter2).selectedTripNumber
                val contextView = (view as View).findViewById<View>(R.id.coordinator_layout)
                contextView.bringToFront()
                if (db.deleteTrip(number)){
                    Snackbar.make(contextView, "Viaggio $number eliminato", Snackbar.LENGTH_SHORT)
                        .setAction("Chiudi") {}
                        .show()
                    checkTripsCount()

                    recyclerView.adapter = PlannerCardAdapter2(db.getTrips(plannerName,day), this)
                } else {
                    Snackbar.make(contextView, "Errore", Snackbar.LENGTH_SHORT)
                        .setAction("Chiudi") {}
                        .show()
                }
            }
        }

        return super.onContextItemSelected(item)
    }

    fun setUp(tabDay: String, planner: String) {
        day = tabDay
        plannerName = planner
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("Planner", plannerName)
        outState.putString("Day", day)
    }

    override fun onEvent(number: String) {
        trainModel.updated = false
        trainModel.searchTrain(number).observe(viewLifecycleOwner, resObserver)
        startFade()
    }

    private fun checkTripsCount() {
        tripsImageView.visibility = if (db.getTripsCount(plannerName, day) == 0) View.VISIBLE else View.INVISIBLE
    }
}