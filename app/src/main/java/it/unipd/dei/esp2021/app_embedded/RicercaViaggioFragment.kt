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
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.android.material.transition.MaterialFadeThrough
import com.test.app_embedded.R
import it.unipd.dei.esp2021.app_embedded.helpers.SolutionsViewModel


class RicercaViaggioFragment : Fragment() {
    private val model : SolutionsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ricerca_viaggio, container, false)

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
                searchSolutions(textViewDepartures.text.toString().lowercase(), textViewArrivals.text.toString().lowercase())
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

    private fun searchSolutions(firstStation : String, secondStation : String) {
        val resObserver = Observer<String> {res ->
            if (res.isEmpty()) {
                val contextView = (view as View).findViewById<View>(R.id.coordinator_layout)
                Snackbar.make(contextView, "Stazioni non valide", Snackbar.LENGTH_SHORT)
                    .setAction("Chiudi") {}
                    .show()
                return@Observer
            }

            Log.e("Res:", res)
            parseSolutions(firstStation, secondStation,  res)
            //TODO Aggiungi risultati su putextra
            //view.findNavController().navigate(R.id.action_ricercaViaggioFragment_to_ricercaViaggioResultFragment)
        }
        model.searchSolutions(firstStation, secondStation).observe(viewLifecycleOwner, resObserver)
    }

    private fun parseSolutions(firstStation : String, secondStation : String, solutions : String) {
        var tmp = solutions.replace("\n", "")
        tmp = tmp.replace("vehicles\":[", "%0%")
        tmp = tmp.replace("durata\":\"", "%1%")
        tmp = tmp.replace("orarioPartenza\":\"", "%2%")
        tmp = tmp.replace("orarioArrivo\":\"", "%3%")
        tmp = tmp.replace("categoriaDescrizione\":\"", "%4%")
        tmp = tmp.replace("numeroTreno\":\"", "%5%")
        val viaggi = mutableSetOf<Viaggio>()

        val viaggiString = tmp.split("%0%") as MutableList<String>

        for (i in 1..viaggiString.lastIndex) {
            val text = viaggiString[i]
            val previousText = viaggiString[i-1]
            val durata = previousText.split("%1%")[1].substringBefore("\"")
            val partenza = text.split("%2%")[1].substringBefore("\"")
            val arrivoList = text.split("%3%")
            val arrivo = arrivoList[arrivoList.lastIndex].substringBefore("\"")
            val categoriaList = text.split("%4%") as MutableList<String>
            val it = categoriaList.listIterator()
            while (it.hasNext()) {
                val cat = it.next().substringBefore("\"")
                it.set(cat)
            }
            val numTrenoList = text.split("%5%") as MutableList<String>
            val it2 = numTrenoList.listIterator()
            while (it2.hasNext()) {
                val num = it2.next().substringBefore("\"")
                it2.set(num)
            }
            viaggi.add(Viaggio(durata, partenza, arrivo, categoriaList, numTrenoList))
        }
    }
}

class Viaggio(durata_: String, orarioPartenza_: String, orarioArrivo_: String, categoria_: MutableList<String>, numeroTreno_: MutableList<String>){
    var durata = durata_
    var orarioPartenza = orarioPartenza_
    var orarioArrivo = orarioArrivo_
    var categoria = categoria_
    var numeroTreno = numeroTreno_
}
