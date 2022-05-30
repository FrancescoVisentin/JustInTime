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

class SolutionsViewModel : ViewModel() {
    private val ret: MutableLiveData<MutableList<HTTParser.SolutionInfo>> by lazy {
        MutableLiveData<MutableList<HTTParser.SolutionInfo>>()
    }
    var updated : Boolean = false

    fun searchSolutions(firstStation : String, secondStation : String, time: String) : MutableLiveData<MutableList<HTTParser.SolutionInfo>> {
        viewModelScope.launch {
            val firstStationID = getStationID(firstStation)
            val secondStationID = getStationID(secondStation)


            if (firstStationID.isEmpty() || secondStationID.isEmpty()){
                ret.value = null
                return@launch
            }

            val solutions = getSolutions(firstStationID.drop(1), secondStationID.drop(1), time)

            updated = true
            ret.value = HTTParser.parseSolutionsInfo(solutions)
        }
        return ret
    }

    //funzione per richiedere i dati alla view model senza effettuare nuovamente la query (utili per recuperare stato)
    fun getSolutions(): MutableList<HTTParser.SolutionInfo>? {
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

    private suspend fun getSolutions(firstStationID: String, secondStationID: String, time: String) : String {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()) //TODO controlla come si comporta il format orario di trenitalia

        return withContext(Dispatchers.IO) {
            val url = URL("http://www.viaggiatreno.it/infomobilita/resteasy/viaggiatreno/soluzioniViaggioNew/$firstStationID/$secondStationID/${date}T$time:00")
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