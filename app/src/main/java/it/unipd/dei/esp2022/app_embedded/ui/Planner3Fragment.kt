package it.unipd.dei.esp2022.app_embedded.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.android.material.transition.MaterialFadeThrough
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.helpers.HTTParser
import it.unipd.dei.esp2022.app_embedded.helpers.SolutionsViewModel


class Planner3Fragment : Fragment() {
    private val model : SolutionsViewModel by activityViewModels()
    private lateinit var resObserver : Observer<MutableList<HTTParser.SolutionInfo>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_ricerca_viaggio, container, false)

        resObserver = Observer<MutableList<HTTParser.SolutionInfo>> { info ->
            if (info == null) {
                val contextView = (view as View).findViewById<View>(R.id.coordinator_layout)
                Snackbar.make(contextView, "Stazioni non valide", Snackbar.LENGTH_SHORT)
                    .setAction("Chiudi") {}
                    .show()
                return@Observer
            }

            //Hide keyboard if present
            val imm = (activity as Activity).getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
            fade()
            view.findNavController().navigate(R.id.action_planner3Fragment_to_planner3ResultFragment)
        }


        val buttonHour: Button = view.findViewById(R.id.button_hour)
        val buttonMin: Button = view.findViewById(R.id.button_min)
        buttonHour.setOnClickListener {
            setUpCLock(buttonHour, buttonMin)
        }
        buttonMin.setOnClickListener {
            setUpCLock(buttonHour, buttonMin)
        }

        val textViewDepartures = view.findViewById<AutoCompleteTextView>(R.id.text_departures)
        val textViewArrivals = view.findViewById<AutoCompleteTextView>(R.id.text_arrivals)
        val items = resources.getStringArray(R.array.stations)
        val adapter = ArrayAdapter(context as Context, android.R.layout.simple_dropdown_item_1line, items)
        textViewDepartures.setAdapter(adapter)
        textViewArrivals.setAdapter(adapter)

        textViewDepartures.setOnEditorActionListener { v, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    if (textViewArrivals.text.isEmpty()) {
                        textViewArrivals.requestFocus()
                        return@setOnEditorActionListener true
                    }

                    //Hide keyboard
                    textViewDepartures.clearFocus()
                    val imm = (activity as Activity).getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(v.windowToken, 0)
                    true
                }
                else -> false
            }
        }

        textViewArrivals.setOnEditorActionListener { v, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    if (textViewDepartures.text.isEmpty()) {
                        textViewDepartures.requestFocus()
                        return@setOnEditorActionListener true
                    }

                    //Hide keyboard
                    textViewArrivals.clearFocus()
                    val imm = (activity as Activity).getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(v.windowToken, 0)
                    true
                }
                else -> false
            }
        }


        val swapButton = view.findViewById<Button>(R.id.swap_button)
        swapButton.setOnClickListener {
            val tmp = textViewArrivals.text
            textViewArrivals.text = textViewDepartures.text
            textViewDepartures.text = tmp
        }


        val searchButton = view.findViewById<Button>(R.id.search_button)
        searchButton.setOnClickListener{
            if (textViewDepartures.text.isNotEmpty() && textViewArrivals.text.isNotEmpty()){
                model.searchSolutions(textViewDepartures.text.toString().lowercase(), textViewArrivals.text.toString().lowercase())
                    .observe(viewLifecycleOwner, resObserver)
            }
        }

        return view
    }

    private fun setUpCLock(buttonHour : Button, buttonMin : Button) {
        val picker: MaterialTimePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(Integer.parseInt((buttonHour.text).toString()))
                .setMinute(Integer.parseInt((buttonMin.text).toString()))
                .setTitleText("Seleziona orario di viaggio")
                .build()

        picker.show(childFragmentManager, "tag")

        picker.addOnPositiveButtonClickListener {
            buttonHour.text = (picker.hour).toString()
            buttonMin.text = (picker.minute).toString()
        }
    }

    private fun fade() {
        val loadingView = (view as View).findViewById<View>(R.id.loading_spinner)
        val time = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

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