package it.unipd.dei.esp2021.app_embedded

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.test.app_embedded.R

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val fab = view.findViewById<FloatingActionButton>(R.id.home_fab)
        fab.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_homeFragment_to_ricercaViaggioFragment)
        }

        val recyclerView : RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.adapter= ListAdapter(requireContext().resources.getStringArray(R.array.train_list))

        return view
    }
}