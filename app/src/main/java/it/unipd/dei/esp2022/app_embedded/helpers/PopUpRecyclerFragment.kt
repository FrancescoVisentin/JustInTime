package it.unipd.dei.esp2022.app_embedded.helpers

import android.content.res.Configuration
import android.view.*
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R

//Classe base per fragment che utilizzano una PopupWindow per descrivere la tratta di un treno.
abstract class PopUpRecyclerFragment: PopUpFragment() {

    //Inserisce tutte le informazioni della tratta all'interno della PopupWindow utilizzando una RecyclerView.
    override fun createPopup(trainInfo: HTTParser.TrainInfo) {
        val inflater = LayoutInflater.from((view as View).context)
        val popupView = inflater.inflate(R.layout.popup_train_description_planner, view as ViewGroup, false)

        popupView.findViewById<TextView>(R.id.train_number).text = trainInfo.trainID
        popupView.findViewById<TextView>(R.id.train_route).text = getString(R.string.train_route, trainInfo.stops.first().stationName, trainInfo.stops.last().stationName)

        val percent = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 0.95 else 0.6

        val width = ((view as View).width*0.85).toInt()
        val height = ((view as View).height*percent).toInt()
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

        //Quando la PopupWindow viene ricreta in automatico da PopupFragment.onViewCreated l'activity sottostante non è
        //ancora stata inizializzata completamente. View.width e View.higth ritorneranno allora 0
        //Il metodo post "rallenta" la creazione dalla PopupWindow a quando questi parametri saranno stati inizializzati correttamente.
        if (height == 0 || width == 0) {
            popupContainerView.post {
                val updatedWidth = ((view as View).width*0.85).toInt()
                val updatedHeight = ((view as View).height*percent).toInt()
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

    //Aggiungo RecyclerView contenente tutte le fermate alla PopupWindow.
    private fun addStationsBar(popupView: View, stops: MutableList<HTTParser.StationInfo>) {
        val recyclerView = popupView.findViewById<RecyclerView>(R.id.stations_recycler_view)
        recyclerView.adapter = StationsListAdapter(stops, -1)
        recyclerView.layoutManager = LinearLayoutManager(context)
    }
}