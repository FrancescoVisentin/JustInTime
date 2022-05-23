package it.unipd.dei.esp2021.app_embedded

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

class HttpViewModel : ViewModel() {

    fun searchStation(station: String) : MutableLiveData<String> {
        val ret: MutableLiveData<String> by lazy {
            MutableLiveData<String>()
        }

        viewModelScope.launch {
            val stationID = getStationID(station)

            if (stationID.isEmpty()){
                ret.value = ""
                return@launch
            }

            val departingTrains = getStationTrains(stationID, "partenze")
            val incomingTrains = getStationTrains(stationID, "arrivi")

            ret.value = "$departingTrains|$incomingTrains"
        }

        return ret
    }

    fun searchSolutions(firstStation : String, secondStation : String) : MutableLiveData<String> {
        val ret: MutableLiveData<String> by lazy {
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

            ret.value = solutions
        }

        return ret
    }

    fun searchTrain(trainID : String) : MutableLiveData<String> {
        val ret: MutableLiveData<String> by lazy {
            MutableLiveData<String>()
        }

        viewModelScope.launch {
            val trainInfo = getTrainID(trainID).split("|")

            if (trainInfo[0].isEmpty()){
                ret.value = ""
                return@launch
            }

            val trainTokens = trainInfo[1].split("-")
            val id = trainTokens[0]
            val origin = trainTokens[1]
            val date = trainTokens[2]

            if (id.compareTo(trainID) != 0) {
                ret.value = ""
                return@launch
            }

            val trainState = getTrainState(id, origin, date)
            val trainStops = getTrainRoute(id, origin, date)

            ret.value = "$trainState|$trainStops"
        }

        return ret
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
        val date = SimpleDateFormat("EE%20MMM%20dd%yyyy%20H:m:s", Locale.ENGLISH).format(Date()) //TODO controlla come si comporta il format orario di trenitalia

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

    private suspend fun getTrainID(trainID: String) : String {
        return withContext(Dispatchers.IO) {
            val url = URL("http://www.viaggiatreno.it/infomobilita/resteasy/viaggiatreno/cercaNumeroTrenoTrenoAutocomplete/$trainID")
            val urlConnection = url.openConnection() as HttpURLConnection
            try {
                val stream = BufferedInputStream(urlConnection.inputStream)
                return@withContext stream.bufferedReader().use { it.readText() }
            } finally {
                urlConnection.disconnect()
            }
        }
    }

    private suspend fun getTrainState(trainID: String, origin : String, date : String ) : String {
        return withContext(Dispatchers.IO) {
            val url = URL("http://www.viaggiatreno.it/infomobilita/resteasy/viaggiatreno/andamentoTreno/$origin/$trainID/$date")
            val urlConnection = url.openConnection() as HttpURLConnection
            try {
                val stream = BufferedInputStream(urlConnection.inputStream)
                return@withContext stream.bufferedReader().use { it.readText() }
            } finally {
                urlConnection.disconnect()
            }
        }
    }

    private suspend fun getTrainRoute(trainID: String, origin : String, date : String ) : String {
        return withContext(Dispatchers.IO) {
            val url = URL("http://www.viaggiatreno.it/infomobilita/resteasy/viaggiatreno/tratteCanvas/$origin/$trainID/$date")
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