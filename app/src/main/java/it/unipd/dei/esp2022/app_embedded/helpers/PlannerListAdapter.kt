package it.unipd.dei.esp2022.app_embedded.helpers

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R

//Adapter per la RecyclerView utilizzata nella schermata 'Planner'.
class PlannerListAdapter(private val plannersNames : ArrayList<String>, private val listener : ClickListener) : RecyclerView.Adapter<PlannerListAdapter.ItemViewHolder>(){
    private lateinit var db: DBHelper

    //Nome del planner su cui ho fatto apparire il context menu.
    var selectedPlannerName : String = ""

    private val onClickListener = View.OnClickListener { v ->
        listener.onEvent(v.findViewById<TextView>(R.id.planner_name).text.toString())
    }

    private val onLongClickListener = View.OnLongClickListener { v ->
        selectedPlannerName = v.findViewById<TextView>(R.id.planner_name).text.toString()
        return@OnLongClickListener false
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener {
        private val plannerName =  itemView.findViewById<TextView>(R.id.planner_name)
        private val plannerTrainsCount = itemView.findViewById<TextView>(R.id.train_count)

        fun bind(name : String, count: String) {
            itemView.setOnCreateContextMenuListener(this)
            plannerName.text = name
            plannerTrainsCount.text = count
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu?.add(Menu.NONE, R.id.delete_option, Menu.NONE, R.string.delete)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.planner_item, parent, false)

        view.setOnClickListener(onClickListener)
        view.setOnLongClickListener(onLongClickListener)

        if (!::db.isInitialized){
            db = DBHelper(parent.context)
        }

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(plannersNames[position], db.getPlannerTrains(plannersNames[position]).toString())
    }

    override fun getItemCount(): Int {
        return plannersNames.size
    }

    interface ClickListener {
        fun onEvent(plannerName:String)
    }
}