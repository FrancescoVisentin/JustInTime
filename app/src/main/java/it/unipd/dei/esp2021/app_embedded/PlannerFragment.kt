package it.unipd.dei.esp2021.app_embedded

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialFadeThrough
import com.test.app_embedded.R

class PlannerFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_planner, container, false)

        val fab = view.findViewById<FloatingActionButton>(R.id.planner_fab)
        fab.setOnClickListener {
            val popupView = inflater.inflate(R.layout.popup_add_planner, view.parent as ViewGroup, false)

            val width = (view.width*0.85).toInt()
            val popupWindow = PopupWindow(popupView, width, ViewGroup.LayoutParams.WRAP_CONTENT,true)

            popupWindow.animationStyle = androidx.appcompat.R.style.Animation_AppCompat_DropDownUp
            popupWindow.elevation = 100F
            popupWindow.isOutsideTouchable = true
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

            val cancelButton = popupView.findViewById<Button>(R.id.exit_button)
            cancelButton.setOnClickListener() {
                popupWindow.dismiss()
            }
        }

        return  view
    }
}