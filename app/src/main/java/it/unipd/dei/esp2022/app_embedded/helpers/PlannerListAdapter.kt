package it.unipd.dei.esp2022.app_embedded.helpers

import android.content.Context
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R


class PlannerListAdapter(private val plannersNames : ArrayList<String>, private val listener : ClickListener) : RecyclerView.Adapter<PlannerListAdapter.ItemViewHolder>(){
    var selectedPlannerName : String = ""
    private lateinit var db: DBHelper

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener {
        private val plannerName =  itemView.findViewById<TextView>(R.id.planner_name)
        private val plannerTrainsCount = itemView.findViewById<TextView>(R.id.train_count)

        fun bind(name : String) {
            itemView.setOnCreateContextMenuListener(this)
            plannerName.text = name
            plannerTrainsCount.text = DBHelper(itemView.context as Context).getPlannerTrains(name).toString()
        }

        fun getName(): String {
            return plannerName.text.toString()
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu?.add(Menu.NONE, R.id.delete_option, Menu.NONE, R.string.delete)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.planner_item, parent, false)
        if (!::db.isInitialized){
            db = DBHelper(parent.context)
        }
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(plannersNames[position])
        //TODO Trovare un'alternativa a questo
        holder.itemView.setOnClickListener {
            selectedPlannerName = holder.getName()
            listener.onEvent(selectedPlannerName)
        }
        holder.itemView.setOnLongClickListener {
            selectedPlannerName = holder.getName()
            return@setOnLongClickListener false
        }
    }

    override fun getItemCount(): Int {
        return plannersNames.size
    }

    interface ClickListener {
        fun onEvent(plannerName:String)
    }
}