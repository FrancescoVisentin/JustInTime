package it.unipd.dei.esp2022.app_embedded.helpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.Train

class CardAdapter2(private val trains: List<Train>)
    : RecyclerView.Adapter<CardAdapter2.CardViewHolder2>() {

    private val onClickListener = View.OnClickListener { v ->
        val trainNumber1 = v.findViewById<TextView>(R.id.train_number_label)
        trainNumber1.text="0000"
    }

    class CardViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView){
        //TODO Dichiarare e inizializzare riferimenti ai componenti del Layout
        val trainNumber : TextView
        init {
            trainNumber=itemView.findViewById<View>(R.id.train_number) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder2 {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_ricerca_viaggio, parent, false)
        view.setOnClickListener(onClickListener)
        return CardViewHolder2(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder2, position: Int) {
        //TODO Modificare campo 'text' con il valore dell'attributo della classe Train
        holder.trainNumber.text = trains[position].numero
    }

    override fun getItemCount(): Int = trains.size
}