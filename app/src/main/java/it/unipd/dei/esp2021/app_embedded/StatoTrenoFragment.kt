package it.unipd.dei.esp2021.app_embedded

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
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
        textView.setOnEditorActionListener { v, actionId, event ->
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

            Log.e("Res:", res)
            parseTrainInfo(trainID, res)
        }
        model.searchTrain(trainID).observe(viewLifecycleOwner, resObserver)
    }

    private fun parseTrainInfo(trainID: String, trainInfo : String) {

        var tmp = trainInfo.replace("\n", "")
        tmp = tmp.replace("stazione\":\"", "%1%")
        tmp = tmp.replace("stazioneUltimoRilevamento\":\"", "%2%")
        tmp = tmp.replace("servizi", "%3%")

        val trainStops = tmp.split("%1%") as MutableList<String>
        val it = trainStops.listIterator()
        while (it.hasNext()) {
            val stop = it.next().substringBefore("\"").lowercase()
            it.set(stop)
        }

        val lastDetection = tmp.split("%2%")[1].substringBefore("\"").lowercase()
        val delay = tmp.split("%3%")[1].substringAfter("ritardo\":").substringBefore(",")

        val trainView = (view as View).findViewById<LinearLayout>(R.id.train_description)
        trainView.findViewById<TextView>(R.id.train_number).text = trainID
        trainView.findViewById<TextView>(R.id.train_position).text = lastDetection
        trainView.findViewById<TextView>(R.id.train_route).text = "${trainStops[1]} - ${trainStops.last()}"
        trainView.findViewById<TextView>(R.id.delay).text = delay
        addStationsBar(trainView, trainStops)
        trainView.visibility = View.VISIBLE
    }

    private fun addStationsBar(trainView: View, stations : MutableList<String>) {

        val stepBar = trainView.findViewById<SeekBar>(R.id.step_bar)

        stepBar.setOnTouchListener { _, _ ->
            true
        }

        stepBar.progress = 3
        stepBar.max = stations.size-2
        stepBar.minWidth = (stations.size-2)*250

        val labelsLayout = trainView.findViewById<LinearLayout>(R.id.labels_layout)

        for (i in 1 until stations.size) {
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
        return LinearLayout.LayoutParams(250, LinearLayout.LayoutParams.WRAP_CONTENT, weight)
    }

}