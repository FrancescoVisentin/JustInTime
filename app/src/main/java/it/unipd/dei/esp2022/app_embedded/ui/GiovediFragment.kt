package it.unipd.dei.esp2022.app_embedded.ui

import android.content.Context
import it.unipd.dei.esp2022.app_embedded.helpers.*
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R
import java.text.SimpleDateFormat
import java.util.*

class GiovediFragment : Fragment(), PlannerCardAdapter2.ClickListener {
    private lateinit var day: String
    private lateinit var plannerName:String
    private lateinit var recyclerView : RecyclerView
    private lateinit var db : DBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_planner_tabs, container, false)
        db = DBHelper(context as Context)
        plannerName
        day = "Giovedi"
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = PlannerCardAdapter2(db.getTrips(plannerName,day), this)
        return view
    }

    override fun onEvent(number: String) {
        TODO("Not yet implemented")
    }
}
