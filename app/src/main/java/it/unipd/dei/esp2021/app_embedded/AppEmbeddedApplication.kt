package it.unipd.dei.esp2021.app_embedded

import android.app.Application
import com.google.android.material.color.DynamicColors

class AppEmbeddedApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //DynamicColors.applyToActivitiesIfAvailable(this)
    }
}