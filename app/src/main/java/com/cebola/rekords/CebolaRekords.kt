package com.cebola.rekords

import android.app.Application
import com.cebola.rekords.data.database.DatabaseInitializer
import com.cebola.rekords.di.ApplicationScope
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class CebolaRekords : Application() {
    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope
    @Inject
    lateinit var databaseInitializer: DatabaseInitializer
    override fun onCreate() {
        super.onCreate()
        
        applicationScope.launch {
            databaseInitializer.initialize()
       
         }
    }
}