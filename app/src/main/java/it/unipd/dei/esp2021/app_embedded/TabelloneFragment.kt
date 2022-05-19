package it.unipd.dei.esp2021.app_embedded

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialFadeThrough
import com.test.app_embedded.R

class TabelloneFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_tabellone, container, false)

        val items = arrayOf<String>("Pippo", "pluto", "topolino")

        val adapter = ArrayAdapter<String>(context as Context, android.R.layout.simple_dropdown_item_1line, items)
        val textView = view.findViewById<AutoCompleteTextView>(R.id.text_autocomplete)
        textView.setAdapter(adapter)

        return  view
    }
}