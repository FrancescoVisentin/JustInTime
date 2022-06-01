package it.unipd.dei.esp2022.app_embedded.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.helpers.StationsViewModel
import it.unipd.dei.esp2022.app_embedded.helpers.TabelloneCardAdapter

class ArriviFragment : Fragment() {

    private val model : StationsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_arrivi, container, false)

        val trainStationInfo = model.getStationTrains()

        val toast = Toast.makeText(this.context, "Nessun Treno", Toast.LENGTH_LONG)


        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val mLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager=mLayoutManager
        var adapter: RecyclerView.Adapter<TabelloneCardAdapter.CardViewHolder>

        if(trainStationInfo!![1].size!=0) {
            adapter = TabelloneCardAdapter(trainStationInfo!![1])
            recyclerView.adapter = adapter
        }
        else
            toast.show()

        return view
    }
}