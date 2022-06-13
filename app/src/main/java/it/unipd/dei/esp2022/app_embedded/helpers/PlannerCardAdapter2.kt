package it.unipd.dei.esp2022.app_embedded.helpers

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R

//Classe Adapter per gestire la componente Card nella Recycler View del Fragment Planner2
//Il parametro trainDB di tipo MutableList prende i dati salvati sul Database
class PlannerCardAdapter2(private val trainDB:  MutableList<DBHelper.TripInfo>, private val listener : ClickListener)
    : RecyclerView.Adapter<PlannerCardAdapter2.CardViewHolder4>() {
    var selectedTripNumber = ""

    //Comportamento del metodo onClickListener
    private val onClickListener = View.OnClickListener {
        val trainNumber = it.findViewById<TextView>(R.id.train_number).text.toString()
        listener.onEvent(trainNumber.split(" ")[0])
    }

    //Classe interna CardViewHolder contenente gli elementi dell'interfaccia utente di Card (card_layout_planner_dbcard)
    class CardViewHolder4(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener {
        val trainNumber: TextView = itemView.findViewById(R.id.train_number)
        val trainOrigin: TextView = itemView.findViewById(R.id.origin)
        val trainDestination: TextView = itemView.findViewById(R.id.destination)
        val departureTime: TextView = itemView.findViewById(R.id.departure_time)
        val arrivalTime: TextView = itemView.findViewById(R.id.arrival_time)
        val duration: TextView = itemView.findViewById(R.id.duration)
        val changes: TextView = itemView.findViewById(R.id.changes)

        fun bind() {
            itemView.setOnCreateContextMenuListener(this)
        }

        fun getNumber(): String {
            return trainNumber.text.toString()
        }

        //Creazione del Menu per Eliminare un elemento Card dalla RecyclerView
        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu?.add(Menu.NONE, R.id.delete_option, Menu.NONE, R.string.delete)
        }
    }

    //Imposta la view (inflated) e il metodo OnClickListener e ritorna il CardViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder4 {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_planner_dbcard, parent, false)
        view.setOnClickListener(onClickListener)
        return CardViewHolder4(view)
    }

    //Ottiene il treno corrente e lo usa per associare la view (binding). Imposta il comportamento del metodo OnLongClickListener()
    override fun onBindViewHolder(holder: CardViewHolder4, position: Int) {
        holder.bind()
        holder.trainNumber.text = trainDB[position].trainNumber
        holder.trainOrigin.text = trainDB[position].departureStation
        holder.trainDestination.text = trainDB[position].arrivalStation
        holder.departureTime.text = trainDB[position].departureTime
        holder.arrivalTime.text = trainDB[position].arrivalTime
        holder.duration.text = trainDB[position].duration
        holder.changes.text = trainDB[position].changes

        holder.itemView.setOnLongClickListener {
            selectedTripNumber = holder.getNumber()
            return@setOnLongClickListener false
        }
    }

    override fun getItemCount(): Int = trainDB.size

    interface ClickListener {
        fun onEvent(number: String)
    }
}