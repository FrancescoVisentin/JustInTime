package it.unipd.dei.esp2021.app_embedded

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R

class ListAdapter(private val trainList : Array<String>) : RecyclerView.Adapter<ListAdapter.ItemViewHolder>() {

    private val onClickListener = View.OnClickListener { v ->
        val train = v.findViewById<TextView>(R.id.train_id)
        train.text = "Train just clicked"
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trainTextView : TextView = itemView.findViewById(R.id.train_id)

        fun bind(word: String) {
            trainTextView.text=word
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_item, parent)
        //view.setOnClickListener(onClickListener)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(trainList[position])
    }

    override fun getItemCount(): Int {
        return trainList.size
    }

}