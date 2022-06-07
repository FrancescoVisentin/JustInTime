package it.unipd.dei.esp2022.app_embedded.helpers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R

class HomeListAdapter(private val trainList : MutableList<DBHelper.TripInfo>, private  val listener : ClickListener) : RecyclerView.Adapter<HomeListAdapter.ItemViewHolder>() {

    private val onClickListener = View.OnClickListener { v ->
        val number = v.findViewById<TextView>(R.id.train_id).text.toString()
        listener.onEvent(number.split(" ")[0])
    }

    class ItemViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
        private val trainId : TextView = itemView.findViewById(R.id.train_id)
        private val plannerName: TextView = itemView.findViewById(R.id.planner_name)
        private val trainRoute: TextView = itemView.findViewById(R.id.train_route)
        private val departureTime: TextView = itemView.findViewById(R.id.departure_time)
        private val arrivalTime: TextView = itemView.findViewById(R.id.arrival_time)

        fun bind(tripInfo: DBHelper.TripInfo) {
            trainId.text = tripInfo.trainNumber
            plannerName.text = tripInfo.plannerName
            trainRoute.text = context.getString(R.string.train_route, tripInfo.departureStation, tripInfo.arrivalStation)
            departureTime.text = context.getString(R.string.home_departure, tripInfo.departureTime)
            arrivalTime.text = context.getString(R.string.home_arrival, tripInfo.arrivalTime)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_item, parent, false)
        view.setOnClickListener(onClickListener)
        return ItemViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(trainList[position])
    }

    override fun getItemCount(): Int {
        return trainList.size
    }

    interface ClickListener {
        fun onEvent(number: String)
    }

}