package it.unipd.dei.esp2022.app_embedded.helpers

import android.annotation.SuppressLint
import android.view.*
import android.widget.*
import com.test.app_embedded.R
import java.text.SimpleDateFormat
import java.util.*

//Classe base per fragment che utilizzano una PopupWindow per descrivere l'andamento di un treno.
abstract class PopUpSeekBarFragment: PopUpFragment() {

    //Inserisce tutte le informazioni relative all'andamento del treno all'interno della PopupWindow.
    override fun createPopup(trainInfo: HTTParser.TrainInfo) {
        val inflater = LayoutInflater.from((view as View).context)
        val popupView = inflater.inflate(R.layout.popup_train_description, view as ViewGroup, false)

        popupView.findViewById<TextView>(R.id.train_number).text = trainInfo.trainID
        popupView.findViewById<TextView>(R.id.train_route).text = getString(R.string.train_route, trainInfo.stops.first().stationName, trainInfo.stops.last().stationName)
        popupView.findViewById<TextView>(R.id.train_position).text = trainInfo.lastDetectionStation
        popupView.findViewById<TextView>(R.id.time_last_detection).text = getDate(trainInfo.lastDetectionTime)
        popupView.findViewById<TextView>(R.id.delay).text = if (trainInfo.delay.toInt() >= 0) "+${trainInfo.delay} min" else "${trainInfo.delay} min"

        val width = ((view as View).width*0.85).toInt()
        popupWindow = PopupWindow(popupView, width, WindowManager.LayoutParams.WRAP_CONTENT,true)

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
        //ancora stata inizializzata completamente. View.width ritorna allora 0
        //Il metodo post "rallenta" la creazione dalla PopupWindow a quando questo parametro sarà stato inizializzato correttamente.
        if (width == 0) {
            popupContainerView.post {
                val updatedWidth = ((view as View).width*0.85).toInt()
                popupWindow?.update(0,0, updatedWidth, WindowManager.LayoutParams.WRAP_CONTENT)
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

        addStationsBar(popupView, trainInfo.stops, trainInfo.currentIndex)
    }

    //Aggiunge l'elenco delle stazioni della tratta ed una progress bar che indica la stazione corrente.
    @SuppressLint("ClickableViewAccessibility")
    private fun addStationsBar(popupView: View, stops: MutableList<HTTParser.StationInfo>, currentIndex: Int){
        val stepBar = popupView.findViewById<SeekBar>(R.id.step_bar)
        //Rendo la SeekBar non interagibile dall'utente.
        //Suppress warning Lint relativo all'accessibilità del componente (in realtà non è più possibile interagire con questa SeekBar)
        stepBar.setOnTouchListener { _, _ -> true }

        val stationsNum = stops.size-1

        stepBar.progress = currentIndex
        stepBar.max = stationsNum
        stepBar.minWidth = stationsNum*300

        val labelsLayout = popupView.findViewById<LinearLayout>(R.id.labels_layout)
        labelsLayout.removeAllViews()

        //Per ogni stazione aggiungo una nuova label al mio layout.
        stops.forEachIndexed { index, stationInfo ->
            val label = TextView(popupView.context)
            label.text = stationInfo.stationName
            label.setPadding(0, 0, 10, 0)
            labelsLayout.addView(label)

            if (index == stationsNum-1) {
                //L'ultima label occuperà tutto lo spazio rimanente.
                label.layoutParams = getLayoutParams(1.0f)
            }
            else {
                label.layoutParams = getLayoutParams(0.0f)
            }
        }
    }

    private fun getLayoutParams(weight: Float): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT, weight)
    }

    //Trasforma unix timestamp in una orario nel formato 'ore:min' (Es: 15:48)
    private fun getDate(date : String) : String {
        return when (date) {
            "null" -> "--"
            else -> SimpleDateFormat("HH:mm", Locale.ENGLISH).format(date.toLong())
        }
    }
}