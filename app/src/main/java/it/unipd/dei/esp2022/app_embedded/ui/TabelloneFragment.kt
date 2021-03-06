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
import androidx.navigation.findNavController
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.helpers.HTTParser
import it.unipd.dei.esp2022.app_embedded.helpers.StationsViewModel

//Fragment resposabile della presentation logic per la schermata 'Tabellone'.
class TabelloneFragment : Fragment() {
    private val model : StationsViewModel by activityViewModels()
    private lateinit var resObserver : Observer<ArrayList<MutableList<HTTParser.TrainStationInfo>>>
    private var station : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_tabellone, container, false)

        //Observer per ottenere i dati elaborati dal parsing
        resObserver = Observer<ArrayList<MutableList<HTTParser.TrainStationInfo>>> { info ->
            if (info == null) {
                val contextView = view.findViewById<View>(R.id.coordinator_layout)
                Snackbar.make(contextView, "Stazione non valida", Snackbar.LENGTH_SHORT)
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
            //Tramite navigation component mi sposto sul fragment Risultati Tabellone passando la stazione con Navigation safe args
            val action = TabelloneFragmentDirections.actionTabelloneFragmentToTabellone2Fragment(station)
            view.findNavController().navigate(action)
        }

        //Configuro il menu di autocompletamento per la text fild stazione
        val textView = view.findViewById<AutoCompleteTextView>(R.id.text_autocomplete)
        val items = resources.getStringArray(R.array.stations)
        val adapter = ArrayAdapter(context as Context, android.R.layout.simple_dropdown_item_1line, items)
        textView.setAdapter(adapter)

        textView.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    textView.clearFocus()

                    if (textView.text.isNotEmpty()){
                        model.updated = false
                        station = textView.text.toString().lowercase()
                        model.searchStation(station).observe(viewLifecycleOwner, resObserver)
                        startFade()
                    }
                    true
                }
                else -> false
            }
        }

        //setOnclickListener() del pulsante per cercare la stazione
        val searchButton = view.findViewById<Button>(R.id.search_button)
        searchButton.setOnClickListener{
            if (textView.text.isNotEmpty()) {
                model.updated = false
                station = textView.text.toString().lowercase()
                model.searchStation(station).observe(viewLifecycleOwner, resObserver)
                startFade()
            }
        }

        return  view
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