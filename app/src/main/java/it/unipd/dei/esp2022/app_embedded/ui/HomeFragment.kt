package it.unipd.dei.esp2022.app_embedded.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialFadeThrough
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.helpers.DBHelper
import it.unipd.dei.esp2022.app_embedded.helpers.HTTParser
import it.unipd.dei.esp2022.app_embedded.helpers.HomeListAdapter
import it.unipd.dei.esp2022.app_embedded.helpers.TrainViewModel
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(), HomeListAdapter.ClickListener {
    private val trainModel : TrainViewModel by activityViewModels()
    private lateinit var resObserver : Observer<HTTParser.TrainInfo>
    private lateinit var db : DBHelper
    private var popupWindow: PopupWindow? = null
    private var popupWindowActivated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

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

        val fab = view.findViewById<FloatingActionButton>(R.id.home_fab)
        fab.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_homeFragment_to_ricercaViaggioFragment)
        }

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

    override fun onEvent(number: String) {
        trainModel.updated = false
        trainModel.searchTrain(number).observe(viewLifecycleOwner, resObserver)
        startFade()
    }

    private fun createPopup(trainInfo: HTTParser.TrainInfo) {
        val inflater = LayoutInflater.from((view as View).context)
        val popupView = inflater.inflate(R.layout.popup_train_description, view as ViewGroup, false)

        popupView.findViewById<TextView>(R.id.train_number).text = trainInfo.trainID
        popupView.findViewById<TextView>(R.id.train_route).text = getString(R.string.train_route, trainInfo.stops.first().stationName, trainInfo.stops.last().stationName)
        popupView.findViewById<TextView>(R.id.train_position).text = trainInfo.lastDetectionStation
        popupView.findViewById<TextView>(R.id.time_last_detection).text = getDate(trainInfo.lastDetectionTime)
        popupView.findViewById<TextView>(R.id.delay).text = if (trainInfo.delay.toInt() >= 0) "+${trainInfo.delay} min" else "${trainInfo.delay} min"

        val width = ((view as View).width*0.85).toInt()
        popupWindow = PopupWindow(popupView, width, WindowManager.LayoutParams.WRAP_CONTENT,true)

        popupWindow?.animationStyle = androidx.appcompat.R.style.Animation_AppCompat_DropDownUp
        popupWindow?.elevation = 100F
        popupWindow?.isOutsideTouchable = true
        popupWindowActivated = true
        popupWindow?.setOnDismissListener {
            popupWindowActivated = false
            popupWindow = null
        }

        val popupContainerView = (view as View).findViewById<View>(R.id.popup_container)

        if (width == 0) {
            popupContainerView.post {
                val updatedWidth = ((view as View).width*0.85).toInt()
                popupWindow?.update(0,0, updatedWidth, WindowManager.LayoutParams.WRAP_CONTENT)
                popupWindow?.showAtLocation(popupContainerView, Gravity.CENTER, 0, 0)
            }
        } else {
            popupWindow?.showAtLocation(popupContainerView, Gravity.CENTER, 0, 0)
        }

        val exitButton = popupView.findViewById<Button>(R.id.exit_button)
        exitButton.setOnClickListener {
            popupWindow?.dismiss()
        }

        addStationsBar(popupView, trainInfo.stops, trainInfo.currentIndex)
    }

    private fun addStationsBar(popupView: View, stops: MutableList<HTTParser.StationInfo>, currentIndex: Int){
        val stepBar = popupView.findViewById<SeekBar>(R.id.step_bar)
        stepBar.setOnTouchListener { _, _ -> true }

        val stationsNum = stops.size-1

        stepBar.progress = currentIndex
        stepBar.max = stationsNum
        stepBar.minWidth = stationsNum*300

        val labelsLayout = popupView.findViewById<LinearLayout>(R.id.labels_layout)
        labelsLayout.removeAllViews()

        stops.forEachIndexed { index, stationInfo ->
            val label = TextView(popupView.context)
            label.text = stationInfo.stationName
            label.setPadding(0, 0, 10, 0)
            labelsLayout.addView(label)

            if (index == stationsNum-1) {
                label.layoutParams = getLayoutParams(1.0f)
            }
            else {
                label.layoutParams = getLayoutParams(0.0f)
            }
        }
    }

    private fun getLayoutParams(weight: Float): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT, weight)
    }

    private fun getDate(date : String) : String {
        return when (date) {
            "null" -> "--"
            else -> SimpleDateFormat("HH:mm", Locale.ENGLISH).format(date.toLong())
        }
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

    private fun startFade() {
        val loadingView = (view as View).findViewById<View>(R.id.loading_spinner)
        val time = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        loadingView.apply {
            bringToFront()
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