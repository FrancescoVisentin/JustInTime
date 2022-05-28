package it.unipd.dei.esp2022.app_embedded.helpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.Train

class CardAdapter(private val trains: List<Train>)
    : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    private val onClickListener = View.OnClickListener { v ->
        val trainNumber1 = v.findViewById<TextView>(R.id.train_number_label)
        trainNumber1.text="0000"
    }

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val trainNumber : TextView
        val trainTime : TextView
        val trainPlace : TextView
        val trainBinary : TextView
        val trainInfo : TextView
        init {
            trainNumber=itemView.findViewById<View>(R.id.train_number) as TextView
            trainTime=itemView.findViewById<View>(R.id.orario) as TextView
            trainPlace=itemView.findViewById<View>(R.id.place) as TextView
            trainBinary=itemView.findViewById<View>(R.id.binario) as TextView
            trainInfo=itemView.findViewById<View>(R.id.information) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_tabellone, parent, false)
        view.setOnClickListener(onClickListener)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.trainNumber.text = trains[position].numero
        holder.trainNumber.visibility=View.VISIBLE
        holder.trainTime.text = trains[position].orario
        holder.trainTime.visibility=View.VISIBLE
        holder.trainPlace.text = trains[position].luogo
        holder.trainPlace.visibility=View.VISIBLE
        holder.trainBinary.text = trains[position].binario
        holder.trainBinary.visibility=View.VISIBLE
        holder.trainInfo.text = trains[position].ritardo
        holder.trainInfo.visibility=View.VISIBLE
    }

    override fun getItemCount(): Int = trains.size
}