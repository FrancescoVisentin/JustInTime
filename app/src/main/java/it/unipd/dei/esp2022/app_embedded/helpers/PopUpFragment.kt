package it.unipd.dei.esp2022.app_embedded.helpers

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.test.app_embedded.R


abstract class PopUpFragment: Fragment() {
    protected val trainModel: TrainViewModel by activityViewModels()
    protected var popupWindow: PopupWindow? = null
    protected var popupWindowActivated: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            popupWindowActivated= savedInstanceState.getBoolean("popup_visibility")

            if (popupWindowActivated) {
                val info = trainModel.getTrainState() ?: return
                createPopup(info)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("popup_visibility", popupWindowActivated)
    }

    override fun onStop() {
        super.onStop()
        val restore = popupWindowActivated
        popupWindow?.dismiss()
        popupWindowActivated = restore
    }

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
