package it.unipd.dei.esp2021.app_embedded.helpers

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
    //TODO questa va tenuta aggiustando il tipo di MutableLiveData<>
    private val ret: MutableLiveData<HTTParser.TrainInfo> by lazy {
        MutableLiveData<HTTParser.TrainInfo>()
    }

    fun searchSolutions(firstStation : String, secondStation : String) : MutableLiveData<String> {
        val ret: MutableLiveData<String> by lazy { //TODO questo ret va eliminato
            MutableLiveData<String>()
        }

        viewModelScope.launch {
            val firstStationID = getStationID(firstStation)
            val secondStationID = getStationID(secondStation)


            if (firstStationID.isEmpty() || secondStationID.isEmpty()){
                ret.value = ""
                return@launch
            }

            val solutions = getSolutions(firstStationID.drop(1), secondStationID.drop(1))

            //TODO aggiungere parsing qua e settare il valore a quello non al json
            ret.value = solutions
        }

        return ret
    }

    //funzione per richiedere i dati alla view model senza effettuare nuovamente la query (utili per recuperare stato)
    fun getSolutions(): HTTParser.TrainInfo? {
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

    private suspend fun getSolutions(firstStationID: String, secondStationID: String) : String {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()) //TODO controlla come si comporta il format orario di trenitalia

        return withContext(Dispatchers.IO) {
            val url = URL("http://www.viaggiatreno.it/infomobilita/resteasy/viaggiatreno/soluzioniViaggioNew/$firstStationID/$secondStationID/${date}T00:00:00")
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