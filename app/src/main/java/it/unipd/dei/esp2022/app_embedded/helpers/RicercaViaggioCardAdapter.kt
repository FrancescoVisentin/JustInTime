package it.unipd.dei.esp2022.app_embedded.helpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R

//Classe Adapter per gestire l'elemento Card nella Recycler View del Fragment RicercaViaggioResult
class RicercaViaggioCardAdapter(private val solutionsInfo:  MutableList<HTTParser.SolutionInfo>, private val listener : ClickListener)
    : RecyclerView.Adapter<RicercaViaggioCardAdapter.CardViewHolder2>() {

    private val onClickListener = View.OnClickListener {
        val trainNumber = it.findViewById<TextView>(R.id.train_number).text.toString()
        listener.onEvent(trainNumber.split(" ")[0])
    }
    //Classe interna CardViewHolder contenente gli elementi dell'interfaccia utente di Card (card_layout_ricerca_viaggio)
    class CardViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView){
        val trainNumber: TextView = itemView.findViewById(R.id.train_number)
        val departureTime: TextView = itemView.findViewById(R.id.departure_time)
        val arrivalTime: TextView = itemView.findViewById(R.id.arrival_time)
        val duration: TextView = itemView.findViewById(R.id.duration)
        val changes: TextView = itemView.findViewById(R.id.changes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder2 {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_ricerca_viaggio, parent, false)
        view.setOnClickListener(onClickListener)
        return CardViewHolder2(view)
    }

    //Ottiene il treno corrente e lo usa per associare la view (binding)
    override fun onBindViewHolder(holder: CardViewHolder2, position: Int) {
        var trainString : String = (solutionsInfo[position].trainNumber).first()
        for(i in 1..(solutionsInfo[position].trainNumber).lastIndex)
        {
            trainString = trainString + " + " + (solutionsInfo[position].trainNumber)[i]
        }
        holder.trainNumber.text = trainString
        holder.departureTime.text = (solutionsInfo[position].departureTime).substringAfter("T").substringBeforeLast(":")
        holder.arrivalTime.text = (solutionsInfo[position].arrivalTime).substringAfter("T").substringBeforeLast(":")
        holder.duration.text = solutionsInfo[position].duration
        holder.changes.text = solutionsInfo[position].changes.toString()
    }

    override fun getItemCount(): Int = solutionsInfo.size

    interface ClickListener {
        fun onEvent(number: String)
    }
}