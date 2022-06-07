package it.unipd.dei.esp2022.app_embedded.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.helpers.DBHelper
import it.unipd.dei.esp2022.app_embedded.helpers.PlannerListAdapter

class PlannerFragment : Fragment(), PlannerListAdapter.ClickListener {
    private lateinit var db : DBHelper
    private lateinit var recyclerView : RecyclerView
    private lateinit var plannerImageView : LinearLayout
    private var popupWindow: PopupWindow? = null
    private var popupWindowActivated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_planner, container, false)

        db = DBHelper(context as Context)
        recyclerView = view.findViewById(R.id.planner_recycler_view)
        plannerImageView = view.findViewById(R.id.no_planners_image)

        recyclerView.adapter = PlannerListAdapter(db.getPlannersName(), this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        registerForContextMenu(recyclerView)
        checkPlannersCount()

        val fab = view.findViewById<FloatingActionButton>(R.id.planner_fab)
        fab.setOnClickListener {
            createPopup()
        }

        return  view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            popupWindowActivated= savedInstanceState.getBoolean("popup_visibility")

            if (popupWindowActivated) {
                createPopup()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("popup_visibility", popupWindowActivated)
    }

    override fun onStop() {
        super.onStop()
        val restore = popupWindowActivated
        popupWindow?.dismiss()
        popupWindowActivated = restore
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_option -> {
                val name = (recyclerView.adapter as PlannerListAdapter).selectedPlannerName
                val contextView = (view as View).findViewById<View>(R.id.coordinator_layout)
                if (db.deletePlanner(name)){
                    Snackbar.make(contextView, "Planner $name eliminato", Snackbar.LENGTH_SHORT)
                        .setAction("Chiudi") {}
                        .show()

                    recyclerView.adapter = PlannerListAdapter(db.getPlannersName(), this)
                    checkPlannersCount()
                } else  {
                    Snackbar.make(contextView, "Errore", Snackbar.LENGTH_SHORT)
                        .setAction("Chiudi") {}
                        .show()
                }
            }
        }

        return super.onContextItemSelected(item)
    }

    override fun onEvent(plannerName:String) {
        val action = PlannerFragmentDirections.actionPlannerFragmentToPlanner2Fragment(plannerName)
        (view as View).findNavController().navigate(action)
    }

    private fun createPopup() {
        val inflater = LayoutInflater.from((view as View).context)
        val popupView = inflater.inflate(R.layout.popup_add_planner, view as ViewGroup, false)

        val width = ((view as View).width*0.85).toInt()
        popupWindow = PopupWindow(popupView, width, ViewGroup.LayoutParams.WRAP_CONTENT,true)

        popupWindow?.animationStyle = androidx.appcompat.R.style.Animation_AppCompat_DropDownUp
        popupWindow?.elevation = 100F
        popupWindow?.isOutsideTouchable = true
        popupWindowActivated = true
        popupWindow?.setOnDismissListener {
            popupWindowActivated = false
            popupWindow = null
        }

        val popupContainerView = (view as View).findViewById<View>(R.id.popup_container)

        if (width == 0) {
            popupContainerView.post {
                val updatedWidth = ((view as View).width*0.85).toInt()
                popupWindow?.update(0,0, updatedWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
                popupWindow?.showAtLocation(popupContainerView, Gravity.CENTER, 0, 0)
                popupWindow?.dimBehind()
            }
        } else {
            popupWindow?.showAtLocation(popupContainerView, Gravity.CENTER, 0, 0)
            popupWindow?.dimBehind()
        }

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
        cancelButton.setOnClickListener {
            popupWindow?.dismiss()
        }

        val addButton = popupView.findViewById<Button>(R.id.add_button)
        addButton.setOnClickListener {
            if (popupTextView.text.isNotEmpty()) {
                //hide keyboard
                val imm = (activity as Activity).getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(popupView.windowToken, 0)

                if (db.addPlanner(popupTextView.text.toString())) {
                    recyclerView.adapter = PlannerListAdapter(db.getPlannersName(), this)
                    checkPlannersCount()
                } else {
                    val contextView = (view as View).findViewById<View>(R.id.coordinator_layout)
                    Snackbar.make(contextView, "Nome non valido", Snackbar.LENGTH_SHORT)
                        .setAction("Chiudi") {}
                        .show()
                }

                popupWindow?.dismiss()
            }
        }
    }

    private fun checkPlannersCount() {
        plannerImageView.visibility = if (db.getPlannersCount() == 0) View.VISIBLE else View.INVISIBLE
    }

    private fun PopupWindow.dimBehind() {
        val container = contentView.rootView
        val context = contentView.context
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.3f
        wm.updateViewLayout(container, p)
    }
}