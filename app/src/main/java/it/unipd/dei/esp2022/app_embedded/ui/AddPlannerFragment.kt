package it.unipd.dei.esp2022.app_embedded.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.test.app_embedded.R

class AddPlannerFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_add_planner, container, false)

        return  view
    }
}