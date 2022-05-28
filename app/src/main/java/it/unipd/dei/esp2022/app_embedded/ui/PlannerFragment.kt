package it.unipd.dei.esp2022.app_embedded.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialFadeThrough
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.helpers.DBHelper
import it.unipd.dei.esp2022.app_embedded.helpers.PlannerListAdapter

class PlannerFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_planner, container, false)

        val db = DBHelper(context as Context)
        val recyclerView = view.findViewById<RecyclerView>(R.id.planner_recycler_view)
        recyclerView.adapter = PlannerListAdapter(db.getPlannersName())
        recyclerView.layoutManager = LinearLayoutManager(context)

        val fab = view.findViewById<FloatingActionButton>(R.id.planner_fab)
        fab.setOnClickListener {
            val popupView = inflater.inflate(R.layout.popup_add_planner, view.parent as ViewGroup, false)

            val width = (view.width*0.85).toInt()
            val popupWindow = PopupWindow(popupView, width, ViewGroup.LayoutParams.WRAP_CONTENT,true)

            popupWindow.animationStyle = androidx.appcompat.R.style.Animation_AppCompat_DropDownUp
            popupWindow.elevation = 100F
            popupWindow.isOutsideTouchable = true
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

            val popupTextView = popupView.findViewById<AutoCompleteTextView>(R.id.text_autocomplete)
            popupTextView.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    popupTextView.clearFocus()
                    //hide keyboard
                    val imm = (activity as Activity).getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(popupView.windowToken, 0)

                    return@setOnEditorActionListener true
                }

                return@setOnEditorActionListener false
            }

            val cancelButton = popupView.findViewById<Button>(R.id.exit_button)
            cancelButton.setOnClickListener() {
                popupWindow.dismiss()
            }

            val addButton = popupView.findViewById<Button>(R.id.add_button)
            addButton.setOnClickListener {
                if (popupTextView.text.isNotEmpty()) {
                    //hide keyboard
                    val imm = (activity as Activity).getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(popupView.windowToken, 0)

                    db.addPlanner(popupTextView.text.toString())
                    recyclerView.adapter = PlannerListAdapter(db.getPlannersName())
                    popupWindow.dismiss()
                }

            }
        }

        return  view
    }
}