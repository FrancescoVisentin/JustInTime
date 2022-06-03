package it.unipd.dei.esp2022.app_embedded.helpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R

class PlannerCardAdapter2(private val trainDB:  MutableList<HTTParser.SolutionInfo>, private val listener : ClickListener)
    : RecyclerView.Adapter<PlannerCardAdapter2.CardViewHolder4>() {

    private val onClickListener = View.OnClickListener {
        val trainNumber = it.findViewById<TextView>(R.id.train_number).text.toString()
        listener.onEvent(trainNumber.split(" ")[0])
    }

    class CardViewHolder4(itemView: View) : RecyclerView.ViewHolder(itemView){
        val trainNumber: TextView = itemView.findViewById(R.id.train_number)
        val trainOrigin: TextView = itemView.findViewById(R.id.origin)
        val trainDestination: TextView = itemView.findViewById(R.id.destination)
        val departureTime: TextView = itemView.findViewById(R.id.departure_time)
        val arrivalTime: TextView = itemView.findViewById(R.id.arrival_time)
        val duration: TextView = itemView.findViewById(R.id.duration)
        val changes: TextView = itemView.findViewById(R.id.changes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder4 {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_planner_dbcard, parent, false)
        view.setOnClickListener(onClickListener)
        return CardViewHolder4(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder4, position: Int) {
        var trainString : String = (trainDB[position].trainNumber).first()
        for(i in 1..(trainDB[position].trainNumber).lastIndex)
        {
            trainString = trainString + " + " + (trainDB[position].trainNumber)[i]
        }
        holder.trainNumber.text = trainString
        holder.departureTime.text = (trainDB[position].departureTime).substringAfter("T").substringBeforeLast(":")
        holder.arrivalTime.text = (trainDB[position].arrivalTime).substringAfter("T").substringBeforeLast(":")
        //holder.trainOrigin.text = trainDB[position].origin
        //holder.trainDestination.text = trainDB[position].destination
        holder.duration.text = trainDB[position].duration
        holder.changes.text = trainDB[position].changes.toString()
    }

    override fun getItemCount(): Int = trainDB.size

    interface ClickListener {
        fun onEvent(number: String)
    }
}