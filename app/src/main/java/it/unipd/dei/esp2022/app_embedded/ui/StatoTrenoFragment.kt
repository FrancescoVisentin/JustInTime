package it.unipd.dei.esp2022.app_embedded.ui

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.transition.MaterialFadeThrough
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.helpers.HTTParser
import it.unipd.dei.esp2022.app_embedded.helpers.StationsListAdapter
import it.unipd.dei.esp2022.app_embedded.helpers.TrainViewModel
import java.text.SimpleDateFormat
import java.util.*


//Fragment resposabile della presentation logic per la schermata 'Stato treno'.
class StatoTrenoFragment : Fragment() {
    private val model : TrainViewModel by activityViewModels()
    private lateinit var resObserver : Observer<HTTParser.TrainInfo>
    private var lastQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_stato_treno, container, false)

        //Observer per ottenere i dati elaborati dal parsing
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

            if (!model.updated) {
                return@Observer
            }

            //Nascondo la tastiera se presente
            val imm = (activity as Activity).getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)

            updateTrainInfo(info)
        }
        model.updated = false
        model.ret.observe(viewLifecycleOwner, resObserver)

        val textView = view.findViewById<TextInputEditText>(R.id.text_train_number)
        textView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                textView.clearFocus()

                if (textView.text.toString().isNotEmpty()) {
                    lastQuery = textView.text.toString()
                    model.searchTrain(lastQuery)
                }

                return@setOnEditorActionListener true
            }

            return@setOnEditorActionListener false
        }

        //setOnclickListener() del pulsante per cercare il treno
        val searchButton = view.findViewById<Button>(R.id.search_button)
        searchButton.setOnClickListener{
            if (textView.text.toString().isNotEmpty()) {
                lastQuery = textView.text.toString()
                model.searchTrain(lastQuery)
            }
        }

        return  view
    }

    //Gestisce il salvataggio di stato
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            val oldVisibility = savedInstanceState.getInt("vis", View.INVISIBLE)
            val trainView = view.findViewById<LinearLayout>(R.id.train_description)
            trainView.visibility = oldVisibility

            lastQuery = savedInstanceState.getString("query", "")

            if (oldVisibility == View.VISIBLE){
                val info = model.getTrainState()
                if (info != null)
                    updateTrainInfo(info)
                else
                    model.searchTrain(lastQuery)
            }
        }

    }

    //Gestisce il salvataggio di stato
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val v = (view as View).findViewById<LinearLayout>(R.id.train_description)
        outState.putInt("vis", v.visibility)
        outState.putString("query", lastQuery)
    }

    //Recupera le informazioni elaborate dal parsing e le inserisce nelle textView dell'interfaccia utente
    private fun updateTrainInfo(info : HTTParser.TrainInfo) {
        val trainView = (view as View).findViewById<LinearLayout>(R.id.train_description)
        trainView.findViewById<TextView>(R.id.train_number).text = info.trainID
        trainView.findViewById<TextView>(R.id.train_position).text = info.lastDetectionStation
        trainView.findViewById<TextView>(R.id.time_last_detection).text = getDate(info.lastDetectionTime)
        trainView.findViewById<TextView>(R.id.train_route).text = getString(R.string.train_route, info.stops.first().stationName, info.stops.last().stationName)
        trainView.findViewById<TextView>(R.id.delay).text = if (info.delay.toInt() >= 0) "+${info.delay} min" else "${info.delay} min"
        addStationsBar(trainView, info.stops, info.currentIndex)

        crossFade(trainView)
    }

    //Recupero la data corrente
    private fun getDate(date : String) : String {
        return when (date) {
            "null" -> "--"
            else -> SimpleDateFormat("HH:mm", Locale.ENGLISH).format(date.toLong())
        }
    }

    //Aggiungo la recyclerView che mostra i dettagli del percorso del treno, includento tutti gli orari di partenza e arrivo ad ogni stazione
    private fun addStationsBar(trainView: View, stations : MutableList<HTTParser.StationInfo>, currentIndex : Int) {
        val stationsAdapter = StationsListAdapter(stations, currentIndex)
        val recyclerView = trainView.findViewById<RecyclerView>(R.id.stations_recycler_view)
        recyclerView.adapter = stationsAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    //Animazione che mostra il caricamento
    private fun crossFade(contentView : View) {
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