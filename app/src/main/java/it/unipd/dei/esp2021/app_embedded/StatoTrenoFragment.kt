package it.unipd.dei.esp2021.app_embedded

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.transition.MaterialFadeThrough
import com.test.app_embedded.R
import java.text.SimpleDateFormat
import java.util.*

class StatoTrenoFragment : Fragment() {
    private val model : HttpViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_stato_treno, container, false)

        val textView = view.findViewById<TextInputEditText>(R.id.text_train_number)
        textView.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //Hide keyboard
                textView.clearFocus()
                val imm = (activity as Activity).getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(v.windowToken, 0)

                if (textView.text.toString().isNotEmpty()) {
                    searchTrain(textView.text.toString())
                }

                return@setOnEditorActionListener true
            }

            return@setOnEditorActionListener false
        }

        val searchButton = view.findViewById<Button>(R.id.search_button)
        searchButton.setOnClickListener{
            if (textView.text.toString().isNotEmpty()) {
                searchTrain(textView.text.toString())
            }
        }

        return  view
    }

    private fun searchTrain(trainID : String) {
        val resObserver = Observer<String> {res ->
            if (res.isEmpty()) {
                val trainView = (view as View).findViewById<LinearLayout>(R.id.train_description)
                trainView.visibility = View.INVISIBLE

                val contextView = (view as View).findViewById<View>(R.id.coordinator_layout)
                Snackbar.make(contextView, "ID non valido", Snackbar.LENGTH_SHORT)
                    .setAction("Chiudi") {}
                    .show()
                return@Observer
            }

            parseTrainInfo(trainID, res)
        }
        model.searchTrain(trainID).observe(viewLifecycleOwner, resObserver)
    }

    private fun parseTrainInfo(trainID: String, trainInfo : String) {

        val tmp = trainInfo.replace("\n", "").split("|")
        val trainState = tmp[0]
        val trainRoute = tmp[1]

        val trainStops = trainRoute.split(",{") as MutableList<String>
        val it = trainStops.listIterator()
        var currentStationIndex = 0
        it.forEach { station ->
            val stationName = station.substringAfter("stazione\":\"").substringBefore("\",")
            if (station.indexOf("Corrente\":true,") != -1) {
                currentStationIndex = it.nextIndex()-1
            }

            it.set(stationName)
        }

        val delay = trainState.substringAfter("],\"ritardo\":").substringBefore(",")
        val lastDetectionInfo = trainState.substringAfter("oraUltimoRilevamento\":")
        val lastDetectionTime = lastDetectionInfo.substringBefore(",")
        val lastDetectionStation = lastDetectionInfo.substringAfter("stazioneUltimoRilevamento\":\"").substringBefore("\",")

        val trainView = (view as View).findViewById<LinearLayout>(R.id.train_description)
        trainView.findViewById<TextView>(R.id.train_number).text = trainID
        trainView.findViewById<TextView>(R.id.train_position).text = lastDetectionStation
        trainView.findViewById<TextView>(R.id.time_last_detection).text = getDate(lastDetectionTime)
        trainView.findViewById<TextView>(R.id.train_route).text = "${trainStops.first()} - ${trainStops.last()}"
        trainView.findViewById<TextView>(R.id.delay).text = if (delay.toInt() >= 0) "+$delay min" else "$delay min"
        addStationsBar(trainView, trainStops, currentStationIndex)

        crossfade(trainView)
    }


    private fun getDate(date : String) : String {
        return when (date) {
            "null" -> "--"
            else -> SimpleDateFormat("HH:mm", Locale.ENGLISH).format(date.toLong())
        }
    }

    private fun addStationsBar(trainView: View, stations : MutableList<String>, currentIndex : Int) {

        val stepBar = trainView.findViewById<SeekBar>(R.id.step_bar)

        stepBar.setOnTouchListener { _, _ -> true}

        stepBar.progress = currentIndex
        stepBar.max = stations.size-1
        stepBar.minWidth = (stations.size-1)*300

        val labelsLayout = trainView.findViewById<LinearLayout>(R.id.labels_layout)
        labelsLayout.removeAllViews()

        for (i in 0 until stations.size) {
            val label = TextView(trainView.context)
            label.text = stations[i]
            label.setPadding(0, 0, 10, 0)
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
        return LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT, weight)
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