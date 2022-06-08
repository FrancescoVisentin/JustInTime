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

//ViewModel relativo ai dati di un singolo treno. Effettua richieste HTTP e gestisce la relativa business logic.
class TrainViewModel : ViewModel() {
    private val ret: MutableLiveData<HTTParser.TrainInfo> by lazy {
        MutableLiveData<HTTParser.TrainInfo>()
    }
    var updated: Boolean = false

    //Se il trainID cercato esiste effettivamente restituisce un opportuno oggetto LiveData contentente tutte le informazioni del treno.
    fun searchTrain(trainID : String) : MutableLiveData<HTTParser.TrainInfo> {
        viewModelScope.launch {
            val trainInfo = getTrainID(trainID).split("\n")[0].split("|")

            //ID non valido.
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
            //Effettua il parsing dei json ottenuti.
            ret.value = HTTParser.parseTrainInfo(trainID, trainState, trainStops)
        }

        return ret
    }

    //Funzione usata per recupero dello stato/passaggio parametri senza ripetere nuovamente le richieste HTTP.
    //Restituisce i dati relativi all'ultima ricerca effettuata (se sono già avvenute richieste) altrimenti null.
    fun getTrainState(): HTTParser.TrainInfo? {
        return ret.value
    }

    //Se il trainID cercato esiste effettivamente, ritorna 'ID-Stazione d'origine-Giorno di partenza' per il treno cercato.
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

    //Ritorna come stringa un json rappresentante lo stato del treno: ritardo, posizione, ultimo rilevamento...
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

    //Ritorna come stringa un json rappresentante la tratta del treno: elenco di tutte le stazioni con relativi orari di partenza/arrivo.
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