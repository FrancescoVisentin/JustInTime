package it.unipd.dei.esp2022.app_embedded.helpers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R
import java.text.SimpleDateFormat
import java.util.*

class StationsListAdapter(private val stations : MutableList<HTTParser.StationInfo>, private val currentIndex : Int) : RecyclerView.Adapter<StationsListAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View, private val currentIndex: Int, private val context: Context) : RecyclerView.ViewHolder(itemView) {
        private val stationName = itemView.findViewById<TextView>(R.id.station_name)
        private val scheduledDeparture = itemView.findViewById<TextView>(R.id.scheduled_departure_value)
        private val scheduledArrival = itemView.findViewById<TextView>(R.id.scheduled_arrival_value)
        private val departureText = itemView.findViewById<TextView>(R.id.departure_text)
        private val arrivalText = itemView.findViewById<TextView>(R.id.arrival_text)
        private val departureValue = itemView.findViewById<TextView>(R.id.departure_value)
        private val arrivalValue = itemView.findViewById<TextView>(R.id.arrival_value)


        fun bind(stationInfo : HTTParser.StationInfo, position: Int) {
            stationName.text = stationInfo.stationName
            scheduledDeparture.text = getDate(stationInfo.scheduledDeparture)
            scheduledArrival.text = getDate(stationInfo.scheduledArrival)

            if (position <= currentIndex) {
                departureText.text = context.getString(R.string.real_departure)
                arrivalText.text = context.getString(R.string.real_arrival)
                departureValue.text = getDate(stationInfo.realDeparture)
                arrivalValue.text = getDate(stationInfo.realArrival)

            } else {
                departureText.text = context.getString(R.string.expected_departure)
                arrivalText.text = context.getString(R.string.expected_arrival)
                departureValue.text = getDate(stationInfo.expectedDeparture)
                arrivalValue.text = getDate(stationInfo.expectedArrival)
            }           
        }

        private fun getDate(date : String) : String {
            return when (date) {
                "null" -> "--"
                else -> SimpleDateFormat("HH:mm", Locale.ENGLISH).format(date.toLong())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.labels_layout, parent, false)
        return ItemViewHolder(view, currentIndex, parent.context)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(stations[position], position)
    }

    override fun getItemCount(): Int {
        return stations.size
    }
}