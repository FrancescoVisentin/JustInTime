package it.unipd.dei.esp2022.app_embedded.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.helpers.DBHelper
import it.unipd.dei.esp2022.app_embedded.helpers.PlannerCardAdapter
import it.unipd.dei.esp2022.app_embedded.helpers.PlannerListAdapter
import it.unipd.dei.esp2022.app_embedded.helpers.SolutionsViewModel
import java.text.SimpleDateFormat
import java.util.*


class Planner3ResultFragment : Fragment() {

    private val model : SolutionsViewModel by activityViewModels()
    private lateinit var db : DBHelper
    private var day :String = ""
    private var plannerName = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ricerca_viaggio_result, container, false)
        val message = Planner3ResultFragmentArgs.fromBundle(requireArguments()).message
        Log.e("Info a planner 3 result", message)
        var tmp = message.split("|")
        view.findViewById<TextView>(R.id.departure).text = tmp[0].capitalize()
        view.findViewById<TextView>(R.id.arrival).text = tmp[1].capitalize()
        view.findViewById<TextView>(R.id.time2).text = tmp[2]
        view.findViewById<TextView>(R.id.date2).text = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date())
        day = tmp[3]
        plannerName = tmp[4]

        val solutionsInfo = model.getSolutions()


        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        var mLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager=mLayoutManager
        var adapter: RecyclerView.Adapter<PlannerCardAdapter.CardViewHolder3>?=null

        adapter= PlannerCardAdapter(solutionsInfo!!)
        recyclerView.adapter = adapter

        return view
    }

}