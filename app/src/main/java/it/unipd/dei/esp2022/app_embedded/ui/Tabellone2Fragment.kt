package it.unipd.dei.esp2022.app_embedded.ui

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.tabs.TabLayoutMediator
import com.test.app_embedded.R
import it.unipd.dei.esp2022.app_embedded.helpers.HTTParser
import it.unipd.dei.esp2022.app_embedded.helpers.TabelloneCardAdapter
import it.unipd.dei.esp2022.app_embedded.helpers.StationsViewModel
import it.unipd.dei.esp2022.app_embedded.helpers.TrainViewModel
import java.text.SimpleDateFormat
import java.util.*

class Tabellone2Fragment : Fragment(), TabelloneCardAdapter.ClickListener {
    private val stationsModel : StationsViewModel by activityViewModels()
    private val trainModel : TrainViewModel by activityViewModels()
    private lateinit var resObserver : Observer<HTTParser.TrainInfo>
    private var popupWindow: PopupWindow? = null
    private var popupWindowActivated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_tabellone2, container, false)

        resObserver = Observer<HTTParser.TrainInfo> { info ->
            if (info.trainID.compareTo("null") == 0) {
                stopFade()
                return@Observer
            }

            if (!trainModel.updated) {
                return@Observer
            }

            stopFade()
            createPopup(info)
        }

        val message = Tabellone2FragmentArgs.fromBundle(requireArguments()).message
        view.findViewById<TextView>(R.id.station).text = message.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
        view.findViewById<TextView>(R.id.station).text = message.capitalize()

        val tabTitle= arrayOf("ARRIVI", "PARTENZE")
        val tabLayout : TabLayout = view.findViewById(R.id.tabs)
        val viewPager : ViewPager2 = view.findViewById(R.id.view_pager)
        val adapter= ViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator (tabLayout, viewPager) {
            tab, position ->
            tab.text=tabTitle[position]
        }.attach()


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                /*if(tab?.text=="PARTENZE") {
                    if(trainStationInfo[0].size==0)
                        toast.show()
                    else {
                        adapter = TabelloneCardAdapter(trainStationInfo[0])
                        recyclerView.adapter = adapter
                        recyclerView.visibility = View.VISIBLE
                    }

                }
                else {
                    if(trainStationInfo[1].size==0)
                        toast.show()
                    else {
                        adapter = TabelloneCardAdapter(trainStationInfo[1])
                        recyclerView.adapter = adapter
                        recyclerView.visibility = View.VISIBLE
                    }

                }*/
                viewPager.currentItem=tab!!.position
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                //recyclerView.visibility=View.VISIBLE
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //recyclerView.visibility=View.INVISIBLE
                //recyclerView.removeAllViews()
            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            popupWindowActivated= savedInstanceState.getBoolean("popup_visibility")

            if (popupWindowActivated) {
                val info = trainModel.getTrainState() ?: return
                createPopup(info)
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

    override fun onEvent(number: String) {
        trainModel.updated = false
        trainModel.searchTrain(number).observe(viewLifecycleOwner, resObserver)
        startFade()
    }

    private fun createPopup(trainInfo: HTTParser.TrainInfo) {
        val inflater = LayoutInflater.from((view as View).context)
        val popupView = inflater.inflate(R.layout.popup_train_description, (view as View).parent as ViewGroup, false)

        popupView.findViewById<TextView>(R.id.train_number).text = trainInfo.trainID
        popupView.findViewById<TextView>(R.id.train_route).text = getString(R.string.train_route, trainInfo.stops.first().stationName, trainInfo.stops.last().stationName)
        popupView.findViewById<TextView>(R.id.train_position).text = trainInfo.lastDetectionStation
        popupView.findViewById<TextView>(R.id.time_last_detection).text = getDate(trainInfo.lastDetectionTime)
        popupView.findViewById<TextView>(R.id.delay).text = if (trainInfo.delay.toInt() >= 0) "+${trainInfo.delay} min" else "${trainInfo.delay} min"

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
            }
        } else {
            popupWindow?.showAtLocation(popupContainerView, Gravity.CENTER, 0, 0)
        }

        val exitButton = popupView.findViewById<Button>(R.id.exit_button)
        exitButton.setOnClickListener {
            popupWindow?.dismiss()
        }

        addStationsBar(popupView, trainInfo.stops, trainInfo.currentIndex)
    }

    private fun addStationsBar(popupView: View, stops: MutableList<HTTParser.StationInfo>, currentIndex: Int){
        val stepBar = popupView.findViewById<SeekBar>(R.id.step_bar)
        stepBar.setOnTouchListener { _, _ -> true }

        val stationsNum = stops.size-1

        stepBar.progress = currentIndex
        stepBar.max = stationsNum
        stepBar.minWidth = stationsNum*300

        val labelsLayout = popupView.findViewById<LinearLayout>(R.id.labels_layout)
        labelsLayout.removeAllViews()

        stops.forEachIndexed { index, stationInfo ->
            val label = TextView(popupView.context)
            label.text = stationInfo.stationName
            label.setPadding(0, 0, 10, 0)
            labelsLayout.addView(label)

            if (index == stationsNum-1) {
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

    private fun getDate(date : String) : String {
        return when (date) {
            "null" -> "--"
            else -> SimpleDateFormat("HH:mm", Locale.ENGLISH).format(date.toLong())
        }
    }

    private fun startFade() {
        val loadingView = (view as View).findViewById<View>(R.id.loading_spinner)
        val time = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        loadingView.apply {
            bringToFront()
            loadingView.alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).duration = time
        }
    }

    private fun stopFade() {
        val loadingView = (view as View).findViewById<View>(R.id.loading_spinner)

        loadingView.apply {
            loadingView.alpha = 0f
            visibility = View.INVISIBLE
        }
    }
}