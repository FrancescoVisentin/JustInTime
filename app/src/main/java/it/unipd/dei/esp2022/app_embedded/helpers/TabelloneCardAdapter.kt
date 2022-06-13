package it.unipd.dei.esp2022.app_embedded.helpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R
import java.util.*

//Classe Adapter per gestire la componente Card nella Recycler View del Fragment Tabellone2

class TabelloneCardAdapter(private val trainStationInfo: MutableList<HTTParser.TrainStationInfo>, private val listener: ClickListener)
    : RecyclerView.Adapter<TabelloneCardAdapter.CardViewHolder>() {

    //Comportamento del metodo onClickListener
    private val onClickListener = View.OnClickListener {
        val number = it.findViewById<TextView>(R.id.train_number).text.toString()
        listener.onEvent(number)
    }

    //Classe interna CardViewHolder contenente gli elementi dell'interfaccia utente di Card (card_layout_tabellone)
    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val trainNumber: TextView = itemView.findViewById(R.id.train_number)
        val trainTime: TextView = itemView.findViewById(R.id.time)
        val trainPlace: TextView = itemView.findViewById(R.id.place)
        val trainBinary: TextView = itemView.findViewById(R.id.binary)
        val trainDelay: TextView = itemView.findViewById(R.id.information)
    }

    //Imposta la view (inflated) e il metodo OnClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_tabellone, parent, false)
        view.setOnClickListener(onClickListener)
        return CardViewHolder(view)
    }

    //Ottiene il treno corrente e lo usa per associare la view (binding)
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.trainNumber.text = trainStationInfo[position].trainNumber
        holder.trainTime.text = trainStationInfo[position].departureArrivalTime
        holder.trainPlace.text = trainStationInfo[position].destinationOrigin
        if(trainStationInfo[position].track.compareTo("null") != 0) {
            holder.trainBinary.text = trainStationInfo[position].track.substringAfter("\"").substringBefore("\"")
        }
        holder.trainDelay.text = trainStationInfo[position].delay.replace(".","")
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    override fun getItemCount(): Int = trainStationInfo.size

    interface ClickListener {
        fun onEvent(number: String)
    }
}