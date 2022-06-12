package it.unipd.dei.esp2022.app_embedded.ui

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R
import androidx.lifecycle.Observer
import it.unipd.dei.esp2022.app_embedded.helpers.*
import java.text.SimpleDateFormat
import java.util.*

//Fragment che permette la visualizzazione dei risultati di Ricerca Viaggio
class RicercaViaggioResultFragment : PopUpRecyclerFragment(), RicercaViaggioCardAdapter.ClickListener {
    private val solutionsModel : SolutionsViewModel by activityViewModels()
    private lateinit var resObserver : Observer<HTTParser.TrainInfo>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_ricerca_viaggio_result, container, false)

        //Observer per ottenere i dati elaborati dal parsing quando si clicca su una card per ottenere maggiori informazioni (tramite popup)
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

        //Interpreto informazioni di Ricerca Viaggio tramite Navigation Safe Args
        val message = RicercaViaggioResultFragmentArgs.fromBundle(requireArguments()).message
        view.findViewById<TextView>(R.id.departure).text = capitalize(message.substringBefore("|"))
        view.findViewById<TextView>(R.id.arrival).text = capitalize(message.substringAfter("|").substringBeforeLast("|"))
        view.findViewById<TextView>(R.id.time2).text = message.substringAfterLast("|")
        view.findViewById<TextView>(R.id.date2).text = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date())

        //Recupero Viaggi corrispondenti alla ricerca effettuata
        val solutionsInfo = solutionsModel.getSolutions() ?: return view

        //Creo la recyclerView che contiene tutte le card rappresentanti i vari viaggi
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = RicercaViaggioCardAdapter(solutionsInfo, this)

        return view
    }

    //chiamato quando si clicca su una card
    override fun onEvent(number: String) {
        trainModel.updated = false
        trainModel.searchTrain(number).observe(viewLifecycleOwner, resObserver)
        startFade()
    }

    private fun capitalize(word : String) : String {
        return word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}