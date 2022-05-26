package it.unipd.dei.esp2022.app_embedded.helpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R

class PlannerListAdapter(private val plannersNames : ArrayList<String>) : RecyclerView.Adapter<PlannerListAdapter.ItemViewHolder>(){

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val plannerName =  itemView.findViewById<TextView>(R.id.planner_name)

        fun bind(name : String) {
            plannerName.text = name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.planner_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(plannersNames[position])
    }

    override fun getItemCount(): Int {
        return plannersNames.size
    }
}