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
import com.google.android.material.tabs.TabLayout
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.helpers.CardAdapter
import it.unipd.dei.esp2022.app_embedded.Train
import it.unipd.dei.esp2022.app_embedded.helpers.StationsViewModel
import it.unipd.dei.esp2022.app_embedded.trainList
import it.unipd.dei.esp2022.app_embedded.trainList2

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
        Log.e("Message:", message)
        val trainStationInfo = model.getStationTrains()
        Log.e("trainInfo: ",trainStationInfo?.get(0).toString())
        trainStationInfo?.get(0)?.forEach {
            Log.e("Info treno:", "Numero: ${it.trainNumber}, tipo: ${it.category}")
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        var mLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager=mLayoutManager
        var adapter:RecyclerView.Adapter<CardAdapter.CardViewHolder>?=null

        adapter= CardAdapter(trainStationInfo!!.get(1))
        recyclerView.adapter = adapter
        val tabLayout : TabLayout = view.findViewById(R.id.tabs)
       /* val cardLayout = view.findViewById<LinearLayout>(R.id.linear_tab2)
        val arrivi : MutableList<CardView> = mutableListOf()
        val partenze : MutableList<CardView> = mutableListOf()
        for(i in 0..6)
        {
            val card = inflater.inflate(R.layout.card_layout, container, false) as CardView
            card.setOnClickListener{
                card.findViewById<TextView>(R.id.train_number).text = i.toString()
            }
            if(i%2==0) {
                card.findViewById<TextView>(R.id.departures).text="Orario Provenienza"
                card.findViewById<TextView>(R.id.arrivals).text=""
                arrivi.add(card)
            }
            else {
                card.findViewById<TextView>(R.id.departures).text="Orario destinazione"
                card.findViewById<TextView>(R.id.arrivals).text=""
                partenze.add(card)
            }
        }
        for (o in arrivi)
            cardLayout.addView(o)
        cardLayout.visibility = View.VISIBLE
        */
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(tab?.text=="PARTENZE") {
                    adapter = CardAdapter(trainStationInfo!!.get(0))
                    recyclerView.adapter=adapter
                    recyclerView.visibility=View.VISIBLE
                }
                else {
                    adapter = CardAdapter(trainStationInfo!!.get(1))
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