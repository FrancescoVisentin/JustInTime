package it.unipd.dei.esp2022.app_embedded.helpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R

class TabelloneCardAdapter(private val trainStationInfo: MutableList<HTTParser.TrainStationInfo>)
    : RecyclerView.Adapter<TabelloneCardAdapter.CardViewHolder>() {

    private val onClickListener = View.OnClickListener { v ->
        val trainNumber1 = v.findViewById<TextView>(R.id.train_number)
        trainNumber1.text="0000"
    }

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val trainNumber = itemView.findViewById<TextView>(R.id.train_number)
        val trainTime = itemView.findViewById<TextView>(R.id.time)
        val trainPlace = itemView.findViewById<TextView>(R.id.place)
        val trainBinary = itemView.findViewById<TextView>(R.id.binary)
        val trainDelay = itemView.findViewById<TextView>(R.id.information)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_tabellone, parent, false)
        view.setOnClickListener(onClickListener)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.trainNumber.text = trainStationInfo[position].trainNumber
        holder.trainTime.text = trainStationInfo[position].departureArrivalTime
        holder.trainPlace.text = trainStationInfo[position].destinationOrigin
        if(trainStationInfo[position].track.contains("null"))
        {
            holder.trainBinary.text = "Non disponibile"
        }
        else
        {
            holder.trainBinary.text = trainStationInfo[position].track.substringAfter("\"").substringBefore("\"")
        }
        holder.trainDelay.text = trainStationInfo[position].delay.replace(".","").capitalize()
    }

    override fun getItemCount(): Int = trainStationInfo.size
}