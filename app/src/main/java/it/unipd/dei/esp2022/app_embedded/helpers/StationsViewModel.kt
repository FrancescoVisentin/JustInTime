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

//ViewModel relativo ai dati di una singola stazione. Effettua richieste HTTP e gestisce la relativa business logic.
class StationsViewModel : ViewModel() {
    private val ret: MutableLiveData<ArrayList<MutableList<HTTParser.TrainStationInfo>>> by lazy {
        MutableLiveData<ArrayList<MutableList<HTTParser.TrainStationInfo>>>()
    }
    var updated: Boolean = false

    //Se la stazione cercata esiste effettivamente restituisce un oggetto LiveData contentente le informazioni dei treni in partenza/arrivo.
    fun searchStation(station: String) : MutableLiveData<ArrayList<MutableList<HTTParser.TrainStationInfo>>> {
        viewModelScope.launch {
            val stationID = getStationID(station)

            //Stazione non valida
            if (stationID.isEmpty()){
                ret.value = null
                return@launch
            }

            val departingTrains = getStationTrains(stationID, "partenze")
            val incomingTrains = getStationTrains(stationID, "arrivi")

            updated = true
            //Effettua il parsing dei json ottenuti.
            ret.value = HTTParser.parseStationsInfo(departingTrains, incomingTrains)
        }
        return ret
    }

    //Funzione usata per recupero dello stato/passaggio parametri senza ripetere nuovamente le richieste HTTP.
    //Restituisce i dati relativi all'ultima ricerca effettuata (se sono già avvenute richieste) altrimenti null.
    fun getStationTrains(): ArrayList<MutableList<HTTParser.TrainStationInfo>>? {
        return ret.value
    }

    //Se la stazione esiste effettivamente restituisce l'ID univoco associato.
    private suspend fun getStationID(station: String) : String {
        //Il server non processa correttamente le stazioni aventi nome più lungo di 20 caratteri.
        //É necessario troncare il nome della stringa e verificare poi che i risultati combacino.
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

                //Verifico se il nome cercato ed il nome relativo all'ID restituito combaciano.
                if (station.compareTo(stationName) != 0) {
                    return@withContext ""
                }
                return@withContext stationID
            } finally {
                urlConnection.disconnect()
            }
        }
    }

    //A seconda parametro 'mode' ritorna come stringa un json rappresentante le informazioni dei treni in partenza o in arrivo dalla stazione.
    private suspend fun getStationTrains(id : String, mode : String) : String {
        val date = SimpleDateFormat("EE%20MMM%20dd%20yyyy%20HH:mm:ss", Locale.ENGLISH).format(Date())
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