package it.unipd.dei.esp2022.app_embedded.ui

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialFadeThrough
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.helpers.DBHelper
import it.unipd.dei.esp2022.app_embedded.helpers.ListAdapter


class HomeFragment : Fragment(), ListAdapter.ClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val fab = view.findViewById<FloatingActionButton>(R.id.home_fab)
        fab.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_homeFragment_to_ricercaViaggioFragment)
        }

        val recyclerView : RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.adapter= ListAdapter(requireContext().resources.getStringArray(R.array.train_list), this)

        return view
    }

    override fun onEvent(train : String) {
        val inflater = LayoutInflater.from((view as View).context)
        val popupView = inflater.inflate(R.layout.popup_train_description, (view as View).parent as ViewGroup, false)

        val trainNum = popupView.findViewById<TextView>(R.id.train_number)
        trainNum.text = train.removePrefix("Treno ")

        val width = ((view as View).width*0.85).toInt()
        val height = ((view as View).height*0.6).toInt()
        val popupWindow = PopupWindow(popupView, width, height,false)

        popupWindow.animationStyle = androidx.appcompat.R.style.Animation_AppCompat_DropDownUp
        popupWindow.elevation = 100F
        popupWindow.isOutsideTouchable = true
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        val exitButton = popupView.findViewById<Button>(R.id.exit_button)
        exitButton.setOnClickListener {
            popupWindow.dismiss()
        }

        addStationsBar(popupView)
    }

    private fun addStationsBar(popupView: View) {
        val num = 12

        val stepBar = popupView.findViewById<SeekBar>(R.id.step_bar)

        stepBar.setOnTouchListener { _, _ -> true }

        stepBar.progress = 3
        stepBar.max = num
        stepBar.minWidth = num*250

        val labelsLayout = popupView.findViewById<LinearLayout>(R.id.labels_layout)

        for (i in 0..num) {
            val label = TextView(popupView.context)
            label.text = "Padova"
            label.setPadding(0, 0, 10, 0)
            labelsLayout.addView(label)

            if (i == num-1) {
                label.layoutParams = getLayoutParams(1.0f)
            }
            else {
                label.layoutParams = getLayoutParams(0.0f)
            }
        }
    }

    private fun getLayoutParams(weight: Float): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(250, LinearLayout.LayoutParams.WRAP_CONTENT, weight)
    }
}