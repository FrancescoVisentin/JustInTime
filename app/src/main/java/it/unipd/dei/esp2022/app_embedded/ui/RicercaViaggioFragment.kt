package it.unipd.dei.esp2022.app_embedded.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
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
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.android.material.transition.MaterialFadeThrough
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.helpers.HTTParser
import it.unipd.dei.esp2022.app_embedded.helpers.SolutionsViewModel
import java.text.SimpleDateFormat
import java.util.*

//Fragment resposabile della presentation logic per la schermata 'Ricerca Viaggio'.
class RicercaViaggioFragment : Fragment() {
    private val model : SolutionsViewModel by activityViewModels()
    private lateinit var resObserver : Observer<MutableList<HTTParser.SolutionInfo>>
    private var departure : String = ""
    private var destination : String = ""
    private var time : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_ricerca_viaggio, container, false)

        //Observer per ottenere i dati elaborati dal parsing
        resObserver = Observer<MutableList<HTTParser.SolutionInfo>> { info ->
            if (info == null) {
                val contextView = (view as View).findViewById<View>(R.id.coordinator_layout)
                Snackbar.make(contextView, "Stazioni non valide", Snackbar.LENGTH_SHORT)
                    .setAction("Chiudi") {}
                    .show()
                stopFade()
                return@Observer
            }

            if (!model.updated) {
                return@Observer
            }

            //Nascondo la tastiera se presente
            val imm = (activity as Activity).getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
            stopFade()
            //Tramite navigation component mi sposto sul fragment Risultati Ricerca Viaggio passando stazione di partenza, di arrivo e orario con Navigation safe args
            val action = RicercaViaggioFragmentDirections.actionRicercaViaggioFragmentToRicercaViaggioResultFragment("$departure|$destination|$time")
            view.findNavController().navigate(action)
        }

        //Imposto il timePicker inserendo l'ora attuale come Default
        val buttonHour: Button = view.findViewById(R.id.button_hour)
        val buttonMin: Button = view.findViewById(R.id.button_min)
        buttonHour.text = SimpleDateFormat("HH", Locale.ENGLISH).format(Date())
        buttonMin.text = SimpleDateFormat("mm", Locale.ENGLISH).format(Date())
        //setOnClickListener() dei pulsanti per modificare l'ora di viaggio
        buttonHour.setOnClickListener {
            setUpCLock(buttonHour, buttonMin)
        }
        buttonMin.setOnClickListener {
            setUpCLock(buttonHour, buttonMin)
        }

        //Configuro i menu di autocompletamento per le text filds stazione di partenza e stazione di arrivo
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

                    //Nascondo la tastiera
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

                    //Nascondo la tastiera
                    textViewArrivals.clearFocus()
                    val imm = (activity as Activity).getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(v.windowToken, 0)
                    true
                }
                else -> false
            }
        }

        //setOnclickListener() del pulsante per scambiare la text field partenza con quella di arrivo
        val swapButton = view.findViewById<Button>(R.id.swap_button)
        swapButton.setOnClickListener {
            val tmp = textViewArrivals.text
            textViewArrivals.text = textViewDepartures.text
            textViewDepartures.text = tmp
        }

        //setOnclickListener() del pulsante per cercare il viaggio
        val searchButton = view.findViewById<Button>(R.id.search_button)
        searchButton.setOnClickListener{
            if (textViewDepartures.text.isNotEmpty() && textViewArrivals.text.isNotEmpty()){
                departure = textViewDepartures.text.toString().lowercase()
                destination = textViewArrivals.text.toString().lowercase()
                time = "${buttonHour.text}:${buttonMin.text}"

                model.updated = false
                model.searchSolutions(departure, destination, time).observe(viewLifecycleOwner, resObserver)

                startFade()
            }
        }

        return view
    }

    //Metodo per impostare il timePicker
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

    //Gestisce l'avvio dell'animazione che mostra il caricamento
    private fun startFade() {
        val loadingView = (view as View).findViewById<View>(R.id.loading_spinner)
        val time = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        loadingView.apply {
            loadingView.alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).duration = time
        }
    }

    //Gestisce la terminazione dell'animazione che mostra il caricamento
    private fun stopFade() {
        val loadingView = (view as View).findViewById<View>(R.id.loading_spinner)

        loadingView.apply {
            loadingView.alpha = 0f
            visibility = View.INVISIBLE
        }
    }
}
