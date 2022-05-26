package it.unipd.dei.esp2021.app_embedded

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.transition.MaterialFadeThrough
import com.test.app_embedded.R
import it.unipd.dei.esp2021.app_embedded.helpers.HTTParser
import it.unipd.dei.esp2021.app_embedded.helpers.TrainViewModel
import java.text.SimpleDateFormat
import java.util.*

class StatoTrenoFragment : Fragment() {
    private val model : TrainViewModel by activityViewModels()
    private lateinit var resObserver : Observer<HTTParser.TrainInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_stato_treno, container, false)

        resObserver = Observer<HTTParser.TrainInfo> { info ->
            if (info.trainID.compareTo("null") == 0) {
                val trainView = view.findViewById<LinearLayout>(R.id.train_description)
                trainView.visibility = View.INVISIBLE

                val contextView = view.findViewById<View>(R.id.coordinator_layout)
                Snackbar.make(contextView, "ID non valido", Snackbar.LENGTH_SHORT)
                    .setAction("Chiudi") {}
                    .show()
                return@Observer
            }
            updateTrainInfo(info)
        }

        val textView = view.findViewById<TextInputEditText>(R.id.text_train_number)
        textView.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //Hide keyboard
                textView.clearFocus()
                val imm = (activity as Activity).getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(v.windowToken, 0)

                if (textView.text.toString().isNotEmpty()) {
                    model.searchTrain(textView.text.toString()).observe(viewLifecycleOwner, resObserver)
                }

                return@setOnEditorActionListener true
            }

            return@setOnEditorActionListener false
        }

        val searchButton = view.findViewById<Button>(R.id.search_button)
        searchButton.setOnClickListener{
            if (textView.text.toString().isNotEmpty()) {
                model.searchTrain(textView.text.toString()).observe(viewLifecycleOwner, resObserver)
            }
        }

        return  view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            val oldVisibility = savedInstanceState.getInt("vis")
            val trainView = view.findViewById<LinearLayout>(R.id.train_description)
            trainView.visibility = oldVisibility

            if (oldVisibility == View.VISIBLE){
                val info = model.getTrainState() ?: return
                updateTrainInfo(info)
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val v = (view as View).findViewById<LinearLayout>(R.id.train_description)
        outState.putInt("vis", v.visibility)
    }

    private fun updateTrainInfo(info : HTTParser.TrainInfo) {
        val trainView = (view as View).findViewById<LinearLayout>(R.id.train_description)
        trainView.findViewById<TextView>(R.id.train_number).text = info.trainID
        trainView.findViewById<TextView>(R.id.train_position).text = info.lastDetectionStation
        trainView.findViewById<TextView>(R.id.time_last_detection).text = getDate(info.lastDetectionTime)
        trainView.findViewById<TextView>(R.id.train_route).text = getString(R.string.train_route, info.stops.first().stationName, info.stops.last().stationName)
        trainView.findViewById<TextView>(R.id.delay).text = if (info.delay.toInt() >= 0) "+${info.delay} min" else "${info.delay} min"
        addStationsBar(trainView, info.stops, info.currentIndex)

        crossfade(trainView)
    }

    private fun getDate(date : String) : String {
        return when (date) {
            "null" -> "--"
            else -> SimpleDateFormat("HH:mm", Locale.ENGLISH).format(date.toLong())
        }
    }

    private fun addStationsBar(trainView: View, stations : MutableList<HTTParser.StationInfo>, currentIndex : Int) {
        val labelsLayout = trainView.findViewById<LinearLayout>(R.id.labels_layout)
        labelsLayout.removeAllViews()

        for (i in 0 until stations.size) {
            val label = layoutInflater.inflate(R.layout.labels_layout, trainView as ViewGroup, false)
            label.findViewById<TextView>(R.id.station_name).text = stations[i].stationName
            label.findViewById<TextView>(R.id.scheduled_departure_value).text = getDate(stations[i].scheduledDeparture)
            label.findViewById<TextView>(R.id.scheduled_arrival_value).text = getDate(stations[i].scheduledArrival)

            if (i <= currentIndex) {
                label.findViewById<TextView>(R.id.departure_text).text = getString(R.string.real_departure)
                label.findViewById<TextView>(R.id.arrival_text).text = getString(R.string.real_arrival)
                label.findViewById<TextView>(R.id.departure_value).text = getDate(stations[i].realDeparture)
                label.findViewById<TextView>(R.id.arrival_value).text = getDate(stations[i].realArrival)

            } else {
                label.findViewById<TextView>(R.id.departure_text).text = getString(R.string.expected_departure)
                label.findViewById<TextView>(R.id.arrival_text).text = getString(R.string.expected_arrival)
                label.findViewById<TextView>(R.id.departure_value).text = getDate(stations[i].expectedDeparture)
                label.findViewById<TextView>(R.id.arrival_value).text = getDate(stations[i].expectedArrival)
            }

            labelsLayout.addView(label)

            if (i == stations.size-1) {
                label.layoutParams = getLayoutParams(1.0f)
            }
            else {
                label.layoutParams = getLayoutParams(0.0f)
            }
        }
    }

    private fun getLayoutParams(weight: Float): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, weight)
    }

    private fun crossfade(contentView : View) {
        val loadingView = (view as View).findViewById<View>(R.id.loading_spinner)
        val time = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        contentView.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(time)
                .setListener(null)
        }

        loadingView.apply {
            loadingView.alpha = 1f
            visibility = View.VISIBLE
            animate()
                .alpha(0f)
                .setDuration(time)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        loadingView.visibility = View.GONE
                    }
                })
        }
    }
}