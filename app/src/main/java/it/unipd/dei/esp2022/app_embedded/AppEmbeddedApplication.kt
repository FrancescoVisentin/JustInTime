package it.unipd.dei.esp2022.app_embedded

import android.app.Application
import android.content.Context
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColors.isDynamicColorAvailable
import com.test.app_embedded.R

//Classe usata per caricare il tema corretto all'avvio dell'applicazione.
class AppEmbeddedApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        //Se disponibili e se attivati dall'utente carica i colori dinamici altrimenti viene caricato il tema di default.
        if (isDynamicColorAvailable()) {
            val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: null
            val mode = sharedPref?.getBoolean("dynamic_colors", false) ?: false

            if (mode) {
                DynamicColors.applyToActivitiesIfAvailable(this)
            } else {
                DynamicColors.applyToActivitiesIfAvailable(this, R.style.Theme_AppEmbedded)
            }
        }
    }
}