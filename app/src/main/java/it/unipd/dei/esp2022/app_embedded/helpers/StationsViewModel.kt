package it.unipd.dei.esp2022.app_embedded.helpers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class StationsViewModel : ViewModel() {
    private val ret: MutableLiveData<Array<MutableList<HTTParser.TrainStationInfo>>> by lazy {
        MutableLiveData<Array<MutableList<HTTParser.TrainStationInfo>>>()
    }
    var updated: Boolean = false

    fun searchStation(station: String) : MutableLiveData<Array<MutableList<HTTParser.TrainStationInfo>>> {
        viewModelScope.launch {
            val stationID = getStationID(station)

            if (stationID.isEmpty()){
                ret.value = null
                return@launch
            }

            val departingTrains = getStationTrains(stationID, "partenze")
            val incomingTrains = getStationTrains(stationID, "arrivi")

            updated = true
            ret.value = HTTParser.parseStationsInfo(departingTrains, incomingTrains)
        }
        return ret
    }

    fun getStationTrains(): Array<MutableList<HTTParser.TrainStationInfo>>? {
        return ret.value
    }

    private suspend fun getStationID(station: String) : String {
        val st = if (station.length < 20) station else station.substring(0,20)

        return withContext(Dispatchers.IO) {
            val url = URL("http://www.viaggiatreno.it/infomobilita/resteasy/viaggiatreno/autocompletaStazione/$st")
            val urlConnection = url.openConnection() as HttpURLConnection
            try {
                val res = BufferedInputStream(urlConnection.inputStream).bufferedReader().use { it.readText() }
                val stationInfo = res.split("\n")[0]

                if (stationInfo.isEmpty()) {
                    return@withContext ""
                }

                val stationTokens = stationInfo.split("|", ignoreCase = false, limit = 2)
                val stationName = stationTokens[0].lowercase()
                val stationID = stationTokens[1]

                if (station.compareTo(stationName) != 0) {
                    return@withContext ""
                }
                return@withContext stationID
            } finally {
                urlConnection.disconnect()
            }
        }
    }

    private suspend fun getStationTrains(id : String, mode : String) : String {
        val date = SimpleDateFormat("EE%20MMM%20dd%20yyyy%20HH:mm:ss", Locale.ENGLISH).format(Date()) //TODO controlla come si comporta il format orario di trenitalia
        return withContext(Dispatchers.IO) {
            val url = URL("http://www.viaggiatreno.it/infomobilita/resteasy/viaggiatreno/$mode/$id/$date%20GMT+0200%20(Ora%20legale%20dell%E2%80%99Europa%20centrale)")
            val urlConnection = url.openConnection() as HttpURLConnection
            try {
                val stream = BufferedInputStream(urlConnection.inputStream)
                return@withContext stream.bufferedReader().use { it.readText() }
            } finally {
                urlConnection.disconnect()
            }
        }
    }
}