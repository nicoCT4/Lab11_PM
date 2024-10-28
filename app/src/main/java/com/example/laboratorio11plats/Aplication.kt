package com.example.laboratorio11plats // Asegúrate de que este sea tu paquete correcto

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializar Firebase App
        FirebaseApp.initializeApp(this)

        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
        firebaseAppCheck.setTokenAutoRefreshEnabled(true) // También puede ser 'false' para evitar auto-refresh

    }
}
