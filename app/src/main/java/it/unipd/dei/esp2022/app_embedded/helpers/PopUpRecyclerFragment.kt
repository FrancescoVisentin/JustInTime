package it.unipd.dei.esp2022.app_embedded.helpers

import android.view.*
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R

abstract class PopUpRecyclerFragment: PopUpFragment() {

    override fun createPopup(trainInfo: HTTParser.TrainInfo) {
        val inflater = LayoutInflater.from((view as View).context)
        val popupView = inflater.inflate(R.layout.popup_train_description_planner, view as ViewGroup, false)

        popupView.findViewById<TextView>(R.id.train_number).text = trainInfo.trainID
        popupView.findViewById<TextView>(R.id.train_route).text = getString(R.string.train_route, trainInfo.stops.first().stationName, trainInfo.stops.last().stationName)

        val width = ((view as View).width*0.85).toInt()
        val height = ((view as View).height*0.6).toInt()
        popupWindow = PopupWindow(popupView, width, height,true)

        popupWindow?.animationStyle = androidx.appcompat.R.style.Animation_AppCompat_DropDownUp
        popupWindow?.elevation = 100F
        popupWindow?.isOutsideTouchable = true
        popupWindowActivated = true
        popupWindow?.setOnDismissListener {
            popupWindowActivated = false
            popupWindow = null
        }

        val popupContainerView = (view as View).findViewById<View>(R.id.popup_container)

        if (height == 0 || width == 0) {
            popupContainerView.post {
                val updatedWidth = ((view as View).width*0.85).toInt()
                val updatedHeight = ((view as View).height*0.6).toInt()
                popupWindow?.update(0,0, updatedWidth, updatedHeight)
                popupWindow?.showAtLocation(popupContainerView, Gravity.CENTER, 0, 0)
                popupWindow?.dimBehind()
            }
        } else {
            popupWindow?.showAtLocation(popupContainerView, Gravity.CENTER, 0, 0)
            popupWindow?.dimBehind()
        }

        val exitButton = popupView.findViewById<Button>(R.id.exit_button)
        exitButton.setOnClickListener {
            popupWindow?.dismiss()
        }

        setupAddButton(popupView, trainInfo.trainID)
        addStationsBar(popupView, trainInfo.stops)
    }

    protected open fun setupAddButton(popupView: View, trainID: String) {
        popupView.findViewById<Button>(R.id.add_button).visibility = View.INVISIBLE
    }

    private fun addStationsBar(popupView: View, stops: MutableList<HTTParser.StationInfo>) {
        val stationsAdapter = StationsListAdapter(stops, -1)
        val recyclerView = popupView.findViewById<RecyclerView>(R.id.stations_recycler_view)
        recyclerView.adapter = stationsAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }
}