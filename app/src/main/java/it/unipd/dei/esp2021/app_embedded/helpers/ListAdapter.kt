package it.unipd.dei.esp2021.app_embedded.helpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R

class ListAdapter(private val trainList : Array<String>,  private  val listener : ClickListener) : RecyclerView.Adapter<ListAdapter.ItemViewHolder>() {

    private val onClickListener = View.OnClickListener { v ->
        val train = v.findViewById<TextView>(R.id.train_id).text.toString()
        listener.onEvent(train)
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trainTextView : TextView = itemView.findViewById(R.id.train_id)

        fun bind(word: String) {
            trainTextView.text=word
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_item, parent, false)
        view.setOnClickListener(onClickListener)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(trainList[position])
    }

    override fun getItemCount(): Int {
        return trainList.size
    }

    interface ClickListener {
        fun onEvent(train : String)
    }

}