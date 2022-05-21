package it.unipd.dei.esp2021.app_embedded

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.transition.MaterialFadeThrough
import com.test.app_embedded.R
import okhttp3.*
import java.io.IOException

class StatoTrenoFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_stato_treno, container, false)

        val textView = view.findViewById<TextInputEditText>(R.id.text_train_number)
        textView.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //Hide keyboard
                textView.clearFocus()
                val imm = (activity as Activity).getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(v.windowToken, 0)

                searchTrain(textView.text.toString())

                return@setOnEditorActionListener true
            }

            return@setOnEditorActionListener false
        }

        val searchButton = view.findViewById<Button>(R.id.search_button)
        searchButton.setOnClickListener{
            searchTrain(textView.text.toString())
        }

        return  view
    }

    private fun searchTrain(number : String) {
        val num = if (number.length < 20) number else number.substring(0,20)
        val client = OkHttpClient()

        var request = Request.Builder()
            .url("http://www.viaggiatreno.it/infomobilita/resteasy/viaggiatreno/cercaNumeroTrenoTrenoAutocomplete/$num")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val res = response.body!!.string()
                    Log.e("LOG", res)
                    if (res.isEmpty()) {
                        Log.e("ERRORE", "Empty: $res")
                        return
                    }

                    val tokens = res.split("|", ignoreCase = false, limit = 2)[1]
                    val ids = tokens.split("-")

                    getTrainState(ids)
                }
            }
        })
    }

    private fun getTrainState(ids : List<String>) {
        val client = OkHttpClient()

        var request = Request.Builder()
            .url("http://www.viaggiatreno.it/infomobilita/resteasy/viaggiatreno/andamentoTreno/${ids[1]}/${ids[0]}/${ids[2]}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val res = response.body!!.string().lowercase()
                    Log.e("LOG", res)
                    if (res.isEmpty()) {
                        Log.e("ERRORE", "Empty: $res")
                        return
                    }

                    val tokens = res.split("{")
                    for (t in tokens)
                        Log.e("RES:", t)
                }
            }
        })
    }

}