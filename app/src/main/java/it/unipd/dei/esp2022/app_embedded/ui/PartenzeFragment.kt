package it.unipd.dei.esp2022.app_embedded.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.helpers.*

class PartenzeFragment : PopUpSeekBarFragment(), TabelloneCardAdapter.ClickListener {
    private val stationsModel : StationsViewModel by activityViewModels()
    lateinit var resObserver : Observer<HTTParser.TrainInfo>
    private var paused: Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_tabellone_tabs, container, false)

        resObserver = Observer<HTTParser.TrainInfo> { info ->
            if (info.trainID.compareTo("null") == 0) {
                stopFade()
                return@Observer
            }

            if (!trainModel.updated || paused) {
                return@Observer
            }

            stopFade()
            createPopup(info)
        }

        val trainStationInfo = stationsModel.getStationTrains() ?: return view

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = TabelloneCardAdapter(trainStationInfo[0], this)

        return view
    }

    override fun onPause() {
        super.onPause()
        paused = true
    }

    override fun onResume() {
        super.onResume()
        paused = false
    }

    override fun onEvent(number: String) {
        trainModel.updated = false
        trainModel.searchTrain(number).observe(viewLifecycleOwner, resObserver)
        startFade()
    }
}