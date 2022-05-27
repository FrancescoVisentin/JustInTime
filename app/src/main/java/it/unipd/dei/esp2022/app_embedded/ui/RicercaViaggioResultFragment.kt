package it.unipd.dei.esp2022.app_embedded.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.activityViewModels
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.helpers.SolutionsViewModel

class RicercaViaggioResultFragment : Fragment() {
    private val model : SolutionsViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_ricerca_viaggio_result, container, false)

        val trainsInfo = model.getSolutions()
        trainsInfo?.forEach {
            Log.e("Info treno:", "Numero: ${it.trainNumber}")
        }

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