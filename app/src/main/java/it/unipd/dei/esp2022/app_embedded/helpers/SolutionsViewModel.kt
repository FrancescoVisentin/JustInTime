package it.unipd.dei.esp2022.app_embedded.helpers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
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
class SolutionsViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val ret: MutableLiveData<MutableList<HTTParser.SolutionInfo>> by lazy {
        MutableLiveData<MutableList<HTTParser.SolutionInfo>>()
    }
    private val lastQuery = savedStateHandle.get<String>("query") ?: ""
    var updated: Boolean = false

    //Se le stazioni cercate esistono effettivamente restituisce un oggetto LiveData contentente le informazioni dei treni che percorrono quella tratta.
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

        savedStateHandle["query"] = "$firstStation|$secondStation|$time"
        return ret
    }

    //Funzione usata per recupero dello stato/passaggio parametri senza ripetere nuovamente le richieste HTTP.
    //Restituisce i dati relativi all'ultima ricerca effettuata (se sono già avvenute richieste) altrimenti null.
    fun getSolutions(): MutableLiveData<MutableList<HTTParser.SolutionInfo>> {
        if (ret.value == null && lastQuery != ""){
            val info = lastQuery.split("|")
            searchSolutions(info[0], info[1], info[2])
        }

        return ret
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

    //Ritorna come stringa un json contenente l'elenco di tutti i treni che percorrono la tratta selezionata, con le relative informazioni.
    private suspend fun getSolutions(firstStationID: String, secondStationID: String, time: String) : String {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())

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