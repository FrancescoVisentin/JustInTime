package it.unipd.dei.esp2021.app_embedded

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.test.app_embedded.R


class RicercaViaggioFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ricerca_viaggio, container, false)
        val button_hour: Button = view.findViewById(R.id.button_hour)
        val button_min: Button = view.findViewById(R.id.button_min)
        button_hour.setOnClickListener {
            val picker: MaterialTimePicker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(Integer.parseInt((button_hour.text).toString()))
                    .setMinute(Integer.parseInt((button_min.text).toString()))
                    .setTitleText("Seleziona orario di viaggio")
                    .build()

            picker.show(childFragmentManager, "tag");

            picker.addOnPositiveButtonClickListener {
                button_hour.text = (picker.hour).toString()
                button_min.text = (picker.minute).toString()
            }
        }
        button_min.setOnClickListener {
            val picker: MaterialTimePicker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(Integer.parseInt((button_hour.text).toString()))
                    .setMinute(Integer.parseInt((button_min.text).toString()))
                    .setTitleText("Seleziona orario di viaggio")
                    .build()

            picker.show(childFragmentManager, "tag");

            picker.addOnPositiveButtonClickListener {
                button_hour.text = (picker.hour).toString()
                button_min.text = (picker.minute).toString()
            }
        }

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //(requireActivity() as AppCompatActivity).supportActionBar?.hide()
        //requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val bu: Button = view.findViewById(R.id.outlinedButton)

        bu.setOnClickListener { // Perform action on click
            view.findNavController().navigate(R.id.action_ricercaViaggioFragment_to_ricercaViaggioResultFragment)
        }
    }

}