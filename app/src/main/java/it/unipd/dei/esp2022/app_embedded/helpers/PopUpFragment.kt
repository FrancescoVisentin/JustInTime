package it.unipd.dei.esp2022.app_embedded.helpers

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.test.app_embedded.R

//Classe di base per Fragment che utilizzano PopupWindow per descrivere l'andamento o la tratta di un treno.
//Gestisce salvataggio di stato ed le animazioni per il caricamento delle PopupWindow.
abstract class PopUpFragment: Fragment() {
    protected val trainModel: TrainViewModel by activityViewModels()
    protected var popupWindow: PopupWindow? = null
    protected var popupWindowActivated: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            popupWindowActivated= savedInstanceState.getBoolean("popup_visibility")

            //Ricreo PopupWindow se questa era attiva quando l'activity è stata terminata.
            if (popupWindowActivated) {
                trainModel.getTrainState().observe(viewLifecycleOwner){ info ->
                    createPopup(info)
                    trainModel.getTrainState().removeObservers(viewLifecycleOwner)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("popup_visibility", popupWindowActivated)
    }

    //Salvo la visibilità della PopupWindow quando l'activity viene terminata, anche automaticamente (Es. ruoto schermo).
    override fun onStop() {
        super.onStop()
        val restore = popupWindowActivated
        popupWindow?.dismiss()
        popupWindowActivated = restore
    }

    override fun onResume() {
        super.onResume()
        if (popupWindowActivated && popupWindow == null) {
            trainModel.getTrainState().observe(viewLifecycleOwner) { info ->
                createPopup(info)
                trainModel.getTrainState().removeObservers(viewLifecycleOwner)
            }
        }
    }

    //Definisce il layout effettivo della PopupWindow.
    protected abstract fun createPopup(trainInfo: HTTParser.TrainInfo)

    protected fun startFade() {
        val loadingView = (view as View).findViewById<View>(R.id.loading_spinner)
        val time = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        loadingView.apply {
            bringToFront()
            loadingView.alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).duration = time
        }
    }

    protected fun stopFade() {
        val loadingView = (view as View).findViewById<View>(R.id.loading_spinner)

        loadingView.apply {
            loadingView.alpha = 0f
            visibility = View.INVISIBLE
        }
    }

    protected fun PopupWindow.dimBehind() {
        val container = contentView.rootView
        val context = contentView.context
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.3f
        wm.updateViewLayout(container, p)
    }
}
