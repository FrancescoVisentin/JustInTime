package it.unipd.dei.esp2021.app_embedded

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R
import org.w3c.dom.Text

class CardAdapter(private val trains: List<Train>)
    : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    private val onClickListener = View.OnClickListener { v ->
        val trainNumber1 = v.findViewById<TextView>(R.id.train_number_label)
        trainNumber1.text="0000"
    }

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val trainNumber : TextView
        val trainTime : TextView
        val trainInfo : TextView
        init {
            trainNumber=itemView.findViewById<View>(R.id.train_number) as TextView
            trainTime=itemView.findViewById<View>(R.id.orario) as TextView
            trainInfo=itemView.findViewById<View>(R.id.information) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_model, parent, false)
        view.setOnClickListener(onClickListener)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.trainNumber.text = trains[position].number
        holder.trainNumber.visibility=View.VISIBLE
        holder.trainTime.text = trains[position].orario
        holder.trainTime.visibility=View.VISIBLE
        holder.trainInfo.text = trains[position].state
        holder.trainInfo.visibility=View.VISIBLE
    }

    override fun getItemCount(): Int = trains.size
}