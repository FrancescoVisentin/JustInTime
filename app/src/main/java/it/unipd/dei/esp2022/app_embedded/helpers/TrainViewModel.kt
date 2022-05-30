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

class TrainViewModel : ViewModel() {
    private val ret: MutableLiveData<HTTParser.TrainInfo> by lazy {
        MutableLiveData<HTTParser.TrainInfo>()
    }
    var updated: Boolean = false

    fun searchTrain(trainID : String) : MutableLiveData<HTTParser.TrainInfo> {
        viewModelScope.launch {
            val trainInfo = getTrainID(trainID).split("\n")[0].split("|")

            if (trainInfo[0].isEmpty()){
                ret.value = HTTParser.TrainInfo("null")
                return@launch
            }

            val trainTokens = trainInfo[1].split("-")
            val id = trainTokens[0]
            val origin = trainTokens[1]
            val date = trainTokens[2]

            if (id.compareTo(trainID) != 0) {
                ret.value = HTTParser.TrainInfo("null")
                return@launch
            }

            val trainState = getTrainState(id, origin, date)
            val trainStops = getTrainRoute(id, origin, date)

            updated = true
            ret.value = HTTParser.parseTrainInfo(trainID, trainState, trainStops)
        }

        return ret
    }

    fun getTrainState(): HTTParser.TrainInfo? {
        return ret.value
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