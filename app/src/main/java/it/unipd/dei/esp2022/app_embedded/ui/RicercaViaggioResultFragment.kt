package it.unipd.dei.esp2022.app_embedded.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.helpers.RicercaViaggioCardAdapter
import it.unipd.dei.esp2022.app_embedded.helpers.SolutionsViewModel
import java.text.SimpleDateFormat
import java.util.*

class RicercaViaggioResultFragment : Fragment() {
    private val model : SolutionsViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_ricerca_viaggio_result, container, false)
        val message = RicercaViaggioResultFragmentArgs.fromBundle(requireArguments()).message
        view.findViewById<TextView>(R.id.departure).text = message.substringBefore("|").capitalize()
        view.findViewById<TextView>(R.id.arrival).text = message.substringAfter("|").substringBeforeLast("|").capitalize()
        view.findViewById<TextView>(R.id.time2).text = message.substringAfterLast("|")
        view.findViewById<TextView>(R.id.date2).text = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date())
        Log.e("Message: ", message)

        val solutionsInfo = model.getSolutions()


        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val mLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager=mLayoutManager
        var adapter: RecyclerView.Adapter<RicercaViaggioCardAdapter.CardViewHolder2>?=null

        adapter= RicercaViaggioCardAdapter(solutionsInfo!!)
        recyclerView.adapter = adapter

        return view
    }

}