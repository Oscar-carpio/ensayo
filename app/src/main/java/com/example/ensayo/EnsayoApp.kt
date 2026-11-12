package com.example.ensayo

import android.app.Application
import com.example.ensayo.data.local.SampleDataSeeder
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@HiltAndroidApp
class EnsayoApp : Application() {
    @Inject lateinit var seeder: SampleDataSeeder

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appScope.launch { seeder.seedIfEmpty() }
    }
}
