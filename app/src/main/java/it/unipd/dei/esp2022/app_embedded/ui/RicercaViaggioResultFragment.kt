package it.unipd.dei.esp2022.app_embedded.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.test.app_embedded.R

class RicercaViaggioResultFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_ricerca_viaggio_result, container, false)

        val cardLayout = view.findViewById<LinearLayout>(R.id.linear)
        for(i in 0..6)
        {
            val card = inflater.inflate(R.layout.card_layout, container, false) as CardView
            card.setOnClickListener{
                card.findViewById<TextView>(R.id.train_number).text = "Ciao mondo"
            }
            cardLayout.addView(card)
        }
        return view
    }

}