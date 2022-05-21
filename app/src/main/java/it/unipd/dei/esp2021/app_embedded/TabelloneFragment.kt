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
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import com.test.app_embedded.R

class TabelloneFragment : Fragment() {
    private val model : HttpViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_tabellone, container, false)

        val textView = view.findViewById<AutoCompleteTextView>(R.id.text_autocomplete)
        val items = resources.getStringArray(R.array.stations)
        val adapter = ArrayAdapter(context as Context, android.R.layout.simple_dropdown_item_1line, items)
        textView.setAdapter(adapter)

        textView.setOnEditorActionListener { v, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    //Hide keyboard
                    textView.clearFocus()
                    val imm = (activity as Activity).getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(v.windowToken, 0)

                    if (textView.text.isNotEmpty()){
                        searchStation(textView.text.toString().lowercase())
                    }
                    true
                }
                else -> false
            }
        }

        val searchButton = view.findViewById<Button>(R.id.search_button)
        searchButton.setOnClickListener{
            if (textView.text.isNotEmpty()){
                searchStation(textView.text.toString().lowercase())
            }
        }

        return  view
    }

    private fun searchStation(station : String) {
        val resObserver = Observer<String> {res ->
            if (res.isEmpty()) {
                val contextView = (view as View).findViewById<View>(R.id.coordinator_layout)
                Snackbar.make(contextView, "Stazione non valida", Snackbar.LENGTH_SHORT)
                    .setAction("Chiudi") {}
                    .show()
                return@Observer
            }

            Log.e("Res:", res)
            //TODO carica res come putExtra e naviga ad Tabellone 2 per parsing dei risultati
        }
        model.searchStation(station).observe(viewLifecycleOwner, resObserver)
    }

}