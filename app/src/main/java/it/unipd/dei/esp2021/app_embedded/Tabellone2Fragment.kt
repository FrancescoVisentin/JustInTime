package it.unipd.dei.esp2021.app_embedded

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.test.app_embedded.R
import org.w3c.dom.Text

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Tabellone2Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Tabellone2Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private fun populateTrain() {
        val train1 = Train(
            "1",
            "10:30",
            "Nessuna"
        )
        trainList.add(train1)

        val train2 = Train(
            "2",
            "19:30",
            "ritardo"
        )
        trainList.add(train2)

        val train3 = Train(
            "3",
            "09:00",
            "Binario rotto"
        )
        trainList2.add(train3)
        trainList2.add(train3)
        trainList2.add(train3)
        trainList2.add(train3)
        trainList2.add(train3)
        trainList2.add(train3)
        trainList2.add(train3)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_tabellone2, container, false)


        populateTrain()

        val view = inflater.inflate(R.layout.fragment_tabellone2, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        var mLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager=mLayoutManager
        var adapter:RecyclerView.Adapter<CardAdapter.CardViewHolder>?=null

        adapter=CardAdapter(trainList)
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
                    adapter = CardAdapter(trainList2)
                    recyclerView.adapter=adapter
                    recyclerView.visibility=View.VISIBLE
                }
                else {
                    adapter = CardAdapter(trainList)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Tabellone2Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Tabellone2Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}