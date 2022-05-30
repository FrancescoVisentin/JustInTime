package it.unipd.dei.esp2022.app_embedded.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.helpers.TabelloneCardAdapter
import it.unipd.dei.esp2022.app_embedded.helpers.StationsViewModel

class Tabellone2Fragment : Fragment() {


    private val model : StationsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_tabellone2, container, false)
        val message = Tabellone2FragmentArgs.fromBundle(requireArguments()).message
        view.findViewById<TextView>(R.id.station).text = message.capitalize()
        val trainStationInfo = model.getStationTrains()


        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val mLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager=mLayoutManager
        var adapter:RecyclerView.Adapter<TabelloneCardAdapter.CardViewHolder>

        adapter= TabelloneCardAdapter(trainStationInfo!![1])
        recyclerView.adapter = adapter
        val tabLayout : TabLayout = view.findViewById(R.id.tabs)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(tab?.text=="PARTENZE") {
                    adapter = TabelloneCardAdapter(trainStationInfo[0])
                    recyclerView.adapter=adapter
                    recyclerView.visibility=View.VISIBLE
                }
                else {
                    adapter = TabelloneCardAdapter(trainStationInfo[1])
                    recyclerView.adapter=adapter
                    recyclerView.visibility=View.VISIBLE

                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                recyclerView.visibility=View.VISIBLE
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                recyclerView.visibility=View.INVISIBLE
                recyclerView.removeAllViews()
            }
        })

        return view
    }
}