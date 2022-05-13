package it.unipd.dei.esp2021.app_embedded

import android.app.Application
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColors.isDynamicColorAvailable
import com.test.app_embedded.R

class AppEmbeddedApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (isDynamicColorAvailable()) {
            DynamicColors.applyToActivitiesIfAvailable(this)
        }
    }
}